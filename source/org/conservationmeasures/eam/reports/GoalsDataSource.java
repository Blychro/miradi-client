package org.conservationmeasures.eam.reports;

import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objects.Goal;
import org.conservationmeasures.eam.project.Project;

public class GoalsDataSource extends CommonDataSource
{
	public GoalsDataSource(Project project)
	{
		super(project);
		ORefList list = project.getPool(Goal.getObjectType()).getORefList();
		setObjectList(list);
	}
} 
