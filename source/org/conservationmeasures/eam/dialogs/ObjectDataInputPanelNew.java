/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.commands.CommandDeleteObject;
import org.conservationmeasures.eam.dialogfields.ObjectAdjustableStringInputField;
import org.conservationmeasures.eam.dialogfields.ObjectCheckBoxField;
import org.conservationmeasures.eam.dialogfields.ObjectClassificationChoiceField;
import org.conservationmeasures.eam.dialogfields.ObjectCodeListField;
import org.conservationmeasures.eam.dialogfields.ObjectDataInputField;
import org.conservationmeasures.eam.dialogfields.ObjectDateChooserInputField;
import org.conservationmeasures.eam.dialogfields.ObjectMultilineDisplayField;
import org.conservationmeasures.eam.dialogfields.ObjectMultilineInputField;
import org.conservationmeasures.eam.dialogfields.ObjectNumericInputField;
import org.conservationmeasures.eam.dialogfields.ObjectRaitingChoiceField;
import org.conservationmeasures.eam.dialogfields.ObjectReadonlyChoiceField;
import org.conservationmeasures.eam.dialogfields.ObjectStringInputField;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.CommandExecutedListener;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objects.EAMObject;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.questions.ChoiceQuestion;
import org.martus.swing.UiLabel;

import com.jhlabs.awt.Alignment;
import com.jhlabs.awt.GridLayoutPlus;

abstract public class ObjectDataInputPanelNew extends ModelessDialogPanel implements CommandExecutedListener
{
	public ObjectDataInputPanelNew(Project projectToUse, ORef orefToUse)
	{
		this(projectToUse, new Vector(Arrays.asList(new ORef[] {orefToUse})));
	}
	
	
	public ObjectDataInputPanelNew(Project projectToUse, Vector orefsToUse)
	{
		GridLayoutPlus layout = new GridLayoutPlus(0, 2);
		layout.setColAlignment(0, Alignment.NORTHEAST);
		setLayout(layout);
		project = projectToUse;
		orefs = orefsToUse;
		fields = new Vector();
		project.addCommandExecutedListener(this);
	}
	
	public void dispose()
	{
		project.removeCommandExecutedListener(this);
		super.dispose();
	}

	public Project getProject()
	{
		return project;
	}
	

	public void setObjectId(Vector orefsToUse)
	{
		saveModifiedFields();
		orefs = orefsToUse;
		updateFieldsFromProject();
	}
	
	public void setFocusOnFirstField()
	{
		//TODO: should be first non read only field.
		if (fields.size()>0)
		{
			((ObjectDataInputField)fields.get(0)).getComponent().requestFocusInWindow();
			Rectangle rect = ((ObjectDataInputField)fields.get(0)).getComponent().getBounds();
			scrollRectToVisible(rect);
		}
	}
		
	public void addField(ObjectDataInputField field)
	{
		fields.add(field);
		addLabel(field.getObjectType(), field.getTag());
		addFieldComponent(field.getComponent());
	}

	public void addLabel(int objectType, String translatedLabelText)
	{
		UiLabel label = new UiLabel(EAM.fieldLabel(objectType, translatedLabelText));
		label.setVerticalAlignment(SwingConstants.TOP);
		add(label);
	}
	
