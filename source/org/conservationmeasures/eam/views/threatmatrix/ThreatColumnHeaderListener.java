/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.threatmatrix;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.conservationmeasures.eam.objects.ConceptualModelNode;
import org.conservationmeasures.eam.objects.ViewData;


public class ThreatColumnHeaderListener extends ColumnHeaderListener
{
	public ThreatColumnHeaderListener(MyThreatGirdPanel threatGirdPanelInUse)
	{
		super(threatGirdPanelInUse);
		sortToggle = new boolean[threatGirdPanelInUse.globalTthreatTable.getRowCount()];
	}
	
	public  boolean toggle(int sortColumn) {
		sortToggle[sortColumn] = !sortToggle[sortColumn];
		return sortToggle[sortColumn];
	}
	
	
	public static void sort(MyThreatGirdPanel threatGirdPanel, String currentSortBy, String currentSortDirection) 
	{
		NonEditableThreatMatrixTableModel modelToSort = 
			(NonEditableThreatMatrixTableModel)threatGirdPanel.globalTthreatTable.getModel();
		
		ConceptualModelNode[] threatList = modelToSort.getDirectThreats();
		
		if (currentSortBy.equals(ViewData.SORT_SUMMARY)) 
		{
			Arrays.sort(threatList, getComparator(modelToSort.getColumnCount(),modelToSort));
		}
		else
		{
			int sortColunm= 0; // Lookup baseId and find it in column
			Arrays.sort(threatList, getComparator(sortColunm, modelToSort));
		}
		
		if (currentSortDirection.equals(ViewData.SORT_DESCENDING)) 
			threatList = reverseSort(threatList);
		
		modelToSort.setThreatRows(threatList);
	}
	
	public void sort(int sortColumn) 
	{
		
		NonEditableThreatMatrixTableModel modelToSort = 
			(NonEditableThreatMatrixTableModel)threatGirdPanel.globalTthreatTable.getModel();
		
		ConceptualModelNode[] threatList = modelToSort.getDirectThreats();

		Comparator comparator = getComparator(sortColumn, modelToSort);

		Arrays.sort(threatList, comparator);
		
		if ( toggle(sortColumn) )  
			threatList = reverseSort(threatList);

		modelToSort.setThreatRows(threatList);
		
		saveState(sortColumn, modelToSort);
	}


	private void saveState(int sortColumn, NonEditableThreatMatrixTableModel modelToSort)
	{
		if(isSummaryColumn(sortColumn, modelToSort))
			saveSortState(sortToggle[sortColumn], ViewData.SORT_SUMMARY);
		else
			saveSortState(sortToggle[sortColumn], modelToSort.getTargets()[sortColumn]);
	}


	private static Comparator getComparator(int sortColumn, NonEditableThreatMatrixTableModel modelToSort)
	{
		Comparator comparator;
		if(isSummaryColumn(sortColumn, modelToSort))
			comparator = new SummaryColumnComparator(modelToSort);
		else
			comparator = new ComparableNode(sortColumn,modelToSort);
		return comparator;
	}

	private static  boolean isSummaryColumn(int sortColumn, NonEditableThreatMatrixTableModel modelToSort) 
	{
	 return (sortColumn == modelToSort.getColumnCount()-1);
	}
	
	private static ConceptualModelNode[] reverseSort(ConceptualModelNode[] threatList)
	{
		Vector list = new Vector(Arrays.asList(threatList));
		Collections.reverse(list);
		threatList = (ConceptualModelNode[]) list.toArray(new ConceptualModelNode[0]);
		return threatList;
	}
	

	boolean sortToggle[];

}
