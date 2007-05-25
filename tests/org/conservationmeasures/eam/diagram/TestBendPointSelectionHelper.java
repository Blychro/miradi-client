/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.diagram;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

import org.conservationmeasures.eam.diagram.cells.LinkCell;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.project.ProjectForTesting;

public class TestBendPointSelectionHelper extends EAMTestCase
{
	public TestBendPointSelectionHelper(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		project = new ProjectForTesting(getName());
	}

	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
	}

	public void testMousePressed() throws Exception
	{
		LinkCell linkCell = project.createLinkCell();
		BendPointSelectionHelper selectionHelper = new BendPointSelectionHelper(linkCell);
		int[] selectionIndexes = selectionHelper.getSelectedIndexes();
		assertEquals("selection list not empty?", 0, selectionIndexes.length);
		
		clickWithNoModifiers();
		clickWithShiftModifier();
	}
	
	private void clickWithNoModifiers() throws Exception
	{
		LinkCell linkCell = project.createLinkCell();
		BendPointSelectionHelper selectionHelper = new BendPointSelectionHelper(linkCell);
		MouseEvent mouseEvent = getMouseEvent(0);
		int selectionIndex = 1;
		
		pressMouseButton(selectionHelper, mouseEvent, selectionIndex);
		assertEquals("not added selection?", 1, selectionHelper.getSelectedIndexes().length);
		int i = selectionHelper.getSelectedIndexes()[0];
		assertEquals("wrong selection added?", 0, i);
		
		pressMouseButton(selectionHelper, mouseEvent, selectionIndex);
		assertEquals("not removed selection?", 1, selectionHelper.getSelectedIndexes().length);
	}
	
	private void clickWithShiftModifier() throws Exception
	{
		LinkCell linkCell = project.createLinkCell();
		BendPointSelectionHelper selectionHelper = new BendPointSelectionHelper(linkCell);
		MouseEvent mouseEventShift = getMouseEvent(InputEvent.SHIFT_DOWN_MASK);
		int index1 = 1;
		int index2 = 2;
		int index3 = 3;
		pressMouseButton(selectionHelper, mouseEventShift, index1);
		pressMouseButton(selectionHelper, mouseEventShift, index2);
		pressMouseButton(selectionHelper, mouseEventShift, index3);
		assertEquals("not added selected point?", 3, selectionHelper.getSelectedIndexes().length);
		
		MouseEvent mouseEventNoShift = getMouseEvent(0);
		pressMouseButton(selectionHelper, mouseEventNoShift, index1);
		assertEquals("not added selected point?", 1, selectionHelper.getSelectedIndexes().length);
	}

	private void pressMouseButton(BendPointSelectionHelper selectionHelper, MouseEvent mouseEvent, int selectionIndex)
	{
		selectionHelper.mousePressed(mouseEvent, selectionIndex);
	}
	
	public void testMouseEvent()
	{
		MouseEvent mouseEvent = getMouseEvent(InputEvent.SHIFT_DOWN_MASK);
		assertEquals("shift not down?", true, mouseEvent.isShiftDown());
		assertEquals("wrong click location?", new Point(0, 0), mouseEvent.getPoint());
		assertEquals("control down?", false, mouseEvent.isControlDown());
	}
	
	private MouseEvent getMouseEvent(int modifiersToUse)
	{
		Component source = new JButton();
		int id = 0;
		long when = 0;
		int modifiers = modifiersToUse;
        int x = 0;
        int y = 0;
        int clickCount = 1;
        boolean popupTrigger = false;
        int button = 0;

		return new MouseEvent(source, id, when, modifiers, x, y, clickCount, popupTrigger, button);
	}
	
	ProjectForTesting project;
}