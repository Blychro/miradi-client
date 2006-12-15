/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.diagram;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

import org.conservationmeasures.eam.project.Project;

public class DiagramClipboard extends Clipboard
{
	public DiagramClipboard(Project projectToUse)
	{
		super(DiagramClipboard.class.getName());
		project = projectToUse;
		
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	}
	
	public synchronized int getPasteOffset() 
	{
		return pasteCount * project.getGridSize();
	}

	public synchronized void incrementPasteCount() 
	{
		++pasteCount;
	}
	

	private synchronized void resetPasteCount() 
	{
		pasteCount = 0;
	}
	
	public void setContents(Transferable contents, ClipboardOwner owner) 
	{
		resetPasteCount();
		clipboard.setContents(contents, owner);
	}
	
	public Transferable getContents(Object requestor)
	{
		return clipboard.getContents(requestor);
	}
	
	private Project project;
	private int pasteCount;
	private final Clipboard clipboard;
}
