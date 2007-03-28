/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objectpools;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdAssigner;
import org.conservationmeasures.eam.objecthelpers.CreateAssignmentParameter;
import org.conservationmeasures.eam.objecthelpers.CreateObjectParameter;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Assignment;
import org.conservationmeasures.eam.objects.BaseObject;

public class AssignmentPool extends EAMNormalObjectPool
{
	public AssignmentPool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.ASSIGNMENT);
	}
	
	public void put(Assignment assignment)
	{
		put(assignment.getId(), assignment);
	}
	
	public Assignment find(BaseId id)
	{
		return (Assignment)getRawObject(id);
	}
	
	BaseObject createRawObject(BaseId actualId, CreateObjectParameter extraInfo) throws Exception
	{
		return new Assignment(actualId, (CreateAssignmentParameter)extraInfo);
	}
}
