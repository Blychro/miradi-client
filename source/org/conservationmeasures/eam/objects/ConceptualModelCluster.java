/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.diagram.nodes.DiagramNode;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.ids.ModelNodeId;
import org.conservationmeasures.eam.objectdata.IdListData;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

public class ConceptualModelCluster extends ConceptualModelNode
{
	public ConceptualModelCluster(ModelNodeId idToUse)
	{
		super(idToUse, DiagramNode.TYPE_CLUSTER);
		memberIds = new IdListData();
	}

	public ConceptualModelCluster(ModelNodeId idToUse, EnhancedJsonObject json) throws Exception
	{
		super(idToUse, DiagramNode.TYPE_CLUSTER, json);
		memberIds = new IdListData(json.optString(TAG_MEMBER_IDS));
	}
	
	public boolean isCluster()
	{
		return true;
	}
	
	public IdList getMemberIds()
	{
		return memberIds.getIdList();
	}

	public String getData(String fieldTag)
	{
		if(fieldTag.equals(TAG_MEMBER_IDS))
			return memberIds.get();
		return super.getData(fieldTag);
	}

	public void setData(String fieldTag, String dataValue) throws Exception
	{
		if(fieldTag.equals(TAG_MEMBER_IDS))
			memberIds.set(dataValue);
		else
			super.setData(fieldTag, dataValue);
	}
	
	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject json = super.toJson();
		json.put(TAG_MEMBER_IDS, memberIds.get());
		return json;
	}

	public static final String TAG_MEMBER_IDS = "Members";
	
	IdListData memberIds;
}
