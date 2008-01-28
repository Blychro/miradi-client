/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.objectdata.ChoiceData;
import org.conservationmeasures.eam.objectdata.DateData;
import org.conservationmeasures.eam.objectdata.StringData;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.ObjectManager;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.questions.ChoiceItem;
import org.conservationmeasures.eam.questions.ProgressReportStatusQuestion;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

public class ProgressReport extends BaseObject
{
	public ProgressReport(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse);
		clear();
	}
		
	public ProgressReport(ObjectManager objectManager, int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new BaseId(idAsInt), json);
	}
	
	public int getType()
	{
		return getObjectType();
	}
	
	public String getTypeName()
	{
		return OBJECT_NAME;
	}

	public static int getObjectType()
	{
		return ObjectType.PROGRESS_REPORT;
	}
	
	public static boolean canOwnThisType(int type)
	{
		return false;
	}
	
	public boolean canHaveIndicators()
	{
		return false;
	}

	public String getDateAsString()
	{
		return progressDate.get();
	}
	
	public ORefList getOwnedObjects(int objectType)
	{
		return new ORefList();
	}
	
	public String toString()
	{
		return getDateAsString() + " " + getLabel();
	}
	
	public String getLabel()
	{
		return getProgressStatusChoice().getLabel();	
	}
	
	public ChoiceItem getProgressStatusChoice()
	{
		return new ProgressReportStatusQuestion().findChoiceByCode(progressStatus.get());
	}
	
	public static boolean is(ORef ref)
	{
		if (ref.getObjectType() == ObjectType.PROGRESS_REPORT)
			return true;
		
		return false;
	}
	
	public static ProgressReport find(ObjectManager objectManager, ORef progressReportRef)
	{
		return (ProgressReport) objectManager.findObject(progressReportRef);
	}
	
	public static ProgressReport find(Project project, ORef progressReportRef)
	{
		return find(project.getObjectManager(), progressReportRef);
	}
	
	void clear()
	{
		super.clear();
		
		progressStatus = new ChoiceData();
		progressDate = new DateData(TAG_PROGRESS_DATE);
		comments = new StringData();
		
		addField(TAG_PROGRESS_STATUS, progressStatus);
		addField(TAG_PROGRESS_DATE, progressDate);
		addField(TAG_COMMENTS, comments);
	}
	
	public static final String OBJECT_NAME = "ProgressReport";
	
	public static final String TAG_PROGRESS_STATUS = "ProgressStatus";
	public static final String TAG_PROGRESS_DATE = "ProgressDate";
	public static final String TAG_COMMENTS = "Comments";
	
	private ChoiceData progressStatus;
	private DateData progressDate;
	private StringData comments;
}
