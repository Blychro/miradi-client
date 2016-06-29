/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
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

package org.miradi.xml.xmpz2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.martus.util.inputstreamwithseek.InputStreamWithSeek;
import org.martus.util.inputstreamwithseek.StringInputStreamWithSeek;
import org.miradi.exceptions.XmlVersionTooOldException;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.migrations.forward.MigrationTo10;
import org.miradi.migrations.forward.MigrationTo11;
import org.miradi.migrations.forward.MigrationTo19;
import org.miradi.migrations.forward.MigrationTo20;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.questions.DayColumnsVisibilityQuestion;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.utils.BiDirectionalHashMap;
import org.miradi.utils.HtmlUtilities;
import org.miradi.utils.StringUtilities;
import org.miradi.utils.XmlUtilities2;
import org.miradi.xml.AbstractXmlImporter;
import org.miradi.xml.xmpz2.objectExporters.ExtraDataExporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Xmpz2ForwardMigration
{
	public Xmpz2MigrationResult migrate(InputStreamWithSeek projectAsInputStream) throws Exception
	{
		// note that this xmpz2 migration only handles structural migrations required to ensure that the resulting xml validates according to the current schema (rnc)
		// project migrations are handled subsequently to this as part of the project opening processes (via MigrationManager)

		Document document = convertToDocument(projectAsInputStream);
		Element rootElement = document.getDocumentElement();
		int xmpz2DocumentSchemaVersion = getXmpz2DocumentSchemaVersion(rootElement);
		boolean schemaVersionWasUpdated = updateXmpz2SchemaVersionToCurrentVersion(rootElement);
		removeLegacyTncFields(rootElement);
		removeHumanWellbeingTargetCalculatedThreatRatingElement(rootElement);
		renameTncFields(document);
		renameLeaderResourceFields(document);
		adjustWhoWhenAssignedFields(document);
		addDayColumnsVisibilityField(document);
		moveIndicatorWorkPlanDataToExtraData(document);

		final String migratedXmlAsString = HtmlUtilities.toXmlString(document);

		return new Xmpz2MigrationResult(new StringInputStreamWithSeek(migratedXmlAsString), schemaVersionWasUpdated, xmpz2DocumentSchemaVersion);
	}

	private void moveIndicatorWorkPlanDataToExtraData(Document document) throws Exception
	{
		Element rootElement = document.getDocumentElement();

		Node indicatorPool = findNode(rootElement.getChildNodes(), Xmpz2XmlWriter.createPoolElementName(Xmpz2XmlConstants.INDICATOR));
		if (indicatorPool != null)
		{
			NodeList indicatorNodes = indicatorPool.getChildNodes();
			for (int index = 0; index < indicatorNodes.getLength(); ++index)
			{
				Node indicatorNode = indicatorNodes.item(index);
				if (indicatorNode != null && indicatorNode.getNodeType() == Node.ELEMENT_NODE)
				{
					removeChildren(indicatorNode, new String[]{Xmpz2XmlConstants.INDICATOR + Xmpz2XmlConstants.TIME_PERIOD_COSTS,});

					// note: work plan data is deprecated for indicators but we need to keep it around for migrations
					// see related changes in Xmpz2ExtraDataImporter where these fields are imported

					moveIndicatorAssignedLeaderResourceIdToExtraData(document, indicatorNode);
					moveIndicatorResourceAssignmentIdListToExtraData(document, indicatorNode);
					moveIndicatorExpenseAssignmentRefListToExtraData(document, indicatorNode);
				}
			}
		}
	}

	private void moveIndicatorAssignedLeaderResourceIdToExtraData(Document document, Node indicatorNode) throws Exception
	{
		String idAsString = getAttributeValue(indicatorNode, Xmpz2XmlConstants.ID);

		String assignedLeaderResourceIdElementName = Xmpz2XmlConstants.INDICATOR + Xmpz2XmlConstants.ASSIGNED_LEADER_RESOURCE_ID;
		String tagName = BaseObject.TAG_ASSIGNED_LEADER_RESOURCE;

		Node assignedLeaderResourceIdNode = findNode(indicatorNode.getChildNodes(), assignedLeaderResourceIdElementName);
		if (assignedLeaderResourceIdNode != null && assignedLeaderResourceIdNode.getNodeType() == Node.ELEMENT_NODE)
		{
			NodeList resourceIdNodes = assignedLeaderResourceIdNode.getChildNodes();

			if (resourceIdNodes.getLength() == 1)
			{
				Node resourceIdNode = resourceIdNodes.item(0);
				String resourceIdAsString = resourceIdNode.getTextContent();
				ORef leaderRef = new ORef(ObjectType.PROJECT_RESOURCE, new BaseId(resourceIdAsString));
				String extraDataItemName = ExtraDataExporter.getExtraDataItemName(IndicatorSchema.OBJECT_NAME, new BaseId(idAsString), tagName);
				String extraDataItemValue = leaderRef.toJson().toString();
				moveDataToExtraData(document, extraDataItemName, extraDataItemValue);
			}

			indicatorNode.removeChild(assignedLeaderResourceIdNode);
		}
	}

	private void moveIndicatorResourceAssignmentIdListToExtraData(Document document, Node indicatorNode) throws Exception
	{
		String idAsString = getAttributeValue(indicatorNode, Xmpz2XmlConstants.ID);

		String resourceAssignmentIdsElementName = Xmpz2XmlConstants.INDICATOR + Xmpz2XmlConstants.RESOURCE_ASSIGNMENT + Xmpz2XmlConstants.IDS;
		String tagName = BaseObject.TAG_RESOURCE_ASSIGNMENT_IDS;

		Node resourceAssignmentIdsNode = findNode(indicatorNode.getChildNodes(), resourceAssignmentIdsElementName);
		if (resourceAssignmentIdsNode != null && resourceAssignmentIdsNode.getNodeType() == Node.ELEMENT_NODE)
		{
			IdList idList = new IdList(ObjectType.RESOURCE_ASSIGNMENT);

			NodeList resourceAssignmentIdNodes = resourceAssignmentIdsNode.getChildNodes();
			for (int index = 0; index < resourceAssignmentIdNodes.getLength(); ++index)
			{
				Node resourceAssignmentIdNode = resourceAssignmentIdNodes.item(index);
				if (resourceAssignmentIdNode != null && resourceAssignmentIdNode.getNodeType() == Node.ELEMENT_NODE)
				{
					String resourceAssignmentId = resourceAssignmentIdNode.getTextContent().trim();
					idList.add(new BaseId(resourceAssignmentId));
				}
			}

			String extraDataItemName = ExtraDataExporter.getExtraDataItemName(IndicatorSchema.OBJECT_NAME, new BaseId(idAsString), tagName);
			String extraDataItemValue = idList.toJson().toString();
			moveDataToExtraData(document, extraDataItemName, extraDataItemValue);

			indicatorNode.removeChild(resourceAssignmentIdsNode);
		}
	}

	private void moveIndicatorExpenseAssignmentRefListToExtraData(Document document, Node indicatorNode) throws Exception
	{
		String idAsString = getAttributeValue(indicatorNode, Xmpz2XmlConstants.ID);

		String expenseAssignmentIdsTagName = Xmpz2XmlConstants.INDICATOR + Xmpz2XmlConstants.EXPENSE_ASSIGNMENT + Xmpz2XmlConstants.IDS;
		String tagName = BaseObject.TAG_EXPENSE_ASSIGNMENT_REFS;

		Node expenseAssignmentIdsNode = findNode(indicatorNode.getChildNodes(), expenseAssignmentIdsTagName);
		if (expenseAssignmentIdsNode != null && expenseAssignmentIdsNode.getNodeType() == Node.ELEMENT_NODE)
		{
			ORefList refList = new ORefList();

			NodeList expenseAssignmentIdNodes = expenseAssignmentIdsNode.getChildNodes();
			for (int index = 0; index < expenseAssignmentIdNodes.getLength(); ++index)
			{
				Node expenseAssignmentIdNode = expenseAssignmentIdNodes.item(index);
				if (expenseAssignmentIdNode != null && expenseAssignmentIdNode.getNodeType() == Node.ELEMENT_NODE)
				{
					String expenseAssignmentId = expenseAssignmentIdNode.getTextContent().trim();
					refList.add(new ORef(ObjectType.EXPENSE_ASSIGNMENT, new BaseId(expenseAssignmentId)));
				}
			}

			String extraDataItemName = ExtraDataExporter.getExtraDataItemName(IndicatorSchema.OBJECT_NAME, new BaseId(idAsString), tagName);
			String extraDataItemValue = refList.toJson().toString();
			moveDataToExtraData(document, extraDataItemName, extraDataItemValue);

			indicatorNode.removeChild(expenseAssignmentIdsNode);
		}
	}

	private void moveDataToExtraData(Document document, String extraDataItemName, String extraDataItemValue) throws Exception
	{
		Element rootElement = document.getDocumentElement();
		final String alias = getNameSpaceAliasName(document.getDocumentElement());

		Node extraDataNode = findNode(rootElement.getChildNodes(), Xmpz2XmlConstants.EXTRA_DATA);
		if (extraDataNode != null && extraDataNode.getNodeType() == Node.ELEMENT_NODE)
		{
			NodeList extraDataSectionNodes = extraDataNode.getChildNodes();
			for (int index = 0; index < extraDataSectionNodes.getLength(); ++index)
			{
				Node extraDataSectionNode = extraDataSectionNodes.item(index);
				if (extraDataSectionNode != null && extraDataSectionNode.getNodeType() == Node.ELEMENT_NODE)
				{
					String owner = getAttributeValue(extraDataSectionNode, Xmpz2XmlConstants.EXTRA_DATA_SECTION_OWNER_ATTRIBUTE);
					if (owner.equals(Xmpz2XmlConstants.MIRADI_CLIENT_EXTRA_DATA_SECTION))
					{
						Node newExtraDataItemNode = document.createElement(alias + COLON +  Xmpz2XmlConstants.EXTRA_DATA_ITEM);
						((Element) newExtraDataItemNode).setAttribute(Xmpz2XmlConstants.EXTRA_DATA_ITEM_NAME, extraDataItemName);

						Node newExtraDataItemValueNode = document.createElement(alias + COLON + Xmpz2XmlConstants.EXTRA_DATA_ITEM_VALUE);

						extraDataItemValue = StringUtilities.escapeQuotesWithBackslash(extraDataItemValue);
						extraDataItemValue = XmlUtilities2.getXmlEncoded(extraDataItemValue);
						newExtraDataItemValueNode.setTextContent(extraDataItemValue);
						newExtraDataItemNode.appendChild(newExtraDataItemValueNode);

						extraDataSectionNode.appendChild(newExtraDataItemNode);

						break;
					}
				}
			}
		}
	}

	private void renameTncFields(Document document) throws Exception
	{
		Element rootElement = document.getDocumentElement();
		Node tncProjectDataNode = findNode(rootElement.getChildNodes(), Xmpz2XmlConstants.TNC_PROJECT_DATA);
		if (tncProjectDataNode != null)
		{	
			BiDirectionalHashMap legacyToNewTncFieldNamesMap = createLegacyTncToNewFieldNamesMap();
			renameElements(document, tncProjectDataNode, legacyToNewTncFieldNamesMap);
		}
	}
	
	private BiDirectionalHashMap createLegacyTncToNewFieldNamesMap()
	{
		BiDirectionalHashMap oldToNewTagMap = new BiDirectionalHashMap();
		oldToNewTagMap.put(Xmpz2XmlConstants.TNC_PROJECT_DATA + MigrationTo10.LEGACY_TAG_MAKING_THE_CASE, Xmpz2XmlConstants.TNC_PROJECT_DATA + MigrationTo10.TAG_OVERALL_PROJECT_GOAL);
		oldToNewTagMap.put(Xmpz2XmlConstants.TNC_PROJECT_DATA + MigrationTo10.LEGACY_TAG_CAPACITY_AND_FUNDING, Xmpz2XmlConstants.TNC_PROJECT_DATA + MigrationTo10.TAG_FINANCIAL_PLAN);
		
		return oldToNewTagMap;
	}

	private void renameElements(Document document, Node parentNode, BiDirectionalHashMap fromToNameMap) throws Exception
	{
		final String alias = getNameSpaceAliasName(document.getDocumentElement());
		HashSet<String> keys = fromToNameMap.getKeys();
		for(String fromName : keys)
		{
			Node childNode = findNode(parentNode, fromName);
			if (childNode == null)
				continue;

			String textToTransferToNewNode = childNode.getTextContent();
			parentNode.removeChild(childNode);
			final String toName = fromToNameMap.getValue(fromName);
			Node newNode = document.createElement(alias + COLON +  toName);
			newNode.setTextContent(textToTransferToNewNode);
			parentNode.appendChild(newNode);
		}
	}

	private void renameLeaderResourceFields(Document document) throws Exception
	{
		Element rootElement = document.getDocumentElement();

		Node strategyPool = findNode(rootElement.getChildNodes(), Xmpz2XmlWriter.createPoolElementName(Xmpz2XmlConstants.STRATEGY));
		BiDirectionalHashMap oldToNewTagMap = createLeaderResourceToNewFieldNamesMap(Xmpz2XmlConstants.STRATEGY);
		renameLeaderResourceFields(document, strategyPool, oldToNewTagMap);

		Node taskPool = findNode(rootElement.getChildNodes(), Xmpz2XmlWriter.createPoolElementName(Xmpz2XmlConstants.TASK));
		oldToNewTagMap = createLeaderResourceToNewFieldNamesMap(Xmpz2XmlConstants.TASK);
		renameLeaderResourceFields(document, taskPool, oldToNewTagMap);

		Node indicatorPool = findNode(rootElement.getChildNodes(), Xmpz2XmlWriter.createPoolElementName(Xmpz2XmlConstants.INDICATOR));
		oldToNewTagMap = createLeaderResourceToNewFieldNamesMap(Xmpz2XmlConstants.INDICATOR);
		renameLeaderResourceFields(document, indicatorPool, oldToNewTagMap);
	}

	private void renameLeaderResourceFields(Document document, Node objectPool, BiDirectionalHashMap oldToNewTagMap) throws Exception
	{
		if (objectPool == null)
			return;

		NodeList children = objectPool.getChildNodes();
		for (int index = 0; index < children.getLength(); ++index)
		{
			Node childNode = children.item(index);
			if (childNode != null)
				replaceElements(document, childNode, oldToNewTagMap);
		}
	}

	private BiDirectionalHashMap createLeaderResourceToNewFieldNamesMap(String objectName)
	{
		BiDirectionalHashMap oldToNewTagMap = new BiDirectionalHashMap();
		oldToNewTagMap.put(objectName + MigrationTo19.LEGACY_TAG_LEADER_RESOURCE + Xmpz2XmlConstants.ID, objectName + MigrationTo19.TAG_ASSIGNED_LEADER_RESOURCE + Xmpz2XmlConstants.ID);

		return oldToNewTagMap;
	}

	private void adjustWhoWhenAssignedFields(Document document) throws Exception
	{
		Element rootElement = document.getDocumentElement();
		final String alias = getNameSpaceAliasName(document.getDocumentElement());

		Node planningViewConfigurationPool = findNode(rootElement.getChildNodes(), Xmpz2XmlWriter.createPoolElementName(Xmpz2XmlConstants.OBJECT_TREE_TABLE_CONFIGURATION));

		if (planningViewConfigurationPool == null)
			return;

		NodeList planningViewConfigurationNodes = planningViewConfigurationPool.getChildNodes();
		for (int index = 0; index < planningViewConfigurationNodes.getLength(); ++index)
		{
			Node planningViewConfiguration = planningViewConfigurationNodes.item(index);
			if (planningViewConfiguration != null)
			{
				Node columnNamesContainer = findNode(planningViewConfiguration.getChildNodes(), Xmpz2XmlConstants.OBJECT_TREE_TABLE_CONFIGURATION + Xmpz2XmlConstants.COLUMN_CONFIGURATION_CODES + Xmpz2XmlConstants.CONTAINER_ELEMENT_TAG);
				if (columnNamesContainer != null)
				{
					ArrayList<String> codesToKeep = new ArrayList<String>();

					NodeList codeList = columnNamesContainer.getChildNodes();
					int codeCount = codeList.getLength();

					for (int i = 0; i < codeCount; ++i)
					{
						Node code = codeList.item(i);
						String codeValue = code.getTextContent().trim().replaceAll(StringUtilities.NEW_LINE, StringUtilities.EMPTY_SPACE);
						if (codeValue.equals(MigrationTo20.LEGACY_READABLE_ASSIGNED_WHEN_TOTAL_CODE))
							codesToKeep.add(MigrationTo20.READABLE_TIMEFRAME_TOTAL_CODE);
						else if (codeValue.equals(MigrationTo20.LEGACY_READABLE_ASSIGNED_WHO_TOTAL_CODE));
							// skip - will be deleted
						else
							codesToKeep.add(codeValue);
					}

					for (int i = 0; i < codeCount; ++i)
					{
						removeChildren(columnNamesContainer, new String[]{Xmpz2XmlConstants.CODE_ELEMENT_NAME,});
					}

					for (String code : codesToKeep)
					{
						if (!code.isEmpty())
						{
							Node newNode = document.createElement(alias + COLON +  Xmpz2XmlConstants.CODE_ELEMENT_NAME);
							newNode.setTextContent(code);
							columnNamesContainer.appendChild(newNode);
						}
					}
				}
			}
		}
	}

	private void addDayColumnsVisibilityField(Document document) throws Exception
	{
		Element rootElement = document.getDocumentElement();

		Node projectPlanningElement = findNode(rootElement.getChildNodes(), Xmpz2XmlConstants.PROJECT_SUMMARY_PLANNING);

		if (projectPlanningElement == null)
			return;

		Node dayColumnsVisibilityElement = findNode(projectPlanningElement.getChildNodes(), Xmpz2XmlConstants.PROJECT_SUMMARY_PLANNING + Xmpz2XmlConstants.DAY_COLUMNS_VISIBILITY);
		if (dayColumnsVisibilityElement == null)
		{
			final String alias = getNameSpaceAliasName(document.getDocumentElement());
			Node newNode = document.createElement(alias + COLON +  Xmpz2XmlConstants.PROJECT_SUMMARY_PLANNING + Xmpz2XmlConstants.DAY_COLUMNS_VISIBILITY);
			newNode.setTextContent(DayColumnsVisibilityQuestion.SHOW_DAY_COLUMNS_CODE_READABLE);
			projectPlanningElement.appendChild(newNode);
		}
	}

	private void replaceElements(Document document, Node parentNode, BiDirectionalHashMap fromToNameMap) throws Exception
	{
		final String alias = getNameSpaceAliasName(document.getDocumentElement());
		HashSet<String> keys = fromToNameMap.getKeys();
		for(String fromName : keys)
		{
			Node childNode = findNode(parentNode, fromName);
			if (childNode == null)
				continue;

			NodeList childNodeList = childNode.getChildNodes();
			parentNode.removeChild(childNode);
			final String toName = fromToNameMap.getValue(fromName);
			Node newNode = document.createElement(alias + COLON +  toName);
			for (int index = 0; index < childNodeList.getLength(); ++index)
			{
				Node grandchildNode = childNodeList.item(index);
				newNode.appendChild(grandchildNode);
			}
			parentNode.appendChild(newNode);
		}
	}

	private Node findNode(Node parentNode, final String elementNameWithoutAlias)
	{
		return findNode(parentNode.getChildNodes(), elementNameWithoutAlias);
	}

	private void removeHumanWellbeingTargetCalculatedThreatRatingElement(Element rootElement)
	{
		Node humanWellbeingTargetPool = findNode(rootElement.getChildNodes(), Xmpz2XmlWriter.createPoolElementName(Xmpz2XmlConstants.HUMAN_WELFARE_TARGET));
		if (humanWellbeingTargetPool == null)
			return;
		
		NodeList children = humanWellbeingTargetPool.getChildNodes();
		for (int index = 0; index < children.getLength(); ++index)
		{
			Node humanWellbeingTarget = children.item(index);
			if (humanWellbeingTarget == null)
				continue;
			
			removeChildren(humanWellbeingTarget, new String[]{Xmpz2XmlConstants.HUMAN_WELFARE_TARGET + Xmpz2XmlConstants.CALCULATED_THREAT_RATING,});
		}
	}

	private void removeLegacyTncFields(Element rootElement)
	{
		Node tncProjectDataNode = findNode(rootElement.getChildNodes(), Xmpz2XmlConstants.TNC_PROJECT_DATA);
		if (tncProjectDataNode != null)
			removeLegacyTncChildren(tncProjectDataNode);
	}

	private Node findNode(NodeList children, final String elementNameWithoutAlias)
	{
		for (int index = 0; index < children.getLength(); ++index)
		{
			Node childNode = children.item(index);
			if (childNode.getNodeName().endsWith(elementNameWithoutAlias))
				return childNode;
		}
		
		return null;
	}

	private void removeLegacyTncChildren(Node tncProjectDataNode)
	{
		String[] elementNamesToRemove = new String[]{createLegacyTncOrganizationalPrioritiesElementName(), createLegacyTncProjectPlaceTypesElementName(), };
		removeChildren(tncProjectDataNode, elementNamesToRemove);
	}
	
	private void removeChildren(Node nodeToRemoveElementsFrom, String[] elementNames)
	{
		NodeList children = nodeToRemoveElementsFrom.getChildNodes();
		Vector<Node> childrenToRemove = new Vector<Node>();
		for(String elementNameToRemove : elementNames)
		{
			final Node nodeToRemove = findNode(children, elementNameToRemove);
			childrenToRemove.add(nodeToRemove);
		}
		
		childrenToRemove.removeAll(Collections.singleton(null));
		for(Node childNodeToRemove : childrenToRemove)
		{
			nodeToRemoveElementsFrom.removeChild(childNodeToRemove);
		}
	}

	public static String createLegacyTncProjectPlaceTypesElementName()
	{
		return Xmpz2XmlConstants.TNC_PROJECT_DATA + "TNC" +  MigrationTo11.LEGACY_TAG_TNC_PROJECT_TYPES + Xmpz2XmlConstants.CONTAINER_ELEMENT_TAG;
	}

	public static String createLegacyTncOrganizationalPrioritiesElementName()
	{
		return Xmpz2XmlConstants.TNC_PROJECT_DATA + "TNC" + MigrationTo11.LEGACY_TAG_TNC_ORGANIZATIONAL_PRIORITIES + Xmpz2XmlConstants.CONTAINER_ELEMENT_TAG;
	}

	private boolean updateXmpz2SchemaVersionToCurrentVersion(Element rootElement) throws Exception
	{
		boolean schemaVersionWasUpdated = false;
		int readInSchemaVersion = getXmpz2DocumentSchemaVersion(rootElement);
		if (readInSchemaVersion < LOWEST_SCHEMA_VERSION)
		{
			throw new XmlVersionTooOldException(Integer.toString(LOWEST_SCHEMA_VERSION), Integer.toString(readInSchemaVersion));
		}
		
		if (readInSchemaVersion <  Integer.parseInt(NAME_SPACE_VERSION))
		{
			setNameSpaceVersion(rootElement, NAME_SPACE_VERSION);
			schemaVersionWasUpdated = true;
		}

		return schemaVersionWasUpdated;
	}

	private int getXmpz2DocumentSchemaVersion(Element rootElement) throws Exception
	{
		final String currentNamespace = getNameSpace(rootElement);
		String readInSchemaVersionAsString = AbstractXmlImporter.getSchemaVersionToImport(currentNamespace);
		return Integer.parseInt(readInSchemaVersionAsString);
	}

	private String getAttributeValue(Node elementNode, String attributeName)
	{
		NamedNodeMap attributes = elementNode.getAttributes();
		Node attributeNode = attributes.getNamedItem(attributeName);
		return attributeNode.getNodeValue();
	}

	public static void setNameSpaceVersion(Element rootElement, String newNameSpaceVersion) throws Exception
	{
		final String attributeName = getNamespaceAttributeName(rootElement);
		final String nameSpaceWithVersion = PARTIAL_NAME_SPACE + newNameSpaceVersion;
		rootElement.setAttribute(attributeName, nameSpaceWithVersion);
	}

	private static String getNameSpace(Element rootElement) throws Exception
	{
		return rootElement.getAttribute(getNamespaceAttributeName(rootElement));
	}

	private static String getNamespaceAttributeName(Element rootElement) throws Exception
	{
		final String alias = getNameSpaceAliasName(rootElement);
		return  XMLNS + COLON + alias;
	}
	
	private static String getNameSpaceAliasName(Element rootElement) throws Exception
	{
		NamedNodeMap attributes = rootElement.getAttributes();
		for (int index = 0; index < attributes.getLength(); ++index)
		{
			final Node attribute = attributes.item(index);
			final String nodeName = attribute.getNodeName();
			if (nodeName.startsWith(Xmpz2XmlConstants.XMLNS))
				return extractAlias(nodeName);
		}
		throw new Exception("Could not find xmlns attribute!");
	}

	private static String extractAlias(String namespaceAttributeName)
	{
		final String aliasNameLeftOver = namespaceAttributeName.replaceAll(Xmpz2XmlConstants.XMLNS + Xmpz2XmlConstants.COLON, "");
		
		return aliasNameLeftOver;
	}

	public static Document convertToDocument(InputStreamWithSeek projectAsInputStream) throws Exception
	{
		InputSource inputSource = new InputSource(projectAsInputStream);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse(inputSource);
		
		return document;
	}
	
	private static final int LOWEST_SCHEMA_VERSION = Xmpz2XmlConstants.LOWEST_SCHEMA_VERSION;
	private static final String NAME_SPACE_VERSION = Xmpz2XmlConstants.NAME_SPACE_VERSION;
	private static final String XMLNS = "xmlns";
	private static final String COLON = ":";
	private static final String PARTIAL_NAME_SPACE = "http://xml.miradi.org/schema/ConservationProject/";
}
