/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogfields;

import java.awt.Color;


public class ChoiceQuestion
{
	public ChoiceQuestion(String tagToUse, String labelToUse, ChoiceItem[] choicesToUse)
	{
		tag = tagToUse;
		label = labelToUse;
		choices = choicesToUse;
	}
	
	public String getTag()
	{
		return tag;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public ChoiceItem[] getChoices()
	{
		return choices;
	}
	
	public ChoiceItem findChoiceByCode(String code)
	{
		for(int i = 0; i < choices.length; ++i)
			if(choices[i].getCode().equals(code))
				return choices[i];
		
		return null;
	}
	
	public static final Color DARK_YELLOW = new Color(255, 230, 0);
	public static final Color LIGHT_GREEN = new Color(128, 255, 0); 
	public static final Color DARK_GREEN = new Color(0, 160, 0);
		
	public static final Color COLOR_1_OF_4 = Color.RED;
	public static final Color COLOR_2_OF_4 = DARK_YELLOW;
	public static final Color COLOR_3_OF_4 = LIGHT_GREEN;
	public static final Color COLOR_4_OF_4 = DARK_GREEN;
	
	String tag;
	String label;
	ChoiceItem[] choices;
}
