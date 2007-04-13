/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.reports;

import net.sf.jasperreports.engine.JRDataSource;

import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.objects.KeyEcologicalAttribute;

public class ViabilityIndicatorsDataSource extends CommonDataSource
{
	public ViabilityIndicatorsDataSource(KeyEcologicalAttribute kea)
	{
		super(kea.getObjectManager().getProject());
		ORefList list = new ORefList(Indicator.getObjectType(), kea.getIndicatorIds());
		setObjectList(list);
	}

	public JRDataSource getViabilityGoalsDataSourceDataSource()
	{
		return new ViabilityGoalsDataSource((Indicator)getCurrentObject());
	}
} 
