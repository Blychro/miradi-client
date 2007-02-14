/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogfields;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComponent;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.project.Project;
import org.martus.swing.UiCheckBox;

public class ObjectCheckBoxField extends ObjectDataInputField
{
	public ObjectCheckBoxField(Project projectToUse, int objectType, BaseId objectId, String tag)
	{
		super(projectToUse, objectType, objectId, tag);
		checkBox = new UiCheckBox();
		checkBox.addItemListener(new StatusChangeHandler());
		addFocusListener();
	}

	public JComponent getComponent()
	{
		return checkBox;
	}

	public String getText()
	{
		if (checkBox.isSelected())
			return Strategy.STATUS_DRAFT;
		return Strategy.STATUS_REAL;
	}

	public void setText(String code)
	{
		checkBox.setSelected(code.equals(Strategy.STATUS_DRAFT));
	}

	public void updateEditableState()
	{
		checkBox.setEnabled(isValidObject());
		if(isValidObject())
		{
			checkBox.setForeground(EAM.EDITABLE_FOREGROUND_COLOR);
			checkBox.setBackground(EAM.EDITABLE_BACKGROUND_COLOR);
		}
		else
		{
			checkBox.setForeground(EAM.READONLY_FOREGROUND_COLOR);
			checkBox.setBackground(EAM.READONLY_BACKGROUND_COLOR);
		}
	}
	
	class StatusChangeHandler implements ItemListener
	{
		public void itemStateChanged(ItemEvent event)
		{
			setNeedsSave();
			saveIfNeeded();
		}
	}
	
	UiCheckBox checkBox;
}
