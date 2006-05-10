/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.strategicplan;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JDialog;

import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objects.ObjectType;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.DialogGridPanel;
import org.conservationmeasures.eam.views.ProjectDoer;
import org.martus.swing.UiButton;
import org.martus.swing.UiLabel;
import org.martus.swing.UiTextField;
import org.martus.swing.Utilities;

public class ModifyActivity extends ProjectDoer
{
	public ModifyActivity(StrategicPlanView viewToUse)
	{
		view = viewToUse;
	}
	
	public boolean isAvailable()
	{
		StratPlanObject selected = view.getSelectedObject();
		if(selected == null)
			return false;
		return (selected.getType() == ObjectType.TASK);
	}

	public void doIt() throws CommandFailedException
	{
		StratPlanActivity selected = (StratPlanActivity)view.getSelectedObject();
		int taskId = selected.getId();
		ModifyActivityDialog dlg = new ModifyActivityDialog(getMainWindow(), taskId);
		dlg.show();
	}
	
	MainWindow getMainWindow()
	{
		return view.getMainWindow();
	}

	static class ModifyActivityDialog extends JDialog
	{
		public ModifyActivityDialog(MainWindow parentToUse, int taskIdToUse)
		{
			super(parentToUse);
			taskId = taskIdToUse;
			
			String existingLabel = getProject().getObjectData(ObjectType.TASK, taskId, Task.TAG_LABEL);
			labelField = new UiTextField(50);
			labelField.setText(existingLabel);
			
			DialogGridPanel grid = new DialogGridPanel();
			grid.add(new UiLabel(EAM.text("Label|Label")));
			grid.add(labelField);
			
			Container contents = getContentPane();
			contents.setLayout(new BorderLayout());

			contents.add(grid, BorderLayout.CENTER);
			contents.add(createButtonBar(), BorderLayout.AFTER_LAST_LINE);
			Utilities.centerDlg(this);
			pack();
			
			setModal(true);
		}

		private Project getProject()
		{
			return ((MainWindow)getParent()).getProject();
		}

		Component createButtonBar()
		{
			okButton = new UiButton(EAM.text("Button|OK"));
			okButton.addActionListener(new OkHandler());
			getRootPane().setDefaultButton(okButton);
			cancelButton = new UiButton(EAM.text("Button|Cancel"));
			cancelButton.addActionListener(new CancelHandler());

			Box buttonBar = Box.createHorizontalBox();
			Component[] components = new Component[] {Box.createHorizontalGlue(), okButton, cancelButton};
			Utilities.addComponentsRespectingOrientation(buttonBar, components);
			return buttonBar;
		}
		
		class CancelHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
			
		}
		
		class OkHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				CommandSetObjectData cmd = new CommandSetObjectData(ObjectType.TASK, taskId, Task.TAG_LABEL, labelField.getText());
				try
				{
					getProject().executeCommand(cmd);
				}
				catch (CommandFailedException e)
				{
					EAM.logException(e);
					EAM.errorDialog("Unexpected error prevented this operation");
				}
				dispose();
			}
		}
		
		int taskId;
		UiTextField labelField;
		UiButton okButton;
		UiButton cancelButton;
	}
	
	StrategicPlanView view;
}
