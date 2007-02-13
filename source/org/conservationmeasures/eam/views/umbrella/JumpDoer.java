/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.umbrella;

import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.views.MainWindowDoer;
import org.conservationmeasures.eam.wizard.WizardManager;

public class JumpDoer extends MainWindowDoer
{
	public JumpDoer(Class actionClassToUse)
	{
		actionClass = actionClassToUse;
	}
	
	public boolean isAvailable()
	{
		if (!getProject().isOpen()) 
			return false;
		
		WizardManager wizardManager = getMainWindow().getWizardManager();
		return wizardManager.isValidStep(actionClass);
	}

	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;
		
		getMainWindow().jump(actionClass);
	}

	Class actionClass;
}
