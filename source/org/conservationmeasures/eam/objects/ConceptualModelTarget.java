/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.diagram.nodes.DiagramNode;
import org.conservationmeasures.eam.diagram.nodetypes.NodeTypeTarget;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.ModelNodeId;
import org.json.JSONObject;


public class ConceptualModelTarget extends ConceptualModelNode
{
	public ConceptualModelTarget(BaseId idToUse)
	{
		super(idToUse, DiagramNode.TYPE_TARGET);
	}
	
	public ConceptualModelTarget(ModelNodeId idToUse, JSONObject json)
	{
		super(idToUse, DiagramNode.TYPE_TARGET, json);
	}

	public boolean isTarget()
	{
		return true;
	}
	
	public boolean canHaveGoal()
	{
		return true;
	}

	public JSONObject toJson()
	{
		JSONObject json = createBaseJsonObject(NodeTypeTarget.TARGET_TYPE);
		return json;
	}

}
