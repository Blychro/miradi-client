/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.viability;

import javax.swing.Icon;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.actions.jump.ActionJumpDiagramWizardDefineTargetsStep;
import org.conservationmeasures.eam.dialogs.ObjectListManagementPanel;
import org.conservationmeasures.eam.icons.KeyEcologicalAttributeIcon;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.SplitterPositionSaverAndGetter;

public class TargetViabilityTreeManagementPanel extends ObjectListManagementPanel
{
	public TargetViabilityTreeManagementPanel(Project projectToUse, SplitterPositionSaverAndGetter splitPositionSaverToUse, FactorId nodeId, Actions actions) throws Exception
	{
		super(splitPositionSaverToUse, TargetViabililtyTreePanel.createTargetViabilityPanel(EAM.mainWindow, projectToUse, nodeId),
				new TargetViabilityTreePropertiesPanel(projectToUse, actions));

	}
	
	public TargetViabilityTreeManagementPanel(Project projectToUse, SplitterPositionSaverAndGetter splitPositionSaverToUse, Actions actions) throws Exception
	{
		super(splitPositionSaverToUse, TargetViabililtyTreePanel.createTargetViabilityPoolPanel(EAM.mainWindow, projectToUse),
				new TargetViabilityTreePropertiesPanel(projectToUse, actions));

	}
	
	public String getSplitterDescription()
	{
		return getPanelDescription() + SPLITTER_TAG;
	}

	public String getPanelDescription()
	{
		return PANEL_DESCRIPTION;
	}
	
	public Icon getIcon()
	{
		return new KeyEcologicalAttributeIcon();
	}
	
	public Class getJumpActionClass()
	{
		return ActionJumpDiagramWizardDefineTargetsStep.class;
	}
	
	private static String PANEL_DESCRIPTION = EAM.text("Tab|Viability"); 
}
