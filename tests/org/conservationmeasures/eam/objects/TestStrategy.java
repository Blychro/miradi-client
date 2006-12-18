/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.objecthelpers.CreateFactorParameter;
import org.conservationmeasures.eam.objecthelpers.ObjectType;

public class TestStrategy extends ObjectTestCase
{
	public TestStrategy(String name)
	{
		super(name);
	}

	public void testFields() throws Exception
	{
		CreateFactorParameter extraInfo = new CreateFactorParameter(Factor.TYPE_STRATEGY);
		verifyFields(ObjectType.FACTOR, extraInfo);
	}
	
	public void testBasics()
	{
		FactorId interventionId = new FactorId(17);
		Strategy intervention = new Strategy(interventionId);
		assertEquals("already has activities?", 0, intervention.getActivityIds().size());
		
		BaseId activityId1 = new BaseId(77);
		intervention.insertActivityId(activityId1, 0);
		IdList afterAdd1 = intervention.getActivityIds();
		assertEquals("didn't add?", 1, afterAdd1.size());
		assertEquals("wrong task id?", activityId1, afterAdd1.get(0));
		
		BaseId activityId2 = new BaseId(92);
		intervention.insertActivityId(activityId2, 0);
		IdList afterAdd2 = intervention.getActivityIds();
		assertEquals("didn't add 2?", 2, afterAdd2.size());
		assertEquals("didn't insert at front?", activityId2, afterAdd2.get(0));
		assertEquals("original id lost?", activityId1, afterAdd2.get(1));
		
		intervention.removeActivityId(activityId2);
		IdList afterRemove = intervention.getActivityIds();
		assertEquals("didn't remove?", 1, afterRemove.size());
		assertEquals("removed wrong id?", activityId1, afterRemove.get(0));
		
	}
	
	public void testActivityIds() throws Exception
	{
		FactorId interventionId = new FactorId(66);
		Strategy intervention = new Strategy(interventionId);
		IdList empty = new IdList(intervention.getData(Strategy.TAG_ACTIVITY_IDS));
		assertEquals("not empty to start?", 0, empty.size());
		
		BaseId activityId = new BaseId(828);
		IdList oneItem = new IdList();
		oneItem.add(activityId);
		intervention.setData(Strategy.TAG_ACTIVITY_IDS, oneItem.toString());
		
		IdList got = new IdList(intervention.getData(Strategy.TAG_ACTIVITY_IDS));
		assertEquals("round trip failed?", oneItem, got);
	}
	
	public void testStatus() throws Exception
	{
		FactorId interventionId = new FactorId(91);
		Strategy intervention = new Strategy(interventionId);
		assertTrue("didn't default to real status?", intervention.isStatusReal());
		assertFalse("defaulted to draft status?", intervention.isStatusDraft());
		assertEquals("Didn't default to valid status?", Strategy.STATUS_REAL, intervention.getData(Strategy.TAG_STATUS));
		intervention.setData(Strategy.TAG_STATUS, Strategy.STATUS_DRAFT);
		assertEquals("set/get didn't work?", Strategy.STATUS_DRAFT, intervention.getData(Strategy.TAG_STATUS));
		assertFalse("didn't unset real status?", intervention.isStatusReal());
		assertTrue("didn't set to draft status?", intervention.isStatusDraft());
		intervention.setData(Strategy.TAG_STATUS, Strategy.STATUS_REAL);
		assertTrue("didn't restore to real status?", intervention.isStatusReal());
		assertFalse("didn't unset draft status?", intervention.isStatusDraft());
		intervention.setData(Strategy.TAG_STATUS, "OIJFW*FJJF");
		assertTrue("didn't treat unknown as real?", intervention.isStatusReal());
		assertFalse("treated unknown as draft?", intervention.isStatusDraft());
		
	}
	
	public void testJson() throws Exception
	{
		FactorId interventionId = new FactorId(17);
		Strategy intervention = new Strategy(interventionId);
		intervention.setData(Strategy.TAG_STATUS, Strategy.STATUS_DRAFT);
		intervention.insertActivityId(new BaseId(23), 0);
		intervention.insertActivityId(new BaseId(37), 1);
		
		Strategy got = (Strategy)EAMBaseObject.createFromJson(intervention.getType(), intervention.toJson());
		assertTrue("Didn't restore status?", got.isStatusDraft());
		assertEquals("Didn't read activities?", intervention.getActivityIds(), got.getActivityIds());
	}
	

	static final BaseId criterionId1 = new BaseId(17);
	static final BaseId criterionId2 = new BaseId(952);
	static final BaseId criterionId3 = new BaseId(2833);
	static final BaseId valueId1 = new BaseId(85);
	static final BaseId valueId2 = new BaseId(2398);
	static final BaseId defaultValueId = new BaseId(7272);

}
