/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.utils;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.martus.swing.UiTextField;

public class UiTextFieldWithLengthLimit extends UiTextField
{
	public UiTextFieldWithLengthLimit(int maxLength)
	{
		super(maxLength);
		setDocument(new PlainDocumentWithLengthLimit(maxLength));
	}
}

class PlainDocumentWithLengthLimit extends PlainDocument 
{
	public PlainDocumentWithLengthLimit(int maxLengthToUse) 
	{
		maxLength = maxLengthToUse;
	}
	
	public void insertString(int param, String str, javax.swing.text.AttributeSet attributeSet) throws BadLocationException 
	{
		int proposedLength = this.getLength() + str.length();
		if (str != null && proposedLength > maxLength) 
		{
			java.awt.Toolkit.getDefaultToolkit().beep();
			return;
		}
 
		super.insertString(param, str, attributeSet);
	}
	
	private int maxLength;
}
