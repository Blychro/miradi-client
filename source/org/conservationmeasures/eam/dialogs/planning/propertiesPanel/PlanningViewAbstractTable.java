/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.planning.propertiesPanel;

import javax.swing.table.TableModel;

import org.conservationmeasures.eam.utils.TableWithHelperMethods;

public class PlanningViewAbstractTable extends TableWithHelperMethods
{
	public PlanningViewAbstractTable(TableModel  modelToUse)
	{
		super(modelToUse);

		//TODO planning table - find better solution - check the other tables two planning tables too
		setRowHeight(getRowHeight() + 10);
	}
}
