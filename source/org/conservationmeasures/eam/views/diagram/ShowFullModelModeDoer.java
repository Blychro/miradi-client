/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import java.text.ParseException;

import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.DiagramComponent;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.diagram.cells.FactorCell;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.ViewData;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.ViewDoer;
import org.jgraph.graph.GraphLayoutCache;

public class ShowFullModelModeDoer extends ViewDoer
{
	public boolean isAvailable()
	{
		try
		{
			ViewData viewData = getProject().getViewData(getView().cardName());
			String currentViewMode = viewData.getData(ViewData.TAG_CURRENT_MODE);
			if(ViewData.MODE_DEFAULT.equals(currentViewMode))
				return false;
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return false;
		}

		return true;
	}

	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;

		try
		{
			IdList factorsToMakeSelected = getFactorsToMakeSelected();
			
			BaseId viewId = getCurrentViewId();
			getProject().executeCommand(new CommandSetObjectData(ObjectType.VIEW_DATA, viewId, 
					ViewData.TAG_CURRENT_MODE, ViewData.MODE_DEFAULT));
			
			selectFactors(factorsToMakeSelected);
			
		}
		catch (Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
	}

	private IdList getFactorsToMakeSelected() throws Exception, ParseException
	{
		Project project = getMainWindow().getProject();
		String listOfIds = project.getCurrentViewData().getData(ViewData.TAG_BRAINSTORM_NODE_IDS);
		IdList factorsToMakeVisible = new IdList(listOfIds);
		return factorsToMakeVisible;
	}

	private void selectFactors(IdList factorIds) throws Exception
	{
		DiagramComponent diagramComponent  = ((DiagramView)getView()).getDiagramComponent();
		GraphLayoutCache glc  = diagramComponent.getGraphLayoutCache();
		DiagramModel diagramModel = getProject().getDiagramModel();
		
		for(int i = 0; i < factorIds.size(); ++i)
		{
			FactorId nodeId = new FactorId(factorIds.get(i).asInt());
			FactorCell diagramFactor = diagramModel.getDiagramFactorByWrappedId(nodeId);
			if (glc.isVisible(diagramFactor))
				diagramComponent.addSelectionCell(diagramFactor);
		}
	}
	
	
	private BaseId getCurrentViewId() throws Exception
	{
		ViewData viewData = getProject().getCurrentViewData();
		return viewData.getId();
	}
}
