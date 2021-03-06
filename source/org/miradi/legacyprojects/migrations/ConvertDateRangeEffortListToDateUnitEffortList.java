/* 
Copyright 2005-2018, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 
package org.miradi.legacyprojects.migrations;

import java.io.File;
import java.text.DecimalFormat;


import org.martus.util.MultiCalendar;
import org.miradi.ids.BaseId;
import org.miradi.legacyprojects.DataUpgrader;
import org.miradi.legacyprojects.JSONFile;
import org.miradi.legacyprojects.ObjectManifest;
import org.miradi.utils.EnhancedJsonArray;
import org.miradi.utils.EnhancedJsonObject;

public class ConvertDateRangeEffortListToDateUnitEffortList
{
	public static void convertToDateUnitEffortList() throws Exception
	{
		final int RESOURCE_ASSIGNMENT_TYPE = 14;
		convertAssingmentDateRangeEffortList(RESOURCE_ASSIGNMENT_TYPE);
		
		final int EXPENSE_ASSIGNMENT_TYPE = 51;
		convertAssingmentDateRangeEffortList(EXPENSE_ASSIGNMENT_TYPE);
	}

	private static void convertAssingmentDateRangeEffortList(int assignmentType) throws Exception
	{
		File jsonDir = DataUpgrader.getTopJsonDir();
		File assignmentDir = DataUpgrader.getObjectsDir(jsonDir, assignmentType);
		if (! assignmentDir.exists())
			return;
		
		File assignmentManifestFile = new File(assignmentDir, "manifest");
		if (! assignmentManifestFile.exists())
			return;
	
		ObjectManifest assignmentManifestObject = new ObjectManifest(JSONFile.read(assignmentManifestFile));
		BaseId[] assignmentIds = assignmentManifestObject.getAllKeys();
		for (int index = 0; index < assignmentIds.length; ++index)
		{
			BaseId thisAssignmentId = assignmentIds[index];
			File assignmentFile = new File(assignmentDir, Integer.toString(thisAssignmentId.asInt()));
			EnhancedJsonObject assignmentJson = DataUpgrader.readFile(assignmentFile);
			
			EnhancedJsonObject dateUnitEffortList = new EnhancedJsonObject();
			String detailsAsString = assignmentJson.optString("Details");
			if (detailsAsString.length() > 0)
			{
				EnhancedJsonObject detailsJson = new EnhancedJsonObject(detailsAsString);
				EnhancedJsonArray dateRangeEfforts = detailsJson.getJsonArray("DateRangeEfforts");
				dateUnitEffortList = convertToDateUnitEffortList(dateRangeEfforts);
			}
			
			assignmentJson.put("Details", dateUnitEffortList);
			DataUpgrader.writeJson(assignmentFile, assignmentJson);
		}
	}

	private static EnhancedJsonObject convertToDateUnitEffortList(EnhancedJsonArray dateRangeEfforts) throws Exception
	{
		EnhancedJsonArray dateUnitEffortsArray = new EnhancedJsonArray();
		EnhancedJsonObject dateUnitEffortList = new EnhancedJsonObject();
		for (int index = 0; index < dateRangeEfforts.length(); ++index)
		{
			EnhancedJsonObject dateRangeEffort = dateRangeEfforts.getJson(index);
			String dateRangeAsString = dateRangeEffort.getString("DateRange");
			String numberOfUnits = dateRangeEffort.getString("NumberOfUnits");
			
			EnhancedJsonObject dateUnitEffortJson = new EnhancedJsonObject();
			EnhancedJsonObject dateUnitJson = toDateRange(new EnhancedJsonObject(dateRangeAsString));
			dateUnitEffortJson.put("DateUnit", dateUnitJson);
			dateUnitEffortJson.put("NumberOfUnits", numberOfUnits);
			
			dateUnitEffortsArray.put(dateUnitEffortJson);
		}
		
		dateUnitEffortList.put("DateUnitEfforts", dateUnitEffortsArray);
		
		return dateUnitEffortList;
	}

	private static EnhancedJsonObject toDateRange(EnhancedJsonObject dateRangeJson)
	{		
		String startIsoDate = dateRangeJson.getString("StartDate");
		String endIsoDate = dateRangeJson.getString("EndDate");
		MultiCalendar startDate = MultiCalendar.createFromIsoDateString(startIsoDate);
		MultiCalendar endDate = MultiCalendar.createFromIsoDateString(endIsoDate);
		EnhancedJsonObject dateUnitJson = new EnhancedJsonObject();
		dateUnitJson.put("DateUnitCode", createDateUnitCodeFromDateRange(startDate, endDate));
		
		return dateUnitJson;
	}
	
	//NOTE: that this is duplicated, from DateUnit and DateRange. 
	//It was duplicated so that any changes there do not have an affect here.  
	//Future fixes in original code do not apply here because this is a snap shot of 
	//the original on the day the migration was written.   
	public static String createDateUnitCodeFromDateRange(MultiCalendar startDate, MultiCalendar endDate)
	{
		String startIso = startDate.toIsoDateString();
		String yearString = startIso.substring(0, 4);
		int startingYear = startDate.getGregorianYear();
		int startingMonth = startDate.getGregorianMonth();

		if(isDay(startIso, endDate.toIsoDateString()))
			return startIso;
		
		if(isMonth(startDate, endDate))
			return startIso.substring(0, 7);
		
		if(isQuarter(startDate, endDate))
		{
			int startingQuarter = (startingMonth - 1) / 3 + 1;
			return yearString + "Q" + startingQuarter;
		}
		
		if(isYear(startDate, endDate))
			return createFiscalYear(startingYear, startingMonth);
		
		return "";
	}
	
	public static String createFiscalYear(Integer startingYear, int startingMonth)
	{
		return YEAR_PREFIX + asFourDigitString(startingYear) + "-" + asTwoDigitString(startingMonth);
	}
	
	private static String asTwoDigitString(int numberToFormat)
	{
		return new DecimalFormat("00").format(numberToFormat);
	}

	private static String asFourDigitString(int numberToFormat)
	{
		return new DecimalFormat("0000").format(numberToFormat);
	}
	
	public static boolean isDay(String startDate, String endDate)
	{
		return (startDate.equals(endDate));
	}
	
	public static boolean isMonth(MultiCalendar startDate, MultiCalendar endDate)
	{
		if(!isDay1(startDate))
			return false;
		
		return isEndDateAfterStartByMonths(1, startDate, endDate);
	}

	public static boolean isQuarter(MultiCalendar startDate, MultiCalendar endDate)
	{
		if(!isStartOnQuarter(startDate))
			return false;
		
		return isEndDateAfterStartByMonths(3, startDate, endDate);
	}
	
	public static boolean isYear(MultiCalendar startDate, MultiCalendar endDate)
	{
		if(!isStartOnQuarter(startDate))
			return false;
		
		return isEndDateAfterStartByMonths(12, startDate, endDate);
	}
	
	private static boolean isStartOnQuarter(MultiCalendar startDate)
	{
		if(!isDay1(startDate))
			return false;
		
		int startMonthIndex = startDate.getGregorianMonth() - 1;
		return (startMonthIndex % 3 == 0);
	}
	
	private static boolean isEndDateAfterStartByMonths(int monthDelta, MultiCalendar startDate, MultiCalendar endDate)
	{
		MultiCalendar nextDate = new MultiCalendar(endDate);
		nextDate.addDays(1);
		if(startDate.getGregorianDay() != nextDate.getGregorianDay())
			return false;
		
		int expectedNextMonth = startDate.getGregorianMonth() + monthDelta;
		int expectedNextYear = startDate.getGregorianYear();
		if(expectedNextMonth > 12)
		{
			expectedNextMonth -= 12;
			++expectedNextYear;
		}
		if(nextDate.getGregorianYear() != expectedNextYear)
			return false;
		
		if(nextDate.getGregorianMonth() != expectedNextMonth)
			return false;
		
		return true;
	}

	private static boolean isDay1(MultiCalendar date)
	{
		return date.getGregorianDay() == 1;
	}
	
	private static final String YEAR_PREFIX = "YEARFROM:";
}
