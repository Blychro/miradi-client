/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.plaf.basic.BasicGraphUI;

public class EAMGraphUI extends BasicGraphUI
{
	protected MouseListener createMouseListener() 
	{
		return new DiagramMouseHandler();
    }
	
	public JGraph getGraph()
	{
		return graph;
	}
	
	public CellView getFocus()
	{
		return focus;
	}

	class DiagramMouseHandler extends BasicGraphUI.MouseHandler
	{
        public void mousePressed(MouseEvent event)
		{
			if(!this.processMouseEvent(event))
				super.mousePressed(event);
		}

		public void mouseReleased(MouseEvent event)
		{
			if(!SwingUtilities.isRightMouseButton(event))
				super.mouseReleased(event);
		}
		
	    public void mouseDragged(MouseEvent event)
		{
			if(!SwingUtilities.isRightMouseButton(event))
				super.mouseDragged(event);
		}

		private boolean processMouseEvent(MouseEvent event)
		{
			if (event.isConsumed())
				return false;
			if(!getGraph().isEnabled())
				return false;
			if(!SwingUtilities.isRightMouseButton(event))
				return false;
			
			CellView thisCell = getGraph().getNextSelectableViewAt(getFocus(), event.getX(), event.getY());
			if(thisCell == null)
				return true;
			if(getGraph().isCellSelected(thisCell.getCell()))
				return true;
			
			replaceSelectionWithThisCell(thisCell);
			return false;
		}

		private void replaceSelectionWithThisCell(CellView thisCell)
		{
			cell = thisCell;
		}

	}

}
