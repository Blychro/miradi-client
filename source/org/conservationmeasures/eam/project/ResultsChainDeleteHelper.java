/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.project;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandDeleteObject;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.dialogs.DiagramPanel;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.DiagramFactor;
import org.conservationmeasures.eam.objects.DiagramFactorLink;
import org.conservationmeasures.eam.objects.DiagramObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.FactorLink;

public class ResultsChainDeleteHelper
{

	public ResultsChainDeleteHelper(Project projectToUse, DiagramPanel diagramPanelToUse)
	{
		project = projectToUse;
		diagramPanel = diagramPanelToUse;
	}
	
	public void deleteResultsChain() throws Exception
	{
		DiagramObject diagramObject = diagramPanel.getDiagramObject();
		deleteAllDiagramFactorLinks();
		deleteAllDiagramFactors();
		CommandDeleteObject deleteResultsChain = new CommandDeleteObject(diagramObject.getRef());
		project.executeCommand(deleteResultsChain);
	}
	
	private void deleteAllDiagramFactors() throws Exception
	{
		DiagramModel model = diagramPanel.getDiagramModel();
		DiagramFactor[] allDiagramFactors = model.getAllDiagramFactorsAsArray();
		
		for (int i = 0; i < allDiagramFactors.length; i++)
		{
			deleteDiagramFactorAndFactor(allDiagramFactors[i]);
		}
	}

	private void deleteDiagramFactorAndFactor(DiagramFactor diagramFactor) throws Exception
	{
		FactorId factorId = diagramFactor.getWrappedId();
		
		DiagramObject diagramObject = diagramPanel.getDiagramObject();
		CommandSetObjectData removeFactorFromChain = CommandSetObjectData.createRemoveIdCommand(diagramObject, DiagramObject.TAG_DIAGRAM_FACTOR_IDS, diagramFactor.getDiagramFactorId());
		project.executeCommand(removeFactorFromChain);
	
		Command[] commandsToClear = diagramFactor.createCommandsToClear();
		project.executeCommands(commandsToClear);
		
		CommandDeleteObject deleteDiagramFactor = new CommandDeleteObject(diagramFactor.getRef());
		project.executeCommand(deleteDiagramFactor);
		
		Factor factor = project.findNode(factorId);
		ObjectManager objectManager = project.getObjectManager();
		ORefList referrers = factor.findObjectsThatReferToUs(objectManager, ObjectType.DIAGRAM_FACTOR, factor.getRef());
		
		if (referrers.size() > 0)
			return;

		Command[] commandsToClearFactor = factor.createCommandsToClear();
		project.executeCommands(commandsToClearFactor);
		
		CommandDeleteObject deleteFactor = new CommandDeleteObject(factor.getRef());
		project.executeCommand(deleteFactor);
	}

	private void deleteAllDiagramFactorLinks() throws Exception
	{
		DiagramModel model = diagramPanel.getDiagramModel();
		DiagramFactorLink[] allDiagramLinks = model.getAllDiagramLinksAsArray();
		
		for (int i = 0; i < allDiagramLinks.length; i++)
		{ 
			deleteDiagramLinkAndFactorLink(allDiagramLinks[i]);
		}
	}
	
	private void deleteDiagramLinkAndFactorLink(DiagramFactorLink diagramLink) throws Exception
	{
		FactorLinkId factorLinkId = diagramLink.getWrappedId();
		
		DiagramObject diagramObject = diagramPanel.getDiagramObject();
		CommandSetObjectData removeFactorLinkFromChain = CommandSetObjectData.createRemoveIdCommand(diagramObject, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS, diagramLink.getDiagramLinkageId());
		project.executeCommand(removeFactorLinkFromChain);
	
		Command[] commandsToClear = diagramLink.createCommandsToClear();
		project.executeCommands(commandsToClear);
		
		CommandDeleteObject deleteDiagramLink = new CommandDeleteObject(diagramLink.getRef());
		project.executeCommand(deleteDiagramLink);
		
		FactorLink factorLink = (FactorLink) project.findObject(new ORef(ObjectType.FACTOR_LINK, factorLinkId));
		ObjectManager objectManager = project.getObjectManager();
		ORefList referrers = factorLink.findObjectsThatReferToUs(objectManager, ObjectType.DIAGRAM_LINK, factorLink.getRef());
		
		if (referrers.size() > 0)
			return;
		
		Command[] commandsToClearFactorLink = factorLink.createCommandsToClear();
		project.executeCommands(commandsToClearFactorLink);
		
		CommandDeleteObject deleteFactorLink = new CommandDeleteObject(factorLink.getRef());
		project.executeCommand(deleteFactorLink);
	}
			
	private Project project;
	private DiagramPanel diagramPanel;
}
