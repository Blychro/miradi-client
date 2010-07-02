/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogs.planning.upperPanel;

import org.miradi.dialogs.planning.AbstractBudgetDetailsTableModel;
import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.TimePeriodCosts;
import org.miradi.project.Project;
import org.miradi.questions.CustomPlanningColumnsQuestion;
import org.miradi.utils.OptionalDouble;

public class RollupReportsBudgetDetailsTableModel extends AbstractBudgetDetailsTableModel
{
	public RollupReportsBudgetDetailsTableModel(Project projectToUse, RowColumnBaseObjectProvider providerToUse, String treeModelIdentifierAsTagToUse) throws Exception
	{
		super(projectToUse, providerToUse, treeModelIdentifierAsTagToUse);
	}
	
	@Override
	protected OptionalDouble getOptionalDoubleAt(int row, int column)
	{
		return calculateRollupValue(row, column);
	}
	
	@Override
	protected void retainDataRelatedToAnyOf(TimePeriodCosts timePeriodCosts, ORefSet objectHierarchy)
	{
		timePeriodCosts.retainWorkUnitDataRelatedToAllOf(objectHierarchy);
		timePeriodCosts.retainExpenseDataRelatedToAllOf(objectHierarchy);
	}
	
	@Override
	public String getColumnGroupCode(int modelColumn)
	{
		return CustomPlanningColumnsQuestion.META_ROLLUP_REPORTS_BUDGET_DETAILS_COLUMN_CODE;
	}
	
	@Override
	public String getUniqueTableModelIdentifier()
	{
		return getTreeModelIdentifierAsTag() + "." + UNIQUE_TABLE_MODEL_IDENTIFIER;
	}
	
	private static final String UNIQUE_TABLE_MODEL_IDENTIFIER = "RollupReportsBudgetDetailsTableModel";
}
