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
package org.miradi.dialogs.base;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.miradi.diagram.renderers.ChoiceItemComboBoxTableCellRenderer;
import org.miradi.dialogs.fieldComponents.ChoiceItemComboBox;
import org.miradi.dialogs.tablerenderers.BasicTableCellEditorOrRendererFactory;
import org.miradi.dialogs.tablerenderers.ChoiceItemComboBoxRendererOrEditorFactory;
import org.miradi.dialogs.tablerenderers.ChoiceItemTableCellRendererFactory;
import org.miradi.dialogs.tablerenderers.DateTableCellEditorOrRendererFactory;
import org.miradi.dialogs.tablerenderers.DefaultFontProvider;
import org.miradi.dialogs.tablerenderers.ExpandingTableCellEditorOrRendererFactory;
import org.miradi.dialogs.tablerenderers.FloatingPointRestrictedTableCellRendererEditorFactory;
import org.miradi.dialogs.tablerenderers.NonNegativeIntegerRestrictedTableCellRendererEditorFactory;
import org.miradi.dialogs.tablerenderers.StressBasedThreatRatingQuestionPopupCellEditorOrRendererFactory;
import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.project.ObjectManager;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceItemBaseObjectWrapper;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.utils.SortableRowTable;
import org.miradi.views.umbrella.ObjectPicker;

