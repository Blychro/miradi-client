package org.conservationmeasures.eam.views.diagram.doers;

import org.conservationmeasures.eam.dialogs.ActivityPoolTablePanel;
import org.conservationmeasures.eam.dialogs.diagram.ActivityShareSelectionDiaglog;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.views.ViewDoer;

public class ShareActivityDoer extends ViewDoer
{	
	public boolean isAvailable()
	{
		if (! isDiagramView())
			return false;
	
		return true;
	}

	public void doIt() throws CommandFailedException
	{
		if (! isAvailable())
			return;
		
		ActivityShareSelectionDiaglog list = new ActivityShareSelectionDiaglog(getMainWindow(), EAM.text("Share Activity"), new ActivityPoolTablePanel(getProject()));
		list.setVisible(true);	
	}
}
