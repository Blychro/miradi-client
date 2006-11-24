/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */

package org.conservationmeasures.eam.diagram;

import org.conservationmeasures.eam.diagram.cells.EAMGraphCell;
import org.conservationmeasures.eam.testall.EAMTestCase;

public class TestEamGraphCell extends EAMTestCase
{
	public TestEamGraphCell(String name) 
	{
		super(name);
	}

	public void testBasics() throws Exception
	{
		EAMGraphCell cell = new EAMGraphCell();
		assertFalse("is node?", cell.isNode());
		assertFalse("is link?", cell.isLinkage());
	}
}
