/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogfields;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.event.CaretEvent;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.CustomDateChooser;
import org.martus.util.MultiCalendar;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

public class ObjectDateChooserInputField extends ObjectDataInputField
{
	public ObjectDateChooserInputField(Project projectToUse, int type, BaseId id, String tag)
	{
		super(projectToUse, type, id, tag);
		project = projectToUse;
		
		DateEditor dateEditor = new DateEditor();
		dateChooser = new CustomDateChooser(dateEditor);
		dateChooser.setDateFormatString("MM/dd/yyyy");

		setDateChooserPreferredSizeWithPadding();
	}

	private void setDateChooserPreferredSizeWithPadding()
	{
		Dimension preferredDimension = dateChooser.getPreferredSize();
		preferredDimension.width = preferredDimension.width + EXTRA_PADDING;
		dateChooser.setMinimumSize(preferredDimension);
		dateChooser.setPreferredSize(preferredDimension);
	}
		
	public String getPanelDescription()
	{
		return EAM.text("Date Chooser");
	}

	public JComponent getComponent()
	{
		return dateChooser;
	}

	public String getText()
	{
		return convertFormat();
	}
	
	private String convertFormat()
	{
		Date date = dateChooser.getDate();
		if (date == null)
			return "";
		
		MultiCalendar calendar = new MultiCalendar();
		calendar.setTime(date);
		return calendar.toIsoDateString();
	}
	
	public void setText(String newValue)
	{
		if (newValue.length() <= 0 )
		{   
			((JDateChooser)getComponent()).setDate(null);		
			clearNeedsSave();
			return;
		}

		MultiCalendar calendar = MultiCalendar.createFromIsoDateString(newValue);
		dateChooser.setDate(calendar.getTime());
		clearNeedsSave();
	}
	
	public void updateEditableState()
	{
		dateChooser.setEnabled(isValidObject());
	}

	class DateEditor extends JTextFieldDateEditor
	{
		public DateEditor()
		{
			super();
		}
		
		public void setDate(Date newDate)
		{
			super.setDate(newDate);
			setForeground(Color.blue);
			saveDate();	
		}

		private void saveDate()
		{
			if (dateChooser == null)
				return;
			
			setNeedsSave();
			saveIfNeeded();
		}

		public void focusLost(FocusEvent arg0)
		{
			super.focusLost(arg0);
			setForeground(Color.BLUE);
			saveDate();
		}

		public void caretUpdate(CaretEvent event)
		{
			super.caretUpdate(event);
			setForeground(Color.BLUE);
		}
		
		public void setEnabled(boolean b)
		{
			super.setEnabled(b);
			setForeground(Color.blue);
		}

	}
	
	CustomDateChooser dateChooser;
	private static final int EXTRA_PADDING = 10;
}


