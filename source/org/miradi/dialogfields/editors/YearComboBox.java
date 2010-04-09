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

package org.miradi.dialogfields.editors;

import javax.swing.DefaultComboBoxModel;

import org.miradi.dialogs.fieldComponents.PanelComboBox;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.project.ProjectCalendar;
import org.miradi.questions.CalendarYearChoiceQuestion;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.FiscalYearChoiceQuestion;

public class YearComboBox extends PanelComboBox
{
	public YearComboBox(ProjectCalendar projectCalendarToUse, DateUnit dateUnit)
	{
		projectCalendar = projectCalendarToUse;
		ChoiceItem[] choices = createChoices();
		setModel(new DefaultComboBoxModel(choices));
		
		setSelectedYear(dateUnit);
	}
	
	public void setSelectedYear(DateUnit dateUnit)
	{
		if (dateUnit != null && dateUnit.isYear())
		{
			ChoiceQuestion question = createYearQuestion();
			ChoiceItem choiceItem = question.findChoiceByCode(dateUnit.getYearYearString());
			setSelectedItem(choiceItem);
		}
	}
	
	public int getYear()
	{
		ChoiceItem selectedItem = (ChoiceItem) getSelectedItem();
		return Integer.parseInt(selectedItem.getCode());
	}

	private ChoiceItem[] createChoices()
	{
		return createYearQuestion().getChoices();
	}
	
	private ChoiceQuestion createYearQuestion()
	{
		int startYear = getProjectCalendar().getPlanningStartMultiCalendar().getGregorianYear();
		int endYear = getProjectCalendar().getPlanningEndMultiCalendar().getGregorianYear();
		int fiscalYearStartMonth = getProjectCalendar().getFiscalYearFirstMonth();
		if (fiscalYearStartMonth == 1)
			return new CalendarYearChoiceQuestion(startYear, endYear);	
		
		return new FiscalYearChoiceQuestion(startYear, endYear);
	}

	private ProjectCalendar getProjectCalendar()
	{
		return projectCalendar;
	}
	
	private ProjectCalendar projectCalendar;
}