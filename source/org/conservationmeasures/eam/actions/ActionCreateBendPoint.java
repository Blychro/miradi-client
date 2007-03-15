/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionCreateBendPoint extends LocationAction
{
	public ActionCreateBendPoint(MainWindow mainWindowToUse)
	{
		//TODO fix icon
		super(mainWindowToUse, getLabel(), "icons/bendPoint.gif");
	}

	private static String getLabel()
	{
		return EAM.text("Action|Create Bend Point");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Create a Bend Point");
	}

}
