/* 
Copyright 2005-2013, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.mpfMigrations;

import java.io.File;

import org.martus.util.UnicodeReader;
import org.martus.util.UnicodeStringReader;
import org.martus.util.UnicodeStringWriter;
import org.martus.util.UnicodeWriter;
import org.miradi.files.AbstractMpfFileFilter;
import org.miradi.project.AbstractMiradiProjectSaver;
import org.miradi.project.RawProjectSaver;
import org.miradi.utils.FileUtilities;

public class MigrationManager
{
	public MigrationManager()
	{
	}
	
	public void safelyMigrate(File projectFile) throws Exception
	{
		createBackup(projectFile);
		String contents = UnicodeReader.getFileContents(projectFile);
		contents = migrate(contents);
		UnicodeWriter fileWriter = new UnicodeWriter(projectFile);
		fileWriter.write(contents);
		fileWriter.close();
	}
	
	private void createBackup(File projectFile) throws Exception
	{
		long timeOfBackup = System.currentTimeMillis();
		File backup = new File(projectFile.getParentFile(), "backup-" + projectFile.getName() + "-" + timeOfBackup + AbstractMpfFileFilter.EXTENSION);
		if (backup.exists())
			throw new Exception("Overriding older backup");
		
		FileUtilities.copyFile(projectFile, backup);
	}

	public String migrate(String mpfAsString) throws Exception
	{
		if (needsMigration(mpfAsString))
		{
			RawProject rawProject = RawProjectLoader.loadProject(new UnicodeStringReader(mpfAsString));

			final RawProject migratedPools = IndicatorFutureStatusDataToNewFutureStatusTypeMigration.migrate(rawProject);
			return convertToMpfString(migratedPools);
		}

		return mpfAsString;
	}
	
	public boolean needsMigration(final File projectFile) throws Exception
	{
		String contents = UnicodeReader.getFileContents(projectFile);
		return needsMigration(contents);
	}
	
	public boolean needsMigration(String mpfAsString) throws Exception
	{
		VersionRange mpfVersionRange = RawProjectLoader.loadVersionRange(new UnicodeStringReader(mpfAsString));
		final int migrationType = getMigrationType(AbstractMiradiProjectSaver.getMiradiVersionRange(), mpfVersionRange);
		
		return migrationType == MIGRATION;
	}
	
	public static int getMigrationType(VersionRange miradiVersionRange, VersionRange mpfVersionRange) throws Exception
	{
		if (miradiVersionRange.doesRangeOverlap(mpfVersionRange))
			return MIGRATION;
		
		if (mpfVersionRange.isEntirelyOlderThan(miradiVersionRange))
			return MIGRATION;
		
		if (mpfVersionRange.isEntirelyNewerThan(miradiVersionRange))
			return TOO_NEW_TO_MIGRATE;
		
		return NO_MIGRATION;
	}

	private String convertToMpfString(RawProject migratedPools) throws Exception
	{
		UnicodeStringWriter stringWriter = UnicodeStringWriter.create();
		RawProjectSaver.saveProject(migratedPools, stringWriter);
		
		return stringWriter.toString();
	}
	
	public static final int NO_MIGRATION = 0;
	public static final int MIGRATION = 1;
	public static final int TOO_NEW_TO_MIGRATE = 2;
}
