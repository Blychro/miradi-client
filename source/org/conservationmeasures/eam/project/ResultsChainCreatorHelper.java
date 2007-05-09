/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.project;

import java.util.HashMap;
import java.util.Vector;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.DiagramChainObject;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.diagram.cells.FactorCell;
import org.conservationmeasures.eam.dialogs.DiagramPanel;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.DiagramFactorLinkId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.CreateDiagramFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.CreateDiagramFactorParameter;
import org.conservationmeasures.eam.objecthelpers.CreateFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.CreateObjectParameter;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.DiagramFactor;
import org.conservationmeasures.eam.objects.DiagramFactorLink;
import org.conservationmeasures.eam.objects.DiagramObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.FactorLink;
import org.conservationmeasures.eam.objects.ResultsChainDiagram;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.utils.PointList;

public class ResultsChainCreatorHelper
{
	public ResultsChainCreatorHelper(Project projectToUse, DiagramPanel diagramPanelToUse)
	{
		project = projectToUse;
		diagramPanel = diagramPanelToUse;
		model = diagramPanel.getDiagramModel();
	}
		
	public BaseId createResultsChain() throws Exception
	{
			CommandCreateObject createResultsChain = new CommandCreateObject(ObjectType.RESULTS_CHAIN_DIAGRAM);
			project.executeCommand(createResultsChain);
			
			BaseId newResultsChainId = createResultsChain.getCreatedId();
			ResultsChainDiagram resultsChain = (ResultsChainDiagram) project.findObject(ObjectType.RESULTS_CHAIN_DIAGRAM, newResultsChainId);
			
			DiagramFactor[] diagramFactors = getSelectedAndRelatedDiagramFactors();
			HashMap clonedDiagramFactors = cloneDiagramFactors(diagramFactors);
			DiagramFactorId[] clonedDiagramFactorIds = extractClonedDiagramFactors(clonedDiagramFactors);
			IdList idList = new IdList(clonedDiagramFactorIds);
			CommandSetObjectData addFactorsToChain = CommandSetObjectData.createAppendListCommand(resultsChain, ResultsChainDiagram.TAG_DIAGRAM_FACTOR_IDS, idList);
			project.executeCommand(addFactorsToChain);

			DiagramFactorLink[] diagramLinks = getDiagramLinksInChain();
			DiagramFactorLinkId[] clonedDiagramLinkIds = cloneDiagramLinks(diagramLinks, clonedDiagramFactors);
			IdList diagramLinkList = new IdList(clonedDiagramLinkIds);
			CommandSetObjectData addLinksToChain = CommandSetObjectData.createAppendListCommand(resultsChain, ResultsChainDiagram.TAG_DIAGRAM_FACTOR_LINK_IDS, diagramLinkList);
			project.executeCommand(addLinksToChain);
			
			String label = getFirstStrategyShortLabel(diagramFactors); 
			CommandSetObjectData setLabelCommand = new CommandSetObjectData(ObjectType.RESULTS_CHAIN_DIAGRAM, newResultsChainId, DiagramObject.TAG_LABEL, label);
			project.executeCommand(setLabelCommand);

			return newResultsChainId;
	}
	
	private String getFirstStrategyShortLabel(DiagramFactor[] diagramFactors) throws Exception
	{
		for (int i = 0; i < diagramFactors.length; ++i)
		{
			DiagramFactor diagramFactor = diagramFactors[i];
			if (isNonDraftStrategy(diagramFactor))
			{
				Strategy strategy = (Strategy) project.findObject(diagramFactor.getWrappedORef());
				return getLabel(strategy);
			}
		}
		
		throw new Exception("No strategy found in results chain");
	}
	
	//TODO RC this test should go away once Draft Strategies are excluded from list
	private boolean isNonDraftStrategy(DiagramFactor diagramFactor)
	{
		if (diagramFactor.getWrappedType() != ObjectType.STRATEGY)
			return false;
		
		Strategy strategy = (Strategy) project.findObject(diagramFactor.getWrappedORef());
		if (strategy.isStatusDraft())
			return false;
		
		return true;
	}

	private String getLabel(Strategy strategy)
	{
		String shortLabel = strategy.getShortLabel();
		if (shortLabel.trim().length() > 0)
			return shortLabel;
		
		return EAM.text("Results Chain");
	}

