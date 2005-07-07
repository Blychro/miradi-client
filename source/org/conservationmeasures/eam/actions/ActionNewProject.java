/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.main.MainWindowAction;

public class ActionNewProject extends MainWindowAction
{
	public ActionNewProject(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel());
	}

	private static String getLabel()
	{
		return EAM.text("Action|New Project");
	}


	public void actionPerformed(ActionEvent arg0)
	{
		JFileChooser dlg = new JFileChooser();
		if(dlg.showSaveDialog(getMainWindow()) != JFileChooser.APPROVE_OPTION)
			return;
		
		File chosen = dlg.getSelectedFile();
		if(chosen.exists())
		{
			String title = EAM.text("Title|Overwrite existing project?");
			String[] body = {EAM.text("This will replace the existing project with a new, empty project")};
			if(!getMainWindow().confirmDialog(title, body))
				return;
			
			chosen.delete();
		}
				
		getMainWindow().loadProject(chosen);
	}

}
