/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionViewThreatMatrix extends MainWindowAction
{
	public ActionViewThreatMatrix(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel());
	}

	private static String getLabel()
	{
		return EAM.text("Action|Threat Rating View");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Switch to the Threat Rating View");
	}
	
	public String toString()
	{
		return getLabel();
	}


}