	private DiagramFactorId[] extractClonedDiagramFactors(HashMap clonedDiagramFactors)
	{
		Vector diagramFactorIds = new Vector();
		Vector diagramFactors = new Vector(clonedDiagramFactors.values());
		
		for (int i = 0; i < diagramFactors.size(); i ++)
		{
			DiagramFactor diagramFactor = ((DiagramFactor) diagramFactors.get(i));
			diagramFactorIds.add(diagramFactor.getDiagramFactorId());
		}
		
		return (DiagramFactorId[]) diagramFactorIds.toArray(new DiagramFactorId[0]);
	}

	private HashMap cloneDiagramFactors(DiagramFactor[] diagramFactors) throws Exception
	{
		HashMap originalAndClonedDiagramFactors = new HashMap();
		for (int i  = 0; i < diagramFactors.length; i++)
		{
			DiagramFactor diagramFactorToBeCloned = diagramFactors[i];
			FactorId factorId = createOrReuseWrappedObject(diagramFactorToBeCloned);
			
			CreateDiagramFactorParameter extraDiagramFactorInfo = new CreateDiagramFactorParameter(factorId);
			CommandCreateObject createDiagramFactor = new CommandCreateObject(ObjectType.DIAGRAM_FACTOR, extraDiagramFactorInfo);
			project.executeCommand(createDiagramFactor);
			
			DiagramFactorId newlyCreatedId = (DiagramFactorId) createDiagramFactor.getCreatedId();
			Command[] commandsToClone = diagramFactorToBeCloned.createCommandsToMirror(newlyCreatedId);
			project.executeCommands(commandsToClone);
			
			DiagramFactor clonedDiagramFactor = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, newlyCreatedId));
			originalAndClonedDiagramFactors.put(diagramFactorToBeCloned, clonedDiagramFactor);
		}
		 
		return originalAndClonedDiagramFactors;
	}
	
	private FactorId createOrReuseWrappedObject(DiagramFactor diagramFactor) throws Exception
	{
		if (diagramFactor.getWrappedType() == ObjectType.TARGET)
			return diagramFactor.getWrappedId();
		
		if (diagramFactor.getWrappedType() == ObjectType.STRATEGY)
			return diagramFactor.getWrappedId();
		
		if (diagramFactor.getWrappedType() == ObjectType.CAUSE)
			return createNewFactorAndSetLabel(diagramFactor);
		
		throw new Exception("wrapped type not found "+diagramFactor.getWrappedType());
	}

	private FactorId createNewFactorAndSetLabel(DiagramFactor diagramFactor) throws Exception, CommandFailedException
	{
		Factor factor = (Factor) project.findObject(diagramFactor.getWrappedORef());
		CommandCreateObject createCommand = createNewFactorCommand(factor);
		project.executeCommand(createCommand);
		
		FactorId newlyCreatedId = new FactorId(createCommand.getCreatedId().asInt());
		String clonedLabel = new String("[ " + factor.getLabel() + " ]");
		CommandSetObjectData setLabelCommand = new CommandSetObjectData(ObjectType.FACTOR, newlyCreatedId, Factor.TAG_LABEL, clonedLabel);
		project.executeCommand(setLabelCommand);
		
		return newlyCreatedId;
	}

	private CommandCreateObject createNewFactorCommand(Factor factor) throws Exception
	{
		if (factor.isDirectThreat())
			return new CommandCreateObject(ObjectType.THREAT_REDUCTION_RESULT);
		
		if (factor.isContributingFactor())
			return new CommandCreateObject(ObjectType.INTERMEDIATE_RESULT);
		
		throw new Exception("cannot create object for type " + factor.getType());
	}

	private DiagramFactor[] getSelectedAndRelatedDiagramFactors()
	{
		FactorCell[] selectedFactorCells = diagramPanel.getOnlySelectedFactorCells();
		if (containsOnlyStrategies(selectedFactorCells))
			return getRelatedDiagramFactors(selectedFactorCells);
			
		return extractDiagramFactors(selectedFactorCells);
	}
	
	private DiagramFactor[] extractDiagramFactors(FactorCell[] selectedFactorCells)
	{
		DiagramFactor diagramFactors[] = new DiagramFactor[selectedFactorCells.length];
		for (int i = 0; i < selectedFactorCells.length; ++i)
		{
			diagramFactors[i] = selectedFactorCells[i].getDiagramFactor();
		}
		
		return diagramFactors;
	}

	private DiagramFactor[] getRelatedDiagramFactors(FactorCell[] selectedFactorCells)
	{
		Vector allDiagramFactors = new Vector();
		for (int i = 0; i < selectedFactorCells.length; i++)
		{
			DiagramChainObject chainObject = createDiagramChainObject(selectedFactorCells, i);
			Factor[] factorsArray = chainObject.getFactorsArray();
			
			Vector diagramFactors = convertToDiagramFactors(factorsArray);
			allDiagramFactors.addAll(diagramFactors);
		}
	
		return (DiagramFactor[]) allDiagramFactors.toArray(new DiagramFactor[0]);	
	}

	private boolean containsOnlyStrategies(FactorCell[] selectedFactorCells)
	{
		for (int i = 0; i < selectedFactorCells.length; ++i)
		{
			if (!isNonDraftStrategy(selectedFactorCells[i].getDiagramFactor()))
				return false;
		}
		
		return true;
	}

	private DiagramFactorLink[] getDiagramLinksInChain() throws Exception
	{
		FactorCell[] selectedFactorCells = diagramPanel.getOnlySelectedFactorCells();
		if (containsOnlyStrategies(selectedFactorCells))
			return getLinksInRelatedFactors(selectedFactorCells);

		return getDiagramLinksInSelection();
	}
	
	private DiagramFactorLink[] getDiagramLinksInSelection()
	{
		DiagramFactorLink[] allDiagramLinks = model.getAllDiagramLinksAsArray();
		Vector containedDiagramLinks = new Vector();
		for (int i = 0; i < allDiagramLinks.length; ++i)
		{
			DiagramFactorId fromId = allDiagramLinks[i].getFromDiagramFactorId();
			DiagramFactorId toId = allDiagramLinks[i].getToDiagramFactorId();
			if (containsDiagramFactor(fromId) && containsDiagramFactor(toId))
				containedDiagramLinks.add(allDiagramLinks[i]);
		}
		
		return (DiagramFactorLink[]) containedDiagramLinks.toArray(new DiagramFactorLink[0]);
	}
	
	private boolean containsDiagramFactor(DiagramFactorId diagramFactorId)
	{
		//TODO build set of diagramFactorIds from selectedFactorCells and ask set if it contains the id, instead of
		// looping through all the cells for each link
		FactorCell[] selectedFactorCells = diagramPanel.getOnlySelectedFactorCells();
		for (int i = 0; i < selectedFactorCells.length; ++i)
		{
			DiagramFactorId selectedDiagramFactorId = selectedFactorCells[i].getDiagramFactorId();
			if (selectedDiagramFactorId.equals(diagramFactorId))
				return true;
		}
		
		return false;
	}

	private DiagramFactorLink[] getLinksInRelatedFactors(FactorCell[] selectedFactorCells) throws Exception
	{
		Vector allDiagramLinks = new Vector();
		for (int i = 0; i < selectedFactorCells.length; i++)
		{
			DiagramChainObject chainObject = createDiagramChainObject(selectedFactorCells, i);
			Vector diagramLinks = convertToDiagramLinks(chainObject.getFactorLinksArray());
			allDiagramLinks.addAll(diagramLinks);
		}
		
		return (DiagramFactorLink[]) allDiagramLinks.toArray(new DiagramFactorLink[0]);
	}

	private DiagramChainObject createDiagramChainObject(FactorCell[] selectedFactorCells, int i)
	{
		DiagramChainObject chainObject = new DiagramChainObject();
		Factor factor = selectedFactorCells[i].getUnderlyingObject();
		chainObject.buildNormalChain(model, factor);
		
		return chainObject;
	}

	private Vector convertToDiagramLinks(FactorLink[] links) throws Exception
	{
		 Vector vector = new Vector();
		 for (int i  = 0; i < links.length; i++)
		 {
			 FactorLinkId id = links[i].getFactorLinkId();
			 DiagramFactorLink link = model.getDiagramFactorLinkbyWrappedId(id);
			 if (canAddLinkToResultsChain((link)))
				 vector.add(link);
		 }
		 
		 return vector;
	}
	
	private DiagramFactorLinkId[] cloneDiagramLinks(DiagramFactorLink[] diagramLinks, HashMap diagramFactors) throws Exception
	{
		Vector createdDiagramLinkIds = new Vector();
		
		for (int i = 0; i < diagramLinks.length; i++)
		{
			DiagramFactorLink diagramLink = diagramLinks[i];
			DiagramFactorLinkId newlyCreatedLinkId = cloneDiagramFactorLink(diagramFactors, diagramLink);
			if (canAddLinkToResultsChain(diagramLink))
				createdDiagramLinkIds.add(newlyCreatedLinkId);
		}
		
		return (DiagramFactorLinkId[]) createdDiagramLinkIds.toArray(new DiagramFactorLinkId[0]);
	}
	
	private boolean canAddTypeToResultsChain(DiagramFactor diagramFactor)
	{
		if (diagramFactor.getWrappedType() == ObjectType.TARGET)
			return true;
		
		if (diagramFactor.getWrappedType() == ObjectType.CAUSE)
			return true;
		
		if (isNonDraftStrategy(diagramFactor))
			return true;
		
		return false;
	}
	
	private boolean canAddLinkToResultsChain(DiagramFactorLink link)
	{
		DiagramFactor fromDiagramFactor = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, link.getFromDiagramFactorId()));
		DiagramFactor toDiatramFactor = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, link.getToDiagramFactorId()));
		
		return (canAddTypeToResultsChain(fromDiagramFactor) && canAddTypeToResultsChain(toDiatramFactor));
	}


	private DiagramFactorLinkId cloneDiagramFactorLink(HashMap diagramFactors, DiagramFactorLink diagramLink) throws CommandFailedException
	{
		DiagramFactorId fromDiagramFactorId = diagramLink.getFromDiagramFactorId();
		DiagramFactor fromDiagramFactor = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, fromDiagramFactorId));
		DiagramFactor fromClonedDiagramFactor = (DiagramFactor) diagramFactors.get(fromDiagramFactor);
		 
		DiagramFactorId toDiagramFactorId = diagramLink.getToDiagramFactorId();
		DiagramFactor toDiagramFactor = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, toDiagramFactorId));
		DiagramFactor toClonedDiagramFactor = (DiagramFactor) diagramFactors.get(toDiagramFactor);
		
		CreateObjectParameter extraInfo = createDiagramLinkExtraInfo(diagramLink, fromDiagramFactor, fromClonedDiagramFactor, toDiagramFactor, toClonedDiagramFactor);
		CommandCreateObject createDiagramLink = new CommandCreateObject(ObjectType.DIAGRAM_LINK, extraInfo);
		project.executeCommand(createDiagramLink);

		DiagramFactorLinkId newlyCreatedLinkId = (DiagramFactorLinkId) createDiagramLink.getCreatedId();
		DiagramFactorLink newlyCreated = (DiagramFactorLink) project.findObject(new ORef(ObjectType.DIAGRAM_LINK, newlyCreatedLinkId));
		PointList bendPoints = diagramLink.getBendPoints();
		CommandSetObjectData setBendPoints = CommandSetObjectData.createNewPointList(newlyCreated, DiagramFactorLink.TAG_BEND_POINTS, bendPoints);
		project.executeCommand(setBendPoints);

		return newlyCreatedLinkId;
	}
	
	
	private CreateObjectParameter createDiagramLinkExtraInfo(DiagramFactorLink diagramLink, DiagramFactor from, DiagramFactor fromCloned, DiagramFactor to, DiagramFactor toCloned) throws CommandFailedException
	{
		if (areSharingTheSameFactor(from, fromCloned, to, toCloned))
			return new CreateDiagramFactorLinkParameter(diagramLink.getWrappedId(), fromCloned.getDiagramFactorId(), toCloned.getDiagramFactorId());
	
		CreateFactorLinkParameter extraInfo = new CreateFactorLinkParameter(fromCloned.getWrappedId(), toCloned.getWrappedId());
		CommandCreateObject createFactorLink = new CommandCreateObject(ObjectType.FACTOR_LINK, extraInfo);
		project.executeCommand(createFactorLink);
		
		FactorLinkId factorLinkId = (FactorLinkId) createFactorLink.getCreatedId();
		return new CreateDiagramFactorLinkParameter(factorLinkId, fromCloned.getDiagramFactorId(), toCloned.getDiagramFactorId());
	}

	private boolean areSharingTheSameFactor(DiagramFactor from, DiagramFactor fromCloned, DiagramFactor to, DiagramFactor toCloned)
	{
		return from.getWrappedId().equals(fromCloned.getWrappedId()) && to.getWrappedId().equals(toCloned.getWrappedId());
	}
	
	private Vector convertToDiagramFactors(Factor[] factors)
	{
		Vector vector = new Vector();
		for (int i = 0; i < factors.length; i++)
		{
			FactorId id = factors[i].getFactorId();
			DiagramFactor diagramFactor = model.getDiagramFactor(id);
			if (canAddTypeToResultsChain(diagramFactor))
				vector.add(diagramFactor);
		}
		
		return vector;
	}

	private DiagramModel model;
	private DiagramPanel diagramPanel;
	private Project project;
}
 