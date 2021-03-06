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

package org.miradi.dialogs.task;

import org.miradi.actions.ActionManageFactorTags;
import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.activity.ActivityFactorVisibilityControlPanel;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.dialogs.diagram.FactorSummaryCorePanel;
import org.miradi.main.MainWindow;
import org.miradi.objects.DiagramFactor;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.utils.ObjectsActionButton;

public class ActivityPropertiesPanelWithTagPanel extends ActivityPropertiesPanel
{
	public ActivityPropertiesPanelWithTagPanel(MainWindow mainWindow) throws Exception
	{
		super(mainWindow);
		
		createSingleSection(FactorSummaryCorePanel.getTagsLabel());

		ObjectsActionButton chooseTagForFactorButton = createObjectsActionButton(getMainWindow().getActions().getObjectsAction(ActionManageFactorTags.class), getPicker());
		ObjectDataInputField readOnlyTaggedObjects = createReadOnlyObjectList(DiagramFactorSchema.getObjectType(), DiagramFactor.TAG_TAGGED_OBJECT_SET_REFS);
		addFieldWithEditButton(FactorSummaryCorePanel.getTagsLabel(), readOnlyTaggedObjects, chooseTagForFactorButton);
	}

	@Override
	protected ObjectDataInputPanel createDetailsPanel(MainWindow mainWindow, ActivityFactorVisibilityControlPanel activityVisibilityButtonPanel) throws Exception
	{
		return new ActivityDetailsPanelWithFormatFields(getProject(), mainWindow, activityVisibilityButtonPanel);
	}
}
