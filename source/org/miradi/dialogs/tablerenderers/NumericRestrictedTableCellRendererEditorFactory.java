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

package org.miradi.dialogs.tablerenderers;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.miradi.dialogs.fieldComponents.PanelTextField;
import org.miradi.utils.NumericRestrictedDocument;

public class NumericRestrictedTableCellRendererEditorFactory extends NumericTableCellRendererFactory
{
	public NumericRestrictedTableCellRendererEditorFactory(RowColumnBaseObjectProvider providerToUse, FontForObjectTypeProvider fontProviderToUse)
	{
		super(providerToUse, fontProviderToUse);
		
		numericRestrictedTextField = new NumbericRestrictedTextField();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		numericRestrictedTextField.setText(value.toString());
		Font font = getCellFont(row, column);
		numericRestrictedTextField.setFont(font);
		
		return numericRestrictedTextField;
	}
	
	@Override
	public Object getCellEditorValue()
	{
		return numericRestrictedTextField.getText();
	}
	
	class NumbericRestrictedTextField extends PanelTextField
	{
		public NumbericRestrictedTextField()
		{
			setDocument(new NumericRestrictedDocument());
			setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		}
	}
	
	private NumbericRestrictedTextField numericRestrictedTextField;
}