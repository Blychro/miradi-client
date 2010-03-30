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

package org.miradi.actions;

import org.miradi.icons.ActivityIcon;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;

abstract public class AbstractActionCreateActivity extends ObjectsAction
{
	public AbstractActionCreateActivity(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel(), new ActivityIcon());
	}

	private static String getLabel()
	{
		return EAM.text("Action|Manage|Create Activity");
	}
	

	@Override
	public String getToolTipText()
	{
		return EAM.text("TT|Create an Activity for the selected Strategy");
	}
}