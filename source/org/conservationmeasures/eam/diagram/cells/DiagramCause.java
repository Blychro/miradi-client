/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.diagram.cells;

import java.awt.Color;

import org.conservationmeasures.eam.diagram.DiagramConstants;
import org.conservationmeasures.eam.main.AppPreferences;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.Cause;
import org.conservationmeasures.eam.objects.DiagramFactor;

//TODO rename to DiagramCauseCell. and the same for sibling classes.  class names should end with Cell
public class DiagramCause extends FactorCell
{
	public DiagramCause(Cause cmFactor, DiagramFactor diagramFactorToUse)
	{
		super(cmFactor, diagramFactorToUse);
	}

	public Color getColor()
	{
		if(isContributingFactor())
			return EAM.mainWindow.getColorPreference(AppPreferences.TAG_COLOR_CONTRIBUTING_FACTOR);
		if(isDirectThreat())
			return EAM.mainWindow.getColorPreference(AppPreferences.TAG_COLOR_DIRECT_THREAT);
		if(isStress())
			return DiagramConstants.COLOR_STRESS;
		
		throw new RuntimeException("Unknown factor type: " + getClass());
	}


}
