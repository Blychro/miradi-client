/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 

package org.conservationmeasures.eam.views.diagram;

import javax.swing.JPopupMenu;

import org.conservationmeasures.eam.actions.ActionDeleteResultsChain;
import org.conservationmeasures.eam.actions.ActionRenameResultsChain;
import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.utils.MenuItemWithoutLocation;
import org.martus.swing.UiPopupMenu;

public class DiagramTabMouseMenuHandler
{
	public DiagramTabMouseMenuHandler(DiagramView viewToUse)
	{
		view = viewToUse;
	}

	public JPopupMenu getPopupMenu()
	{
		UiPopupMenu menu = new UiPopupMenu();
		Actions actions = view.getMainWindow().getActions();
		menu.add(actions.get(ActionDeleteResultsChain.class));
		menu.add(new MenuItemWithoutLocation(actions.getMainWindowAction(ActionRenameResultsChain.class)));
		return menu;
	}

	DiagramView view;
}
