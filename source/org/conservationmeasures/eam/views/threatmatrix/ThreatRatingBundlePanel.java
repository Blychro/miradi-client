/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.threatmatrix;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.project.ThreatRatingBundle;
import org.martus.swing.UiLabel;

import com.jhlabs.awt.BasicGridLayout;

public class ThreatRatingBundlePanel extends JPanel
{
	public ThreatRatingBundlePanel(ThreatMatrixView viewToUse) throws Exception
	{
	    super(new BasicGridLayout(0, 1));

		view = viewToUse;
		project = view.getProject();
		
		add(createHeader());
		add(createRatingPanel());
		add(Box.createVerticalGlue());
	}
	
	
	
	public ThreatRatingBundle getBundle()
	{
		return workingBundle;
	}
	
	public void selectBundle(ThreatRatingBundle bundleToUse) throws Exception
	{
		if(bundleToUse == null)
			workingBundle = null;
		else
			workingBundle = new ThreatRatingBundle(bundleToUse);
		ratingPanel.setBundle(workingBundle);
		updateValues();
	}
	
	private void updateValues() throws Exception
	{
		if(workingBundle == null)
		{
			threatName.setText("");
			targetName.setText("");
			threatName.setBorder(null);
			targetName.setBorder(null);
		}
		else
		{
			FactorId threatId = workingBundle.getThreatId();
			FactorId targetId = workingBundle.getTargetId();
			threatName.setText(getNodeName(threatId));
			targetName.setText(getNodeName(targetId));
			threatName.setBorder(new LineBorder(Color.BLACK));
			targetName.setBorder(new LineBorder(Color.BLACK));
		}
	}
	
	private String getNodeName(FactorId nodeId) throws Exception
	{
		DiagramModel model = project.getDiagramModel();
		return model.getNodeById(nodeId).getLabel();

	}
	
	private JPanel createHeader() throws Exception
	{
		targetName = createNameArea();
		threatName = createNameArea();

		JPanel headerBox = new JPanel(new BasicGridLayout(2, 2));		
		headerBox.add(new UiLabel("Threat:"));
		headerBox.add(threatName);
		headerBox.add(new UiLabel("Target:"));
		headerBox.add(targetName);
		
		JPanel panel = new JPanel();
		panel.add(headerBox);

		panel.setBorder(new EmptyBorder(2, 5, 2, 5));
		return panel;
	}



	private JTextArea createNameArea()
	{
		JTextArea area = new JTextArea("");
		area.setWrapStyleWord(true);
		area.setLineWrap(true);
		area.setOpaque(false);
		area.setFont(new Font(null,Font.BOLD,12));
		area.setPreferredSize(new Dimension(150, 60));
		area.setEditable(false);
		return area;
	}

	private ThreatRatingPanel createRatingPanel()
	{
		ratingPanel = new ThreatRatingPanel(view);
		return ratingPanel;
	}
		
	ThreatMatrixView view;
	Project project;
	ThreatRatingBundle workingBundle;
	ThreatRatingPanel ratingPanel;

	JTextArea threatName;
	JTextArea targetName;
}
