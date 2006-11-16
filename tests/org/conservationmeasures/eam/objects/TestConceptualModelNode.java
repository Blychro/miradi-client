/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.ids.ModelNodeId;
import org.martus.util.TestCaseEnhanced;

public class TestConceptualModelNode extends TestCaseEnhanced
{
	public TestConceptualModelNode(String name)
	{
		super(name);
	}
	
	public void testComments() throws Exception
	{
		ModelNodeId id = new ModelNodeId(35);
		ConceptualModelFactor factor = new ConceptualModelFactor(id);
		assertEquals("started with a comment?", "", factor.getComment());
		String sampleComment = "yowza";
		factor.setComment(sampleComment);
		assertEquals("couldn't getComment?", sampleComment, factor.getComment());
	}
	
	public void testSetGetData() throws Exception
	{
		ModelNodeId id = new ModelNodeId(72);
		ConceptualModelTarget target = new ConceptualModelTarget(id);
		String[] tags = {
			ConceptualModelNode.TAG_COMMENT,
			ConceptualModelNode.TAG_INDICATOR_IDS,
			ConceptualModelNode.TAG_GOAL_IDS,
			ConceptualModelNode.TAG_OBJECTIVE_IDS,
		};
		
		IdList sampleIdList = new IdList();
		sampleIdList.add(12);
		sampleIdList.add(275);
		
		String[] sampleData = {
			"Whatever comment",
			sampleIdList.toString(),
			sampleIdList.toString(),
			sampleIdList.toString(),
		};
		
		for(int i = 0; i < tags.length; ++i)
		{
			target.setData(tags[i], sampleData[i]);
			assertEquals("didn't set/get for " + tags[i], sampleData[i], target.getData(tags[i]));
		}
		
		String nodeTypeTag = ConceptualModelNode.TAG_NODE_TYPE;
		try
		{
			target.setData(nodeTypeTag, "This won't be used anyway");
			fail("Should have thrown attempting to setData for node type");
		}
		catch(RuntimeException ignoreExpected)
		{
		}
		
		try
		{
			target.getData(nodeTypeTag);
			fail("Should have thrown attempting to getData for node type");
		}
		catch(RuntimeException ignoreExpected)
		{
		}
	}

	public void testJson() throws Exception
	{
		IdList goals = new IdList();
		goals.add(new BaseId(2));
		goals.add(new BaseId(5));
		
		IdList objectives = new IdList();
		objectives.add(new BaseId(7));
		objectives.add(new BaseId(9));
		
		IdList indicators = new IdList();
		indicators.add(new BaseId(23));
		indicators.add(new BaseId(422));

		ModelNodeId factorId = new ModelNodeId(2342);
		ConceptualModelFactor factor = new ConceptualModelFactor(factorId);
		factor.setLabel("JustAName");
		factor.setComment("This is a great comment");
		factor.setIndicators(indicators);
		factor.setGoals(goals);
		factor.setObjectives(objectives);
		ConceptualModelFactor got = (ConceptualModelFactor)ConceptualModelNode.createFromJson(factor.getType(), factor.toJson());
		assertEquals("wrong type?", factor.getNodeType(), got.getNodeType());
		assertEquals("wrong id?", factor.getId(), got.getId());
		assertEquals("wrong name?", factor.getLabel(), got.getLabel());
		assertEquals("wrong comment?", factor.getComment(), got.getComment());
		assertEquals("wrong indicator count?", factor.getIndicators().size(), got.getIndicators().size());
		assertEquals("wrong indicators?", factor.getIndicators(), got.getIndicators());
		assertEquals("wrong goals count?", factor.getGoals().size(), got.getGoals().size());
		assertEquals("wrong goals?", factor.getGoals(), got.getGoals());
		assertEquals("wrong objectives count?", factor.getObjectives().size(), got.getObjectives().size());
		assertEquals("wrong objectives?", factor.getObjectives(), got.getObjectives());
	}
}
