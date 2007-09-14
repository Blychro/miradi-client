/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.planning.propertiesPanel;

import java.text.DecimalFormat;

import org.conservationmeasures.eam.objecthelpers.DateRangeEffortList;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objects.Assignment;
import org.conservationmeasures.eam.objects.ProjectResource;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.project.ProjectCalendar;
import org.conservationmeasures.eam.utils.DateRange;
import org.conservationmeasures.eam.utils.DateRangeEffort;

abstract public class PlanningViewAbstractBudgetTableModel extends PlanningViewAbstractAssignmentTabelModel
{
	public PlanningViewAbstractBudgetTableModel(Project projectToUse) throws Exception
	{
		super(projectToUse);
		
		dateRanges = new ProjectCalendar(getProject()).getQuarterlyDateDanges();
		decimalFormatter = getProject().getDecimalFormatter();
	}

	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return true;
	}

	public String getColumnName(int col)
	{
		return dateRanges[col].toString();
	}

	public int getColumnCount()
	{
		return dateRanges.length;
	}

	public String getUnit(DateRangeEffortList effortList, DateRange dateRange) throws Exception
	{
		double units = effortList.getTotalUnitQuantity(dateRange);
		return decimalFormatter.format(units);
	}

	public ProjectResource getCurrentResource(Assignment assignment)
	{
		ORef resourceRef = assignment.getResourceRef();
		ProjectResource resource = (ProjectResource) getProject().findObject(resourceRef);
		
		return resource;
	}
	
	protected Object getUnits(int row, int column) throws Exception
	{
		DateRangeEffortList effortList = getDateRangeEffortList(row);
		DateRange dateRange = dateRanges[column];
		
		return getUnit(effortList, dateRange);
	}
	
	protected DateRangeEffortList getDateRangeEffortList(int row) throws Exception
	{
		return getAssignment(row).getDateRangeEffortList();
	}
		
	protected DateRangeEffort getDateRangeEffort(Assignment assignment, DateRange dateRange) throws Exception
	{
		DateRangeEffort dateRangeEffort = null;
		DateRangeEffortList effortList = assignment.getDateRangeEffortList();
		dateRangeEffort = effortList.getEffortForDateRange(dateRange);
		return dateRangeEffort;
	}
	
	protected DateRange[] dateRanges;
	protected DecimalFormat decimalFormatter;
}
