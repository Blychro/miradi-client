/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.xml.xmpz;

import org.miradi.objects.ProjectMetadata;
import org.miradi.xml.AbstractXmpzObjectImporter;
import org.miradi.xml.wcs.WcsXmlConstants;
import org.w3c.dom.Node;

public class ProjectSummaryImporter extends AbstractXmpzObjectImporter
{
	public ProjectSummaryImporter(XmpzXmlImporter importerToUse)
	{
		super(importerToUse, WcsXmlConstants.PROJECT_SUMMARY);
	}
	
	@Override
	public void importElement() throws Exception
	{
		Node projectSumaryNode = getImporter().getNode(getImporter().getRootNode(), WcsXmlConstants.PROJECT_SUMMARY);
				
		importProjectMetadataField(projectSumaryNode, ProjectMetadata.TAG_PROJECT_NAME);
		importProjectMetadataField(projectSumaryNode, ProjectMetadata.TAG_PROJECT_LANGUAGE);
		importProjectMetadataField(projectSumaryNode, ProjectMetadata.TAG_DATA_EFFECTIVE_DATE);
		importProjectMetadataField(projectSumaryNode, ProjectMetadata.TAG_OTHER_ORG_PROJECT_NUMBER);
		importProjectMetadataField(projectSumaryNode, ProjectMetadata.TAG_OTHER_ORG_RELATED_PROJECTS);
		importProjectMetadataField(projectSumaryNode, ProjectMetadata.TAG_PROJECT_URL);
		importProjectMetadataField(projectSumaryNode, ProjectMetadata.TAG_PROJECT_DESCRIPTION);
		importProjectMetadataField(projectSumaryNode, ProjectMetadata.TAG_PROJECT_STATUS);
		importProjectMetadataField(projectSumaryNode, ProjectMetadata.TAG_NEXT_STEPS);
	}

	private void importProjectMetadataField(Node projectSummaryNode, String tag) throws Exception
	{
		importField(projectSummaryNode, getMetadataRef(), tag);
	}
}
