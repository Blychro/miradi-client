/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.ids.KeyEcologicalAttributeId;
import org.conservationmeasures.eam.objectdata.IdListData;
import org.conservationmeasures.eam.objectdata.StringData;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.ObjectManager;
import org.conservationmeasures.eam.project.TNCViabilityFormula;
import org.conservationmeasures.eam.questions.KeyEcologicalAttributeTypeQuestion;
import org.conservationmeasures.eam.utils.CodeList;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

public class KeyEcologicalAttribute extends BaseObject
{
	public KeyEcologicalAttribute(ObjectManager objectManager, KeyEcologicalAttributeId idToUse)
	{
		super(objectManager, idToUse);
		clear();
	}

	public KeyEcologicalAttribute(KeyEcologicalAttributeId idToUse)
	{
		super(idToUse);
		clear();
	}
	
	public KeyEcologicalAttribute(ObjectManager objectManager, int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new BaseId(idAsInt), json);
	}
	
	public KeyEcologicalAttribute(int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(new BaseId(idAsInt), json);
	}
	
	void clear()
	{
		super.clear();
		indicatorIds = new IdListData();
		description = new StringData();
		keyEcologicalAttributeType = new StringData();
		psuedoViabilityStatus = new PsuedoStringData(PSEUDO_TAG_VIABILITY_STATUS);
		keyEcologicalAttributeTypeValue = new PseudoQuestionData(new KeyEcologicalAttributeTypeQuestion(TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE));
		
		addField(TAG_INDICATOR_IDS, indicatorIds);
		addField(TAG_DESCRIPTION, description);
		addField(TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE, keyEcologicalAttributeType);
		addField(PSEUDO_TAG_VIABILITY_STATUS, psuedoViabilityStatus);
		addField(PSEUDO_TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE_VALUE, keyEcologicalAttributeTypeValue);
	}
	
	public int getType()
	{
		return getObjectType();
	}

	public static int getObjectType()
	{
		return ObjectType.KEY_ECOLOGICAL_ATTRIBUTE;
	}
	
	
	public static boolean canOwnThisType(int type)
	{
		switch(type)
		{
			case ObjectType.INDICATOR: 
				return true;
			default:
				return false;
		}
	}
	
	
	public static boolean canReferToThisType(int type)
	{
		return false;
	}
	
	
	public ORefList getOwnedObjects(int objectType)
	{
		ORefList list = super.getOwnedObjects(objectType);
		
		switch(objectType)
		{
			case ObjectType.INDICATOR: 
				list.addAll(new ORefList(objectType, getIndicatorIds()));
		}
		return list;
	}
	

	public IdList getIndicatorIds()
	{
		return indicatorIds.getIdList();
	}
	
	public String getKeyEcologicalAttributeType()
	{
		return keyEcologicalAttributeType.toString();
	}

	public Object getPseudoData(String fieldTag)
	{
		if(fieldTag.equals(PSEUDO_TAG_VIABILITY_STATUS))
			return computeTNCViability();
		return super.getPseudoData(fieldTag);
	}
	
	public String computeTNCViability()
	{
		CodeList statuses = new CodeList();
		for(int i = 0; i < indicatorIds.size(); ++i)
		{
			String status = objectManager.getObjectData(ObjectType.INDICATOR, indicatorIds.get(i), Indicator.TAG_MEASUREMENT_STATUS);
			statuses.add(status);
		}
		return TNCViabilityFormula.getAverageRatingCode(statuses);
	}
	
	public static final String TAG_INDICATOR_IDS = "IndicatorIds";
	public static final String TAG_DESCRIPTION = "Description";
	public static final String TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE = "KeyEcologicalAttributeType";
	public static final String PSEUDO_TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE_VALUE = "KeyEcologicalAttributeTypeLabel";
	public static final String PSEUDO_TAG_VIABILITY_STATUS = "PseudoTagViabilityStatus";

	public static final String OBJECT_NAME = "Key Ecological Attribute";
	
	IdListData indicatorIds;
	StringData description;
	StringData keyEcologicalAttributeType;
	PseudoQuestionData keyEcologicalAttributeTypeValue;
	PsuedoStringData psuedoViabilityStatus;
}
