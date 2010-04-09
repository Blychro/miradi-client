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

import org.miradi.dialogs.fieldComponents.PanelComboBox;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.MonthQuestion;
import org.miradi.questions.StaticQuestionManager;

public class MonthComboBox extends PanelComboBox
{
	public MonthComboBox(DateUnit dateUnit)
	{
		super(createChoices());
		
		setSelectedMonth(dateUnit);
	}
	
	public int getMonth()
	{
		ChoiceItem selectedItem = (ChoiceItem) getSelectedItem();
		return Integer.parseInt(selectedItem.getCode());
	}

	private static ChoiceItem[] createChoices()
	{
		return getMonthQuestion().getChoices();
	}

	private static ChoiceQuestion getMonthQuestion()
	{
		return StaticQuestionManager.getQuestion(MonthQuestion.class);
	}
	
	private void setSelectedMonth(DateUnit dateUnit)
	{
		if (dateUnit != null && dateUnit.isMonth())
		{
			int monthToSelect = dateUnit.getMonth();
			ChoiceItem choiceItemToSelect = getMonthQuestion().findChoiceByCode(monthToSelect);
			setSelectedItem(choiceItemToSelect);
		}
	}
}