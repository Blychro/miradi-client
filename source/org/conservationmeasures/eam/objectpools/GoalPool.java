/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objectpools;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdAssigner;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.EAMObject;
import org.conservationmeasures.eam.objects.Goal;

public class GoalPool extends DesirePool
{
	public GoalPool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.GOAL);
	}
	
	public Goal find(BaseId id)
	{
		return (Goal)findDesire(id);
	}

	EAMObject createRawObject(BaseId actualId)
	{
		return new Goal(actualId);
	}

}
