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

package org.miradi.questions;

import org.miradi.main.EAM;

public class MonthQuestion extends StaticChoiceQuestion
{
	@Override
	protected ChoiceItem[] createChoices()
	{
		return new ChoiceItem[]{
				new ChoiceItem("1", EAM.text("Month|January")),
				new ChoiceItem("2", EAM.text("Month|February")),
				new ChoiceItem("3", EAM.text("Month|March")),
				new ChoiceItem("4", EAM.text("Month|April")),
				new ChoiceItem("5", EAM.text("Month|May")),
				new ChoiceItem("6", EAM.text("Month|June")),
				new ChoiceItem("7", EAM.text("Month|July")),
				new ChoiceItem("8", EAM.text("Month|August")),
				new ChoiceItem("9", EAM.text("Month|September")),
				new ChoiceItem("10", EAM.text("Month|October")),
				new ChoiceItem("11", EAM.text("Month|November")),
				new ChoiceItem("12", EAM.text("Month|December")),
		};
	}
}