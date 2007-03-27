/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views;

import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import org.conservationmeasures.eam.icons.ActivityIcon;
import org.conservationmeasures.eam.icons.FactorIcon;
import org.conservationmeasures.eam.icons.GoalIcon;
import org.conservationmeasures.eam.icons.IndicatorIcon;
import org.conservationmeasures.eam.icons.KeyEcologicalAttributeIcon;
import org.conservationmeasures.eam.icons.MethodIcon;
import org.conservationmeasures.eam.icons.ObjectiveIcon;
import org.conservationmeasures.eam.icons.TaskIcon;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objectpools.EAMObjectPool;
import org.conservationmeasures.eam.objects.EAMObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.umbrella.ObjectPicker;
import org.martus.swing.UiLabel;

import com.java.sun.jtreetable.JTreeTable;
import com.java.sun.jtreetable.TreeTableModel;

public class TreeTableWithIcons extends JTreeTable implements ObjectPicker
{

	public TreeTableWithIcons(Project projectToUse, GenericTreeTableModel treeTableModelToUse)
	{
		super(treeTableModelToUse);
		treeTableModel = treeTableModelToUse;
		project = projectToUse;
		selectionListeners = new Vector();

		setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		getTree().setShowsRootHandles(true);
		getTree().setRootVisible(false);
		getTree().setCellRenderer(new Renderer());
		getTree().setEditable(false);
		getColumnModel().getColumn(0).setPreferredWidth(200);
		TableCellEditor ce = new NonEditableTreeTableCellEditor();
		setDefaultEditor(TreeTableModel.class, ce);
		if (getRowCount()>0)
			setRowSelectionInterval(0,0);
	}
	
	public Project getProject()
	{
		return project;
	}

	public GenericTreeTableModel getTreeTableModel()
	{
		return treeTableModel;
	}
	
	public static Font createFristLevelFont(Font defaultFontToUse)
	{
		Map map = defaultFontToUse.getAttributes();
	    map.put(TextAttribute.SIZE, new Float(defaultFontToUse.getSize2D() + 2));
	    map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
	    Font customFont = new Font(map);
		return customFont;
	}

	protected static class Renderer extends DefaultTreeCellRenderer
	{		
		public Renderer()
		{
			factorRenderer = new DefaultTreeCellRenderer();
			
			objectiveRenderer = new DefaultTreeCellRenderer();
			objectiveRenderer.setClosedIcon(new ObjectiveIcon());
			objectiveRenderer.setOpenIcon(new ObjectiveIcon());
			objectiveRenderer.setLeafIcon(new ObjectiveIcon());
			objectiveRenderer.setFont(getBoldFont());

			indicatorRenderer = new DefaultTreeCellRenderer();
			indicatorRenderer.setClosedIcon(new IndicatorIcon());
			indicatorRenderer.setOpenIcon(new IndicatorIcon());
			indicatorRenderer.setLeafIcon(new IndicatorIcon());
			
			goalRenderer = new DefaultTreeCellRenderer();
			goalRenderer.setClosedIcon(new GoalIcon());
			goalRenderer.setOpenIcon(new GoalIcon());
			goalRenderer.setLeafIcon(new GoalIcon());
			goalRenderer.setFont(getBoldFont());
			
			activityRenderer = new DefaultTreeCellRenderer();
			activityRenderer.setClosedIcon(new ActivityIcon());
			activityRenderer.setOpenIcon(new ActivityIcon());
			activityRenderer.setLeafIcon(new ActivityIcon());

			keyEcologicalAttributeRenderer = new DefaultTreeCellRenderer();
			keyEcologicalAttributeRenderer.setClosedIcon(new KeyEcologicalAttributeIcon());
			keyEcologicalAttributeRenderer.setOpenIcon(new KeyEcologicalAttributeIcon());
			keyEcologicalAttributeRenderer.setLeafIcon(new KeyEcologicalAttributeIcon());
			
			methodRenderer = new DefaultTreeCellRenderer();
			methodRenderer.setClosedIcon(new MethodIcon());
			methodRenderer.setOpenIcon(new MethodIcon());
			methodRenderer.setLeafIcon(new MethodIcon());

			taskRenderer = new DefaultTreeCellRenderer();
			taskRenderer.setClosedIcon(new TaskIcon());
			taskRenderer.setOpenIcon(new TaskIcon());
			taskRenderer.setLeafIcon(new TaskIcon());
			taskRenderer.setFont(getItalicFont());

			stringNoIconRenderer = new DefaultTreeCellRenderer();
			stringNoIconRenderer.setClosedIcon(null);
			stringNoIconRenderer.setOpenIcon(null);
			stringNoIconRenderer.setLeafIcon(null);
			Font customFont = createFristLevelFont(getDefaultFont());
			stringNoIconRenderer.setFont(customFont);
			
			defaultRenderer = new DefaultTreeCellRenderer();
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocusToUse)
		{
			TreeCellRenderer renderer = defaultRenderer;
			
			TreeTableNode node = (TreeTableNode) value;
			if (node.getType() == ObjectType.FAKE)
				renderer  = stringNoIconRenderer;
			else if(node.getType() == ObjectType.INDICATOR)
				renderer = indicatorRenderer;
			else if(node.getType() == ObjectType.FACTOR)
				renderer = getStrategyRenderer((Factor)node.getObject());
			else if(node.getType() == ObjectType.OBJECTIVE)
				renderer = objectiveRenderer;
			else if(node.getType() == ObjectType.GOAL)
				renderer = goalRenderer;
			else if(node.getType() == ObjectType.TASK)
				renderer = getTaskRenderer((Task)node.getObject());
			else if(node.getType() == ObjectType.KEY_ECOLOGICAL_ATTRIBUTE)
				renderer = keyEcologicalAttributeRenderer;
			
			return renderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		}
		
