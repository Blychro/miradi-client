/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.threatmatrix.wizard;

import org.conservationmeasures.eam.utils.HtmlViewPanel;

public class ThreatRatingWizardCheckTotalsStep extends ThreatRatingWizardStep
{
	public ThreatRatingWizardCheckTotalsStep(ThreatRatingWizardPanel wizardToUse)
	{
		super(wizardToUse);
	}
	

	public void linkClicked(String linkDescription)
	{
		if(linkDescription.equals(SHOW_RULES))
		{
			HtmlViewPanel htmlViewPanel = new HtmlViewPanel("Bundle Rules", this.getClass(), getResourceBundleRulesFileName());
			htmlViewPanel.showOkDialog();
		}
		else 
			super.linkClicked(linkDescription);
	}

	
	public String getResourceBundleRulesFileName()
	{
		return HTML_BUNDLE_RULES_FILENAME;
	}
	
	public String getResourceFileName()
	{
		return HTML_FILENAME;
	}

	String HTML_FILENAME = "ThreatRatingCheckTotals.html";
	String HTML_BUNDLE_RULES_FILENAME = "ThreatRatingBundleRules.html";
	static final String SHOW_RULES = "ShowRules";
}

