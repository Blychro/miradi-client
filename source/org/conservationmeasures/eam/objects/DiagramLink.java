/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objects;

import java.awt.Point;
import java.util.Set;
import java.util.Vector;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.DiagramFactorLinkId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.objectdata.BaseIdData;
import org.conservationmeasures.eam.objectdata.ORefListData;
import org.conservationmeasures.eam.objectdata.PointListData;
import org.conservationmeasures.eam.objecthelpers.CreateDiagramFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.CreateObjectParameter;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.ObjectManager;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;
import org.conservationmeasures.eam.utils.PointList;

public class DiagramLink extends BaseObject
{
	public DiagramLink(ObjectManager objectManager, BaseId idToUse, CreateDiagramFactorLinkParameter extraInfo) throws Exception
	{
		super(objectManager, new DiagramFactorLinkId(idToUse.asInt()));
		
		setData(TAG_WRAPPED_ID, extraInfo.getFactorLinkId().toString());
		setData(TAG_FROM_DIAGRAM_FACTOR_ID, extraInfo.getFromFactorId().toString());
		setData(TAG_TO_DIAGRAM_FACTOR_ID, extraInfo.getToFactorId().toString());
	}
	
	public DiagramLink(ObjectManager objectManager, int idToUse, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new DiagramFactorLinkId(idToUse), json);
		
