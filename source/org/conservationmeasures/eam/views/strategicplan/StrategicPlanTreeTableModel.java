/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.strategicplan;

import javax.swing.tree.TreePath;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.ConceptualModelIntervention;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.Project;

import com.java.sun.jtreetable.AbstractTreeTableModel;
import com.java.sun.jtreetable.TreeTableModel;

public class StrategicPlanTreeTableModel extends AbstractTreeTableModel
{
	static public StrategicPlanTreeTableModel createForProject(Project project)
	{
		return new StrategicPlanTreeTableModel(project, new StratPlanRoot(project));
	}
	
	static public StrategicPlanTreeTableModel createForStrategy(Project project, ConceptualModelIntervention strategy)
	{
		return new StrategicPlanTreeTableModel(project, new StratPlanStrategy(project, strategy));
	}
	
	
	private StrategicPlanTreeTableModel(Project projectToUse, StratPlanObject root)
	{
		super(root);
		project = projectToUse;
	}
	
	public ConceptualModelIntervention getParentIntervention(Task activity)
	{
		TreePath interventionPath = getPathOfParent(activity.getType(), activity.getId());
		StratPlanStrategy strategy = (StratPlanStrategy)interventionPath.getLastPathComponent();
		return strategy.getIntervention();
	}
	
	public int getColumnCount()
	{
		return columnNames.length;
	}

	public String getColumnName(int column)
	{
		return columnNames[column];
	}

	public Object getValueAt(Object rawNode, int column)
	{
		StratPlanObject node = (StratPlanObject)rawNode;
		return node.getValueAt(column);
	}

	public int getChildCount(Object parent)
	{
		return ((StratPlanObject)parent).getChildCount();
	}

	public Object getChild(Object parent, int index)
	{
		return ((StratPlanObject)parent).getChild(index);
	}

	public Class getColumnClass(int column)
	{
		if(column == 0)
			return TreeTableModel.class;
		return String.class;
	}
	
	public TreePath getPathOfNode(int objectType, BaseId objectId)
	{
		return findObject(getPathToRoot(), objectType, objectId);
	}

	public TreePath getPathToRoot()
	{
		return new TreePath(getRootStratPlanObject());
	}
	
	public TreePath getPathOfParent(int objectType, BaseId objectId)
	{
		TreePath path = getPathOfNode(objectType, objectId);
		if(path == null)
			return null;
		return path.getParentPath();
	}
	
	public void objectiveWasModified()
	{
EAM.logDebug("objectiveWasModified");
		getRootStratPlanObject().rebuild();
		fireTreeStructureChanged(getRoot(), new Object[] {getRoot()}, null, null);
	}
	
	public void dataWasChanged(int objectType, BaseId objectId)
	{
		getRootStratPlanObject().rebuild();
		fireTreeStructureChanged(getRoot(), new Object[] {getRoot()}, null, null);
		
EAM.logDebug("dataWasChanged");
	}

	public void idListWasChanged(int objectType, BaseId objectId, String newIdListAsString)
	{
		getRootStratPlanObject().rebuild();
		fireTreeStructureChanged(getRoot(), new Object[] {getRoot()}, null, null);

EAM.logDebug("idListWasChanged");
	}
	
	TreePath findObject(TreePath pathToStartSearch, int objectType, BaseId objectId)
	{
		StratPlanObject nodeToSearch = (StratPlanObject)pathToStartSearch.getLastPathComponent();
		if(nodeToSearch.getType() == objectType && nodeToSearch.getId().equals(objectId))
			return pathToStartSearch;
		
		for(int i = 0; i < nodeToSearch.getChildCount(); ++i)
		{
			StratPlanObject thisChild = (StratPlanObject)nodeToSearch.getChild(i);
			TreePath childPath = pathToStartSearch.pathByAddingChild(thisChild);
			TreePath found = findObject(childPath, objectType, objectId);
			if(found != null)
				return found;
		}
		
		return null;
	}
	
	StratPlanObject getRootStratPlanObject()
	{
		return (StratPlanObject)getRoot();
	}

	public static final int labelColumn = 0;
	public static final int resourcesColumn = 1;
	public static final int budgetColumn = 2;
	public static final int datesColumn = 3;
	
	static final String[] columnNames = {"Item", "Resources", "Budget", "Dates", };

	Project project;
}

