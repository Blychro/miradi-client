/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionAboutCMP extends MainWindowAction
{
	public ActionAboutCMP(MainWindow mainWindow)
	{
		super(mainWindow, getLabel(), "icons/cmp16.png");
	}

	private static String getLabel()
	{
		return EAM.text("Action|About the CMP");
	}

}
