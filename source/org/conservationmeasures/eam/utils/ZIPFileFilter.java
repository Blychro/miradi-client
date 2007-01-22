/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.conservationmeasures.eam.main.EAM;

public class ZIPFileFilter extends FileFilter
{

	public boolean accept(File pathname)
	{
		if (pathname.isDirectory())
			return true;
		return (pathname.getName().toLowerCase().endsWith(EXTENSION));
	}

	public String getDescription()
	{
		return EAM.text("FileFilter|ZIP (*.zip)");
	}

	public static final String EXTENSION = ".zip";
}
