/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram.nodes;

import java.awt.Color;

public class DiagramFactor extends DiagramNode
{
	public DiagramFactor(ConceptualModelFactor cmFactor)
	{
		super(cmFactor);
	}

	public Color getColor()
	{
		if(isIndirectFactor())
			return COLOR_INDIRECT_FACTOR;
		if(isDirectThreat())
			return COLOR_DIRECT_THREAT;
		if(isStress())
			return COLOR_STRESS;
		
		throw new RuntimeException("Unknown factor type: " + getType().getClass());
	}
	
	public static final Color COLOR_STRESS = new Color(204, 153, 255);
	public static final Color COLOR_DIRECT_THREAT = new Color(255, 153, 255);
	public static final Color COLOR_INDIRECT_FACTOR = new Color(255, 153, 0);

}
