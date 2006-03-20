/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.threatmatrix;

import javax.swing.JScrollPane;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.ThreatRatingCriterion;
import org.conservationmeasures.eam.objects.ThreatRatingValueOption;
import org.conservationmeasures.eam.project.ThreatRatingFramework;
import org.conservationmeasures.eam.utils.HtmlViewer;
import org.conservationmeasures.eam.utils.HyperlinkHandler;

public class ThreatRatingWizardSetValue extends ThreatRatingWizardStep implements HyperlinkHandler
{
	public ThreatRatingWizardSetValue(ThreatRatingWizardPanel wizardToUse, int criterionId) throws Exception
	{
		wizard = wizardToUse;

		criterion = getFramework().getCriterion(criterionId);

		htmlViewer = new HtmlViewer("", this);
		JScrollPane scrollPane = new JScrollPane(htmlViewer);
		add(scrollPane);
	}

	public void refresh() throws Exception
	{
		int valueId = wizard.getSelectedBundle().getValueId(criterion.getId());
		value = getFramework().getValueOption(valueId);
		String htmlText = new ThreatRatingWizardSetValueText(getValueOptionLabels(), value.getLabel()).getText();
		htmlViewer.setText(htmlText);
	}
	
	String[] getValueOptionLabels()
	{
		ThreatRatingValueOption[] options = getFramework().getValueOptions();
		String[] optionLabels = new String[options.length];
		for(int i = 0; i < optionLabels.length; ++i)
			optionLabels[i] = options[i].getLabel();
		return optionLabels;
	}

	private ThreatRatingFramework getFramework()
	{
		return wizard.getFramework();
	}
	
	public void linkClicked(String linkDescription)
	{
	}

	public void valueChanged(String widget, String newValue)
	{
		setValue(newValue);
	}
	
	public void setValue(String newValue)
	{
		value = findValueOptionByName(newValue);
	}
	
	public ThreatRatingValueOption findValueOptionByName(String label)
	{
		ThreatRatingValueOption[] options = getFramework().getValueOptions();
		for(int i = 0; i < options.length; ++i)
			if(label.equals(options[i].getLabel()))
				return options[i];
			
		throw new RuntimeException("Unknown value option: " + label);
	}

	public void buttonPressed(String buttonName)
	{
		try
		{
			wizard.getView().setBundleValue(criterion, value);
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}

	ThreatRatingWizardPanel wizard;
	ThreatRatingCriterion criterion;
	ThreatRatingValueOption value;
	HtmlViewer htmlViewer;
}
