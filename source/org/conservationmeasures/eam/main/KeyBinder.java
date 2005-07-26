/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.main;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class KeyBinder
{
	public static final int KEY_MODIFIER_NONE = 0;
	public static final int KEY_MODIFIER_CTRL = KeyEvent.CTRL_DOWN_MASK;
	
	public static void bindKey(JComponent component, int key, int keyModifier, Action contextMenuAction)
	{
		String thisName = (String)contextMenuAction.getValue(Action.NAME);
		component.getActionMap().put(thisName, contextMenuAction);
		component.getInputMap().put(KeyStroke.getKeyStroke(key, keyModifier), thisName);
	}

}
