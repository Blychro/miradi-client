/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.xml.generic;

import org.miradi.objects.Organization;
import org.miradi.xml.wcs.XmpzXmlConstants;

public class OrganizationObjectSchemaElement extends BaseObjectSchemaElementWithLabel
{
	public OrganizationObjectSchemaElement()
	{
		super(XmpzXmlConstants.ORGANIZATION);
		
		createOptionalTextField(Organization.TAG_SHORT_LABEL);
		createOptionalTextField(Organization.TAG_ROLES_DESCRIPTION);
		createOptionalTextField(Organization.TAG_CONTACT_FIRST_NAME);
		createOptionalTextField(Organization.TAG_CONTACT_LAST_NAME);
		createOptionalTextField(Organization.TAG_EMAIL);
		createOptionalTextField(Organization.TAG_PHONE_NUMBER);
		createOptionalTextField(Organization.TAG_COMMENTS);
	}
}
