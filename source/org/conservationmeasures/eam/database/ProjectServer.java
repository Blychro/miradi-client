/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.database;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.Vector;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.diagram.nodes.LinkageData;
import org.conservationmeasures.eam.exceptions.UnknownCommandException;
import org.conservationmeasures.eam.main.EAM;
import org.json.JSONObject;
import org.martus.util.DirectoryUtils;

public class ProjectServer
{
	public ProjectServer() throws IOException
	{
		commands = new Vector();
		db = new DatabaseWrapper();
	}

	public int getCommandCount()
	{
		return commands.size();
	}

	public Command getCommandAt(int i)
	{
		if(i < 0 || i >= getCommandCount())
			throw new RuntimeException("Command " + i + " not found");
		
		return (Command)commands.get(i);
	}

	protected void clear()
	{
		commands.clear();
	}
	
	public void addCommandWithoutSaving(Command command) throws IOException
	{
		addCommand(command);
	}
	
	public void appendCommand(Command command) throws IOException
	{
		if(!isOpen())
			throw new IOException("FileStorage: Can't append if no file open");
		
		DoneCommand commandToSave = DoneCommand.buildFromCommand(command);
		db.insert(commandToSave);
		addCommandWithoutSaving(command);
	}

	public void close() throws IOException
	{
		EAM.logDebug("Closing database");
		clear();
		db.close();
		topDirectory = null;
		name = null;
	}

	public static boolean doesProjectExist(File directoryToCheck)
	{
		return isExistingProject(directoryToCheck);
	}
	
	public boolean isOpen()
	{
		return db.isOpen();
	}

	public String getName()
	{
		return name;
	}
	
	public File getTopDirectory()
	{
		return topDirectory;
	}

	public void open(File directory) throws IOException
	{
		openNonDatabaseStore(directory);

		if(doesProjectExist(directory))
		{
			db.openDiskDatabase(getDatabaseFileBase(directory));
		}
		else
		{
			DirectoryUtils.deleteEntireDirectoryTree(directory);
			db.openDiskDatabase(getDatabaseFileBase(directory));
			createCommandsTable();
			db.flush();
		}
	}

	protected void openNonDatabaseStore(File directory)
	{
		clear();
		topDirectory = directory;
		getLinkagesDirectory().mkdirs();
		name = topDirectory.getName();
	}
	

	public Vector load() throws IOException, UnknownCommandException
	{
		Vector loaded = new Vector();
		EAM.logVerbose("---Loading---");
		ResultSet allCommands = db.rawSelect("SELECT * FROM DoneCommands ORDER BY id");
		try
		{
			while(allCommands.next())
			{
				String commandName = allCommands.getString("name");
				byte[] data = allCommands.getBytes("data");
				DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(data));
				
				Command command = Command.createFrom(commandName, dataIn);
				loaded.add(command);
				EAM.logVerbose(command.toString());
			}
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return null;
		}
		EAM.logVerbose("---Finished---");
		return loaded;
	}
	
	public static boolean isExistingProject(File projectDirectory)
	{
		if(projectDirectory == null)
			return false;
		
		if(!projectDirectory.isDirectory())
			return false;
		
		String projectName = projectDirectory.getName();
		File script = new File(projectDirectory, projectName + ".script");
		return script.exists();
	}
	
	public void writeLinkage(LinkageData linkage) throws IOException, ParseException
	{
		int id = linkage.getId();
		JSONFile.write(getLinkageFile(id), linkage.toJson());
		addToLinkageManifest(id);
	}

	public LinkageData readLinkage(int id) throws IOException, ParseException
	{
		return new LinkageData(JSONFile.read(getLinkageFile(id)));
	}
	
	public void deleteLinkage(int id) throws IOException, ParseException
	{
		removeFromLinkageManifest(id);
		getLinkageFile(id).delete();
	}
	
	private void removeFromLinkageManifest(int idToRemove) throws IOException, ParseException
	{
		LinkageManifest manifest = readLinkageManifest();
		manifest.remove(idToRemove);
		writeLinkageManifest(manifest);
	}
	
	private void addToLinkageManifest(int idToAdd) throws IOException, ParseException
	{
		LinkageManifest manifest = readLinkageManifest();
		manifest.put(idToAdd);
		writeLinkageManifest(manifest);
	}
	
	public LinkageManifest readLinkageManifest() throws IOException, ParseException
	{
		File manifestFile = getLinkageManifestFile();
		if(!manifestFile.exists())
			return new LinkageManifest();
		JSONObject rawManifest = JSONFile.read(manifestFile);
		return new LinkageManifest(rawManifest);
	}
	
	private void writeLinkageManifest(LinkageManifest manifest) throws IOException
	{
		manifest.write(getLinkageManifestFile());
	}

	private static File getDatabaseFileBase(File directory)
	{
		return new File(directory, directory.getName());
	}
	
	private File getJsonDirectory()
	{
		return new File(getTopDirectory(), "json");
	}

	private File getLinkagesDirectory()
	{
		return new File(getJsonDirectory(), "linkages");
	}
	
	private File getLinkageManifestFile()
	{
		return new File(getLinkagesDirectory(), "manifest");
	}
	
	private File getLinkageFile(int id)
	{
		return new File(getLinkagesDirectory(), Integer.toString(id));
	}
	
	protected void createCommandsTable() throws IOException
	{
		db.rawExecute("CREATE TABLE DoneCommands (id INTEGER IDENTITY PRIMARY KEY, name VARCHAR, data LONGVARBINARY);");
	}

	public void dropAllTables() throws IOException
	{
		db.rawExecute("DROP TABLE DoneCommands IF EXISTS;");
	}

	private void addCommand(Command command)
	{
		commands.add(command);
	}
	
	static public String OBJECT_TYPE = "Type";
	static public String LINKAGE_MANIFEST = "LinkageManifest";

	protected Vector commands;
	File topDirectory;
	String name;
	protected DatabaseWrapper db;

}
