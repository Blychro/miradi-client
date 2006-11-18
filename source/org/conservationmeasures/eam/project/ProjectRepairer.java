package org.conservationmeasures.eam.project;

import java.awt.Point;
import java.util.Vector;

import org.conservationmeasures.eam.diagram.nodes.DiagramNode;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.DiagramNodeId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.ids.IndicatorId;
import org.conservationmeasures.eam.ids.ModelNodeId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.ConceptualModelNode;
import org.conservationmeasures.eam.objects.EAMObject;
import org.conservationmeasures.eam.objects.ProjectMetadata;

public class ProjectRepairer
{
	public static void repairAnyProblems(Project project) throws Exception
	{
		ProjectRepairer repairer = new ProjectRepairer(project);
		repairer.repair();
	}
	
	public ProjectRepairer(Project projectToRepair)
	{
		project = projectToRepair;
	}
	
	void repair() throws Exception
	{
		fixNodeAnnotationIds();
		fixDeletedTeamMembers();
		repairUnsnappedNodes();
	}
	
	void fixNodeAnnotationIds() throws Exception
	{
		ModelNodeId[] nodeIds = project.getNodePool().getModelNodeIds();
		for(int i = 0; i < nodeIds.length; ++i)
		{
			ModelNodeId nodeId = nodeIds[i];
			ConceptualModelNode node = project.findNode(nodeId);
			fixGhostIndicatorIds(node);
			removeInvalidGoalIds(node);
			removeInvalidObjectiveIds(node);
			removeMissingObjectiveIds(node);
		}
	}
	
	private void fixGhostIndicatorIds(ConceptualModelNode node)
	{
		IdList newIndicatorIds = new IdList();
		IdList oldIndicatorIds = node.getIndicators();
		for(int j = 0; j < oldIndicatorIds.size(); ++j)
		{
			IndicatorId indicatorId = new IndicatorId(oldIndicatorIds.get(j).asInt());
			if(indicatorId.isInvalid())
				continue;
			EAMObject indicator = project.findObject(ObjectType.INDICATOR, indicatorId);
			if(indicator == null)
				EAM.logWarning("Fixing node " + node.getId() + " ghost indicatorId " + indicatorId);
			else
				newIndicatorIds.add(indicatorId);
		}
		if(newIndicatorIds.equals(oldIndicatorIds))
			return;
		
		try
		{
			node.setIndicators(newIndicatorIds);
			project.writeNode(node.getModelNodeId());
		}
		catch (Exception logAndContinue)
		{
			EAM.logError("Repair failed");
			EAM.logException(logAndContinue);
		}

	}
	
	private void removeInvalidGoalIds(ConceptualModelNode node)
	{
		IdList ids = node.getGoals();
		if(!ids.contains(BaseId.INVALID))
			return;
		
		EAM.logWarning("Removing invalid goal id for " + node.getId());
		ids.removeId(BaseId.INVALID);
		node.setGoals(ids);
		try
		{
			project.writeNode(node.getModelNodeId());
		}
		catch(Exception logAndContinue)
		{
			EAM.logError("Repair failed");
			EAM.logException(logAndContinue);
		}
	}
	
	private void removeInvalidObjectiveIds(ConceptualModelNode node)
	{
		IdList ids = node.getObjectives();
		if(!ids.contains(BaseId.INVALID))
			return;
		
		EAM.logWarning("Removing invalid objective id for " + node.getId());
		ids.removeId(BaseId.INVALID);
		node.setObjectives(ids);
		try
		{
			project.writeNode(node.getModelNodeId());
		}
		catch(Exception logAndContinue)
		{
			EAM.logError("Repair failed");
			EAM.logException(logAndContinue);
		}
	}
	
	private void removeMissingObjectiveIds(ConceptualModelNode node)
	{
		IdList newIds = new IdList();
		IdList oldIds = node.getObjectives();
		for(int i = 0; i < oldIds.size(); ++i)
		{
			BaseId id = oldIds.get(i);
			if(project.findObject(ObjectType.OBJECTIVE, id) == null)
				EAM.logWarning("Removing missing objective id " + id + " for " + node.getId());
			else
				newIds.add(id);
		}

		if(newIds.size() == oldIds.size())
			return;
		
		node.setObjectives(newIds);
		try
		{
			project.writeNode(node.getModelNodeId());
		}
		catch(Exception logAndContinue)
		{
			EAM.logError("Repair failed");
			EAM.logException(logAndContinue);
		}
	}
	
	void fixDeletedTeamMembers() throws Exception
	{
		ProjectMetadata metadata = project.getMetadata();
		if(metadata == null)
			return;
		
		IdList teamMemberIds = metadata.getTeamResourceIdList();
		for(int i = 0; i < teamMemberIds.size(); ++i)
		{
			BaseId teamMemberId = teamMemberIds.get(i);
			EAMObject resource = project.findObject(ObjectType.PROJECT_RESOURCE, teamMemberId);
			if(resource == null)
			{
				EAM.logWarning("Removing deleted team member " + teamMemberId);
				teamMemberIds.removeId(teamMemberId);
				try
				{
					project.setObjectData(metadata.getType(), metadata.getId(), metadata.TAG_TEAM_RESOURCE_IDS, teamMemberIds.toString()); 
				}
				catch(Exception e)
				{
					EAM.logError("Repair failed");
					EAM.logException(e);
				}
			}
		}
	}
	

	private void repairUnsnappedNodes()
	{
		Vector diagramNodes = project.getDiagramModel().getAllNodes();
		for (int i=0; i<diagramNodes.size(); ++i) 
		{
			DiagramNode diagramNode = (DiagramNode) diagramNodes.get(i);	
			Point currentLocation = diagramNode.getLocation();
			fixLocation(diagramNode, currentLocation );
		}
	}

	private void fixLocation(DiagramNode digramNode, Point currentLocation)
	{
		Point expectedLocation  = project.getSnapped(currentLocation);
		int x = expectedLocation.x - currentLocation.x;
		int y = expectedLocation.y - currentLocation.y;

		if(x != 0 || y != 0)
			try
			{
				project.moveNodes(x, y, new DiagramNodeId[] { digramNode.getDiagramNodeId() });
			}
			catch(Exception e)
			{
				EAM.logException(e);
			}
	}	
	
	Project project;
}
