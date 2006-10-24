/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.diagram;

import java.awt.Component;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import org.conservationmeasures.eam.diagram.nodes.DiagramFactor;
import org.conservationmeasures.eam.diagram.nodes.DiagramIntervention;
import org.conservationmeasures.eam.diagram.nodes.DiagramTarget;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.project.Project;
import org.martus.swing.UiCheckBox;
import org.martus.swing.UiVBox;

public class LayerDialog extends JDialog implements ActionListener
{
	public LayerDialog(MainWindow mainWindowToUse) throws HeadlessException
	{
		super(mainWindowToUse, EAM.text("Title|View Layers"));
		mainWindow = mainWindowToUse;
		
		interventionCheckBox = new UiCheckBox(EAM.text("Label|Show Interventions"));
		interventionCheckBox.addActionListener(this);
		factorCheckBox = new UiCheckBox(EAM.text("Label|Show Factors"));
		factorCheckBox.addActionListener(this);
		targetCheckBox = new UiCheckBox(EAM.text("Label|Show Targets"));
		targetCheckBox.addActionListener(this);
		desireCheckBox = new UiCheckBox(EAM.text("Label|Show Goals and Objectives"));
		desireCheckBox.addActionListener(this);
		
		setControlsFromLayerManager();
		
		UiVBox bigBox = new UiVBox();
		bigBox.add(createLayerOptions());
		
		Container contents = getContentPane();
		contents.add(bigBox);
		pack();
		setResizable(true);
		setModal(false);
	}

	private Component createLayerOptions()
	{
		UiVBox options = new UiVBox();
		options.add(interventionCheckBox);
		options.add(factorCheckBox);
		options.add(targetCheckBox);
		options.add(desireCheckBox);
		return options;
	}
	
	public void actionPerformed(ActionEvent event)
	{
		applyChanges();
	}
	
	public boolean getResult()
	{
		return result;
	}
	
	private void setControlsFromLayerManager()
	{
		interventionCheckBox.setSelected(getLayerManager().isTypeVisible(DiagramIntervention.class));
		factorCheckBox.setSelected(getLayerManager().isTypeVisible(DiagramFactor.class));
		targetCheckBox.setSelected(getLayerManager().isTypeVisible(DiagramTarget.class));
		desireCheckBox.setSelected(getLayerManager().areDesiresVisible());
	}
	
	private void updateLayerManagerFromControls()
	{
		getLayerManager().setVisibility(DiagramIntervention.class, interventionCheckBox.isSelected());
		getLayerManager().setVisibility(DiagramFactor.class, factorCheckBox.isSelected());
		getLayerManager().setVisibility(DiagramTarget.class, targetCheckBox.isSelected());
		getLayerManager().setDesiresVisible(desireCheckBox.isSelected());
	}
	
	public void applyChanges()
	{
		updateLayerManagerFromControls();
		getProject().updateVisibilityOfNodes();
		mainWindow.updateStatusBar();
	}

	private LayerManager getLayerManager()
	{
		LayerManager manager = getProject().getLayerManager();
		return manager;
	}

	private Project getProject()
	{
		return mainWindow.getProject();
	}
	
	MainWindow mainWindow;
	boolean result;

	UiCheckBox interventionCheckBox;
	UiCheckBox factorCheckBox;
	UiCheckBox targetCheckBox;
	UiCheckBox desireCheckBox;
}
