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

import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ProgressReport;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.EmptyChoiceItem;
import org.miradi.questions.TaglessChoiceItem;

public class ProgressReportTableModel extends EditableObjectTableModel
{
	public ProgressReportTableModel(Project projectToUse)
	{
		super(projectToUse);
		
		clearProgressReportRefs();
	}

	private void clearProgressReportRefs()
	{
		progressRefs = new ORefList();
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return true;
	}

	@Override
	public String getUniqueTableModelIdentifier()
	{
		return "ProgressReportTableModel";
	}

	@Override
	public void setObjectRefs(ORef[] hierarchyToSelectedRef)
	{
		clearProgressReportRefs();
		if (hierarchyToSelectedRef.length > 0)
		{
			//TODO progressReport - isnt there a better way
			ORef ref = hierarchyToSelectedRef[0];
			if (ref.isValid())
			{
				BaseObject baseObject = BaseObject.find(getProject(), ref);
				progressRefs = baseObject.getProgressReportRefs();
			}
		}
	}

	public String getColumnTag(int column)
	{
		return columnTags[column];
	}

	public BaseObject getBaseObjectForRowColumn(int row, int column)
	{
		return ProgressReport.find(getProject(), progressRefs.get(row));
	}

	public int getRowCount()
	{
		return progressRefs.size();
	}

	public int getColumnCount()
	{
		return columnTags.length;
	}
	
	@Override
	public String getColumnName(int column)
	{
		return EAM.fieldLabel(ProgressReport.getObjectType(), getColumnTag(column));
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		ProgressReport progressReport = getProgressReportForRow(rowIndex, columnIndex);
		if (isDateColumn(columnIndex))
			return new TaglessChoiceItem(progressReport.getDateAsString());
		
		if (isProgressStatusColumn(columnIndex))
			return progressReport.getProgressStatusChoice();
		
		if (isDetailsColumn(columnIndex))
			return new TaglessChoiceItem(progressReport.getDetails()); 
			
		return new EmptyChoiceItem();
	}

	@Override
	public void setValueAt(Object value, int row, int column)
	{
		if (value == null)
			return;
		
		ORef ref = getBaseObjectForRowColumn(row, column).getRef();
		if (isDateColumn(column))
			setProgressReportValue(ref, column, value.toString());
		
		if (isProgressStatusColumn(column))
			setProgressReportValue(ref, column, ((ChoiceItem)value).getCode());
		
		if (isDetailsColumn(column))
			setProgressReportValue(ref, column, value.toString());
	}

	private void setProgressReportValue(ORef ref, int column, String value)
	{
		setValueUsingCommand(ref, getColumnTag(column), value);
	}

	public boolean isDateColumn(int columnIndex)
	{
		return getColumnTag(columnIndex).equals(ProgressReport.TAG_PROGRESS_DATE);
	}

	private boolean isDetailsColumn(int columnIndex)
	{
		return getColumnTag(columnIndex).equals(ProgressReport.TAG_DETAILS);
	}

	public boolean isProgressStatusColumn(int columnIndex)
	{
		return getColumnTag(columnIndex).equals(ProgressReport.TAG_PROGRESS_STATUS);
	}
	
	private ProgressReport getProgressReportForRow(int rowIndex, int columnIndex)
	{
		return (ProgressReport) getBaseObjectForRowColumn(rowIndex, columnIndex);
	}
	
	private ORefList progressRefs;
	private static final String[] columnTags = new String[]{
		ProgressReport.TAG_PROGRESS_DATE, 
		ProgressReport.TAG_PROGRESS_STATUS, 
		ProgressReport.TAG_DETAILS, 
		};
}