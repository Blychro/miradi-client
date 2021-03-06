/* 
Copyright 2005-2018, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 
package org.miradi.project;

import java.text.ParseException;

import org.jgraph.graph.GraphSelectionModel;
import org.miradi.commands.Command;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.DiagramComponent;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.ids.DiagramFactorId;
import org.miradi.ids.IdList;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.*;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.schemas.KeyEcologicalAttributeSchema;
import org.miradi.schemas.StressSchema;
import org.miradi.schemas.SubTargetSchema;
import org.miradi.schemas.TaskSchema;
import org.miradi.utils.CommandVector;
import org.miradi.views.diagram.DeleteAnnotationDoer;
import org.miradi.views.umbrella.DeleteActivityDoer;

public class FactorDeleteHelper
{
	public static FactorDeleteHelper createFactorDeleteHelper(DiagramComponent diagramToUse)
	{
		return new FactorDeleteHelper(diagramToUse);
	}
	
	public static FactorDeleteHelper createFactorDeleteHelperForNonSelectedFactors(DiagramObject diagramObjectToUse)
	{
		return new FactorDeleteHelper(diagramObjectToUse);
	}
		
	private FactorDeleteHelper(DiagramComponent diagramToUse)
	{
		this(diagramToUse.getDiagramObject());
		
		selectionModel = diagramToUse.getSelectionModel();
	}

	private FactorDeleteHelper(DiagramObject diagramObjectToUse)
	{
		diagramObject = diagramObjectToUse;
	}

	public void deleteFactorAndDiagramFactor(DiagramFactor diagramFactorToDelete) throws Exception
	{
		clearSelection();
		
		if (Target.is(diagramFactorToDelete.getWrappedType()))
			deleteAnyRelatedStressBubbles((Target) diagramFactorToDelete.getWrappedFactor());
		
		if (Strategy.is(diagramFactorToDelete.getWrappedType()))
			deleteAnyRelatedActivityBubbles((Strategy) diagramFactorToDelete.getWrappedFactor());
		
		deleteDiagramFactorAndUnderlyingFactor(diagramFactorToDelete);
	}

	private void deleteAnyRelatedActivityBubbles(Strategy strategy) throws Exception
	{
		deleteRelatedFactorDiagramFactors(strategy.getActivityRefs());
	}

	private void deleteAnyRelatedStressBubbles(Target target) throws Exception
	{
		deleteRelatedFactorDiagramFactors(target.getStressRefs());
	}

	public CommandVector buildCommandsToDeleteDiagramFactor(DiagramFactor diagramFactor) throws Exception
	{
		//TODO This looks very odd here. either rename method or do something with this. Also check other callers
		//to clearSelection.
		clearSelection();
		
		return diagramFactor.createCommandsToDeleteChildrenAndObject();
	}

	private void removeNodeFromDiagram(DiagramObject diagramObjectToUse, DiagramFactorId idToDelete) throws CommandFailedException, ParseException
	{
		clearSelection();
		
		CommandSetObjectData removeDiagramFactor = buildCommandToRemoveNodeFromDiagram(diagramObjectToUse, idToDelete);
		getProject().executeCommand(removeDiagramFactor);
	}

	public CommandSetObjectData buildCommandToRemoveNodeFromDiagram(DiagramObject diagramObjectToUse, DiagramFactorId idToDelete) throws ParseException
	{
		clearSelection();
		
		return CommandSetObjectData.createRemoveIdCommand(diagramObjectToUse, DiagramObject.TAG_DIAGRAM_FACTOR_IDS, idToDelete);
	}

	private void clearSelection()
	{
		if(selectionModel == null)
			return;
		
		selectionModel.clearSelection();
	}

	private void deleteRelatedFactorDiagramFactors(ORefList factorRefs) throws Exception
	{
		for (int index = 0; index < factorRefs.size(); ++index)
		{
			Factor factor = (Factor) getProject().findObject(factorRefs.get(index));
			deleteFactorDiagramFactorInCurrentDiagram(factor);
		}
	}

	private void deleteFactorDiagramFactorInCurrentDiagram(Factor factor) throws Exception
	{
		ORefList diagramFactorReferrerRefs = factor.findObjectsThatReferToUs(DiagramFactorSchema.getObjectType());
		ORefList currentContainedDiagramFactors = getDiagramObject().getAllDiagramFactorRefs();
		for (int index = 0; index < diagramFactorReferrerRefs.size(); ++index)
		{
			if (currentContainedDiagramFactors.contains(diagramFactorReferrerRefs.get(index)))
			{
				DiagramFactor diagramFactor = DiagramFactor.find(getProject(), diagramFactorReferrerRefs.get(index));		
				removeFromDiagramAndDelete(diagramFactor);
			}
		}
	}

	private void deleteDiagramFactorAndUnderlyingFactor(DiagramFactor diagramFactorToDelete) throws Exception
	{
		Factor underlyingFactor = diagramFactorToDelete.getWrappedFactor();
		removeFromGroupBox(diagramFactorToDelete);
		removeFromThreatReductionResults(diagramFactorToDelete.getWrappedFactor());
		removeFromView(diagramFactorToDelete.getWrappedORef());
		removeFromDiagramAndDelete(diagramFactorToDelete);
				
		if (underlyingFactor.mustBeDeletedBecauseParentIsGone())
			return;

		deleteAnnotations(underlyingFactor);
		deleteUnderlyingNode(underlyingFactor);
	}
	
	private void removeFromDiagramAndDelete(DiagramFactor diagramFactor) throws Exception
	{
		removeNodeFromDiagram(getDiagramObject(), diagramFactor.getDiagramFactorId());
		getProject().executeCommands(buildCommandsToDeleteDiagramFactor(diagramFactor));
	}

	private void removeFromGroupBox(DiagramFactor diagramFactor) throws Exception
	{
		ORef owningGroupRef = diagramFactor.getOwningGroupBoxRef();
		if (owningGroupRef.isInvalid())
			return;
		
		DiagramFactor owningGroup = DiagramFactor.find(getProject(), owningGroupRef);
		CommandSetObjectData removeDiagramFactorFromGroup = CommandSetObjectData.createRemoveORefCommand(owningGroup, DiagramFactor.TAG_GROUP_BOX_CHILDREN_REFS, diagramFactor.getRef());
		getProject().executeCommand(removeDiagramFactorFromGroup);
	}

	private void removeFromThreatReductionResults(Factor factor) throws CommandFailedException
	{
		if (factor.isDirectThreat())
		{
			Cause threat = (Cause) factor;
			CommandVector commandsToRemoveFromThreatReductionResults  = threat.getCommandsToRemoveFromThreatReductionResults();
			getProject().executeCommands(commandsToRemoveFromThreatReductionResults);
		}
	}

	private void removeFromView(ORef factorRef) throws ParseException, Exception, CommandFailedException
	{
		Command[] commandsToRemoveFromView = getProject().getCurrentViewData().buildCommandsToRemoveNode(factorRef);
		for(int i = 0; i < commandsToRemoveFromView.length; ++i)
			getProject().executeCommand(commandsToRemoveFromView[i]);
	}
	
	private void deleteUnderlyingNode(Factor factorToDelete) throws Exception
	{
		getProject().executeCommands(factorToDelete.createCommandsToDeleteChildrenAndObject());
	}
	
	public void deleteAnnotations(Factor factorToDelete) throws Exception
	{
		if (factorToDelete.canHaveObjectives())
			deleteAnnotationIds(factorToDelete, ObjectType.OBJECTIVE, factorToDelete.TAG_OBJECTIVE_IDS);
		
		if (canReferToIndicators(factorToDelete))
			deleteAnnotationIds(factorToDelete, ObjectType.INDICATOR, factorToDelete.TAG_INDICATOR_IDS);
		
		//TODO: there is much common code between DeleteAnnotationDoer and DeleteActivity classes and this class; 
		// for example DeleteActivity.deleteTaskTree( is general and and good not just for activities
		// I am thinking that each object Task should be able to handle its own deletion so when you call it it would delete all its own 
		// children enforcing referential integrity as a cascade, instead of having the the code here.
		if (factorToDelete.isStrategy())
			removeAndDeleteTasksInList(factorToDelete, Strategy.TAG_ACTIVITY_IDS);
		
		deleteTargetAnnotations(factorToDelete);
	}

	private boolean canReferToIndicators(Factor factorToDelete)
	{
		return factorToDelete.isTarget() || factorToDelete.canDirectlyOwnIndicators();
	}

	private void deleteTargetAnnotations(Factor factorToDelete) throws Exception
	{
		if (AbstractTarget.isAbstractTarget(factorToDelete))
		{
			deleteAnnotationIds(factorToDelete, ObjectType.GOAL, AbstractTarget.TAG_GOAL_IDS);
			deleteAnnotationIds(factorToDelete, KeyEcologicalAttributeSchema.getObjectType(), Target.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS);
			deleteAnnotationRefs(factorToDelete, SubTargetSchema.getObjectType(), Target.TAG_SUB_TARGET_REFS);
		}
		
		if (Target.is(factorToDelete))
		{
			deleteAnnotationRefs(factorToDelete, StressSchema.getObjectType(), Target.TAG_STRESS_REFS);
		}
	}

	private void deleteAnnotationRefs(Factor factorToDelete, int annotationType, String annotationListTag) throws Exception
	{
		ORefList annotationRefs = new ORefList(factorToDelete.getData(annotationListTag));
		deleteAnnotations(factorToDelete, annotationRefs, annotationListTag);
	}
	
	private void deleteAnnotationIds(Factor factorToDelete, int annotationType, String annotationListTag) throws Exception
	{
		IdList ids = new IdList(annotationType, factorToDelete.getData(annotationListTag));
		deleteAnnotations(factorToDelete, new ORefList(annotationType, ids), annotationListTag);
	}
	
	private void deleteAnnotations(Factor factorToDelete, ORefList annotationRefs, String annotationListTag) throws Exception
	{
		for (int i = 0; i < annotationRefs.size(); ++i)
		{
			BaseObject thisAnnotation = getProject().findObject(annotationRefs.get(i));
			Command[] commands = DeleteAnnotationDoer.buildCommandsToDeleteAnnotation(getProject(), factorToDelete, annotationListTag, thisAnnotation);
			getProject().executeCommands(commands);
		}
	}
	
	private void removeAndDeleteTasksInList(BaseObject objectToDelete, String annotationListTag) throws Exception
	{
		ORefList hierarchyWithParent = new ORefList();
		hierarchyWithParent.add(objectToDelete.getRef());
		IdList ids = new IdList(TaskSchema.getObjectType(), objectToDelete.getData(annotationListTag));
		for(int annotationIndex = 0; annotationIndex < ids.size(); ++annotationIndex)
		{
			Task childTask = (Task)getProject().findObject(ObjectType.TASK, ids.get(annotationIndex));
			DeleteActivityDoer.deleteTaskTree(getProject(), hierarchyWithParent, childTask);
		}
	}
	
	private DiagramObject getDiagramObject()
	{
		return diagramObject;
	}
	
	private Project getProject()
	{
		return diagramObject.getProject();
	}
	
	private DiagramObject diagramObject;
	private GraphSelectionModel selectionModel;
}
