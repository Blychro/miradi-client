/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.monitoring;

import org.conservationmeasures.eam.dialogs.ObjectPoolTablePanel;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.Project;

public class IndicatorPoolTablePanel extends ObjectPoolTablePanel
{
	public IndicatorPoolTablePanel(Project projectToUse)
	{
		super(projectToUse, ObjectType.INDICATOR, 
				new IndicatorPoolTableModel(projectToUse)
		);
	}
}
