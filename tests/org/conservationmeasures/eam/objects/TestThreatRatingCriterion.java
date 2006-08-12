/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.testall.EAMTestCase;

public class TestThreatRatingCriterion extends EAMTestCase
{
	public TestThreatRatingCriterion(String name)
	{
		super(name);
	}

	public void testSetGetData() throws Exception
	{
		
		verifySetGetData(ThreatRatingCriterion.TAG_LABEL, "Hi mom!");
	}
	
	public void testGetDataBadFieldTag()
	{
		BaseId id = new BaseId(6);
		ThreatRatingCriterion option = new ThreatRatingCriterion(id);
		try
		{
			option.getData("not a valid tag");
			fail("Should have thrown for invalid tag");
		}
		catch (RuntimeException ignoreExpected)
		{
		}
	}
	
	public void testSetDataBadFieldTag() throws Exception
	{
		BaseId id = new BaseId(6);
		ThreatRatingCriterion option = new ThreatRatingCriterion(id);
		try
		{
			option.setData("not a valid tag", "whatever");
			fail("Should have thrown for invalid tag");
		}
		catch (RuntimeException ignoreExpected)
		{
		}
	}

	private void verifySetGetData(String tag, String value) throws Exception
	{
		BaseId id = new BaseId(6);
		ThreatRatingCriterion option = new ThreatRatingCriterion(id);
		option.setData(tag, value);
		assertEquals(value, option.getData(tag));
	}

	public void testEquals() throws Exception
	{
		BaseId id = new BaseId(32);
		String label = "Text";
		ThreatRatingCriterion a = new ThreatRatingCriterion(id);
		a.setData(ThreatRatingCriterion.TAG_LABEL, label);
		ThreatRatingCriterion b = new ThreatRatingCriterion(id);
		b.setData(ThreatRatingCriterion.TAG_LABEL, "other label");
		ThreatRatingCriterion c = new ThreatRatingCriterion(new BaseId(id.asInt() + 1));
		c.setData(ThreatRatingCriterion.TAG_LABEL, label);
		assertEquals("id same not good enough?", a, b);
		assertNotEquals("id different still equals?", a, c);
		assertNotEquals("different type equals?", a, new Object());
	}
}
