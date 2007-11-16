/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Factor;

public class CreateObjective extends CreateAnnotationDoer
{
	public int getAnnotationType()
	{
		return ObjectType.OBJECTIVE;
	}
	
	public String getAnnotationListTag()
	{
		return Factor.TAG_OBJECTIVE_IDS;
	}
}