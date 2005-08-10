/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */

package org.conservationmeasures.eam.diagram.nodes;

import java.awt.Point;

public class NodeData 
{
	public NodeData(EAMGraphCell cell) throws Exception
	{
		if(!cell.isNode())
			throw new Exception("EAMGraphCell not a Node");
		id = cell.getId();
		text = cell.getText();
		location = cell.getLocation();
	}
	
	public String getText()
	{
		return text;
	}
	
	public Point getLocation()
	{
		return location;
	}
	
	public int getId()
	{
		return id;
	}
	
	private String text;
	private Point location;
	private int id;
}
