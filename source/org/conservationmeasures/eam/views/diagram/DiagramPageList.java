/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objectpools.EAMObjectPool;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.DiagramObject;
import org.conservationmeasures.eam.objects.ViewData;
import org.conservationmeasures.eam.project.Project;

abstract public class DiagramPageList extends JList
{
	public DiagramPageList(Project projectToUse, int objectTypeToUse)
	{
		super();
		project = projectToUse;
		objectType = objectTypeToUse;
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setBorder(BorderFactory.createEtchedBorder());
	}
	
	public void fillListWithSelectedDiagramObject(DiagramObject diagramObject)
	{
		try
		{
			fillList();
			ViewData diagramViewData = project.getViewData(DiagramView.getViewName());
			CommandSetObjectData setViewData = new CommandSetObjectData(diagramViewData.getRef(), ViewData.TAG_CURRENT_DIAGRAM_REF, diagramObject.getRef());
			project.executeCommand(setViewData);
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}

	public void fillList()
	{
		EAMObjectPool pool = project.getPool(objectType);
		ORefList refList = pool.getORefList();
		BaseObject[] diagramObjects = project.getObjectManager().findObjects(refList);
		setListData(diagramObjects);
	}
	
	public int getContentType()
	{
		return objectType;
	}
	
	public int getListSize()
	{
		return getModel().getSize();
	}
	
	abstract public boolean isResultsChainPageList();
	
	abstract public boolean isConceptualModelPageList();
	
	Project project;
	int objectType;
}