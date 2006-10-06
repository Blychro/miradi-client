/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */

package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.json.JSONObject;


public class Goal extends Desire 
{
	public Goal(BaseId idToUse)
	{
		super(idToUse);
	}
	
	public Goal(int idAsInt, JSONObject json)
	{
		super(new BaseId(idAsInt), json);
	}
	
	public int getType()
	{
		return ObjectType.GOAL;
	}

}
