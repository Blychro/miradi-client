/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.threatmatrix.wizard;


public class ThreatRatingWizardIrreversibilityStep extends ThreatRatingWizardSetValue
{
	public ThreatRatingWizardIrreversibilityStep(ThreatRatingWizardPanel wizardToUse) throws Exception
	{
		super(wizardToUse, "Irreversibility");
	}
	
	public ThreatRatingWizardIrreversibilityStep(ThreatRatingWizardPanel wizardToUse, String critertion) throws Exception
	{
		super(wizardToUse, critertion);
	}
}
