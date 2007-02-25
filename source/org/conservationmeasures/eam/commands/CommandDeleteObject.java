/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.commands;

import java.util.HashMap;

import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.objecthelpers.CreateObjectParameter;
import org.conservationmeasures.eam.objects.EAMObject;
import org.conservationmeasures.eam.project.Project;

public class CommandDeleteObject extends Command
{
	public CommandDeleteObject(int objectType, BaseId objectId)
	{
		type = objectType;
		id = objectId;
		reverseExtraInfo = null;
	}
	
	public int getObjectType()
	{
		return type;
	}
	
	public BaseId getObjectId()
	{
		return id;
	}

	public String getCommandName()
	{
		return COMMAND_NAME;
	}

	public void execute(Project target) throws CommandFailedException
	{
		try
		{
			EAMObject object = target.findObject(type, id);
			reverseExtraInfo = object.getCreationExtraInfo();
			target.deleteObject(type, id);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}
	
	public Command getReverseCommand() throws CommandFailedException
	{
		CommandCreateObject command = new CommandCreateObject(type, reverseExtraInfo);
		command.setCreatedId(id);
		return command;
	}
	
	public HashMap getLogData()
	{
		HashMap dataPairs = new HashMap();
		dataPairs.put("OBJECT_TYPE", new Integer(type));
		dataPairs.put(BaseId.class.getSimpleName(), id);
		return dataPairs;
	}
	
	public final static String COMMAND_NAME = "DeleteObject";

	int type;
	BaseId id;
	CreateObjectParameter reverseExtraInfo;
}
