/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.project;

import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objectpools.TargetPool;

public class TestTargetPool extends TestFactorPool
{
	public TestTargetPool(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		pool = project.getTargetPool();

		for(int i = 0; i < 4; ++i)
		{
			addNewlyCreatedNodeToPool(ObjectType.TARGET);
		}
	}

	public void testBasics() throws Exception
	{
		assertEquals("wrong target count?", 4, pool.getTargets().length);
	}
	
	TargetPool pool;
}
