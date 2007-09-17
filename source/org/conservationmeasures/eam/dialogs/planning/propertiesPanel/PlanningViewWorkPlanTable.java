/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.planning.propertiesPanel;

import javax.swing.JTable;

import org.conservationmeasures.eam.main.AppPreferences;
import org.conservationmeasures.eam.project.Project;

public class PlanningViewWorkPlanTable extends PlanningViewTableWithSizedColumns
{
	public PlanningViewWorkPlanTable(Project projectToUse, PlanningViewAbstractBudgetTableModel modelToUse) throws Exception
	{
		super(modelToUse);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setBackground(AppPreferences.WORKPLAN_TABLE_BACKGROUND);
		getTableHeader().setBackground(getBackground());
	}
}
