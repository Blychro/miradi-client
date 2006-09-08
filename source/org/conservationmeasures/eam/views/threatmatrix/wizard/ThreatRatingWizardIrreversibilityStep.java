/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.threatmatrix.wizard;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.project.ThreatRatingFramework;

public class ThreatRatingWizardIrreversibilityStep extends ThreatRatingWizardSetValue
{
	public static ThreatRatingWizardIrreversibilityStep create(ThreatRatingWizardPanel wizardToUse) throws Exception
	{
		ThreatRatingFramework framework = wizardToUse.getView().getProject().getThreatRatingFramework();
		BaseId criterionId = framework.findCriterionByLabel("Irreversibility").getId();
		ThreatRatingWizardIrreversibilityStep step = new ThreatRatingWizardIrreversibilityStep(wizardToUse, criterionId);
		return step;
	}
	
	private ThreatRatingWizardIrreversibilityStep(ThreatRatingWizardPanel wizardToUse, BaseId criterionIdToUse) throws Exception
	{
		super(wizardToUse, criterionIdToUse);
	}

	public String getText()
	{
		return new ThreatRatingWizardIrreversibilityText(getValueOptionLabels(), value.getLabel()).getText();
	}
	
	public void linkClicked(String linkDescription)
	{
		if(linkDescription.equals("Definition:Irreversibility"))
			EAM.okDialog("Definition: Irreversibility", new String[] {"Irreversibility is..."});
	}



}
