/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionViewMonitoring extends MainWindowAction
{
	public ActionViewMonitoring(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel());
	}

	private static String getLabel()
	{
		return EAM.text("Action|Monitoring");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Switch to the Monitoring View");
	}
	
	public String toString()
	{
		return getLabel();
	}
}
