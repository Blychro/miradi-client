/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objectdata;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.utils.InvalidDateException;
import org.martus.util.MultiCalendar;

public class DateData extends ObjectData
{
	public DateData()
	{
		date = null;
	}

	public DateData(String valueToUse)
	{
		this();
		try
		{
			set(valueToUse);
		}
		catch (Exception e)
		{
			EAM.logDebug("DateData ignoring invalid: " + valueToUse);
		}
	}

	public void set(String newValue) throws Exception
	{
		if(newValue.length() == 0)
		{
			date = null;
			return;
		}
		
		try
		{
			date = MultiCalendar.createFromIsoDateString(newValue);
		}
		catch (Exception e)
		{
			throw new InvalidDateException(e);
		}
	}

	public String get()
	{
		if(date == null)
			return "";
		return date.toIsoDateString();
	}

	public boolean equals(Object rawOther)
	{
		if(!(rawOther instanceof DateData))
			return false;
		
		DateData other = (DateData)rawOther;
		return date.equals(other.date);
	}

	public int hashCode()
	{
		return date.hashCode();
	}
	
	MultiCalendar date;
}
