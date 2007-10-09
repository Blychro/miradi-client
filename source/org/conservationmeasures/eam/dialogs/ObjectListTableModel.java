/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.project.Project;

public class ObjectListTableModel extends ObjectTableModel
{
	public ObjectListTableModel(Project projectToUse, int objectType, BaseId objectId, 
			String idListFieldTag, int listedItemType, String tableColumnTag)
	{
		this(projectToUse, objectType, objectId, idListFieldTag, listedItemType, new String[] {tableColumnTag});
	}
	
	public ObjectListTableModel(Project projectToUse, int objectType, BaseId objectId, 
			String idListFieldTag, int listedItemType, String[] tableColumnTags)
	{
		super(projectToUse, listedItemType, tableColumnTags);
		containingObjectType = objectType;
		containingObjectId = objectId;
		tagOfIdList = idListFieldTag;

		setRowObjectIds(getLatestIdListFromProject());
	}
	
	public int getContainingObjectType()
	{
		return containingObjectType;
	}
	
	public BaseId getContainingObjectId()
	{
		return containingObjectId;
	}
	
	public String getFieldTag()
	{
		return tagOfIdList;
	}

	public BaseObject getContainingObject()
	{
		return project.findObject(containingObjectType, containingObjectId);
	}

	//TODO: the general model shold handle different list data types.
	public IdList getLatestIdListFromProject()
	{
		try
		{
			return new IdList(getContainingObject().getData(tagOfIdList));
		}
		catch(Exception e)
		{
			EAM.logException(e);
			throw new RuntimeException();
		}
	}
	
	int containingObjectType;
	BaseId containingObjectId;
	String tagOfIdList;
}
