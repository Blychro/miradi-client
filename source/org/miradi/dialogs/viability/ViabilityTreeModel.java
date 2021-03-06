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
package org.miradi.dialogs.viability;

import org.miradi.dialogs.viability.nodes.ViabilityMeasurementNode;
import org.miradi.objects.BaseObject;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Measurement;
import org.miradi.objects.Target;

public class ViabilityTreeModel extends GenericViabilityTreeModel
{
	public ViabilityTreeModel(Object root)
	{
		super(root);
	}

	@Override
	public String[] getColumnTags()
	{
		return columnTags;
	}
	
	@Override
	public String getUniqueTreeTableModelIdentifier()
	{
		return UNIQUE_TREE_TABLE_IDENTIFIER;
	}
	
	private static final String UNIQUE_TREE_TABLE_IDENTIFIER = "ViabilityTreeModel";

	public static String[] columnTags = {DEFAULT_COLUMN,
										 Target.TAG_VIABILITY_MODE,
										 ViabilityTreeModel.VIRTUAL_TAG_STATUS,
										 ViabilityTreeModel.VIRTUAL_TAG_FUTURE_STATUS,
										 KeyEcologicalAttribute.TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE,
										 ViabilityMeasurementNode.POOR,
										 ViabilityMeasurementNode.FAIR,
										 ViabilityMeasurementNode.GOOD,
										 ViabilityMeasurementNode.VERY_GOOD,
										 Measurement.TAG_EVIDENCE_CONFIDENCE,
										 BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE,};

	public static final String VIRTUAL_TAG_STATUS = "Status";
	public static final String VIRTUAL_TAG_FUTURE_STATUS = "FutureStatus";
}
