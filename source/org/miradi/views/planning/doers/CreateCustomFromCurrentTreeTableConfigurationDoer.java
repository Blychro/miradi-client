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

package org.miradi.views.planning.doers;

import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.dialogs.planning.PlanningTreeManagementPanel;
import org.miradi.dialogs.planning.RowColumnProvider;
import org.miradi.objects.ViewData;
import org.miradi.utils.CodeList;
import org.miradi.views.ObjectsDoer;
import org.miradi.views.TabbedView;
import org.miradi.views.planning.ConfigurablePlanningManagementPanel;
import org.miradi.views.planning.PlanningView;

public class CreateCustomFromCurrentTreeTableConfigurationDoer extends ObjectsDoer
{
	@Override
	public boolean isAvailable()
	{
		if(!super.isAvailable())
			return false;

		return isPlanningView();
	}
	
	@Override
	protected void doIt() throws Exception
	{
		if (!isAvailable())
			return;

		getProject().executeCommand(new CommandBeginTransaction());
		try
		{
			PlanningTreeManagementPanel tab = (PlanningTreeManagementPanel) getView().getCurrentTabPanel();
			RowColumnProvider provider = tab.getPlanningTreeTablePanel().getRowColumnProvider();
			CodeList columnCodes = provider.getColumnCodesToShow();
			CodeList rowCodes = provider.getRowCodesToShow();
			
			switchToCustomTab();
			saveCannedConfigurationAsNewCustomization(rowCodes, columnCodes);
			showCustomizationEditorDialog();
		}
		finally
		{
			getProject().executeCommand(new CommandEndTransaction());
		}
	}

	private void showCustomizationEditorDialog() throws Exception
	{
		PlanningCustomizeDialogPopupDoer.showCustomizeDialog(getMainWindow());
	}

	private void saveCannedConfigurationAsNewCustomization(CodeList rowCodes, CodeList columnCodes) throws Exception
	{
		CreatePlanningViewPrefilledConfigurationDoer.createPlanningViewConfiguration(getProject(), rowCodes, columnCodes);
	}

	private void switchToCustomTab() throws Exception
	{
		TabbedView view = (TabbedView) getView();	
		String customTabName = ConfigurablePlanningManagementPanel.class.getSimpleName();
		int customTabIndex = view.getTabIndex(customTabName);
		getProject().executeCommand(getViewData().createTabChangeCommand(customTabIndex));
	}

	private ViewData getViewData() throws Exception
	{
		return getProject().getViewData(PlanningView.getViewName());
	}
}
