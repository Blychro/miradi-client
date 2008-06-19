/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.utils;

import javax.swing.JPopupMenu;

import org.martus.swing.HyperlinkHandler;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.resources.ResourcesHandler;

public class DefaultHyperlinkHandler implements HyperlinkHandler
{
	public DefaultHyperlinkHandler(MainWindow mainWindowToUse)
	{
		mainWindow = mainWindowToUse;
	}
	
	public MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	public void linkClicked(String linkDescription)
	{
		if (getMainWindow().mainLinkFunction(linkDescription))
			return;
	}

	public void buttonPressed(String buttonName)
	{
		try
		{
			String title = EAM.text("Title|Information");
			HtmlViewPanelWithMargins.createFromHtmlFileName(getMainWindow(), title, ResourcesHandler.class, buttonName).showAsOkDialog();
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Internal error loading contents for " + buttonName);
		}
	}

	public JPopupMenu getRightClickMenu(String url)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void valueChanged(String widget, String newValue)
	{
	}
	
	private MainWindow mainWindow;
}
