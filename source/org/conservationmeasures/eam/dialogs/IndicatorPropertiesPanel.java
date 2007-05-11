/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IndicatorId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.questions.IndicatorStatusRatingQuestion;
import org.conservationmeasures.eam.questions.PriorityRatingQuestion;
import org.conservationmeasures.eam.questions.StatusConfidenceQuestion;

public class IndicatorPropertiesPanel extends ObjectDataInputPanel
{
	public IndicatorPropertiesPanel(Project projectToUse, Actions actions) throws Exception
	{
		this(projectToUse, actions, new IndicatorId(BaseId.INVALID.asInt()));
	}
	
	public IndicatorPropertiesPanel(Project projectToUse, Actions actions, Indicator indicator) throws Exception
	{
		this(projectToUse, actions, (IndicatorId)indicator.getId());
	}
	
	public IndicatorPropertiesPanel(Project projectToUse, Actions actions, IndicatorId idToShow) throws Exception
	{
		super(projectToUse, ObjectType.INDICATOR, idToShow);

		addField(createStringField(Indicator.TAG_SHORT_LABEL,10));
		addField(createStringField(Indicator.TAG_LABEL));
		addField(createReadonlyTextField(Indicator.PSEUDO_TAG_FACTOR));
		addField(createRatingChoiceField(new PriorityRatingQuestion(Indicator.TAG_PRIORITY)));
		addField(createRatingChoiceField(new IndicatorStatusRatingQuestion(Indicator.TAG_STATUS)));
		
		addField(createReadonlyTextField(Indicator.PSEUDO_TAG_METHODS));
		addField(createReadonlyTextField(Indicator.PSEUDO_TAG_STRATEGIES));
		addField(createReadonlyTextField(Indicator.PSEUDO_TAG_DIRECT_THREATS));
		addField(createReadonlyTextField(Indicator.PSEUDO_TAG_TARGETS));
		
		addField(createDateChooserField(Indicator.TAG_MEASUREMENT_DATE));
		addField(createStringField(Indicator.TAG_MEASUREMENT_SUMMARY));
		addField(createMultilineField(Indicator.TAG_MEASUREMENT_DETAIL));
		addField(createMultilineField(Indicator.TAG_MEASUREMENT_TREND));
		addField(createChoiceField(Indicator.getObjectType(), new StatusConfidenceQuestion(Indicator.TAG_MEASUREMENT_STATUS_CONFIDENCE)));
				
		updateFieldsFromProject();
	}

	public String getPanelDescription()
	{
		return EAM.text("Title|Indicator Properties");
	}

}
