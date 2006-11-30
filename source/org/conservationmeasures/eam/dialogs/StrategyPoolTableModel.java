/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.dialogs;

import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.project.Project;

public class StrategyPoolTableModel extends ObjectPoolTableModel
{
	public StrategyPoolTableModel(Project projectToUse)
	{
		super(projectToUse, ObjectType.FACTOR,projectToUse.getFactorPool().getInterventionIds(), COLUMN_TAGS);	
	}

	private static final String[] COLUMN_TAGS = new String[] {
		Strategy.TAG_LABEL,
	};

}
