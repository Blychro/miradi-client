/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.conservationmeasures.eam.actions.EAMAction;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;

public class MouseAdapterDoubleClickDelegator extends MouseAdapter
{
	
	public MouseAdapterDoubleClickDelegator(EAMAction delegateActionIn) 
	{
		super();
		delegateAction = delegateActionIn;
	}
	
    public void mouseClicked(MouseEvent e)
    {
		if(e.getClickCount() == 2)
		{
			try
			{
				delegateAction.doAction();
			}
			catch(CommandFailedException ex)
			{
				EAM.logException(ex);
				EAM.errorDialog("Action failed on mouseClicked:" + ex);
			}
		}
	}
    
    EAMAction delegateAction;
}
