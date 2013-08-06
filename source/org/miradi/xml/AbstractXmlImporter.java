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
package org.miradi.xml;

import java.awt.Point;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.martus.util.UnicodeReader;
import org.martus.util.inputstreamwithseek.InputStreamWithSeek;
import org.miradi.exceptions.UnsupportedNewVersionSchemaException;
import org.miradi.exceptions.ValidationException;
import org.miradi.exceptions.XmpzVersionTooOldException;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.objectdata.BooleanData;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.utils.CodeList;
import org.miradi.utils.XmlUtilities2;
import org.miradi.xml.wcs.Xmpz1XmlConstants;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

abstract public class AbstractXmlImporter
{
	public AbstractXmlImporter(Project projectToFill) throws Exception
	{
		project = projectToFill;
	}
		
	public void importProject(InputStreamWithSeek projectAsInputStream) throws Exception
	{
		loadXml(projectAsInputStream);
		importXml();
	}
	
	private void loadXml(InputStreamWithSeek projectAsInputStream) throws Exception
	{
		InputSource inputSource = new InputSource(projectAsInputStream);
		document = createDocument(inputSource);
				
		String nameSpaceUri = getNamespaceURI();
		if (!isSameNameSpace(nameSpaceUri))
		{
			throw new Exception("Name space mismatch should be: " + getPartialNameSpace() + " <br> however it is: " + nameSpaceUri); 
		}
				
		if (isUnsupportedNewVersion(nameSpaceUri))
		{
			throw new UnsupportedNewVersionSchemaException();
		}
		
		if (isUnsupportedOldVersion(nameSpaceUri))
		{
			throw new XmpzVersionTooOldException(getNameSpaceVersion(), getSchemaVersionToImport(nameSpaceUri));
		}
		
		projectAsInputStream.seek(0);
		EAM.logVerbose("XML being imported:");
		EAM.logVerbose(getXmlTextForDebugging(projectAsInputStream));
		
		if (!createXmlValidator().isValid(projectAsInputStream))
		{
			throw new ValidationException(EAM.text("File to import does not validate."));
		}
		
		xPath = createXPath();
	}

	private String getXmlTextForDebugging(InputStreamWithSeek projectAsInputStream) throws Exception
	{
		UnicodeReader reader = new UnicodeReader(projectAsInputStream);
		String xml = reader.readAll();
		reader.close();
		projectAsInputStream.seek(0);
		return xml;
	}

	public boolean isTrue(String value)
	{
		if (value.length() == 0)
			return false;
		
		if (value.equals(BooleanData.BOOLEAN_TRUE))
			return true;
		
		if (value.equals(BooleanData.BOOLEAN_FALSE))
			return false;
		
		return Boolean.parseBoolean(value);
	}

	private Document createDocument(InputSource inputSource) throws Exception
	{
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		documentFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		
		return documentBuilder.parse(inputSource);
	}

	private XPath createXPath()
	{
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath  thisXPath = xPathFactory.newXPath();
		thisXPath.setNamespaceContext(getNamespaceContext());
		
		return thisXPath;
	}
	
	public String escapeDueToParserUnescaping(String data)
	{
		return XmlUtilities2.getXmlEncoded(data);
	}
	
	protected Point extractPointFromNode(Node pointNode) throws Exception
	{
		Node xNode = getNamedChildNode(pointNode, Xmpz1XmlConstants.X_ELEMENT_NAME);
		Node yNode = getNamedChildNode(pointNode, Xmpz1XmlConstants.Y_ELEMENT_NAME);
		int x = extractNodeTextContentAsInt(xNode);
		int y = extractNodeTextContentAsInt(yNode);
		
		return new Point(x, y);
	}
	
	protected int extractNodeTextContentAsInt(Node node) throws Exception
	{
		return Integer.parseInt(node.getTextContent());
	}

	public void importField(Node node, String path, ORef ref, String destinationTag) throws Exception
	{
		importField(node, new String[]{path,}, ref, destinationTag);
	}
	
	private void importField(Node node, String[] elements, ORef ref, String destinationTag) throws Exception 
	{
		String data = getPathData(node, elements);
		data = escapeDueToParserUnescaping(data);
		importField(ref, destinationTag, data);
	}

	protected void importField(ORef ref, String destinationTag, String data)	throws Exception
	{
		setData(ref, destinationTag, data);
	}
	
	public void setData(BaseObject baseObject, String tag, String data) throws Exception
	{
		setData(baseObject.getRef(), tag, data);
	}
	
	public void setData(ORef ref, String tag, int data) throws Exception
	{
		setData(ref, tag, Integer.toString(data));
	}
		
	public void setData(ORef ref, String tag, ORefList refList) throws Exception
	{
		setData(ref, tag, refList.toString());
	}
	
	public void setData(ORef ref, String tag, IdList idList) throws Exception
	{
		setData(ref, tag, idList.toString());
	}
	
	public void setData(ORef ref, String tag, CodeList codes) throws Exception
	{
		setData(ref, tag, codes.toString());
	}
	
	public void setData(ORef ref, String tag, String data) throws Exception
	{
		getProject().setObjectData(ref, tag, data.trim());
	}

	public String generatePath(String[] pathElements)
	{
		StringBuffer path = new StringBuffer();
		for (int index = 0; index < pathElements.length; ++index)
		{
			String elementName = pathElements[index];
			String localName = "*[local-name()='" + elementName + "']";
			path.append(localName);
			if ((index + 1) < pathElements.length)
				path.append("/");
		}
		
		return path.toString();
	}

