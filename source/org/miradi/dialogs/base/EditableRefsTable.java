/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

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

import javax.swing.JTable;

import org.miradi.main.MainWindow;

abstract public class EditableRefsTable extends EditableObjectTable
{
	public EditableRefsTable(MainWindow mainWindowToUse, EditableObjectTableModel modelToUse)
	{
		super(mainWindowToUse, modelToUse, modelToUse.getUniqueTableModelIdentifier());
		
		rebuildColumnEditorsAndRenderers();
		listenForColumnWidthChanges(this);
		//TODO shouldn't set row height to constant value
		setRowHeight(26);
	}
	
	private void listenForColumnWidthChanges(JTable table)
	{
		table.getColumnModel().addColumnModelListener(new ColumnMarginResizeListenerValidator(this));
	}
	
	abstract public void rebuildColumnEditorsAndRenderers();
}