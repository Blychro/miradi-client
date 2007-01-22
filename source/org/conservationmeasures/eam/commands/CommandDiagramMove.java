/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.commands;

import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.project.Project;

public class CommandDiagramMove extends Command
{
	public CommandDiagramMove(int deltaX, int deltaY, DiagramFactorId[] idsToMove)
	{
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		this.ids = idsToMove;
	}
	
	public String toString()
	{
		String stringOfIds = "(";
		for(int i=0; i < ids.length; ++i)
			stringOfIds += ids[i] + ",";
		stringOfIds += ")";
		return getCommandName() + ":" + stringOfIds + "," + deltaX + "," + deltaY;
	}
	
	public String getCommandName()
	{
		return COMMAND_NAME;
	}

	public void execute(Project target) throws CommandFailedException
	{
		try
		{
			target.moveFactors(getDeltaX(), getDeltaY(), getIds());
		}
		catch (Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
	}
	
	public Command getReverseCommand() throws CommandFailedException
	{
		CommandDiagramMove command = new CommandDiagramMove(-getDeltaX(), -getDeltaY(), getIds());
		return command;
	}

	public void undo(Project target) throws CommandFailedException
	{
		try
		{
			target.moveFactors(-getDeltaX(), -getDeltaY(), getIds());
		}
		catch (Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
	}
	
	public int getDeltaX()
	{
		return deltaX;
	}
	
	public int getDeltaY()
	{
		return deltaY;
	}
	
	public DiagramFactorId[] getIds()
	{
		return ids;
	}


	public static final String COMMAND_NAME = "DiagramMove";

	int deltaX;
	int deltaY;
	DiagramFactorId[] ids;
}
