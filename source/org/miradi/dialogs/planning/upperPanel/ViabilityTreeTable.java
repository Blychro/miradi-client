/* 
Copyright 2005-2018, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 

package org.miradi.dialogs.planning.upperPanel;

import java.util.HashSet;
import java.util.Set;

import org.miradi.actions.ActionExpandToFutureStatus;
import org.miradi.actions.ActionExpandToHumanWelfareTarget;
import org.miradi.actions.ActionExpandToIndicator;
import org.miradi.actions.ActionExpandToKeyEcologicalAttribute;
import org.miradi.actions.ActionExpandToMeasurement;
import org.miradi.actions.ActionExpandToMenu;
import org.miradi.actions.ActionExpandToTarget;
import org.miradi.dialogs.treetables.GenericTreeTableModel;
import org.miradi.main.MainWindow;

public class ViabilityTreeTable extends PlanningTreeTable
{
	public ViabilityTreeTable(MainWindow mainWindowToUse, GenericTreeTableModel planningTreeModelToUse)
	{
		super(mainWindowToUse, planningTreeModelToUse);
	}

	@Override
	protected Set<Class> getRelevantActions()
	{
		HashSet<Class> relevantActions = new HashSet<Class>();
		relevantActions.addAll(super.getRelevantActions());
		relevantActions.add(ActionExpandToMenu.class);
		relevantActions.add(ActionExpandToTarget.class);
		relevantActions.add(ActionExpandToHumanWelfareTarget.class);
		relevantActions.add(ActionExpandToKeyEcologicalAttribute.class);
		relevantActions.add(ActionExpandToIndicator.class);
		relevantActions.add(ActionExpandToMeasurement.class);
		relevantActions.add(ActionExpandToFutureStatus.class);
		
		return relevantActions;
	}
}
