/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.questions;

import java.awt.Color;

public class KeyEcologicalAttributeTypeQuestion extends ChoiceQuestion
{
	public KeyEcologicalAttributeTypeQuestion(String tag)
	{
		super(tag, "Key Ecological Attribute Types", getKEATypeChoices());
	}
	
	static ChoiceItem[] getKEATypeChoices()
	{
		return new ChoiceItem[] {
			new ChoiceItem("", "Not Specified", Color.WHITE),
			new ChoiceItem("1", "Size", COLOR_1_OF_4),
			new ChoiceItem("2", "Condition", COLOR_2_OF_4),
			new ChoiceItem("3", "LandScape", COLOR_3_OF_4),
		};
	}

}
