/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.utils;

import java.awt.Point;
import java.util.List;
import java.util.Vector;

public class PointList
{
	public PointList()
	{
		this(new Vector());
	}
	
	public PointList(PointList copyFrom)
	{
		this(new Vector(copyFrom.data));
	}
	
	public PointList(EnhancedJsonObject json) throws Exception
	{
		this();
		EnhancedJsonArray array = json.optJsonArray(TAG_POINTS);
		if(array == null)
			array = new EnhancedJsonArray();
		
		for(int i = 0; i < array.length(); ++i)
		{
			Point point = EnhancedJsonObject.convertToPoint(array.getString(i));
			add(point);
		}
	}
	
	public PointList(String listAsJsonString) throws Exception
	{
		this(new EnhancedJsonObject(listAsJsonString));
	}
	
	private PointList(List dataToUse)
	{
		data = new Vector(dataToUse);
	}
	
	public int size()
	{
		return data.size();
	}
	
	public void add(Point point)
	{
		data.add(point);
	}
		
	public Point get(int index)
	{
		return (Point)data.get(index);
	}
	
	public boolean contains(Point point)
	{
		return data.contains(point);
	}
	
	public int find(Point point)
	{
		return data.indexOf(point);
	}
	
	public void removePoint(Point point)
	{
		if(!data.contains(point))
			throw new RuntimeException("Attempted to remove non-existant point: " + point + " from: " + toString());
		
		data.remove(point);
	}
	
	public void subtract(PointList other)
	{
		for(int i = 0; i < other.size(); ++i)
		{
			Point point = other.get(i);
			if (contains(point))
				removePoint(point);
		}
	}
	
	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject json = new EnhancedJsonObject();
		EnhancedJsonArray array = new EnhancedJsonArray();
		for(int i = 0; i < size(); ++i)
		{
			String pointAsString = EnhancedJsonObject.convertFromPoint(get(i));
			array.put(pointAsString);
		}
		json.put(TAG_POINTS, array);

		return json;
	}
	
	public String toString()
	{
		if(size() == 0)
			return "";
		
		return toJson().toString();
	}
	
	public boolean equals(Object rawOther)
	{
		if(! (rawOther instanceof PointList))
			return false;
		
		PointList other = (PointList)rawOther;
		return data.equals(other.data);
	}
	
	public int hashCode()
	{
		return data.hashCode();
	}
		
	private static final String TAG_POINTS = "Points";

	Vector data;

}
