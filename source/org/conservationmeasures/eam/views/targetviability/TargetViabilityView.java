/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.targetviability;

import javax.swing.JComponent;

import org.conservationmeasures.eam.actions.ActionCreateKeyEcologicalAttributeIndicator;
import org.conservationmeasures.eam.actions.ActionDeleteKeyEcologicalAttributeIndicator;
import org.conservationmeasures.eam.dialogs.viability.TargetViabilityTreeManagementPanel;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.TabbedView;
import org.conservationmeasures.eam.views.diagram.CreateKeyEcologicalAttributeIndicatorDoer;
import org.conservationmeasures.eam.views.diagram.DeleteKeyEcologicalAttributeIndicatorDoer;
import org.conservationmeasures.eam.wizard.WizardPanel;

public class TargetViabilityView extends TabbedView
{
	public TargetViabilityView(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
		addDoersToMap();
		wizardPanel = new WizardPanel(mainWindowToUse, this);
	}
	
	public String cardName()
	{
		return getViewName();
	}
	
	static public String getViewName()
	{
		return Project.TARGET_VIABILITY_NAME;
	}

	public JComponent createToolBar()
	{
		return new TargetViabilityToolBar(getActions());
	}

	public void createTabs() throws Exception
	{
		viabilityPanel = new TargetViabilityTreeManagementPanel(getProject(), getMainWindow(), getMainWindow().getActions());
		addNonScrollableTab(viabilityPanel);
	}

	public void becomeActive() throws Exception
	{
		super.becomeActive();
		
		viabilityPanel.updateSplitterLocation();
		
		getMainWindow().setStatusBarIfDataExistsOutOfRange();
	}
	
	public void deleteTabs() throws Exception
	{
		viabilityPanel.dispose();
		viabilityPanel = null;
	}

	public WizardPanel createWizardPanel() throws Exception
	{
		return wizardPanel;
	}
	
	public void jump(Class stepMarker) throws Exception
	{
		wizardPanel.jump(stepMarker);
	}

	
	private void addDoersToMap()
	{
		//addDoerToMap(ActionCreateKeyEcologicalAttribute.class, new CreateKeyEcologicalAttributeDoer());
		//addDoerToMap(ActionDeleteKeyEcologicalAttribute.class, new DeleteKeyEcologicalAttributeDoer());
		addDoerToMap(ActionCreateKeyEcologicalAttributeIndicator.class, new CreateKeyEcologicalAttributeIndicatorDoer());
		addDoerToMap(ActionDeleteKeyEcologicalAttributeIndicator.class, new DeleteKeyEcologicalAttributeIndicatorDoer());
	}
	
	
	public BaseObject getSelectedObject()
	{
		if (viabilityPanel != null)
			return viabilityPanel.getObject();
		
		return null;
	}
	
	
	TargetViabilityTreeManagementPanel viabilityPanel;
}