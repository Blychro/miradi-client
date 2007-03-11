/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.ids;

import java.text.ParseException;
import java.util.List;
import java.util.Vector;

import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.utils.EnhancedJsonArray;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;
import org.json.JSONArray;

public class IdList
{
	public IdList()
	{
		this(new Vector());
	}
	
	public IdList(IdList copyFrom)
	{
		this(new Vector(copyFrom.data));
	}
	
	public IdList(BaseId[] ids)
	{
		this();
		for(int i = 0; i < ids.length; ++i)
			add(ids[i]);
	}
	
	public IdList(EnhancedJsonObject json)
	{
		this();
		EnhancedJsonArray array = json.optJsonArray(TAG_IDS);
		if(array == null)
			array = new EnhancedJsonArray();
		for(int i = 0; i < array.length(); ++i)
			add(new BaseId(array.getInt(i)));
		
	}
	
	public IdList(String listAsJsonString) throws ParseException
	{
		this(new EnhancedJsonObject(listAsJsonString));
	}
	
	private IdList(List dataToUse)
	{
		data = new Vector(dataToUse);
	}
	
	public int size()
	{
		return data.size();
	}
	
	public void clear()
	{
		data.clear();
	}
	
	public boolean isEmpty()
	{
		return (size() == 0);
	}
	
	public void add(BaseId id)
	{
		data.add(id);
	}
	
	public void add(int id)
	{
		add(new BaseId(id));
	}
	
	public void addAll(IdList otherList)
	{
		for(int i = 0; i < otherList.size(); ++i)
			add(otherList.get(i));
	}
	
	public void insertAt(BaseId id, int at)
	{
		data.insertElementAt(id, at);
	}
	
	public BaseId get(int index)
	{
		return (BaseId)data.get(index);
	}
	
	public boolean contains(BaseId id)
	{
		return data.contains(id);
	}
	
	public int find(BaseId id)
	{
		return data.indexOf(id);
	}
	
	public void removeId(BaseId id)
	{
		if(!data.contains(id))
			throw new RuntimeException("Attempted to remove non-existant Id: " + id + " from: " + toString());
		data.remove(id);
	}
	
	public void subtract(IdList other)
	{
		for(int i = 0; i < other.size(); ++i)
		{
			BaseId id = other.get(i);
			if(contains(id))
				removeId(id);
		}
	}
	
	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject json = new EnhancedJsonObject();
		JSONArray array = new JSONArray();
		for(int i = 0; i < size(); ++i)
			array.appendInt(get(i).asInt());
		json.put(TAG_IDS, array);
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
		if(! (rawOther instanceof IdList))
			return false;
		
		IdList other = (IdList)rawOther;
		return data.equals(other.data);
	}
	
	public int hashCode()
	{
		return data.hashCode();
	}
	
	public IdList createClone()
	{
		return new IdList(this);
	}
	
	static public IdList extractNewlyAddedIds(CommandExecutedEvent event)
	{
		try
		{
			CommandSetObjectData cmd = (CommandSetObjectData)event.getCommand();
			IdList oldIdList = new IdList(cmd.getPreviousDataValue());
			IdList newIdList = new IdList(cmd.getDataValue());
			newIdList.subtract(oldIdList);
			return newIdList;
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return new IdList();
		}
	}
	
	private static final String TAG_IDS = "Ids";

	Vector data;

}
