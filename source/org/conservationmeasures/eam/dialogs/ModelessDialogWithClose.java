/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.conservationmeasures.eam.main.EAM;
import org.martus.swing.UiButton;
import org.martus.swing.Utilities;

public class ModelessDialogWithClose extends EAMDialog
{
	public ModelessDialogWithClose(JFrame parent)
	{
		super(parent);
		setModal(false);
	}
	public ModelessDialogWithClose(JFrame parent,String headingText)
	{
		this(parent);
		setTitle(headingText);
	}
	
	public ModelessDialogWithClose(JFrame parent, JPanel panel, String headingText)
	{
		this(parent, headingText);
	
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(panel, BorderLayout.CENTER);
		mainPanel.add(createButtonBar(), BorderLayout.AFTER_LAST_LINE);
		getContentPane().add(mainPanel);
	}
	
	private Box createButtonBar()
	{
		UiButton closeButton = new UiButton(EAM.text("Button|Close"));
		closeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		
		getRootPane().setDefaultButton(closeButton);
		Box buttonBar = Box.createHorizontalBox();
		Component[] components = new Component[] {Box.createHorizontalGlue(), closeButton};
		Utilities.addComponentsRespectingOrientation(buttonBar, components);
		return buttonBar;
	}

	
}
