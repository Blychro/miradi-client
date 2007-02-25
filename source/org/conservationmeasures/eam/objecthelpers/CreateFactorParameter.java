/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objecthelpers;

import java.util.HashMap;

import org.conservationmeasures.eam.diagram.factortypes.FactorType;

public class CreateFactorParameter extends CreateObjectParameter
{
	public CreateFactorParameter(FactorType type)
	{
		factorType = type;
	}

	public FactorType getNodeType()
	{
		return factorType;
	}
	
	public HashMap getLogData()
	{
		HashMap dataPairs = new HashMap();
		dataPairs.put(FactorType.class.getSimpleName(), factorType);
		return dataPairs;
	}
	
	FactorType factorType;
}
