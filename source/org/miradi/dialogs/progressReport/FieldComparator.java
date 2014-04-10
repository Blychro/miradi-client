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

package org.miradi.dialogs.progressReport;

import java.util.Comparator;

import org.miradi.objects.BaseObject;
import org.miradi.utils.BaseObjectFieldComparator;

public class FieldComparator implements Comparator<BaseObject>
{
	public FieldComparator(String tagToUse)
	{
		tag = tagToUse;
	}
	
	public int compare(BaseObject baseObject1, BaseObject baseObject2)
	{
		return BaseObjectFieldComparator.compare(baseObject1, baseObject2, tag);
	}	
	
	private String tag;
}