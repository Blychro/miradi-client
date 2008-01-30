/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.summary;

import org.conservationmeasures.eam.dialogfields.ObjectDataInputField;
import org.conservationmeasures.eam.dialogs.base.ObjectDataInputPanel;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objects.ProjectMetadata;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.questions.ProtectedAreaStatusQuestion;

public class SummaryScopePanel extends ObjectDataInputPanel
{
	public SummaryScopePanel(Project projectToUse, ORef orefToUse)
	{
		super(projectToUse, orefToUse);
		
		addField(createStringField(ProjectMetadata.TAG_SHORT_PROJECT_SCOPE));
		addField(createMultilineField(ProjectMetadata.TAG_PROJECT_SCOPE));
		
		ObjectDataInputField projectAreaField = createNumericField(ProjectMetadata.TAG_PROJECT_AREA);
		ObjectDataInputField projectAreaNotesField = createStringField(ProjectMetadata.TAG_PROJECT_AREA_NOTES);
		addFieldsOnOneLine(EAM.text("Label|Project Area(ha)"), new ObjectDataInputField[]{projectAreaField, projectAreaNotesField});


		ObjectDataInputField protectedAreaStatusField = createChoiceField(ProjectMetadata.getObjectType(), ProjectMetadata.TAG_PROTECTED_AREA_STATUS, new ProtectedAreaStatusQuestion());
		ObjectDataInputField protectedAreaStatusNotesField = createStringField(ProjectMetadata.TAG_PROJECT_AREA_NOTES);
		addFieldsOnOneLine(EAM.text("Label|Protected Area Status"), new ObjectDataInputField[]{protectedAreaStatusField, protectedAreaStatusNotesField});
		
		addField(createStringField(ProjectMetadata.TAG_RED_LIST_SPECIES));
		addField(createStringField(ProjectMetadata.TAG_OTHER_NOTABLE_SPECIES));
		
		ObjectDataInputField humanPopulationField = createNumericField(ProjectMetadata.TAG_HUMAN_POPULATION);
		ObjectDataInputField humanPopulationNotesField = createMultilineField(ProjectMetadata.TAG_HUMAN_POPULATION_NOTES);
		addFieldsOnOneLine(EAM.text("Label|Human Population"), new ObjectDataInputField[]{humanPopulationField, humanPopulationNotesField});
		
		addField(createMultilineField(ProjectMetadata.TAG_SOCIAL_CONTEXT));
		
		addField(createMultilineField(ProjectMetadata.TAG_PROJECT_VISION));
		addField(createMultilineField(ProjectMetadata.TAG_SCOPE_COMMENTS));
		
		updateFieldsFromProject();
	}

	public String getPanelDescription()
	{
		return EAM.text("Scope");
	}
}
