/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.wizard.noproject.projectlist;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.miradi.main.EAM;
import org.miradi.main.MainWindow;

abstract class ProjectListAction extends AbstractAction
{
	public ProjectListAction(ProjectListTreeTable tableToUse, String buttonLabel, Icon buttonIcon)
	{
		super(buttonLabel, buttonIcon);
		table = tableToUse;
		
		updateEnabledState();
	}
	
	public ProjectListAction(ProjectListTreeTable tableToUse, String buttonLabel)
	{
		this(tableToUse, buttonLabel, null);	
	}
	
	public void actionPerformed(ActionEvent event)
	{
		try
		{
			doWork();
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog(getErrorMessage() + e.getMessage());
		}
		refresh();
	}
	
	protected File getSelectedFile()
	{
		return table.getSelectedFile();
	}
	
	protected void updateEnabledState()
	{
		try
		{
			boolean newState = isAvailable();
			setEnabled(newState);
		}
		catch(Exception e)
		{
			EAM.logException(e);
			setEnabled(false);
		}
	}

	private boolean isAvailable() throws Exception
	{
		return isProjectDirectory();
	}

	protected boolean isProjectDirectory() throws Exception
	{
		return ProjectListTreeTable.isProjectDirectory(getSelectedFile());
	}
	
	protected boolean isDirectorySelected()
	{
		File fileToValidate = getSelectedFile();
		if (fileToValidate == null)
			return false;
		
		return fileToValidate.isDirectory();
	}
	
	protected void refresh()
	{
		table.refresh();
	}
	
	protected MainWindow getMainWindow()
	{
		return table.getMainWindow();
	}
	
	abstract protected void doWork() throws Exception;
	
	abstract protected String getErrorMessage();
	
	private ProjectListTreeTable table;
}
