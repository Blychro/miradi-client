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

package org.miradi.questions;

import org.miradi.main.EAM;

public class SortDirectionQuestion extends StaticChoiceQuestion
{
	public SortDirectionQuestion()
	{
		super(getStaticChoices());
	}

	static ChoiceItem[] getStaticChoices()
	{
		return new ChoiceItem[] {
			new ChoiceItem(DEFAULT_SORT_ORDER_CODE, EAM.text("Ascending")),	
			new ChoiceItem(REVERSED_SORT_ORDER_CODE, EAM.text("Descending")),	
		};
	}
	
	public static final String getReversedSortDirectionCode(String sortCode)
	{
		if (sortCode.equals(DEFAULT_SORT_ORDER_CODE))
			return REVERSED_SORT_ORDER_CODE;
		
		return DEFAULT_SORT_ORDER_CODE;
	}

	public static final String DEFAULT_SORT_ORDER_CODE = "";
	public static final String REVERSED_SORT_ORDER_CODE = "ReversedSortOrderCode";
}
