/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.summary.wizard;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.views.umbrella.WizardPanel;
import org.conservationmeasures.eam.views.umbrella.WizardStep;

public class SummaryWizardDefineProjectLeader extends WizardStep
{
	public SummaryWizardDefineProjectLeader(WizardPanel panelToUse) 
	{
		super(panelToUse);
	}

	
	public void linkClicked(String linkDescription)
	{
		if(linkDescription.equals("Definition:InitialProjectTeam"))
		{
			EAM.okDialog("Definition: Initial Project Team", new String[] {
					"The people who conceive of and initiate the project."});
		}
	}
	
	public String getResourceFileName()
	{
		return HTML_FILENAME;
	}
	
	String HTML_FILENAME = "SummaryDefineProjectLeaderStep.html";
}


