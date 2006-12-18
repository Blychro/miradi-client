/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.dialogs;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.EAMObject;
import org.conservationmeasures.eam.views.workplan.WorkPlanPanel;

public class WorkPlanManagementPanel extends VerticalSplitPanel
{
	public WorkPlanManagementPanel(WorkPlanPanel treeTablePanelToUse, ObjectDataInputPanel propertiesPanelToUse) throws Exception
	{
		treeTableComponent = treeTablePanelToUse;
		propertiesPanel = propertiesPanelToUse;
    
		treeTableComponent.setPropertiesPanel(propertiesPanel);
		createVerticalSplitPane(treeTableComponent, propertiesPanel);
	}
	
	public void dispose()
	{
		treeTableComponent.dispose();
		treeTableComponent = null;
		
		propertiesPanel.dispose();
		propertiesPanel = null;
		
		super.dispose();
	}

	public EAMObject getObject()
	{
		return treeTableComponent.getSelectedObject().getObject();
	}

	public String getPanelDescription()
	{
		return EAM.text("Work Plan");
	}
	
	public WorkPlanPanel getWorkPlanPanel()
	{
		return treeTableComponent;
	}
	
	WorkPlanPanel treeTableComponent;
	ObjectDataInputPanel propertiesPanel;
}
