/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.diagram.wizard;

import org.conservationmeasures.eam.views.umbrella.WizardPanel;
import org.conservationmeasures.eam.views.umbrella.WizardStep;

public class DiagramWizardPanel extends WizardPanel
{
	public DiagramWizardPanel() throws Exception
	{
		steps = new WizardStep[STEP_COUNT];

		steps[OVERVIEW] = new DiagramWizardOverviewStep(this);
		steps[PROJECT_SCOPE] = new DiagramWizardProjectScopeStep(this);
		steps[VISION] = new DiagramWizardVisionStep(this);
		steps[CONSERVATION_TARGET] = new DiagramWizardOverviewStep(this);
		steps[REVIEW_AND_MODIFY_TARGETS] = new DiagramWizardOverviewStep(this);
		
		setStep(OVERVIEW);
	}

	public void next() throws Exception
	{
		int nextStep = currentStep + 1;
		if(nextStep >= steps.length)
			nextStep = 0;
		
		setStep(nextStep);
	}
	
	public void previous() throws Exception
	{
		int nextStep = currentStep - 1;
		if(nextStep < 0)
			return;
		
		setStep(nextStep);
	}
	
	public void setStep(int newStep) throws Exception
	{
		currentStep = newStep;
		steps[currentStep].refresh();
		setContents(steps[currentStep]);
	}
	
	public void refresh() throws Exception
	{
		for(int i = 0; i < steps.length; ++i)
			steps[i].refresh();
		validate();
	}
	

	final static int OVERVIEW = 0;
	final static int PROJECT_SCOPE = 1;
	final static int VISION = 2;
	final static int CONSERVATION_TARGET = 3;
	final static int REVIEW_AND_MODIFY_TARGETS = 4;
	
	final static int STEP_COUNT = 5;
	
	WizardStep[] steps;
	int currentStep;
}