	public void addFieldComponent(Component component)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(component, BorderLayout.BEFORE_LINE_BEGINS);
		add(panel);
	}
	
	
	public ObjectDataInputField createCheckBoxField(int objectType, String tag, String on, String off)
	{
		return new ObjectCheckBoxField(project, objectType, getObjecIdtForType(objectType), tag, on, off);
	}

	public ObjectDataInputField createStringField(int objectType, String tag)
	{
		return new ObjectStringInputField(project, objectType, getObjecIdtForType(objectType), tag);
	}
	
	public ObjectDataInputField createStringField(int objectType, String tag, int column)
	{
		return new ObjectAdjustableStringInputField(project, objectType, getObjecIdtForType(objectType), tag, column);
	}
	
	public ObjectDataInputField createDateChooserField(int objectType, String tag)
	{
		return new ObjectDateChooserInputField(project, objectType, getObjecIdtForType(objectType), tag);
	}
	
	public ObjectDataInputField createNumericField(int objectType, String tag, int column)
	{
		return new ObjectNumericInputField(project, objectType, getObjecIdtForType(objectType), tag, column);
	}
	
	public ObjectDataInputField createNumericField(int objectType, String tag)
	{
		return new ObjectNumericInputField(project, objectType, getObjecIdtForType(objectType), tag);
	}
	
	public ObjectDataInputField createMultilineField(int objectType, String tag)
	{
		return new ObjectMultilineInputField(project, objectType, getObjecIdtForType(objectType), tag);
	}
	
	public ObjectDataInputField createMultiCodeField(int objectType, ChoiceQuestion question)
	{
		return new ObjectCodeListField(project, objectType, getObjecIdtForType(objectType), question);
	}
	
	public ObjectDataInputField createReadonlyTextField(int objectType, String tag)
	{
		return new ObjectMultilineDisplayField(project, objectType, getObjecIdtForType(objectType), tag);
	}
		
	public ObjectDataInputField createClassificationChoiceField(int objectType, ChoiceQuestion question)
	{
		return new ObjectClassificationChoiceField(project, objectType, getObjecIdtForType(objectType), question);
	}
	
	public ObjectDataInputField createRatingChoiceField(int objectType, ChoiceQuestion question)
	{
		return new ObjectRaitingChoiceField(project, objectType, getObjecIdtForType(objectType), question);
	}

	public ObjectDataInputField createReadOnlyChoiceField(int objectType, ChoiceQuestion question)
	{
		return new ObjectReadonlyChoiceField(project, objectType, getObjecIdtForType(objectType), question);
	}
	
	
	private BaseId getObjecIdtForType(int objectType)
	{
		for (int i=0; i<orefs.size(); ++i)
		{
			int type = ((ORef)orefs.get(i)).getObjectType();
			if (objectType == type)
				return  ((ORef)orefs.get(i)).getObjectId();
		}
		return BaseId.INVALID;
	}
	
	
	public void saveModifiedFields()
	{
		for(int i = 0; i < fields.size(); ++i)
		{
			ObjectDataInputField field = (ObjectDataInputField)fields.get(i);
			field.saveIfNeeded();
		}
	}
	
	public void updateFieldsFromProject()
	{
		setFieldObjectIds();
		for(int i = 0; i < fields.size(); ++i)
		{
			ObjectDataInputField field = (ObjectDataInputField)fields.get(i);
			field.updateFromObject();
		}
	}
	
	public void setFieldObjectIds()
	{
		for(int i = 0; i < fields.size(); ++i)
		{
			ObjectDataInputField field = (ObjectDataInputField)fields.get(i);
			field.setObjectId(getObjecIdtForType(field.getObjectType()));
		}
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		if(wasOurObjectJustDeleted(event))
		{
			CommandDeleteObject cmd = (CommandDeleteObject)event.getCommand();
			deleteObjectFromList(cmd.getObjectId());
			setFieldObjectIds();
			return;
		}
		updateFieldsFromProject();
	}
	

	public void deleteObjectFromList(BaseId baseId)
	{
		for (int i=0; i<orefs.size(); ++i)
		{
			BaseId objectId = ((ORef)orefs.get(i)).getObjectId();
			if (objectId.equals(baseId))
				orefs.remove(i);
		}
	}

	boolean wasOurObjectJustDeleted(CommandExecutedEvent event)
	{
		if(!event.getCommandName().equals(CommandDeleteObject.COMMAND_NAME))
			return false;
		
		CommandDeleteObject cmd = (CommandDeleteObject)event.getCommand();
		if(isTypeInList(cmd.getObjectType()))
			return false;
		if(!cmd.getObjectId().equals(getObjecIdtForType(cmd.getObjectType())))
			return false;
		return true;
	}


	boolean wasOurObjectJustCreateUndone(CommandExecutedEvent event)
	{
		if(!event.getCommandName().equals(CommandCreateObject.COMMAND_NAME))
			return false;
		
		CommandCreateObject cmd = (CommandCreateObject)event.getCommand();
		if(isTypeInList(cmd.getObjectType()))
			return false;

		return (cmd.getCreatedId().equals(getObjecIdtForType(cmd.getObjectType())));
	}
	

	private boolean isTypeInList(int objectType)
	{
		return (getObjecIdtForType(objectType)!=BaseId.INVALID);
	}


	//TODO: Is this used  anywhree in orig input panel can we delte from ModelssDialgo Panel
	public EAMObject getObject()
	{
		return null;
	}

	//TODO: Is this used  anywhree in orig input panel 
	public BaseId[] getObjectId()
	{
		return null;
	}


	private Project project;
	private Vector orefs;
	private Vector fields;
}
