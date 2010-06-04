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

package org.miradi.xml;

import org.miradi.objecthelpers.ORef;
import org.miradi.objects.TncProjectData;
import org.miradi.objects.WcpaProjectData;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.utils.CodeList;
import org.miradi.xml.generic.XmlSchemaCreator;
import org.miradi.xml.wcs.TagToElementNameMap;
import org.miradi.xml.wcs.WcsXmlConstants;
import org.miradi.xml.xmpz.XmpzXmlImporter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

abstract public class AbstractXmpzObjectImporter
{
	public AbstractXmpzObjectImporter(XmpzXmlImporter importerToUse, String poolNameToUse)
	{
		importer = importerToUse;
		poolName = poolNameToUse;
	}
	
	protected XmpzXmlImporter getImporter()
	{
		return importer;
	}
	
	protected ORef getMetadataRef()
	{
		return getImporter().getProjectMetadataRef();
	}
	
	protected ORef getWcpaProjectDataRef()
	{
		return getProject().getSingletonObjectRef(WcpaProjectData.getObjectType());
	}
	
	protected ORef getTncProjectDataRef()
	{
		return getProject().getSingletonObjectRef(TncProjectData.getObjectType());
	}
	
	public Project getProject()
	{
		return getImporter().getProject();
	}
	
	protected void importCodeField(Node node, String elementContainerName, ORef destinationRef, String destinationTag, ChoiceQuestion question) throws Exception
	{
		TagToElementNameMap map = new TagToElementNameMap();
		String elementName = map.findElementName(elementContainerName, destinationTag);
		getImporter().importCodeField(node, elementContainerName + elementName, destinationRef, destinationTag, question);
	}

	protected void importCodeListField(Node node, String elementContainerName, ORef destinationRef, String destinationTag) throws Exception
	{
		TagToElementNameMap map = new TagToElementNameMap();
		String elementName = map.findElementName(elementContainerName, destinationTag);
		NodeList codeNodes = getImporter().getNodes(node, new String[]{elementContainerName + elementName + WcsXmlConstants.CONTAINER_ELEMENT_TAG, XmlSchemaCreator.CODE_ELEMENT_NAME});
		CodeList codesToImport = new CodeList();
		for (int index = 0; index < codeNodes.getLength(); ++index)
		{
			Node codeNode = codeNodes.item(index);
			String code = getImporter().getSafeNodeContent(codeNode);
			codesToImport.add(code);
		}
		
		getImporter().setData(destinationRef, destinationTag, codesToImport.toString());
	}

	protected void importField(Node node, ORef destinationRef, String destinationTag) throws Exception
	{
		TagToElementNameMap map = new TagToElementNameMap();
		String elementName = map.findElementName(getPoolName(), destinationTag);
		getImporter().importField(node, getPoolName() + elementName, destinationRef, destinationTag);
	}
	
	protected void importField(Node node, String elementName, ORef destinationRef, String destinationTag) throws Exception
	{
		getImporter().importField(node, elementName, destinationRef, destinationTag);
	}
	
	public String getPoolName()
	{
		return poolName;
	}
	
	abstract public void importElement() throws Exception;
	
	private XmpzXmlImporter importer;
	private String poolName;
}