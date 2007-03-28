/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objectpools;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdAssigner;
import org.conservationmeasures.eam.objecthelpers.CreateObjectParameter;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.RatingCriterion;

public class RatingCriterionPool extends EAMNormalObjectPool
{
	public RatingCriterionPool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.RATING_CRITERION);
	}

	BaseObject createRawObject(BaseId actualId, CreateObjectParameter extraInfo)
	{
		return new RatingCriterion(actualId);
	}

}
