/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.main;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Vector;

import org.conservationmeasures.eam.diagram.cells.DiagramFactor;
import org.conservationmeasures.eam.diagram.cells.EAMGraphCell;
import org.conservationmeasures.eam.diagram.cells.FactorDataMap;
import org.conservationmeasures.eam.diagram.cells.FactorLinkDataMap;

public class TransferableEamList implements Transferable 
{
	public TransferableEamList (String projectFileName, Object[] cells)
	{
		super();
		projectName = projectFileName;
		links = new Vector();
		factors = new Vector();
		storeData(cells);
	}
	
	public String getProjectFileName()
	{
		return projectName;
	}
	
	private void storeData(Object[] cells)
	{
		for (int i = 0; i < cells.length; i++) 
		{
			EAMGraphCell cell = (EAMGraphCell)cells[i];
			try 
			{
				if(cell.isFactorLink())
				{
					links.add(cell.getDiagramFactorLink().createLinkageDataMap());
				}
				if(cell.isFactor())
				{
					factors.add(((DiagramFactor)cell).createFactorDataMap());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public DataFlavor[] getTransferDataFlavors() 
	{
		DataFlavor[] flavorArray = {eamListDataFlavor};
		return flavorArray;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) 
	{
		DataFlavor[] flavors = getTransferDataFlavors();
		for(int i = 0; i < flavors.length; ++i)
			if(flavor.equals(flavors[i]))
				return true;
		return false;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException 
	{
		if(isDataFlavorSupported(flavor))
			return this;
		throw new UnsupportedFlavorException(flavor);
	}
	
	public FactorLinkDataMap[] getArrayOfFactorLinkDataMaps()
	{
		return (FactorLinkDataMap[])links.toArray(new FactorLinkDataMap[0]);
	}
	
	public FactorDataMap[] getArrayOfFactorDataMaps()
	{
		return (FactorDataMap[])factors.toArray(new FactorDataMap[0]);
	}

	public static DataFlavor eamListDataFlavor = new DataFlavor(TransferableEamList.class, "EAM Objects");
	
	String projectName;
	Vector links;
	Vector factors;
}