		private TreeCellRenderer getTaskRenderer(Task task)
		{
			if(task.isActivity())
				return activityRenderer;
			if(task.isMethod())
				return methodRenderer;
			return taskRenderer;
		}

		protected DefaultTreeCellRenderer getStrategyRenderer(Factor factor)
		{
			DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
			FactorIcon factorIcon = new FactorIcon(factor);

			renderer.setClosedIcon(factorIcon);
			renderer.setOpenIcon(factorIcon);
			renderer.setLeafIcon(factorIcon);
			
			return renderer;
		}
		
		public static Font getDefaultFont()
		{
			Font defaultFont = new UiLabel().getFont();
			return defaultFont;
		}
		
		public static Font getBoldFont()
		{
			Font defaultFont = getDefaultFont();
			return defaultFont.deriveFont(Font.BOLD);
		}

		public static Font getItalicFont()
		{
			Font defaultFont = getDefaultFont();
			return defaultFont.deriveFont(Font.ITALIC);
		}

		DefaultTreeCellRenderer objectiveRenderer;
		DefaultTreeCellRenderer goalRenderer;
		DefaultTreeCellRenderer indicatorRenderer;
		DefaultTreeCellRenderer activityRenderer;
		DefaultTreeCellRenderer methodRenderer;
		DefaultTreeCellRenderer taskRenderer;
		DefaultTreeCellRenderer defaultRenderer;
		DefaultTreeCellRenderer factorRenderer;
		DefaultTreeCellRenderer stringNoIconRenderer;
		DefaultTreeCellRenderer keyEcologicalAttributeRenderer;
	}

	class NonEditableTreeTableCellEditor extends TreeTableCellEditor
	{
		public NonEditableTreeTableCellEditor() 
		{
		    super();
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c)
		{
		    ((JTextField)getComponent()).setEditable(false);
			return super.getTableCellEditorComponent(table, value, isSelected, r, c);
		}
	}
	
	public TreeTableNode[] getSelectedTreeNodes()
	{
		return new TreeTableNode[] {(TreeTableNode)getTree().getLastSelectedPathComponent()};
	}

	 //TODO: This method needs review as it seems a bit complex
	public EAMObject[] getSelectedObjects()
	{
		TreeTableNode selectedNode = (TreeTableNode)getTree().getLastSelectedPathComponent();
		
		if (selectedNode == null)
			return new EAMObject[0];
		
		ORef oRef = selectedNode.getObjectReference();
		EAMObjectPool pool = project.getPool(oRef.getObjectType());
		
		if (pool == null)
			return new EAMObject[0];
		
		EAMObject foundObject = pool.findObject(oRef.getObjectId());
		
		if (foundObject == null)
			return new EAMObject[0];
		
		return new EAMObject[] {foundObject};
	}

	public void ensureObjectVisible(ORef ref)
	{
		// TODO Auto-generated method stub
		// we should scroll the table as needed to make this 
		// probably-newly-created object visible
	}

	public void addSelectionChangeListener(SelectionChangeListener listener)
	{
		selectionListeners.add(listener);
	}

	public void removeSelectionChangeListener(SelectionChangeListener listener)
	{
		selectionListeners.remove(listener);
	}

	public void valueChanged(ListSelectionEvent e)
	{
		super.valueChanged(e);
		
		if(selectionListeners == null)
			return;
		
		for(int i = 0; i < selectionListeners.size(); ++i)
		{
			SelectionChangeListener listener = (SelectionChangeListener)selectionListeners.get(i);
			listener.selectionHasChanged();
		}
	}

	private GenericTreeTableModel treeTableModel;
	Project project;
	Vector selectionListeners;
}
