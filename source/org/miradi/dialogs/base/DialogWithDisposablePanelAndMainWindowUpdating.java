/* 
Copyright 2005-2018, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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

package org.miradi.dialogs.base;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;

import org.miradi.main.MainWindow;

public class DialogWithDisposablePanelAndMainWindowUpdating extends	DialogWithDisposablePanel
{
	public DialogWithDisposablePanelAndMainWindowUpdating(JDialog owner, MainWindow mainWindowToUse)
	{
		super(owner);
		
		setMainWindow(mainWindowToUse);
		addWindowListener();
	}

	public DialogWithDisposablePanelAndMainWindowUpdating(MainWindow parent)
	{
		super(parent);
		
		setMainWindow(parent);
		addWindowListener();
	}
	
	public DialogWithDisposablePanelAndMainWindowUpdating(MainWindow parent, DisposablePanel panelToUse)
	{
		this(parent);
		
		setMainPanel(panelToUse);
	}
	
	private void setMainWindow(MainWindow mainWindowToUse)
	{
		mainWindow = mainWindowToUse;
	}
	
	private void addWindowListener()
	{
		addWindowListener(new WindowEventHandler(mainWindow));
	}

	public MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	static class WindowEventHandler implements WindowListener
	{
		public WindowEventHandler(MainWindow mainWindowToUse)
		{
			mainWindow = mainWindowToUse;
		}

		public void windowActivated(WindowEvent arg0)
		{
			mainWindow.updateActionStates();
		}
	
		public void windowClosed(WindowEvent arg0)
		{
		}
	
		public void windowClosing(WindowEvent arg0)
		{
		}
	
		public void windowDeactivated(WindowEvent arg0)
		{
		}
	
		public void windowDeiconified(WindowEvent arg0)
		{
		}
	
		public void windowIconified(WindowEvent arg0)
		{
		}
	
		public void windowOpened(WindowEvent arg0)
		{
		}
		
		private MainWindow mainWindow;
	}

	private MainWindow mainWindow;
}
