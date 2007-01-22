/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.questions;

import org.conservationmeasures.eam.dialogfields.ChoiceItem;
import org.conservationmeasures.eam.dialogfields.ChoiceQuestion;

public class ResourceRoleQuestion extends ChoiceQuestion
{
	public ResourceRoleQuestion(String tagToUse)
	{
		super(tagToUse, "Role", getRoleChoices());
	}

	static ChoiceItem[] getRoleChoices()
	{
		return new ChoiceItem[] {
				new ChoiceItem("Contact", "Team Contact"),
				new ChoiceItem(TeamMemberRoleCode, "Team Member"),
				new ChoiceItem("Leader", "Leader/Manager"),
				new ChoiceItem("Facilitator", "Process Facilitator"),
				new ChoiceItem("Advisor", "Project Advisor"),
				new ChoiceItem("Stakeholder", "Stakeholder"),
		};
	}
	public static final String TeamMemberRoleCode = "TeamMember";
}
