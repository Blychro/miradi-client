/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.commands;

import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.project.Project;

public class CommandBeginTransaction extends Command 
{
	public CommandBeginTransaction() 
	{
		super();
	}
	
	public void execute(Project target) throws CommandFailedException 
	{
	}

	public Command getReverseCommand() throws CommandFailedException
	{
		return new CommandEndTransaction();
	}

	public String getCommandName()
	{
		return COMMAND_NAME;
	}

	public String toString()
	{
		return getCommandName();
	}

	public boolean isBeginTransaction()
	{
		return true;
	}
	

	public static final String COMMAND_NAME = "BeginTransaction";

}
