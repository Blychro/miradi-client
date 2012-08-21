/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.project;

import java.io.File;
import java.util.Calendar;

import org.martus.util.DirectoryUtils;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.utils.MiradiZipFile;
import org.miradi.utils.MpfFileFilter;
import org.miradi.utils.MpzFileFilter;
import org.miradi.utils.ZipUtilities;
import org.miradi.views.umbrella.MpzProjectImporter;
import org.miradi.wizard.noproject.FileSystemTreeNode;

public class OldProjectDirToMpfConverter
{
	public static File convert(MainWindow mainWindow, File oldProjectDirectory) throws Exception
	{
		File backupFolder = new File(EAM.getHomeDirectory(), FileSystemTreeNode.BACKUP_FOLDER_NAME);
		backupFolder.mkdirs();
		
		File oldProjectZippedAsBackup = new File(backupFolder, "v3-Backup-" + oldProjectDirectory.getName() + "-" + Calendar.getInstance().getTimeInMillis() + MpzFileFilter.EXTENSION);
		if (oldProjectZippedAsBackup.exists())
			throw new Exception("Attempted to override an existing backup file when converting old project dir to new project format:" + oldProjectZippedAsBackup.getAbsolutePath());
		
		ZipUtilities.createZipFromDirectory(oldProjectDirectory, oldProjectZippedAsBackup);
		if (!ZipUtilities.doesProjectZipContainAllProjectFiles(new MiradiZipFile(oldProjectZippedAsBackup), oldProjectDirectory))
			throw new Exception("Mpz to Mpf data conversion failed");
		
		MpzProjectImporter importer = new MpzProjectImporter(mainWindow);
		File proposedProjectFile = new File(MpfFileFilter.createNameWithExtension(oldProjectDirectory.getName()));
		File importedFile = importer.importProject(oldProjectZippedAsBackup, proposedProjectFile);
		if (importedFile != null)
			DirectoryUtils.deleteEntireDirectoryTree(oldProjectDirectory);
		
		return importedFile;
	}
}
