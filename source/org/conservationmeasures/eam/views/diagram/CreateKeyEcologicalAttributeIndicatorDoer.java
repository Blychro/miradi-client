/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import java.text.ParseException;

import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.CreateTaskParameter;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.KeyEcologicalAttribute;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.ViewDoer;

public class CreateKeyEcologicalAttributeIndicatorDoer extends ViewDoer
{
	public boolean isAvailable()
	{
		return true;
	}

	public void doIt() throws CommandFailedException
	{
		doInsertKEAIndicator();
	}

	private void doInsertKEAIndicator() throws CommandFailedException
	{
		if(!isAvailable())
			return;

		KeyEcologicalAttribute kea = (KeyEcologicalAttribute)getView().getSelectedObject();

		try
		{
			insertKEAIndicator(getProject(), kea, kea.getIndicatorIds().size());
		}
		catch (Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
	}

	public static void insertKEAIndicator(Project project, KeyEcologicalAttribute kea, int childIndex) throws CommandFailedException, ParseException, Exception
	{
		project.executeCommand(new CommandBeginTransaction());
		try
		{
			CreateTaskParameter parentRef = new CreateTaskParameter(kea.getRef());
			CommandCreateObject create = new CommandCreateObject(ObjectType.INDICATOR, parentRef);
			project.executeCommand(create);
			BaseId createdId = create.getCreatedId();
	
			CommandSetObjectData addChild = CommandSetObjectData.createInsertIdCommand(kea, 
					KeyEcologicalAttribute.TAG_INDICATOR_IDS, createdId, childIndex);
			project.executeCommand(addChild);
		}
		finally
		{
			project.executeCommand(new CommandEndTransaction());
		}
		
	}
}
