package org.conservationmeasures.eam.views.umbrella;

import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.views.ViewDoer;
import org.conservationmeasures.eam.wizard.SkeletonWizardStep;
import org.conservationmeasures.eam.wizard.WizardManager;
import org.conservationmeasures.eam.wizard.WizardPanel;

abstract public class WizardNavigationDoer extends ViewDoer
{
	abstract String getControlName();
	
	public boolean isAvailable()
	{
		if(getWizardPanel() == null)
			return false;
		
		Class destination = getPotentialDestination();
		return (destination != null);
	}

	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;
		
		try
		{
			SkeletonWizardStep currentStep = getWizardManager().findStep(getWizardPanel().currentStepName);
			currentStep.buttonPressed(getControlName());
			getMainWindow().updateActionsAndStatusBar();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new CommandFailedException("Error going to wizard step");
		}
	}

	private Class getPotentialDestination()
	{
		SkeletonWizardStep currentStep = getWizardManager().findStep(getWizardPanel().currentStepName);
		Class destination = getWizardManager().findControlTargetStep(getControlName(), currentStep);
		return destination;
	}

	private WizardPanel getWizardPanel()
	{
		return getView().getWizardPanel();
	}

	private WizardManager getWizardManager()
	{
		return getMainWindow().getWizardManager();
	}

}
