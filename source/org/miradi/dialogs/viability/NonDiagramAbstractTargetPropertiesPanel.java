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

package org.miradi.dialogs.viability;

import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.dialogs.base.ObjectDataInputPanelWithSections;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.HumanWelfareTarget;
import org.miradi.objects.Target;
import org.miradi.project.Project;
import org.miradi.questions.CustomPlanningColumnsQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.questions.StatusQuestion;
import org.miradi.schemas.HumanWelfareTargetSchema;
import org.miradi.schemas.TargetSchema;

import java.util.Vector;

public class NonDiagramAbstractTargetPropertiesPanel extends ObjectDataInputPanelWithSections
{
	public NonDiagramAbstractTargetPropertiesPanel(Project projectToUse, int targetTypeToUse) throws Exception
	{
		super(projectToUse, targetTypeToUse);
		
		targetType = targetTypeToUse;
		
		TargetCoreSubPanel targetCoreSubPanel = new TargetCoreSubPanel(getProject(), targetType);
		addSubPanelWithTitledBorder(targetCoreSubPanel);
		
		simpleModeStatusSubPanel = new SimpleModeStatusSubPanel(getProject(), ORef.createInvalidWithType(targetType));
		viabilityModeStatusSubPanel = new ViabilityModeStatusSubPanel(getProject(), ORef.createInvalidWithType(targetType));

		Vector<String> sectionTags = new Vector<String>();
		sectionTags.add(CustomPlanningColumnsQuestion.META_CURRENT_RATING);
		sectionTags.add(ViabilityTreeModel.VIRTUAL_TAG_FUTURE_STATUS);
		createSingleSection(EAM.text("Status"), sectionTags);
		addSubPanelField(simpleModeStatusSubPanel);
		addSubPanelField(viabilityModeStatusSubPanel);
		
		ModelessTargetSubPanel modelessTargetSubPanel = new ModelessTargetSubPanel(getProject(), targetType);
		addSubPanelWithTitledBorder(modelessTargetSubPanel);
		
		updateFieldsFromProject();
	}
	
	@Override
	public void setObjectRefs(ORef[] orefsToUse)
	{
		super.setObjectRefs(orefsToUse);
		
		ORef abstractTargetRef = extractTargetRef(new ORefList(orefsToUse));
		if (abstractTargetRef.isValid())
		{
			AbstractTarget abstractTarget = AbstractTarget.findTarget(getProject(), abstractTargetRef);
			simpleModeStatusSubPanel.setVisible(!abstractTarget.isViabilityModeKEA());
			viabilityModeStatusSubPanel.setVisible(abstractTarget.isViabilityModeKEA());
		}
	}

	private ORef extractTargetRef(ORefList refs)
	{
		ORef targetRef = refs.getRefForType(TargetSchema.getObjectType());
		if (targetRef.isValid())
			return targetRef;
		
		return refs.getRefForType(HumanWelfareTargetSchema.getObjectType());
	}
	
	@Override
	public String getPanelDescription()
	{
		if (HumanWelfareTarget.is(targetType))
			return EAM.text("Title|Human Wellbeing Target Properties");

		return EAM.text("Title|Target Properties");
	}

	private class SimpleModeStatusSubPanel extends ObjectDataInputPanel
	{
		public SimpleModeStatusSubPanel(Project projectToUse, ORef refToUse) throws Exception
		{
			super(projectToUse, refToUse);
			
			addField(createRatingChoiceField(targetType, Target.TAG_TARGET_STATUS, StaticQuestionManager.getQuestion(StatusQuestion.class)));
			addField(createMultilineField(targetType, AbstractTarget.TAG_CURRENT_STATUS_JUSTIFICATION));
			addField(createRatingChoiceField(targetType, Target.TAG_TARGET_FUTURE_STATUS, StaticQuestionManager.getQuestion(StatusQuestion.class)));
			addField(createMultilineField(targetType, AbstractTarget.TAG_FUTURE_STATUS_JUSTIFICATION));
		}

		@Override
		public String getPanelDescription()
		{
			return "SimpleModeStatusSubPanel";
		}
	}

	private class ViabilityModeStatusSubPanel extends ObjectDataInputPanel
	{
		public ViabilityModeStatusSubPanel(Project projectToUse, ORef refToUse) throws Exception
		{
			super(projectToUse, refToUse);
			
			addField(createReadOnlyChoiceField(targetType, AbstractTarget.PSEUDO_TAG_TARGET_VIABILITY, StaticQuestionManager.getQuestion(StatusQuestion.class)));
			addField(createMultilineField(targetType, AbstractTarget.TAG_CURRENT_STATUS_JUSTIFICATION));
			addField(createReadOnlyChoiceField(targetType, AbstractTarget.PSEUDO_TAG_TARGET_FUTURE_VIABILITY, StaticQuestionManager.getQuestion(StatusQuestion.class)));
			addField(createMultilineField(targetType, AbstractTarget.TAG_FUTURE_STATUS_JUSTIFICATION));
		}

		@Override
		public String getPanelDescription()
		{
			return "ViabilityModeStatusSubPanel";
		}
	}
	
	private int targetType;	
	private SimpleModeStatusSubPanel simpleModeStatusSubPanel;
	private ViabilityModeStatusSubPanel viabilityModeStatusSubPanel;
}
