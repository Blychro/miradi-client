/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import java.awt.Component;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.conservationmeasures.eam.diagram.DiagramComponent;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.diagram.EAMGraphSelectionModel;
import org.conservationmeasures.eam.diagram.cells.EAMGraphCell;
import org.conservationmeasures.eam.diagram.cells.FactorCell;
import org.conservationmeasures.eam.diagram.cells.LinkCell;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objects.DiagramFactorLink;
import org.conservationmeasures.eam.objects.DiagramObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.ResultsChainDiagram;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.diagram.ConceptualModelDiagramSplitPane;
import org.conservationmeasures.eam.views.diagram.DiagramLegendPanel;
import org.conservationmeasures.eam.views.diagram.DiagramModelUpdater;
import org.conservationmeasures.eam.views.diagram.DiagramSplitPane;
import org.conservationmeasures.eam.views.diagram.ResultsChainDiagramSplitPane;

public class DiagramPanel extends AbstractObjectDataInputPanel
{
	public DiagramPanel(MainWindow mainWindowToUse, Project project, DiagramObject diagramObjectToUse) throws Exception
	{
		super(project, diagramObjectToUse.getRef());
		try
		{
			mainWindow = mainWindowToUse;
			diagramObject = diagramObjectToUse;

			createAndAddDiagram();

			if (diagramObject.getType()== ResultsChainDiagram.getObjectType())
				splitPane = new ResultsChainDiagramSplitPane(mainWindow, diagram);
			else
				splitPane = new ConceptualModelDiagramSplitPane(mainWindow, diagram);

			add(splitPane);
		}
		catch (Exception e)
		{
			//TODO should we call dispose instead
			project.removeCommandExecutedListener(this);
			throw e;
		}
	}
	
	static public DiagramComponent createDiagram(MainWindow mainWindow, DiagramObject diagramObject) throws Exception
	{
		DiagramModel diagramModel = new DiagramModel(diagramObject.getObjectManager().getProject());
		diagramModel.fillFrom(diagramObject);
		diagramModel.updateProjectScopeBox();
		DiagramComponent diagram = new DiagramComponent(mainWindow);
		diagram.setModel(diagramModel);
		diagram.setGraphLayoutCache(diagramModel.getGraphLayoutCache());
		return diagram;
	}
	
	private void createAndAddDiagram() throws Exception
	{
		diagram = createDiagram(mainWindow, diagramObject);
		selectionModel = diagram.getEAMGraphSelectionModel();
	}
	
	public DiagramObject getDiagramObject()
	{
		return diagramObject;
	}
		
	public void setSelectionModel(EAMGraphSelectionModel selectionModelToUse)
	{
		selectionModel = selectionModelToUse;
	}
	
	public EAMGraphCell[] getSelectedAndRelatedCells()
	{
		Object[] selectedCells = selectionModel.getSelectionCells();
		Vector cellVector = getAllSelectedCellsWithRelatedLinkages(selectedCells);
		return (EAMGraphCell[])cellVector.toArray(new EAMGraphCell[0]);
	}
	
	//FIXME this same method exists inside project
	public Vector getAllSelectedCellsWithRelatedLinkages(Object[] selectedCells) 
	{
		DiagramModel model = getDiagramModel();
		Vector selectedCellsWithLinkages = new Vector();
		for(int i=0; i < selectedCells.length; ++i)
		{
			EAMGraphCell cell = (EAMGraphCell)selectedCells[i];
			if(cell.isFactorLink())
			{
				if(!selectedCellsWithLinkages.contains(cell))
					selectedCellsWithLinkages.add(cell);
			}
			else if(cell.isFactor())
			{
				Set linkages = model.getFactorLinks((FactorCell)cell);
				for (Iterator iter = linkages.iterator(); iter.hasNext();) 
				{
					EAMGraphCell link = (EAMGraphCell) iter.next();
					if(!selectedCellsWithLinkages.contains(link))
						selectedCellsWithLinkages.add(link);
				}
				selectedCellsWithLinkages.add(cell);
			}
		}
		return selectedCellsWithLinkages;
	}
	
