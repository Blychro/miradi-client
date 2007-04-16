/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.DiagramFactor;
import org.conservationmeasures.eam.objects.DiagramFactorLink;
import org.conservationmeasures.eam.objects.DiagramObject;
import org.conservationmeasures.eam.project.Project;

public class DiagramModelUpdater
{

	public DiagramModelUpdater(Project projectToUse, DiagramModel modelToUse, DiagramObject diagramObjectToUse)
	{
		project = projectToUse;
		model = modelToUse;
		diagramObject = diagramObjectToUse;
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		if (! event.getCommandName().equals(CommandSetObjectData.COMMAND_NAME))
			return;
		
		try
		{
			CommandSetObjectData setCommand = (CommandSetObjectData) event.getCommand();
			
			if (! setCommand.getObjectORef().equals(diagramObject.getRef()))
				return;
						
			updateFactors(setCommand);
			updateLinks(setCommand);
			model.updateVisibilityOfFactors();
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}
	
	private void updateFactors(CommandSetObjectData setCommand) throws Exception
	{
		if (! setCommand.getFieldTag().equals(DiagramObject.TAG_DIAGRAM_FACTOR_IDS))
			return;

		String dataValueBefore = setCommand.getPreviousDataValue();
		String dataValueAfter = setCommand.getDataValue();
		IdList factorIdsBefore = new IdList(dataValueBefore);
		IdList factorIdsAfter = new IdList(dataValueAfter);

		IdList factorIdsToAdd = getAddedIds(factorIdsBefore, factorIdsAfter); 
		addDiagramFactors(factorIdsToAdd);
		
		IdList factorIdsToRemove = getRemovedIds(factorIdsBefore, factorIdsAfter);
		removeDiagramFactors(factorIdsToRemove);
	}
	
	private void updateLinks(CommandSetObjectData setCommand) throws Exception
	{
		if (! setCommand.getFieldTag().equals(DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS))
			return;
		
		String dataValueBefore = setCommand.getPreviousDataValue();
		String dataValueAfter = setCommand.getDataValue();
		
		IdList factorLinkIdsBefore = new IdList(dataValueBefore);
		IdList factorLinkIdsAfter = new IdList(dataValueAfter);

		IdList addedLinkIds = getAddedIds(factorLinkIdsBefore, factorLinkIdsAfter);
		addDiagamLinks(addedLinkIds);
		
		IdList removedLinkIds = getRemovedIds(factorLinkIdsBefore, factorLinkIdsAfter);
		removeDiagramLinks(removedLinkIds);
	}

	private void removeDiagramLinks(IdList removedFactorLinkIds) throws Exception
	{
		for (int i = 0; i < removedFactorLinkIds.size(); i++)
		{
			DiagramFactorLink diagramFactorLink = (DiagramFactorLink) project.findObject(new ORef(ObjectType.DIAGRAM_LINK, removedFactorLinkIds.get(i)));
			model.deleteDiagramFactorLink(diagramFactorLink);
		}
	}

	private void addDiagamLinks(IdList addedLinkIds) throws Exception
	{
		for (int i = 0; i < addedLinkIds.size(); i++)
		{
			DiagramFactorLink diagramFactorLink = (DiagramFactorLink) project.findObject(new ORef(ObjectType.DIAGRAM_LINK, addedLinkIds.get(i)));
			model.addLinkToDiagram(diagramFactorLink);
		}
	}

	private void removeDiagramFactors(IdList factorIdsToRemove) throws Exception
	{
		for (int i = 0; i < factorIdsToRemove.size(); i++)
		{
			model.removeDiagramFactor(new DiagramFactorId(factorIdsToRemove.get(i).asInt()));
		}
	}
	
	private void addDiagramFactors(IdList addedFactorIds) throws Exception
	{
		for (int i = 0; i < addedFactorIds.size(); i++)
		{
			DiagramFactor diagramFactor = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, addedFactorIds.get(i)));
			model.addDiagramFactor(diagramFactor);
		}
	}

	private IdList getAddedIds(IdList factorIdsBefore, IdList factorIdsAfter)
	{
		IdList addedIds = new IdList(factorIdsAfter);
		addedIds.subtract(factorIdsBefore);
		return addedIds;
	}
	
	private IdList getRemovedIds(IdList factorIdsBefore, IdList factorIdsAfter)
	{
		IdList removedIds = new IdList(factorIdsBefore);
		removedIds.subtract(factorIdsAfter);
		return removedIds;
	}
		
	private Project project;
	private DiagramModel model;
	private DiagramObject diagramObject;
}
