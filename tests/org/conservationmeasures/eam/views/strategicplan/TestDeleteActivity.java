/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.strategicplan;

import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.nodetypes.NodeTypeIntervention;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.objecthelpers.CreateModelNodeParameter;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.ConceptualModelIntervention;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.project.ProjectForTesting;
import org.conservationmeasures.eam.testall.EAMTestCase;
import org.conservationmeasures.eam.views.umbrella.Redo;
import org.conservationmeasures.eam.views.umbrella.Undo;

public class TestDeleteActivity extends EAMTestCase
{
	public TestDeleteActivity(String name)
	{
		super(name);
	}

	public void testDeleteActivity() throws Exception
	{
		Project project = new ProjectForTesting(getName());
		try
		{
			CreateModelNodeParameter parameter = new CreateModelNodeParameter(new NodeTypeIntervention());
			BaseId interventionId = project.createObject(ObjectType.MODEL_NODE, BaseId.INVALID, parameter);
			ConceptualModelIntervention intervention = (ConceptualModelIntervention)project.findNode(interventionId);
			BaseId resourceId = project.createObject(ObjectType.PROJECT_RESOURCE);
	//		ProjectResource resource = (ProjectResource)project.findObject(ObjectType.PROJECT_RESOURCE, resourceId);
			
			InsertActivity.insertActivity(project, intervention, 0);
			BaseId activityId = intervention.getActivityIds().get(0);
			Task activity = (Task)project.findObject(ObjectType.TASK, activityId);
			CommandSetObjectData addResource = CommandSetObjectData.createAppendIdCommand(activity, Task.TAG_RESOURCE_IDS, resourceId);
			project.executeCommand(addResource);
			
			DeleteActivity.deleteActivity(project, intervention, activity);
			Undo.undo(project);
			activity = (Task)project.findObject(ObjectType.TASK, activityId);
			assertEquals("Didn't restore resource?", 1, activity.getResourceCount());
			Undo.undo(project);
			Redo.redo(project);
			Redo.redo(project);
		}
		finally
		{
			project.close();
		}
	}
}
