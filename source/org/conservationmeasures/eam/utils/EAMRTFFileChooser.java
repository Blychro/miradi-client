/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.utils;

import javax.swing.filechooser.FileFilter;

import org.conservationmeasures.eam.main.MainWindow;

public class EAMRTFFileChooser extends EAMFileSaveChooser
{

	public EAMRTFFileChooser(MainWindow mainWindow)
	{
		super(mainWindow);
	}

	public String getDialogApproveTitleText()
	{
		return "Title|Save RTF File";
	}

	public String getApproveButtonToolTipText()
	{
		return "TT|Save RTF File";
	}

	public String getDialogApprovelButtonText()
	{
		return "Save Report";
	}

	public String getDialogOverwriteTitleText()
	{
		return "Title|Overwrite existing file?";
	}

	public String getDialogOverwriteBodyText()
	{
		return "This will replace the existing file.";
	}

	public FileFilter[] getFileFilter()
	{
		return new FileFilter[] {new RTFFileFilter()};
	}
}

