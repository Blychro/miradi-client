/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.questions;

import org.conservationmeasures.eam.icons.IndicatorIcon;
import org.martus.swing.ResourceImageIcon;


public class TrendQuestion extends ChoiceQuestion
{
	public TrendQuestion(String tagToUse)
	{
		super(tagToUse, "Trends", getTrends());
	}

	static ChoiceItem[] getTrends()
	{
		return new ChoiceItem[] {
				new ChoiceItem("", "Not Specified", new IndicatorIcon()),
				new ChoiceItem("Unknown", "Unknown", new ResourceImageIcon("images/arrows/va_unknown16.png")),
				new ChoiceItem("StrongIncrease", "Strong Increase", new ResourceImageIcon("images/arrows/va_strongup16.png")),
				new ChoiceItem("MildIncrease", "Mild Increase", new ResourceImageIcon("images/arrows/va_mildup16.png")),
				new ChoiceItem("Flat", "Flat", new ResourceImageIcon("images/arrows/va_flat16.png")),
				new ChoiceItem("MildDecrease", "Mild Decrease", new ResourceImageIcon("images/arrows/va_milddown16.png")),
				new ChoiceItem("StrongDecrease", "Strong Decrease", new ResourceImageIcon("images/arrows/va_strongdown16.png")),
		};
	}
}