/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.viability;

import java.util.Vector;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Target;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.questions.StatusQuestion;
import org.conservationmeasures.eam.views.TreeTableNode;

public class ViabilityProjectNode extends TreeTableNode
{
	public ViabilityProjectNode(Project projectToUse)
	{
		project = projectToUse;
		statusQuestion = new StatusQuestion(Target.TAG_TARGET_STATUS);
		rebuild();
	}
	
	public BaseObject getObject()
	{
		return null;
	}

	public TreeTableNode getChild(int index)
	{
		return (TreeTableNode)children.get(index);
	}

	public int getChildCount()
	{
		return children.size();
	}

	public ORef getObjectReference()
	{
		return new ORef(ObjectType.FAKE, new BaseId(Target.getObjectType()));
	}
	
	public int getType()
	{
		return ObjectType.FAKE;
	}

	public Object getValueAt(int column)
	{
		if (ViabilityTreeModel.columnTags[column].equals("Status"))
		{
			String code = Target.computeTNCViability(getTargetList());
			return statusQuestion.findChoiceByCode(code);
		}
		
		return "";
	}

	public String toString()
	{
		return "Project";
	}
	
	public BaseId getId()
	{
		return null;
	}
	
	public void rebuild()
	{
		Vector vector = new Vector();
		Target[] factors  = project.getTargetPool().getTargets();
		for (int i=0; i< factors.length; ++i)
		{
			vector.add(new TargetViabilityNode(project, (FactorId) factors[i].getId()));
		}
		children = vector;
	}
	
	
	private Target[] getTargetList()
	{
		Target[] targets = new Target[children.size()];
		for(int i = 0; i < children.size(); ++i)
		{
			targets[i] = (Target)((TargetViabilityNode)children.get(i)).getObject();
		}
		return targets;
	}
	
	private StatusQuestion statusQuestion;
	private Project project;
	private Vector children;
}