		underlyingObjectId.setId(json.getId(TAG_WRAPPED_ID));
		fromId.setId(json.getId(TAG_FROM_DIAGRAM_FACTOR_ID));
		toId.setId(json.getId(TAG_TO_DIAGRAM_FACTOR_ID));
	}
	
	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject json = super.toJson();
		json.putId(TAG_WRAPPED_ID, underlyingObjectId.getId());
		json.putId(TAG_FROM_DIAGRAM_FACTOR_ID, fromId.getId());
		json.putId(TAG_TO_DIAGRAM_FACTOR_ID, toId.getId());
		
		return json;
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
		return ObjectType.DIAGRAM_LINK;
	}
	
	public ORefList getAllObjectsToDeepCopy()
	{
		ORefList deepObjectRefsToCopy = super.getAllObjectsToDeepCopy();
		deepObjectRefsToCopy.addAll(getGroupedDiagramLinkRefs());
		
		return deepObjectRefsToCopy;
	}
	
	public static boolean canOwnThisType(int type)
	{
		return false;
	}
	
	
	public static boolean canReferToThisType(int type)
	{
		switch(type)
		{
			case ObjectType.FACTOR_LINK: 
				return true;
			case ObjectType.DIAGRAM_FACTOR: 
				return true;
			default:
				return false;
		}
	}
	
	public Set<String> getReferencedObjectTags()
	{
		Set<String> set = super.getReferencedObjectTags();
		set.add(TAG_FROM_DIAGRAM_FACTOR_ID);
		set.add(TAG_TO_DIAGRAM_FACTOR_ID);
		set.add(TAG_WRAPPED_ID);
		set.add(TAG_GROUPED_DIAGRAM_LINK_REFS);
		
		return set;
	}
	
	public ORef getDiagramFactorRef(int direction)
	{
		if(direction == FactorLink.FROM)
			return getFromDiagramFactorRef();
		if(direction == FactorLink.TO)
			return getToDiagramFactorRef();
		throw new RuntimeException("Unknown direction: " + direction);
	}
	
	public ORef getOppositeDiagramFactorRef(int direction)
	{
		if(direction == FactorLink.FROM)
			return getToDiagramFactorRef();
		if(direction == FactorLink.TO)
			return getFromDiagramFactorRef();
		throw new RuntimeException("Unknown direction: " + direction);
	}
	
	public DiagramFactor getDiagramFactor(int direction)
	{
		return DiagramFactor.find(objectManager, getDiagramFactorRef(direction));
	}
	
	public DiagramFactor getOppositeDiagramFactor(int direction)
	{
		return DiagramFactor.find(objectManager, getOppositeDiagramFactorRef(direction));
	}
	
	public ORef getFromDiagramFactorRef()
	{
		return new ORef(DiagramFactor.getObjectType(), getFromDiagramFactorId());
	}
	
	public ORef getToDiagramFactorRef()
	{
		return new ORef(DiagramFactor.getObjectType(), getToDiagramFactorId());
	}
	
	public DiagramFactorId getFromDiagramFactorId()
	{
		return new DiagramFactorId(fromId.getId().asInt());
	}
	
	public DiagramFactorId getToDiagramFactorId()
	{
		return new DiagramFactorId(toId.getId().asInt());
	}

	public DiagramFactorLinkId getDiagramLinkageId()
	{
		return (DiagramFactorLinkId)getId(); 
	}
	
	public ORefList getGroupedDiagramLinkRefs()
	{
		return groupedDiagramLinkRefs.getORefList();
	}
	
	public ORef getWrappedRef()
	{
		return getUnderlyingLink().getRef();
	}
	
	public FactorLinkId getWrappedId()
	{
		return new FactorLinkId(underlyingObjectId.getId().asInt());
	}
	
	public int getAnnotationType(String tag)
	{
		if (tag.equals(TAG_GROUPED_DIAGRAM_LINK_REFS))
			return DiagramLink.getObjectType();
		
		return super.getAnnotationType(tag);
	}
	
	public boolean isRefList(String tag)
	{
		if (tag.equals(TAG_GROUPED_DIAGRAM_LINK_REFS))
			return true;
		
		return super.isRefList(tag);
	}
	
	public boolean alsoLinksOurFromOrTo(DiagramLink otherDiagramLink)
	{
		if (isToOrFrom(otherDiagramLink.getFromDiagramFactorRef()))
			return true;
		
		if (isToOrFrom(otherDiagramLink.getToDiagramFactorRef()))
			return true;
		
		return false;
	}
	
	public boolean isToOrFrom(DiagramFactor diagramFactor)
	{
		return isToOrFrom(diagramFactor.getRef());
	}
	
	public boolean isToOrFrom(ORef diagramFactorRef)
	{
		if (getToDiagramFactorRef().equals(diagramFactorRef))
			return true;
		
		if (getFromDiagramFactorRef().equals(diagramFactorRef))
			return true;
		
		return false;
	}
	
	public boolean isTargetLink()
	{
		if (getUnderlyingLink() != null)
			return getUnderlyingLink().isTargetLink();
		
		FactorLink[] factorLinkChildren = getSelfOrGroupBoxUnderlyingChildren();
		for (int i = 0; i < factorLinkChildren.length; ++i)
		{
			if (factorLinkChildren[i].isTargetLink())
				return true;
		}
		
		return false;
	}
	
	public boolean isBidirectional()
	{
		if (getUnderlyingLink() != null)
			return getUnderlyingLink().isBidirectional();
		
		if (getGroupedDiagramLinkRefs().size() == 0)
			return false;
		
		ORef diagramLinkRef = getGroupedDiagramLinkRefs().getRefForType(DiagramLink.getObjectType());
		DiagramLink diagramLink = DiagramLink.find(getProject(), diagramLinkRef);
		return diagramLink.isBidirectional();
	}
	
	public FactorLink getUnderlyingLink()
	{
		return (FactorLink)getProject().findObject(new ORef(FactorLink.getObjectType(), underlyingObjectId.getId()));
	}
	
	public String getData(String fieldTag)
	{
		if(fieldTag.equals(TAG_WRAPPED_ID))
			return underlyingObjectId.get();
		if(fieldTag.equals(TAG_FROM_DIAGRAM_FACTOR_ID))
			return fromId.get();
		if(fieldTag.equals(TAG_TO_DIAGRAM_FACTOR_ID))
			return toId.get();
		
		return super.getData(fieldTag);
	}

	public boolean bendPointAlreadyExists(Point location)
	{
		if (location == null)
			return false;
		
		return getBendPoints().contains(location);
	}

	public ORefList getSelfOrChildren()
	{
		if (isGroupBoxLink())
			return getGroupedDiagramLinkRefs();
		
		return new ORefList(getRef());
	}
	
	public boolean isGroupBoxLink()
	{
		if (DiagramFactor.find(getProject(), getFromDiagramFactorRef()).isGroupBoxFactor())
			return true;
		
		if (DiagramFactor.find(getProject(), getToDiagramFactorRef()).isGroupBoxFactor())
			return true;
		
		return false;
	}
	
	public String getToolTipString() 
	{
		DiagramFactor fromDiagramFactor = DiagramFactor.find(getProject(), getFromDiagramFactorRef());
		DiagramFactor toDiagramFactor = DiagramFactor.find(getProject(), getToDiagramFactorRef());
		Factor fromFactor = Factor.findFactor(getProject(), fromDiagramFactor.getWrappedORef());
		Factor toFactor = Factor.findFactor(getProject(), toDiagramFactor.getWrappedORef());
		String toolTipText = "<html><b>From : " + fromFactor.getLabel() + "</b><BR>" +
				           		   "<b>To : " + toFactor.getLabel() + "</b>";
		
		String[] calculatedRatings = getRelevantStressesAsHTML();
		if (calculatedRatings.length == 0)
			return toolTipText;
		
		String header = "Stresses:";
		toolTipText += "<hr>" + header + "<ul>";
		for (int i = 0; i < calculatedRatings.length; ++i)
		{
			toolTipText += calculatedRatings[i];
		}

		return toolTipText;
	}
	
	private String[] getRelevantStressesAsHTML()
	{
		String[] stressNames = getRelevantStressNames();
		String[] StressNamesAsHTML = new String[stressNames.length];
		for (int i = 0; i < stressNames.length; ++i)
		{
			StressNamesAsHTML[i] = "<li>" + stressNames[i] + "</li>";
		}
		
		return StressNamesAsHTML;
	}
	
	public String[] getRelevantStressNames()
	{
		Vector<String> allStressNames = new Vector();
		FactorLink[] factorLinks = getSelfOrGroupBoxUnderlyingChildren();
		for (int i = 0; i < factorLinks.length; ++i)
		{
			allStressNames.addAll(getStressNames(factorLinks[i]));
		}
		
		return allStressNames.toArray(new String[0]);
	}

	private FactorLink[] getSelfOrGroupBoxUnderlyingChildren()
	{
		if (getUnderlyingLink() != null)
			return new FactorLink[] {getUnderlyingLink()};
		
		Vector<FactorLink> allChildrenFactorLinks = new Vector();
		ORefList childLinkRefs = getGroupedDiagramLinkRefs();
		for (int i = 0; i < childLinkRefs.size(); ++i)
		{
			DiagramLink diagramLink = DiagramLink.find(getProject(), childLinkRefs.get(i));
			allChildrenFactorLinks.add(diagramLink.getUnderlyingLink());
		}
		
		return allChildrenFactorLinks.toArray(new FactorLink[0]);
	}

	private Vector<String> getStressNames(FactorLink factorLink)
	{
		ORefList threatStressRatingRefs = factorLink.getThreatStressRatingRefs();
	
		Vector<String> stressNames = new Vector();
		for(int i = 0; i < threatStressRatingRefs.size(); ++i)
		{
			ThreatStressRating threatStressRating = ThreatStressRating.find(getProject(), threatStressRatingRefs.get(i));
			Stress stress = Stress.find(getProject(), threatStressRating.getStressRef());
			if (threatStressRating.isActive())
				stressNames.add(stress.toString());
		}
		
		return stressNames;
	}
	
	public CreateObjectParameter getCreationExtraInfo()
	{
		return new CreateDiagramFactorLinkParameter(
				new FactorLinkId(underlyingObjectId.getId().asInt()), 
				new DiagramFactorId(fromId.getId().asInt()), 
				new DiagramFactorId(toId.getId().asInt()));
	}
	
	public PointList getBendPoints()
	{
		return bendPoints.getPointList();
	}
	
	public boolean isCoveredByGroupBoxLink()
	{
		ORefList groupBoxLinks = findObjectsThatReferToUs(DiagramLink.getObjectType());
		return (groupBoxLinks.size() > 0);
	}

	public static DiagramLink find(ObjectManager objectManager, ORef diagramLinkRef)
	{
		return (DiagramLink) objectManager.findObject(diagramLinkRef);
	}
	
	public static DiagramLink find(Project project, ORef diagramLinkRef)
	{
		return find(project.getObjectManager(), diagramLinkRef);
	}
		
	void clear()
	{
		super.clear();

		underlyingObjectId = new BaseIdData(TAG_WRAPPED_ID, FactorLink.getObjectType());
		fromId = new BaseIdData(TAG_FROM_DIAGRAM_FACTOR_ID, DiagramFactor.getObjectType());
		toId = new BaseIdData(TAG_TO_DIAGRAM_FACTOR_ID, DiagramFactor.getObjectType());
		bendPoints = new PointListData(TAG_BEND_POINTS);
		groupedDiagramLinkRefs = new ORefListData(TAG_GROUPED_DIAGRAM_LINK_REFS);
		
		addNoClearField(TAG_WRAPPED_ID, underlyingObjectId);
		addNoClearField(TAG_FROM_DIAGRAM_FACTOR_ID, fromId);
		addNoClearField(TAG_TO_DIAGRAM_FACTOR_ID, toId);
		addField(TAG_BEND_POINTS, bendPoints);
		addField(TAG_GROUPED_DIAGRAM_LINK_REFS, groupedDiagramLinkRefs);
	}
	
	public static final String TAG_WRAPPED_ID = "WrappedLinkId";
	public static final String TAG_FROM_DIAGRAM_FACTOR_ID = "FromDiagramFactorId";
	public static final String TAG_TO_DIAGRAM_FACTOR_ID = "ToDiagramFactorId";
	public static final String TAG_BEND_POINTS = "BendPoints";
	public static final String TAG_GROUPED_DIAGRAM_LINK_REFS = "GroupedDiagramLinkRefs";
	
	static final String OBJECT_NAME = "DiagramLink";
	
	private BaseIdData underlyingObjectId;
	private BaseIdData fromId;
	private BaseIdData toId;
	private PointListData bendPoints;
	private ORefListData groupedDiagramLinkRefs;
}
