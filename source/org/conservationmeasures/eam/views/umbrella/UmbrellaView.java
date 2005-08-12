/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.umbrella;

import java.awt.Point;

import javax.swing.JPanel;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.main.BaseProject;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.views.Doer;
import org.conservationmeasures.eam.views.diagram.InsertGoal;
import org.conservationmeasures.eam.views.diagram.InsertIntervention;
import org.conservationmeasures.eam.views.diagram.InsertThreat;

abstract public class UmbrellaView extends JPanel
{
	public UmbrellaView(MainWindow mainWindowToUse)
	{
		mainWindow = mainWindowToUse;
	}
	
	abstract public String cardName();
	
	public MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	public BaseProject getProject()
	{
		return getMainWindow().getProject();
	}
	
	public Actions getActions()
	{
		return getMainWindow().getActions();
	}
	
	public About getAboutDoer()
	{
		return new About();
	}
	
	public NewProject getNewProjectDoer()
	{
		return new NewProject(getMainWindow());
	}
	
	public OpenProject getOpenProjectDoer()
	{
		return new OpenProject(getMainWindow());
	}
	
	public Exit getExitDoer()
	{
		return new Exit(getMainWindow());
	}
	
	public Doer getInsertGoalDoer(Point invocationPoint)
	{
		return new InsertGoal(getProject(), invocationPoint);
	}
	
	public Doer getInsertThreatDoer(Point invocationPoint)
	{
		return new InsertThreat(getProject(), invocationPoint);
	}
	
	public Doer getInsertInterventionDoer(Point invocationPoint)
	{
		return new InsertIntervention(getProject(), invocationPoint);
	}
	
	private MainWindow mainWindow;
}
