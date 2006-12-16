/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.dialogs;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.Project;

public class ActivityPoolTableModel extends ObjectPoolTableModel
{
	public ActivityPoolTableModel(Project projectToUse)
	{
		super(projectToUse, ObjectType.TASK, COLUMN_TAGS);
	}
	
	private static final String[] COLUMN_TAGS = new String[] {
		Task.TAG_LABEL,
		Task.PSEUDO_TAG_STRATEGY_LABEL,
	};
	
	public IdList getLatestIdListFromProject()
	{
		IdList filteredTasks = new IdList();
		
		IdList tasks = super.getLatestIdListFromProject();
		for (int i=0; i<tasks.size(); ++i)
		{
			BaseId baseId = tasks.get(i);
			Task task = (Task) project.findObject(ObjectType.TASK, baseId);
			if ((task.getParentRef().getObjectType() == ObjectType.FACTOR))
			{
				BaseId objectId = task.getParentRef().getObjectId();
				Factor factor = (Factor)project.findObject(ObjectType.FACTOR, objectId);
				if (factor.isStrategy() && !factor.isStatusDraft())
					filteredTasks.add(baseId);
			}
		}
		return filteredTasks;
	}


}
