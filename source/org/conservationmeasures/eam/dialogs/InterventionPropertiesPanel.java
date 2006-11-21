/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.dialogs;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.ConceptualModelIntervention;
import org.conservationmeasures.eam.project.Project;

public class InterventionPropertiesPanel extends ObjectDataInputPanel
{
	public InterventionPropertiesPanel(Project projectToUse, int objectTypeToUse, BaseId idToShow)
	{
		super(projectToUse, objectTypeToUse, idToShow);
		addField(createStringField(ConceptualModelIntervention.TAG_LABEL));
		updateFieldsFromProject();
	}

	public String getPanelDescription()
	{
		return EAM.text("Title|Strategy Properties");
	}

}
