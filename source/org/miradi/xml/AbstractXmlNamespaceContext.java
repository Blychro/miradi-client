/* 
Copyright 2005-2018, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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

package org.miradi.xml;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

abstract public class AbstractXmlNamespaceContext implements NamespaceContext
{
	public String getNamespaceURI(String prefix) 
	{
		if (prefix == null) 
			throw new NullPointerException("Null prefix");
		
		else if (prefix.equals(getPrefix())) 
			return getNameSpace();
		
		else if ("xml".equals(prefix)) 
			return XMLConstants.XML_NS_URI;
		
		return XMLConstants.NULL_NS_URI;
	}

	//NOTE: This method isn't necessary for XPath processing.
	public String getPrefix(String uri) 
	{
		throw new UnsupportedOperationException();
	}

	//NOTE: This method isn't necessary for XPath processing either.
	public Iterator getPrefixes(String uri) 
	{
		throw new UnsupportedOperationException();
	}
	
	abstract protected String getNameSpace();

	abstract protected String getPrefix();
}
