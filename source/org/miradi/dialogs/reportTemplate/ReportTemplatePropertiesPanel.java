/* 
Copyright 2005-2018, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 
package org.miradi.dialogs.reportTemplate;

import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.icons.ReportTemplateIcon;
import org.miradi.main.EAM;
import org.miradi.objects.ReportTemplate;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.ReportTemplateContentQuestion;
import org.miradi.schemas.ReportTemplateSchema;

public class ReportTemplatePropertiesPanel extends ObjectDataInputPanel
{
	public ReportTemplatePropertiesPanel(Project projectToUse) throws Exception
	{
		super(projectToUse, ReportTemplateSchema.getObjectType());
			
		ObjectDataInputField shortLabelField = createStringField(ReportTemplate.TAG_SHORT_LABEL, 10);
		ObjectDataInputField labelField = createExpandableField(ReportTemplate.TAG_LABEL);
		addFieldsOnOneLine(EAM.text("Report Template"), new ReportTemplateIcon(), new ObjectDataInputField[]{shortLabelField, labelField,});
		
		ChoiceQuestion reportContentsQuestion = new ReportTemplateContentQuestion(getProject());
		addField(createSingleColumnCodeListField(ReportTemplateSchema.getObjectType(), ReportTemplate.TAG_INCLUDE_SECTION_CODES, reportContentsQuestion));
		addField(createMultilineField(ReportTemplate.TAG_COMMENTS));
		updateFieldsFromProject();
	}
	
	@Override
	public String getPanelDescription()
	{
		return EAM.text("Title|Report Template Properties");
	}
}
