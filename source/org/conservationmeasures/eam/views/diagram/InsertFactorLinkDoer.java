/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import java.text.ParseException;

import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.DiagramFactorLinkId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.main.ConnectionPropertiesDialog;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.CreateDiagramFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.CreateFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.DiagramFactor;
import org.conservationmeasures.eam.objects.DiagramFactorLink;
import org.conservationmeasures.eam.objects.DiagramObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.FactorLink;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.project.ProjectChainObject;
import org.conservationmeasures.eam.views.ViewDoer;

public class InsertFactorLinkDoer extends ViewDoer
{
	public boolean isAvailable()
	{
		if (!isDiagramView())
			return false;
		
		
		return (getDiagramView().getDiagramModel().getFactorCount() >= 2);
	}

	public void doIt() throws CommandFailedException
	{
		DiagramView diagramView = getDiagramView();
		ConnectionPropertiesDialog dialog = new ConnectionPropertiesDialog(getMainWindow(), diagramView.getDiagramPanel());
		dialog.setVisible(true);
		if(!dialog.getResult())
			return;
			
		DiagramModel model = diagramView.getDiagramModel();
		DiagramFactor fromDiagramFactor = dialog.getFrom();
		DiagramFactor toDiagramFactor = dialog.getTo();
		
		try
		{
			if (linkWasRejected(model, fromDiagramFactor, toDiagramFactor))
				return;
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
		
		getProject().executeCommand(new CommandBeginTransaction());
		try
		{
			createModelLinkageAndAddToDiagramUsingCommands(model, fromDiagramFactor, toDiagramFactor);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
		finally
		{
			getProject().executeCommand(new CommandEndTransaction());	
		}
	}

	public static boolean linkWasRejected(DiagramModel model, DiagramFactorId fromDiagramFactorId, DiagramFactorId toDiagramFactorId) throws Exception
	{
		Project project = model.getProject();
		DiagramFactor fromDiagramFactor = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, fromDiagramFactorId));
		DiagramFactor toDiagramFactor = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, toDiagramFactorId));
		
		return linkWasRejected(model, fromDiagramFactor, toDiagramFactor);
	}
	
	public static boolean linkWasRejected(DiagramModel model, DiagramFactor fromDiagramFactor, DiagramFactor toDiagramFactor) throws Exception
	{
		if(fromDiagramFactor.getDiagramFactorId().equals(toDiagramFactor.getDiagramFactorId()))
		{
			String[] body = {EAM.text("Can't link an item to itself"), };
			EAM.okDialog(EAM.text("Can't Create Link"), body);
			return true;
		}
		
		if(model.areLinked(fromDiagramFactor.getDiagramFactorId(), toDiagramFactor.getDiagramFactorId()))
		{
			String[] body = {EAM.text("Those items are already linked"), };
			EAM.okDialog(EAM.text("Can't Create Link"), body);
			return true;
		}
		
		if (wouldCreateLinkageLoop(model, fromDiagramFactor.getWrappedId(), toDiagramFactor.getWrappedId()))
		{
			String[] body = {EAM.text("Cannot create that link because it would cause a loop."), };
			EAM.okDialog(EAM.text("Error"), body);
			return true;
		}
		
		if(fromDiagramFactor.getDiagramFactorId().isInvalid() || toDiagramFactor.getDiagramFactorId().isInvalid())
		{
			EAM.logWarning("Unable to Paste Link : from " + fromDiagramFactor.getDiagramFactorId() + " to OriginalId:" + toDiagramFactor.getDiagramFactorId()+" node deleted?");	
			return true;
		}

		if (! model.containsDiagramFactor(fromDiagramFactor.getDiagramFactorId()) || ! model.containsDiagramFactor(toDiagramFactor.getDiagramFactorId()))
			return true;

		
		return false;
	}
	
	public static boolean wouldCreateLinkageLoop(DiagramModel model, DiagramFactorId fromDiagramFactorId, DiagramFactorId toDiagramFactorId)
	{
		FactorId fromId = model.getWrappedId(fromDiagramFactorId);
		FactorId toId = model.getWrappedId(toDiagramFactorId);
	
		return wouldCreateLinkageLoop(model, fromId, toId);
	}
	
	public static boolean wouldCreateLinkageLoop(DiagramModel model, FactorId fromId, FactorId toId)
    {
		Factor fromFactor = model.getProject().findNode(fromId);
		ProjectChainObject chainObject = new ProjectChainObject();
		chainObject.buildUpstreamChain(fromFactor);
		Factor[] upstreamFactors = chainObject.getFactorsArray();
		
		for (int i  = 0; i < upstreamFactors.length; i++)
			if (upstreamFactors[i].getId().equals(toId))
				return true;
		
		return false;
    }
	
	public static DiagramFactorLink createModelLinkageAndAddToDiagramUsingCommands(DiagramModel model, DiagramFactor diagramFactorFrom, DiagramFactor diagramFactorTo) throws Exception
	{
		DiagramObject diagramObject = model.getDiagramObject();
		Project project = model.getProject();
		
		return createModelLinkageAndAddToDiagramUsingCommands(project, diagramObject, diagramFactorFrom, diagramFactorTo);
	}

	public static DiagramFactorLink createModelLinkageAndAddToDiagramUsingCommands(Project project, DiagramObject diagramObject, DiagramFactor diagramFactorFrom, DiagramFactor diagramFactorTo) throws CommandFailedException, ParseException
	{
		FactorId fromFactorId = diagramFactorFrom.getWrappedId();
		FactorId toFactorId = diagramFactorTo.getWrappedId();
		FactorLinkId modelLinkageId = project.getFactorLinkPool().getLinkedId(fromFactorId, toFactorId);
		if(modelLinkageId != null)
		{
			FactorLink link = (FactorLink)project.findObject(FactorLink.getObjectType(), modelLinkageId);
			if(!link.getFromFactorId().equals(fromFactorId))
				throw new RuntimeException(EAM.text("Link in opposite direction already exists"));
		}
		if(modelLinkageId == null)
		{
			CreateFactorLinkParameter extraInfo = new CreateFactorLinkParameter(fromFactorId, toFactorId);
			CommandCreateObject createModelLinkage = new CommandCreateObject(ObjectType.FACTOR_LINK, extraInfo);
			project.executeCommand(createModelLinkage);
			modelLinkageId = (FactorLinkId)createModelLinkage.getCreatedId();
		}
		DiagramFactorId fromDiagramFactorId = diagramFactorFrom.getDiagramFactorId();
		DiagramFactorId toDiagramFactorId = diagramFactorTo.getDiagramFactorId();
		CreateDiagramFactorLinkParameter diagramLinkExtraInfo = createDiagramFactorLinkParameter(project, fromDiagramFactorId, toDiagramFactorId, modelLinkageId);
		CommandCreateObject createDiagramLinkCommand =  new CommandCreateObject(ObjectType.DIAGRAM_LINK, diagramLinkExtraInfo);
		project.executeCommand(createDiagramLinkCommand);
    	
    	BaseId rawId = createDiagramLinkCommand.getCreatedId();
		DiagramFactorLinkId createdDiagramLinkId = new DiagramFactorLinkId(rawId.asInt());
		
		CommandSetObjectData addDiagramLink = CommandSetObjectData.createAppendIdCommand(diagramObject, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS, createdDiagramLinkId);
		project.executeCommand(addDiagramLink);
		
		return (DiagramFactorLink) project.findObject(new ORef(ObjectType.DIAGRAM_LINK, createdDiagramLinkId));
	}

	private static CreateDiagramFactorLinkParameter createDiagramFactorLinkParameter(Project projectToUse, DiagramFactorId fromId, DiagramFactorId toId, FactorLinkId modelLinkageId)
	{
		CreateDiagramFactorLinkParameter diagramLinkExtraInfo = new CreateDiagramFactorLinkParameter(modelLinkageId, fromId, toId);
		
		return diagramLinkExtraInfo;
	}
}
