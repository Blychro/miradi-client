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
import org.conservationmeasures.eam.views.umbrella.UmbrellaView;

public class ActionAbout extends MainWindowAction
{
	public ActionAbout(MainWindow mainWindow)
	{
		super(mainWindow, getLabel());
	}

	private static String getLabel()
	{
		return EAM.text("Action|About...");
	}

	public void doAction(UmbrellaView view, ActionEvent event) throws CommandFailedException
	{
		view.getAboutDoer().doIt();
	}

	public boolean shouldBeEnabled(UmbrellaView view)
	{
		return view.getAboutDoer().isAvailable();
	}
	
}
