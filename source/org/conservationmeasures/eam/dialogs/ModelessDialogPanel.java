/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.dialogs;

import java.awt.BorderLayout;
import java.awt.LayoutManager2;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.EAMObject;

abstract public class ModelessDialogPanel extends DisposablePanel
{
	public ModelessDialogPanel()
	{
		this(new BorderLayout());
	}
	
	public ModelessDialogPanel(LayoutManager2 layoutToUse)
	{
		super(layoutToUse);
	}
	
	public void objectWasSelected(BaseId selectedId)
	{
		
	}
	
	public void selectObject(EAMObject objectToSelect)
	{
		EAM.logDebug("selectObject not handled by " + getClass().getName());
	}
	
	abstract public EAMObject getObject();
	abstract public String getPanelDescription();

}
