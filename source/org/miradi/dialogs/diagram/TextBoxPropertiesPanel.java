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
package org.miradi.dialogs.diagram;

import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.TextBox;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.DiagramFactorBackgroundQuestion;
import org.miradi.questions.DiagramFactorFontColorQuestion;
import org.miradi.questions.DiagramFactorFontSizeQuestion;
import org.miradi.questions.DiagramFactorFontStyleQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.questions.TextBoxZOrderQuestion;
import org.miradi.schemas.DiagramFactorSchema;

public class TextBoxPropertiesPanel extends ObjectDataInputPanel
{
	public TextBoxPropertiesPanel(Project projectToUse, DiagramFactor diagramFactor) throws Exception
	{
		super(projectToUse, diagramFactor.getWrappedORef());

		setObjectRefs(new ORef[] {diagramFactor.getWrappedORef(), diagramFactor.getRef()});

		addField(createExpandableField(TextBox.TAG_LABEL));
		addField(createChoiceField(DiagramFactorSchema.getObjectType(), DiagramFactor.TAG_BACKGROUND_COLOR, new DiagramFactorBackgroundQuestion()));
		addField(createChoiceField(DiagramFactorSchema.getObjectType(), DiagramFactor.TAG_FONT_SIZE, new DiagramFactorFontSizeQuestion()));
		addField(createChoiceField(DiagramFactorSchema.getObjectType(), DiagramFactor.TAG_FOREGROUND_COLOR, new DiagramFactorFontColorQuestion()));
		addField(createChoiceField(DiagramFactorSchema.getObjectType(), DiagramFactor.TAG_FONT_STYLE, new DiagramFactorFontStyleQuestion()));
		
		ChoiceQuestion zPositionQuestion = StaticQuestionManager.getQuestion(TextBoxZOrderQuestion.class);
		addRadioButtonField(DiagramFactorSchema.getObjectType(), DiagramFactor.TAG_TEXT_BOX_Z_ORDER_CODE, zPositionQuestion);

		updateFieldsFromProject();
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Text Box Properties");
	}
}
