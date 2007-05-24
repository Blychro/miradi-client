package org.conservationmeasures.eam.questions;

public class FontFamiliyQuestion extends ChoiceQuestion
{
	public FontFamiliyQuestion(String tag)
	{
		super(tag, "Font Family", getFamilyChoices());
	}
	
	static ChoiceItem[] getFamilyChoices()
	{
		return new ChoiceItem[] {
			new ChoiceItem("", "Sans Serif"),
			new ChoiceItem("Serif", "Serif"),
		};
	}
}
