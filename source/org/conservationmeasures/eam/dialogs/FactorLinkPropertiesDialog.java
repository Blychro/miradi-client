package org.conservationmeasures.eam.dialogs;

import javax.swing.Box;

import org.conservationmeasures.eam.main.MainWindow;

public class FactorLinkPropertiesDialog extends ModelessDialogWithClose
{

	public FactorLinkPropertiesDialog(MainWindow parent, ModelessDialogPanel panel, String headingText)
	{
		super(parent, panel, headingText);
		factorPanel = panel;
	}
	
	public void addAdditoinalButtons(Box buttonBar)
	{
		createDirectionsButton(buttonBar);
	}

	protected Class getJumpAction()
	{
		return factorPanel.getJumpActionClass();
	}
	
	ModelessDialogPanel factorPanel;
}