	public String[] extractNodesAsList(String path) throws Exception
	{
		XPathExpression expression = getXPath().compile(path);
		NodeList nodeList = (NodeList) expression.evaluate(getDocument(), XPathConstants.NODESET);
		Vector<String> nodes = new Vector<String>();
		for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); ++nodeIndex) 
		{
			nodes.add(nodeList.item(nodeIndex).getTextContent());
		}
		
		return nodes.toArray(new String[0]);
	}
	
	public String getAttributeValue(Node elementNode, String attributeName)
	{
		NamedNodeMap attributes = elementNode.getAttributes();
		Node attributeNode = attributes.getNamedItem(attributeName);
		return attributeNode.getNodeValue();
	}
	
	public ORef getNodeAsRef(Node node, String element, int type) throws Exception
	{
		// FIXME low - Why do we need to trim??.  We have this fixme in other places and need to figure out
		//why a new line has been appended.  Export is not appending a new line and has been verified in output.s
		String trimmedIdAsString = getNamedChildNodeContent(node, element).trim();
		return new ORef(type, new BaseId(trimmedIdAsString));
	}
	
	public Node getNode(String path) throws Exception
	{
		XPathExpression expression = getXPath().compile(path);
		return (Node) expression.evaluate(getDocument(), XPathConstants.NODE);
	}
	
	protected String getNamedChildNodeContent(Node parentNode, String childNodeName) throws Exception
	{
		Node foundNode = getNamedChildNode(parentNode, childNodeName);
		return getSafeNodeContent(foundNode);
	}
	
	public String getSafeNodeContent(Node foundNode)
	{
		if (foundNode == null)
			return "";
		
		return foundNode.getTextContent();
	}
	
	public Node getRootNode() throws Exception
	{
		return getNode(generatePath(new String[]{getRootNodeName()}));
	}
	
	public String getPathData(Node node, String xpathExpression) throws Exception
	{
		return getPathData(node, new String[]{xpathExpression, });
	}

	public Node getNamedChildNode(Node parent, String childNodeName)
	{
		NodeList childNodes = parent.getChildNodes();
		for(int index = 0; index < childNodes.getLength(); ++index)
		{
			Node child = childNodes.item(index);
			String childName = child.getLocalName();
			if(childName != null && childName.equals(childNodeName))
				return child;
		}
		
		return null;
	}
	
	public Node getNode(Node node, String[] xpathExpressions) throws Exception
	{
		return (Node) evaluate(node, xpathExpressions, XPathConstants.NODE);
	}
	
	public NodeList getNamedChildNodes(Node node, String childNodeName) throws Exception
	{
		return getNodes(node, new String[]{childNodeName, });
	}
	
	public NodeList getNodes(Node node, String[] xpathExpressions) throws Exception
	{
		return (NodeList) evaluate(node, xpathExpressions, XPathConstants.NODESET);
	}

	private Object evaluate(Node node, String[] xpathExpressions, final QName qName) throws Exception
	{
		String generatedPath = "";
		try
		{
			generatedPath = generatePath(xpathExpressions);
			XPathExpression expression = getXPath().compile(generatedPath);

			return expression.evaluate(node, qName);
		}
		catch (Exception e)
		{
			throw createNewExceptionWithPathData(e, generatedPath);
		}
	}

	public String getPathData(Node node, String[] xpathExpressions) throws Exception
	{
		String generatedPath = "";
		try
		{
			generatedPath = generatePath(xpathExpressions);
			return getXPath().evaluate(generatedPath, node);
		}
		catch (Exception e)
		{
			throw createNewExceptionWithPathData(e, generatedPath);
		}
	}
	
	private Exception createNewExceptionWithPathData(Exception exception, String generatedPath)
	{
		return new Exception(createErrorMessage(generatedPath), exception);
	}

	private String createErrorMessage(String generatedPath)
	{
		return "Exception thrown trying to evalulate path:" + generatedPath;
	}
	
	private boolean isUnsupportedNewVersion(String nameSpaceUri) throws Exception
	{
		return getSchemaVersionToImport(nameSpaceUri).compareTo(getNameSpaceVersion()) > 0;
	}

	private boolean isUnsupportedOldVersion(String nameSpaceUri) throws Exception
	{
		return getSchemaVersionToImport(nameSpaceUri).compareTo(getNameSpaceVersion()) < 0;
	}

	private boolean isSameNameSpace(String nameSpaceUri) throws Exception
	{
		return nameSpaceUri.startsWith(getPartialNameSpace());
	}
	
	private String getSchemaVersionToImport(String nameSpaceUri) throws Exception
	{
		int lastSlashIndexBeforeVersion = nameSpaceUri.lastIndexOf("/");
		String versionAsString = nameSpaceUri.substring(lastSlashIndexBeforeVersion + 1, nameSpaceUri.length());
		
		return versionAsString;
	}
	
	protected ORef getProjectMetadataRef()
	{
		return getProject().getMetadata().getRef();
	}
	
	private String getNamespaceURI()
	{
		return getDocument().getDocumentElement().getNamespaceURI();
	}
	
	public Document getDocument()
	{
		return document;
	}

	public XPath getXPath()
	{
		return xPath;
	}
	
	public Project getProject()
	{
		return project;
	}
	
	abstract protected void importXml() throws Exception;
	
	abstract protected String getRootNodeName();
	
	abstract protected String getNameSpaceVersion();
	
	abstract protected String getPartialNameSpace();
	
	abstract public AbstractXmlNamespaceContext getNamespaceContext();
	
	abstract protected MiradiXmlValidator createXmlValidator();

	private Project project;
	private XPath xPath;
	private Document document;	
}
