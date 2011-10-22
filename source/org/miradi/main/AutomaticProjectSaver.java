/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

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

package org.miradi.main;

import java.io.File;
import java.io.IOException;

import org.martus.util.UnicodeStringWriter;
import org.martus.util.UnicodeWriter;
import org.miradi.project.Project;
import org.miradi.project.ProjectSaver;
import org.miradi.utils.FileLocker;

public class AutomaticProjectSaver implements CommandExecutedListener
{
	public AutomaticProjectSaver(Project projectToTrack)
	{
		project = projectToTrack;
		project.addCommandExecutedListener(this);
		locker = new FileLocker();
	}
	
	public void startSaving(File projectFileToUse) throws Exception
	{
		locker.lock(getLockFile(projectFileToUse));
		projectFile = projectFileToUse;
	}
	
	public void stopSaving() throws Exception
	{
		projectFile = null;
		locker.close();
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		if(projectFile == null)
			return;
		
		if(project.isInTransaction())
			return;
		
		try
		{
			safeSave(projectFile);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void safeSave(File currentFile) throws IOException, Exception
	{
		File oldFile = getOldFile(currentFile);
		File newFile = getNewFile(currentFile);

		safeDelete(newFile);
		save(newFile);

		safeDelete(oldFile);
		safeRename(currentFile, oldFile);

		safeRename(newFile, currentFile);
		
		// NOTE: recovery steps:
		// 1. if valid new file exists, use it, else
		// 2. if valid current file exists, use it, else
		// 3. if valid old file exists, use it
	}

	private void safeDelete(File file) throws IOException
	{
		if(!file.exists())
			return;
		
		if(!file.delete())
			throw new IOException("Delete failed: " + file.getAbsolutePath());
	}

	private void safeRename(File fromFile, File toFile) throws IOException
	{
		if(!fromFile.exists())
			return;
		
		if(!fromFile.renameTo(toFile))
			throw new IOException("Rename failed: " + fromFile.getAbsolutePath() + "->" + toFile.getAbsolutePath());
	}

	public static File getOldFile(File currentFile)
	{
		return getFileWithSuffix(currentFile, ".old");
	}

	public static File getNewFile(File currentFile)
	{
		return getFileWithSuffix(currentFile, ".new");
	}

	public static File getLockFile(File currentFile)
	{
		return getFileWithSuffix(currentFile, ".lock");
	}

	private static File getFileWithSuffix(File currentFile, String suffix)
	{
		return new File(currentFile.getAbsolutePath() + suffix);
	}
	
	private void save(File file) throws Exception
	{
		long startedAt = System.currentTimeMillis();
		UnicodeStringWriter stringWriter = UnicodeStringWriter.create();
		ProjectSaver.saveProject(project, stringWriter);
		
		UnicodeWriter fileWriter = new UnicodeWriter(file);
		fileWriter.write(stringWriter.toString());
		fileWriter.close();
		long endedAt = System.currentTimeMillis();
		EAM.logDebug("Saved project: " + (endedAt - startedAt) + "ms");
	}
	
	private File projectFile;
	private Project project;
	private FileLocker locker;
}
