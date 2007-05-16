package org.conservationmeasures.eam.utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.conservationmeasures.eam.main.EAM;

public class MPZFileFilter extends FileFilter implements MiradiFileFilter
{

	public boolean accept(File pathname)
	{
		if (pathname.isDirectory())
			return true;
		return (pathname.getName().toLowerCase().endsWith(EXTENSION));
	}

	public String getDescription()
	{
		return EAM.text("FileFilter|Miradi Project Zip (*.mpz)");
	}
	
	public String getFileExtension()
	{
		return EXTENSION;
	}

	public static final String EXTENSION = ".mpz";
}
