/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.treeViews;

import javax.swing.tree.TreePath;

import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.views.GenericTreeTableModel;
import org.conservationmeasures.eam.views.TreeTableNode;

public class TaskTreeTableModel extends GenericTreeTableModel
{
	public TaskTreeTableModel(Object root)
	{
		super(root);
	}

	public int getColumnCount()
	{
		return 0;
	}

	public String getColumnName(int column)
	{
		return null;
	}

	public TreePath getPathOfParent(int objectType, BaseId objectId)
	{
		TreePath path = getPathOfNode(objectType, objectId);
		if(path == null)
			return null;
		return path.getParentPath();
	}

	public TreePath getPathOfNode(int objectType, BaseId objectId)
	{
		return findObject(getPathToRoot(), objectType, objectId);
	}

	public TreePath getPathToRoot()
	{
		return new TreePath(getRootWorkPlanObject());
	}

	TreeTableNode getRootWorkPlanObject()
	{
		return (TreeTableNode)getRoot();
	}

	public void objectiveWasModified()
	{
		rebuildEntreTree();
	}

	static boolean isTreeStructureChangingCommand(CommandSetObjectData cmd)
	{
		int type = cmd.getObjectType();
		String tag = cmd.getFieldTag();
		if(type == ObjectType.FACTOR && tag.equals(Strategy.TAG_ACTIVITY_IDS))
			return true;
		if(type == ObjectType.INDICATOR && tag.equals(Indicator.TAG_TASK_IDS))
			return true;
		if(type == ObjectType.TASK && tag.equals(Task.TAG_SUBTASK_IDS))
			return true;
		if(type == ObjectType.TASK && tag.equals(Task.TAG_LABEL))
			return true;
		
		return false;
	}

}
