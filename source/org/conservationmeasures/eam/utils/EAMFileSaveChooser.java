/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.utils;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;


public abstract class EAMFileSaveChooser
{

	MainWindow mainWindow;

	EAMFileSaveChooser(MainWindow mainWindow)
	{
		this.mainWindow = mainWindow;
	}

	public File displayChooser() throws CommandFailedException
	{
		
		JFileChooser dlg = new JFileChooser(currentDirectory);

		dlg.setDialogTitle(EAM.text(getDialogApproveTitleText()));
		dlg.setFileFilter(getFileFilter());
		dlg.setDialogType(JFileChooser.CUSTOM_DIALOG);
		dlg.setApproveButtonToolTipText(EAM.text(getApproveButtonToolTipText()));
		if (dlg.showDialog(mainWindow, EAM.text(getDialogApprovelButtonText())) != JFileChooser.APPROVE_OPTION)
			return null;

		File chosen = dlg.getSelectedFile();
		if (!chosen.getName().toLowerCase().endsWith(getFileExtension()))
			chosen = new File(chosen.getAbsolutePath() + getFileExtension());

		if (chosen.exists())
		{
			String title = EAM.text(getDialogOverwriteTitleText());
			String[] body = { EAM.text(getDialogOverwriteBodyText()) };
			if (!EAM.confirmDialog(title, body))
				return null;
			chosen.delete();
		}

		currentDirectory = chosen.getParent();
		return chosen;

	}

	public abstract String getDialogApproveTitleText();

	public abstract String getApproveButtonToolTipText();

	public abstract String getDialogApprovelButtonText();

	public abstract String getDialogOverwriteTitleText();

	public abstract String getDialogOverwriteBodyText();

	public abstract String getFileExtension();

	public abstract FileFilter getFileFilter();
	
	private static String currentDirectory;

}
