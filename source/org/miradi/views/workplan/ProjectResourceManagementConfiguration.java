/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.views.workplan;

import javax.swing.Icon;

import org.miradi.actions.ActionCreateResource;
import org.miradi.actions.ActionDeleteResource;
import org.miradi.dialogs.planning.ProjectResourceRowColumnProvider;
import org.miradi.dialogs.planning.CategoryTreeRowColumnProvider;
import org.miradi.icons.ProjectResourceIcon;
import org.miradi.main.EAM;
import org.miradi.project.Project;

public class ProjectResourceManagementConfiguration extends	AbstractManagementConfiguration
{
	public ProjectResourceManagementConfiguration(Project projectToUse)
	{
		super(projectToUse);
	}

	@Override
	public Class[] getButtonActions()
	{
		return new Class[] {
				ActionCreateResource.class, 
				ActionDeleteResource.class,
		};
	}

	@Override
	public Icon getIcon()
	{
		return new ProjectResourceIcon();
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Project Resources");	
	}

	@Override
	public CategoryTreeRowColumnProvider getRowColumnProvider() throws Exception
	{
		return new ProjectResourceRowColumnProvider();
	}

	@Override
	public String getUniqueTreeTableIdentifier()
	{
		return UNIQUE_TREE_TABLE_IDENTIFIER;
	}

	private static final String UNIQUE_TREE_TABLE_IDENTIFIER = "ProjectResourceTreeTableModel";
}