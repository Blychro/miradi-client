/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.objectdata.DateData;
import org.conservationmeasures.eam.objectdata.IntegerData;
import org.conservationmeasures.eam.objectdata.NumberData;
import org.conservationmeasures.eam.objectdata.StringData;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.ObjectManager;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

public class ProjectMetadata extends BaseObject
{
	public ProjectMetadata(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse);
		clear();
	}

	public ProjectMetadata(BaseId idToUse)
	{
		super(idToUse);
		clear();
	}
	
	public ProjectMetadata(ObjectManager objectManager, int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new BaseId(idAsInt), json);
	}
	
	
	public ProjectMetadata(int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(new BaseId(idAsInt), json);
	}
	
	public int getType()
	{
		return getObjectType();
	}

	public static int getObjectType()
	{
		return ObjectType.PROJECT_METADATA;
	}
	
	
	public static boolean canOwnThisType(int type)
	{
		return false;
	}
	
	
	public static boolean canReferToThisType(int type)
	{
		return false;
	}
	
	
	public String getProjectName()
	{
		return projectName.get();
	}
	
	public String getProjectScope()
	{
		return projectScope.get();
	}
	
	public String getShortProjectScope()
	{
		return shortProjectScope.get();
	}
	
	public String getProjectVision()
	{
		return projectVision.get();
	}
	
	public String getShortProjectVision()
	{
		return shortProjectVision.get();
	}
	
	public String getStartDate()
	{
		return startDate.get();
	}
	
	public String getExpectedEndDate()
	{
		return expectedEndDate.get();
	}
	
	public String getEffectiveDate()
	{
		return effectiveDate.get();
	}
	
	public String getSizeInHectares()
	{
		return sizeInHectares.get();
	}
	
	public int getCurrencyDecimalPlaces()
	{
		return currencyDecimalPlaces.asInt();
	}

	void clear()
	{
		super.clear();
		projectName = new StringData();
		projectScope = new StringData();
		shortProjectScope = new StringData();
		projectVision = new StringData();
		shortProjectVision = new StringData();
		startDate = new DateData();
		expectedEndDate = new DateData();
		effectiveDate = new DateData();
		sizeInHectares = new NumberData();
		currencyDecimalPlaces = new IntegerData();
		
		addField(TAG_PROJECT_NAME, projectName);
		addField(TAG_PROJECT_SCOPE, projectScope);
		addField(TAG_SHORT_PROJECT_SCOPE, shortProjectScope);
		addField(TAG_PROJECT_VISION, projectVision);
		addField(TAG_SHORT_PROJECT_VISION, shortProjectVision);
		addField(TAG_START_DATE, startDate);
		addField(TAG_EXPECTED_END_DATE, expectedEndDate);
		addField(TAG_DATA_EFFECTIVE_DATE, effectiveDate);
		addField(TAG_TNC_SIZE_IN_HECTARES, sizeInHectares);
		addField(TAG_CURRENCY_DECIMAL_PLACES, currencyDecimalPlaces);
		
		
		tncLessonsLearned = new StringData();
		tncWorkbookVersionNumber = new StringData();
		tncWorkbookVersionDate = new DateData();
		tncDatabaseDownloadDate = new DateData();
		tncPlanningTeamComment = new StringData();
		tncEcoregion = new StringData();
		tncCountry = new StringData();
		tncOperatingUnits = new StringData();
		
		addField(TAG_TNC_LESSONS_LEARNED, tncLessonsLearned);
		addField(TAG_TNC_WORKBOOK_VERSION_NUMBER, tncWorkbookVersionNumber);
		addField(TAG_TNC_WORKBOOK_VERSION_DATE, tncWorkbookVersionDate);
		addField(TAG_TNC_DATABASE_DOWNLOAD_DATE, tncDatabaseDownloadDate);
		addField(TAG_TNC_PLANNING_TEAM_COMMENT, tncPlanningTeamComment);
		addField(TAG_TNC_ECOREGION, tncEcoregion);
		addField(TAG_TNC_COUNTRY, tncCountry);
		addField(TAG_TNC_OPERATING_UNITS, tncOperatingUnits);
	}

	public static final String TAG_PROJECT_NAME = "ProjectName";
	public static final String TAG_PROJECT_SCOPE = "ProjectScope";
	public static final String TAG_SHORT_PROJECT_SCOPE = "ShortProjectScope";
	public static final String TAG_PROJECT_VISION = "ProjectVision";
	public static final String TAG_SHORT_PROJECT_VISION = "ShortProjectVision";
	public static final String TAG_START_DATE = "StartDate";
	public static final String TAG_EXPECTED_END_DATE = "ExpectedEndDate";
	public static final String TAG_DATA_EFFECTIVE_DATE = "DataEffectiveDate";
	public static final String TAG_CURRENCY_DECIMAL_PLACES = "CurrencyDecimalPlaces";
	
	public static final String PSEUDO_TAG_PROJECT_FILENAME = "PseudoTagProjectFilename";
	
	public static final String TAG_TNC_LESSONS_LEARNED = "TNC.LessonsLearned";
	public static final String TAG_TNC_WORKBOOK_VERSION_NUMBER = "TNC.WorkbookVersionNumber";
	public static final String TAG_TNC_WORKBOOK_VERSION_DATE = "TNC.WorkbookVersionDate";
	public static final String TAG_TNC_DATABASE_DOWNLOAD_DATE = "TNC.DatabaseDownloadDate";
	public static final String TAG_TNC_PLANNING_TEAM_COMMENT = "TNC.PlanningTeamComment";
	public static final String TAG_TNC_SIZE_IN_HECTARES = "TNC.SizeInHectares";
	public static final String TAG_TNC_ECOREGION = "TNC.Ecoregion";
	public static final String TAG_TNC_COUNTRY = "TNC.Country";
	public static final String TAG_TNC_OPERATING_UNITS = "TNC.OperatingUnits";


	StringData projectName;
	StringData projectScope;
	StringData shortProjectScope;
	StringData projectVision;
	StringData shortProjectVision;
	DateData startDate;
	DateData expectedEndDate;
	DateData effectiveDate;
	NumberData sizeInHectares;
	IntegerData currencyDecimalPlaces;
	
	StringData tncLessonsLearned;
	StringData tncWorkbookVersionNumber;
	DateData tncWorkbookVersionDate;
	DateData tncDatabaseDownloadDate;
	StringData tncPlanningTeamComment;
	StringData tncEcoregion;
	StringData tncCountry;
	StringData tncOperatingUnits;
}