	public void selectFactor(FactorId idToUse)
	{
		try
		{
			FactorCell nodeToSelect = getDiagramModel().getFactorCellByWrappedId(idToUse);
			selectionModel.setSelectionCell(nodeToSelect);
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}
	
	public DiagramFactorLink[] getOnlySelectedLinks()
	{
		if(selectionModel == null)
			return new DiagramFactorLink[0];
		
		Object[] rawCells = selectionModel.getSelectionCells();
		return getOnlySelectedLinks(rawCells);
	}
	
	public DiagramFactorLink[] getOnlySelectedLinks(Object [] allSelectedCells)
	{
		Vector linkages = new Vector();
		for(int i = 0; i < allSelectedCells.length; ++i)
		{
			if(((EAMGraphCell)allSelectedCells[i]).isFactorLink())
			{
				LinkCell cell = (LinkCell)allSelectedCells[i];
				linkages.add(cell.getDiagramFactorLink());
			}
		}
		return (DiagramFactorLink[])linkages.toArray(new DiagramFactorLink[0]);
	}
	
	public FactorCell[] getOnlySelectedFactorCells()
	{
		if(selectionModel == null)
			return new FactorCell[0];
		
		Object[] rawCells = selectionModel.getSelectionCells();
		return getOnlySelectedFactorCells(rawCells);
	}
	
	public FactorCell[] getOnlySelectedFactorCells(Object[] allSelectedCells)
	{
		Vector nodes = new Vector();
		for(int i = 0; i < allSelectedCells.length; ++i)
		{
			if(((EAMGraphCell)allSelectedCells[i]).isFactor())
				nodes.add(allSelectedCells[i]);
		}
		return (FactorCell[])nodes.toArray(new FactorCell[0]);
	}
	
	public Factor[] getOnlySelectedFactors()
	{
		if (selectionModel == null)
			return new Factor[0];
		
		Object[] rawCells = selectionModel.getSelectionCells();
		return getOnlySelectedFactors(rawCells);
	}
	
	private Factor[] getOnlySelectedFactors(Object[] allSelectedFactors)
	{
		Vector nodes = new Vector();
		for(int i = 0; i < allSelectedFactors.length; ++i)
		{
			EAMGraphCell graphCell = ((EAMGraphCell)allSelectedFactors[i]);
			if(graphCell.isFactor())
			{
				ORef ref = graphCell.getDiagramFactor().getWrappedORef();
				Factor factor = (Factor) getProject().findObject(ref);
				nodes.add(factor);
			}
		}
		return (Factor[])nodes.toArray(new Factor[0]);

	}
	
	public EAMGraphCell[] getOnlySelectedCells()
	{
		Object[] rawCells = selectionModel.getSelectionCells();
		EAMGraphCell[] cells = new EAMGraphCell[rawCells.length];
		for(int i=0; i < cells.length; ++i)
			cells[i] = (EAMGraphCell)rawCells[i];
		return cells;
	}
	
	public void moveFactors(int deltaX, int deltaY, DiagramFactorId[] ids) throws Exception 
	{
		getDiagramModel().moveFactors(deltaX, deltaY, ids);
	}

	public DiagramModel getDiagramModel()
	{
		DiagramComponent diagramComponent = getdiagramComponent();
		if (diagramComponent==null)
			return null;
		return diagramComponent.getDiagramModel();
	}
	
	public DiagramComponent getdiagramComponent()
	{
		return diagram;
	}

	public String getPanelDescription()
	{
		return EAM.text("Title|Diagram Panel");
	}

	public void dispose()
	{
		super.dispose();
		//FIXME dispose properly
		getDiagramModel().dispose();

		diagram = null;
		selectionModel = null;
	}
	
	public void addFieldComponent(Component component)
	{
		throw new RuntimeException("Not yet implemented");
	}

	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);
		
		try
		{
			DiagramModelUpdater modelUpdater = new DiagramModelUpdater(getProject(), getDiagramModel(), diagramObject);
			modelUpdater.commandExecuted(event);
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
		
	}
	
	public DiagramLegendPanel getDiagramLegendPanel()
	{
		return splitPane.getLegendPanel();
	}

	private DiagramSplitPane splitPane;
	private DiagramObject diagramObject;
	private EAMGraphSelectionModel selectionModel;
	private DiagramComponent diagram;
	private MainWindow mainWindow;
}
