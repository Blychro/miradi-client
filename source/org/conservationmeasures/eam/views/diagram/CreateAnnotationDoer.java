/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.dialogs.AnnotationSelectionDlg;
import org.conservationmeasures.eam.dialogs.ObjectTablePanel;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.views.ObjectsDoer;
import org.conservationmeasures.eam.views.umbrella.ObjectPicker;

public abstract class CreateAnnotationDoer extends ObjectsDoer
{
	
	abstract int getAnnotationType();
	abstract String getAnnotationIdListTag();


	public boolean isAvailable()
	{
		if (! isDiagramView())
			return false;
		
		return (getSelectedFactor() != null);
	}

	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;
		
		Factor factor = getSelectedFactor();
		
		getProject().executeCommand(new CommandBeginTransaction());
		try
		{
			CommandCreateObject create = createObject();
			BaseId createdId = create.getCreatedId();
			getProject().executeCommand(CommandSetObjectData.createAppendIdCommand(factor, getAnnotationIdListTag(), createdId));
			
			ORef ref = new ORef(create.getObjectType(), createdId);
			ObjectPicker picker = getPicker();
			if(picker != null)
				picker.ensureObjectVisible(ref);
		}
		catch(Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
		finally
		{
			getProject().executeCommand(new CommandEndTransaction());
		}
	}
	
	protected CommandCreateObject createObject() throws CommandFailedException
	{
		CommandCreateObject create = new CommandCreateObject(getAnnotationType());
		getProject().executeCommand(create);
		return create;
	}
	
	protected CommandCreateObject cloneObject(BaseObject objectToClone) throws CommandFailedException
	{
		CommandCreateObject create = new CommandCreateObject(getAnnotationType());
		getProject().executeCommand(create);
		CommandSetObjectData[]  commands = objectToClone.createCommandsToClone(create.getCreatedId());
		getProject().executeCommands(commands);
		return create;
	}
	
	protected BaseObject displayAnnotationList(String title, ObjectTablePanel tablePanel)
	{
		AnnotationSelectionDlg list = new AnnotationSelectionDlg(getMainWindow(), title, tablePanel);
		list.setVisible(true);
		return list.getSelectedAnnotaton();
	}
	
	public Factor getSelectedFactor()
	{
		BaseObject selected = getView().getSelectedObject();
		if(selected == null)
			return null;
		
		if(! Factor.isFactor(selected.getType()))
			return null;
		
		return (Factor)selected;
	}
	

}