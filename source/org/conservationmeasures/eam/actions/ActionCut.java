/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.actions;

import java.awt.event.ActionEvent;

import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionCut extends MainWindowAction
{
	public ActionCut(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel(), "icons/cut.gif");
	}

	private static String getLabel()
	{
		return EAM.text("Action|Cut");
	}


	public void doAction(ActionEvent event) throws CommandFailedException
	{
		// TODO Auto-generated method stub

	}

}
