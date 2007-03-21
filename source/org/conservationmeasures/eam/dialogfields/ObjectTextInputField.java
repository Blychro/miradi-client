/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogfields;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.umbrella.CopyTextAction;
import org.conservationmeasures.eam.views.umbrella.CutTextAction;
import org.conservationmeasures.eam.views.umbrella.PasteTextAction;
import org.martus.swing.ResourceImageIcon;
import org.martus.swing.UiTextArea;

public class ObjectTextInputField extends ObjectDataInputField
{
	public ObjectTextInputField(Project projectToUse, int objectType, BaseId objectId, String tag, JTextComponent componentToUse)
	{
		super(projectToUse, objectType, objectId, tag);
		field = componentToUse;
		addFocusListener();
		setEditable(true);
		field.getDocument().addDocumentListener(new DocumentEventHandler());
		field.addMouseListener(new MouseHandler());
	}	

	public JComponent getComponent()
	{
		return field;
	}

	public String getText()
	{
		return field.getText();
	}

	public void setText(String newValue)
	{
		field.setText(newValue);
		clearNeedsSave();
	}
	
	public void updateEditableState()
	{
		boolean editable = allowEdits() && isValidObject();
		field.setEditable(editable);
		Color fg = EAM.EDITABLE_FOREGROUND_COLOR;
		Color bg = EAM.EDITABLE_BACKGROUND_COLOR;
		if(!editable)
		{
			fg = EAM.READONLY_FOREGROUND_COLOR;
			bg = EAM.READONLY_BACKGROUND_COLOR;
		}
		field.setForeground(fg);
		field.setBackground(bg);
	}

	public void focusGained(FocusEvent e)
	{
		field.setSelectionStart(0);
		field.setSelectionEnd(field.getSize().width);
	}
	
	class DocumentEventHandler implements DocumentListener
	{
		public void changedUpdate(DocumentEvent arg0)
		{
			setNeedsSave();
		}

		public void insertUpdate(DocumentEvent arg0)
		{
			setNeedsSave();
		}

		public void removeUpdate(DocumentEvent arg0)
		{
			setNeedsSave();
		}
	}
	
	public void setupFixedSizeTextField(int row, int column)
	{
		JTextComponent textComponent = (JTextComponent)getComponent();
		UiTextArea textArea = new UiTextArea(row,column);
		textComponent.setBorder(textArea.getBorder());
		textComponent.setFont(textArea.getFont());
		int preferredHeight = textComponent.getPreferredSize().height;
		int preferredWidth = textArea.getPreferredSize().width;
		Dimension preferredSize = new Dimension(preferredWidth, preferredHeight);
		textComponent.setPreferredSize(preferredSize);
	}
	
	public class MouseHandler extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			if(e.isPopupTrigger())
				fireRightClick(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			if(e.isPopupTrigger())
				fireRightClick(e);
		}
		
		void fireRightClick(MouseEvent e)
		{
			getRightClickMenu().show(field, e.getX(), e.getY());
		}
		
		public JPopupMenu getRightClickMenu()
		{
			JPopupMenu menu = new JPopupMenu();
			
			JMenuItem menuItemCopy = createMenuItem(new CopyTextAction(field), "icons/copy.gif");
			menuItemCopy.setText(EAM.text("Copy"));
			menu.add(menuItemCopy);
		
			JMenuItem menuItemCut = createMenuItem(new CutTextAction(field), "icons/cut.gif");
			menuItemCut.setText(EAM.text("Cut"));
			menu.add(menuItemCut);
			
			JMenuItem menuItemPaste = createMenuItem(new PasteTextAction(field), "icons/paste.gif");
			menuItemPaste.setText(EAM.text("Paste"));
			menu.add(menuItemPaste);
			
			return menu;
		}
		
		private JMenuItem createMenuItem(Action action, String iconLocation)
		{
			JMenuItem menuItem = new JMenuItem(action);
			ResourceImageIcon icon = new ResourceImageIcon(iconLocation);
			menuItem.setIcon(icon);
			
			return menuItem;
		}
	}
	
	JTextComponent field;
}