abstract public class EditableBaseObjectTable extends SortableRowTable  implements ObjectPicker
{
	public EditableBaseObjectTable(MainWindow mainWindowToUse, EditableObjectTableModel modelToUse, String uniqueTableIdentifierToUse)
	{
		super(mainWindowToUse, modelToUse, uniqueTableIdentifierToUse);
		
		model = modelToUse;
		selectionListeners = new Vector<ListSelectionListener>();
		
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
	
	@Override
	public void dispose()
	{
		if(factoriesToDispose != null)
		{
			for(BasicTableCellEditorOrRendererFactory factory : factoriesToDispose)
			{
				factory.dispose();
			}
			factoriesToDispose.clear();
		}

		super.dispose();
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		Dimension size = new Dimension(getPreferredSize());
		size.height = Math.max(size.height, MINIMUM_VIEWPORT_HEIGHT);
		size.height = Math.min(size.height, getMaximumTableHeightBeforeScroll());
		return size;
	}

	protected int getMaximumTableHeightBeforeScroll()
	{
		return MAXIMUM_VIEWPORT_HEIGHT;
	}
	
	protected ObjectManager getObjectManager()
	{
		return getProject().getObjectManager();
	}
		
	public TreeTableNode[] getSelectedTreeNodes()
	{
		return null;
	}
		
	protected int getColumnWidth(int column)
	{
		return getColumnHeaderWidth(column);
	}
	
	protected void createFloatingPointRestrictedColumn(int tableColumn)
	{
		DefaultFontProvider fontProvider = new DefaultFontProvider(getMainWindow());
		FloatingPointRestrictedTableCellRendererEditorFactory rendererFactory = new FloatingPointRestrictedTableCellRendererEditorFactory(model, fontProvider);
		FloatingPointRestrictedTableCellRendererEditorFactory editorFactory = new FloatingPointRestrictedTableCellRendererEditorFactory(model, fontProvider);
		setDisposableRendererAndEditorFactories(tableColumn, rendererFactory, editorFactory);
	}
	
	protected void createNonNegativeIntegerRestrictedColumn(int tableColumn)
	{
		DefaultFontProvider fontProvider = new DefaultFontProvider(getMainWindow());
		NonNegativeIntegerRestrictedTableCellRendererEditorFactory rendererFactory = new NonNegativeIntegerRestrictedTableCellRendererEditorFactory(model, fontProvider);
		NonNegativeIntegerRestrictedTableCellRendererEditorFactory editorFactory = new NonNegativeIntegerRestrictedTableCellRendererEditorFactory(model, fontProvider);
		setDisposableRendererAndEditorFactories(tableColumn, rendererFactory, editorFactory);
	}
	
	protected void createWrappableTextFieldColumn(int tableColumn)
	{
		DefaultFontProvider fontProvider = new DefaultFontProvider(getMainWindow());
		ExpandingTableCellEditorOrRendererFactory rendererFactory = new ExpandingTableCellEditorOrRendererFactory(getMainWindow(), model, fontProvider);
		ExpandingTableCellEditorOrRendererFactory editorFactory = new ExpandingTableCellEditorOrRendererFactory(getMainWindow(), model, fontProvider);
		setDisposableRendererAndEditorFactories(tableColumn, rendererFactory, editorFactory);
	}
	
	protected void createDateColumn(int tableColumn)
	{
		DefaultFontProvider fontProvider = new DefaultFontProvider(getMainWindow());
		DateTableCellEditorOrRendererFactory rendererFactory = new DateTableCellEditorOrRendererFactory(model, fontProvider);
		DateTableCellEditorOrRendererFactory editorFactory = new DateTableCellEditorOrRendererFactory(model, fontProvider);
		setDisposableRendererAndEditorFactories(tableColumn, rendererFactory, editorFactory);
	}
		
	protected ChoiceItemComboBoxRendererOrEditorFactory createComboColumn(BaseObject[] content, int tableColumn, BaseObject invalidObject)
	{
		Arrays.sort(content, new SorterByToString());
		Vector<ChoiceItem> comboContent = addEmptySpaceAtStart(content, invalidObject);
		DefaultFontProvider fontProvider = new DefaultFontProvider(getMainWindow());
		ChoiceItemComboBoxRendererOrEditorFactory renderer = new ChoiceItemComboBoxRendererOrEditorFactory(model, fontProvider, comboContent);
		setPlainRendererAndEditorFactories(tableColumn, renderer, renderer);
		return renderer;
	}
	
	protected void createComboColumn(ChoiceItem[] choices, int tableColumn)
	{
		ChoiceItemComboBox comboBox = new ChoiceItemComboBox(choices);
		ChoiceItemComboBoxTableCellRenderer rendererFactory = new ChoiceItemComboBoxTableCellRenderer(choices);
		DefaultCellEditor editorFactory = new DefaultCellEditor(comboBox);
		setPlainRendererAndEditorFactories(tableColumn, rendererFactory, editorFactory);
	}
	
	protected void createThreatStressRatingPopupColumn(ChoiceQuestion question, int tableColumn) throws Exception
	{
		DefaultFontProvider fontProvider = new DefaultFontProvider(getMainWindow());
		StressBasedThreatRatingQuestionPopupCellEditorOrRendererFactory rendererFactory = new StressBasedThreatRatingQuestionPopupCellEditorOrRendererFactory(getProject(), question, model, fontProvider);
		StressBasedThreatRatingQuestionPopupCellEditorOrRendererFactory editorFactory = new StressBasedThreatRatingQuestionPopupCellEditorOrRendererFactory(getProject(), question, model, fontProvider);
		setDisposableRendererAndEditorFactories(tableColumn, rendererFactory, editorFactory);
	}

	protected void createReadonlyChoiceItemColumn(ChoiceItem[] choices, int tableColumn)
	{
		ChoiceItemTableCellRendererFactory rendererFactory = new ChoiceItemTableCellRendererFactory(model, new DefaultFontProvider(getMainWindow()));
		TableColumn column = getColumnModel().getColumn(tableColumn);
		column.setCellRenderer(rendererFactory);
		setPlainRendererAndEditorFactories(tableColumn, rendererFactory, null);
	}
	
	private Vector<ChoiceItem> addEmptySpaceAtStart(BaseObject[] content, BaseObject invalidObject)
	{
		Vector<ChoiceItem> comboContent = new Vector<ChoiceItem>();
		comboContent.add(new ChoiceItemBaseObjectWrapper(invalidObject));
	
		for (int index = 0; index < content.length; ++index)
		{
			comboContent.add(new ChoiceItemBaseObjectWrapper(content[index]));
		}
		
		return comboContent;
	}
	
	public BaseObject[] getSelectedObjects()
	{
		int selectedRow = getSelectedRow();
		if (selectedRow < 0)
			return new BaseObject[0];
		
		if (selectedRow >=  model.getRowCount())
			return new BaseObject[0];
		
		BaseObject selectedObject = model.getBaseObjectForRowColumn(selectedRow, 0);
		if (selectedObject == null)
			return new BaseObject[0];
	
		return new BaseObject[] {selectedObject};
	}
	
	public ORefList getSelectionHierarchy()
	{
		return null;
	}
		
	public ORefList[] getSelectedHierarchies()
	{
		int[] rows = getSelectedRows();
		ORefList[] selectedHierarchies = new ORefList[rows.length];
		for(int i = 0; i < rows.length; ++i)
		{
			BaseObject objectFromRow = model.getBaseObjectForRowColumn(rows[i], 0);
			ORefList selectedObjectRefs = new ORefList();
			selectedObjectRefs.add(objectFromRow.getRef());
			selectedHierarchies[i] = selectedObjectRefs;
		}
		
		return selectedHierarchies;
	}

	public void ensureOneCopyOfObjectSelectedAndVisible(ORef ref)
	{
		// TODO Auto-generated method stub
		// we should scroll the table as needed to make this 
		// probably-newly-created object visible
	}

	public void addSelectionChangeListener(ListSelectionListener listener)
	{
		selectionListeners.add(listener);
	}

	public void removeSelectionChangeListener(ListSelectionListener listener)
	{
		selectionListeners.remove(listener);
	}
	
	public void expandTo(int typeToExpandTo) throws Exception
	{
	}
	
	public void expandAll() throws Exception
	{
	}
	
	public void collapseAll() throws Exception
	{	
	}
	
	public boolean isActive()
	{
		return isActive;
	}
	
	public void becomeActive()
	{
		isActive = true;
	}

	public void becomeInactive()
	{
		isActive = false;
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		super.valueChanged(e);
		if(selectionListeners == null)	
			return;
		
		for(int i = 0; i < selectionListeners.size(); ++i)
		{
			ListSelectionListener listener = selectionListeners.get(i);
			listener.valueChanged(null);
		}
	}
	
	protected void createComboQuestionColumn(ChoiceQuestion question, int tableColumn)
	{
		createComboColumn(question.getChoices(), tableColumn);
	}
	
	protected void createReadonlyComboQuestionColumn(ChoiceQuestion question, int tableColumn)
	{
		createReadonlyChoiceItemColumn(question.getChoices(), tableColumn);
	}

	private void setDisposableRendererAndEditorFactories(int tableColumn, BasicTableCellEditorOrRendererFactory rendererFactory, BasicTableCellEditorOrRendererFactory editorFactory)
	{
		if(factoriesToDispose == null)
			factoriesToDispose = new HashSet<BasicTableCellEditorOrRendererFactory>();
		
		factoriesToDispose.add(rendererFactory);
		factoriesToDispose.add(editorFactory);
		setPlainRendererAndEditorFactories(tableColumn, rendererFactory, editorFactory);
	}

	private void setPlainRendererAndEditorFactories(int tableColumn, TableCellRenderer rendererFactory, 	TableCellEditor editorFactory)
	{
		TableColumn column = getColumnModel().getColumn(tableColumn);
		column.setCellRenderer(rendererFactory);
		if(editorFactory != null)
			column.setCellEditor(editorFactory);
	}
	
	public class SorterByToString implements Comparator<BaseObject>
	{
		public int compare(BaseObject o1, BaseObject o2)
		{
			return o1.toString().compareToIgnoreCase(o2.toString());
		}	
	}
			
	private Vector<ListSelectionListener> selectionListeners;
	private EditableObjectTableModel model;
	private boolean isActive;
	private HashSet<BasicTableCellEditorOrRendererFactory> factoriesToDispose; 
	private static final int MINIMUM_VIEWPORT_HEIGHT = 100;
	private static final int MAXIMUM_VIEWPORT_HEIGHT = 400;
}

