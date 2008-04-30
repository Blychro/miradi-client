/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.xml.conpro.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.martus.util.MultiCalendar;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.RelevancyOverride;
import org.miradi.objecthelpers.RelevancyOverrideSet;
import org.miradi.objecthelpers.StringMap;
import org.miradi.objects.Cause;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Objective;
import org.miradi.objects.ProgressReport;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.Strategy;
import org.miradi.objects.Stress;
import org.miradi.objects.SubTarget;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.project.Project;
import org.miradi.questions.BudgetCostModeQuestion;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.StatusQuestion;
import org.miradi.questions.TncFreshwaterEcoRegionQuestion;
import org.miradi.questions.TncMarineEcoRegionQuestion;
import org.miradi.questions.TncTerrestrialEcoRegionQuestion;
import org.miradi.questions.ViabilityModeQuestion;
import org.miradi.utils.CodeList;
import org.miradi.utils.DateRange;
import org.miradi.xml.conpro.ConProMiradiCodeMapHelper;
import org.miradi.xml.conpro.ConProMiradiXml;
import org.miradi.xml.conpro.exporter.ConProMiradiXmlValidator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class ConProXmlImporter implements ConProMiradiXml
{
	public ConProXmlImporter(Project projectToFill) throws Exception
	{
		project = projectToFill;
		codeMapHelper = new ConProMiradiCodeMapHelper();
	}
	
	public void populateProjectFromFile(File fileToImport) throws Exception
	{
		importConProProject(fileToImport);
	}

	private void importConProProject(File fileToImport) throws Exception
	{
		FileInputStream fileInputStream = new FileInputStream(fileToImport);
		try
		{
			if (!new ConProMiradiXmlValidator().isValid(fileInputStream))
				throw new Exception("Could not validate file for importing.");

			document = createDocument(fileToImport);
			xPath = createXPath();

			//FIXME finish method implementations
			importProjectSummaryElement();
			importTargets();
			importKeyEcologicalAttributes();
			importThreats();
			importIndicators();
			importObjectives();
			importStrategies();
			importViability();
		}
		finally
		{
			fileInputStream.close();
		}
	}

	private void importStrategies() throws Exception
	{
		String path = generatePath(new String[] {CONSERVATION_PROJECT, STRATEGIES, STRATEGY});
		NodeList strategyNodeList = getNodes(path);
		for (int nodeIndex = 0; nodeIndex < strategyNodeList.getLength(); ++nodeIndex) 
		{
			Node strategyNode = strategyNodeList.item(nodeIndex);
			String strategyId = getAttributeValue(strategyNode, ID);
			ORef strategyRef = getProject().createObject(Strategy.getObjectType(), new BaseId(strategyId));
			importObjectives(strategyNode, strategyRef);
			importField(strategyNode, NAME, strategyRef, Strategy.TAG_LABEL);
			importField(strategyNode, TAXONOMY_CODE, strategyRef, Strategy.TAG_TAXONOMY_CODE);
			importCodeField(strategyNode, LEVERAGE, strategyRef, Strategy.TAG_IMPACT_RATING, getCodeMapHelper().getConProToMiradiRatingMap());
			importCodeField(strategyNode, FEASABILITY, strategyRef, Strategy.TAG_FEASIBILITY_RATING, getCodeMapHelper().getConProToMiradiRatingMap());
			
			writeStrategyStatus(strategyNode, strategyRef);
			importField(strategyNode, COMMENT, strategyRef, Strategy.TAG_COMMENT);
			importActivities(strategyNode, strategyRef);
		}
	}

	private void writeStrategyStatus(Node strategyNode, ORef strategyRef) throws Exception
	{
		String generatedPath = generatePath(new String[]{SELECTED});
		String data = getXPath().evaluate(generatedPath, strategyNode);
		boolean status = Boolean.parseBoolean(data);
		String statusValue = Strategy.STATUS_DRAFT;
		if (status)
			statusValue = Strategy.STATUS_REAL;
		
		setData(strategyRef, Strategy.TAG_STATUS, statusValue);
	}

	private void importActivities(Node strategyNode, ORef strategyRef) throws Exception
	{
		ORefList activityRefs = new ORefList();
		NodeList activityNodeList = getNodes(strategyNode, new String[] {ACTIVITIES, ACTIVITY});
		for (int nodeIndex = 0; nodeIndex < activityNodeList.getLength(); ++nodeIndex) 
		{
			Node activityNode = activityNodeList.item(nodeIndex);
			ORef activityRef = getProject().createObject(Task.getObjectType());
			activityRefs.add(activityRef);
					
			importField(activityNode, NAME, activityRef, Task.TAG_LABEL);
			importWhenOverride(activityNode, activityRef);
		}
		
		setData(strategyRef, Strategy.TAG_ACTIVITY_IDS, activityRefs, Task.getObjectType());
	}

	private void importWhenOverride(Node activityNode, ORef activityRef) throws Exception
	{
		String startDateAsString = getNode(activityNode, ACTIVITY_START_DATE).getTextContent();
		String endDateAsString = getNode(activityNode, ACTIVITY_END_DATE).getTextContent();
		if (startDateAsString.length() > 0 && endDateAsString.length() > 0)
		{
			MultiCalendar startDate = MultiCalendar.createFromIsoDateString(startDateAsString);
			MultiCalendar endDate = MultiCalendar.createFromIsoDateString(endDateAsString);
			DateRange dateRange = new DateRange(startDate, endDate);
			setData(activityRef, Task.TAG_BUDGET_COST_MODE, BudgetCostModeQuestion.OVERRIDE_MODE_CODE);
			setData(activityRef, Task.TAG_WHEN_OVERRIDE, dateRange.toJson().toString());
		}
	}
		private void importObjectives(Node strategyNode, ORef strategyRef) throws Exception
	{
		ORefList objectiveRefs = new ORefList();
		NodeList objectiveNodeList = getNodes(strategyNode, new String[] {OBJECTIVES, OBJECTIVE_ID});
		for (int nodeIndex = 0; nodeIndex < objectiveNodeList.getLength(); ++nodeIndex) 
		{
			Node objectiveNode = objectiveNodeList.item(nodeIndex);
			String objectiveId = objectiveNode.getTextContent();
			ORef objectiveRef = new ORef(Objective.getObjectType(), new BaseId(objectiveId));
			
			objectiveRefs.add(objectiveRef);
		}
		
		setData(strategyRef, Strategy.TAG_OBJECTIVE_IDS, objectiveRefs, Objective.getObjectType());
	}

	private void importObjectives() throws Exception
	{
		String path = generatePath(new String[] {CONSERVATION_PROJECT, OBJECTIVES, OBJECTIVE});
		NodeList objectiveNodeList = getNodes(path);
		for (int nodeIndex = 0; nodeIndex < objectiveNodeList.getLength(); ++nodeIndex) 
		{
			Node objectiveNode = objectiveNodeList.item(nodeIndex);
			String objectiveId = getAttributeValue(objectiveNode, ID);
			ORef objectiveRef = getProject().createObject(Objective.getObjectType(), new BaseId(objectiveId));
			importRelevantIndicators(objectiveNode, objectiveRef);
			importField(objectiveNode, NAME, objectiveRef, Objective.TAG_LABEL);
			importField(objectiveNode, COMMENT, objectiveRef, Objective.TAG_COMMENTS);
		}
	}

	private void importRelevantIndicators(Node objectiveNode, ORef objectiveRef) throws Exception
	{
		RelevancyOverrideSet relevantIndicators = new RelevancyOverrideSet();
		NodeList indicatorIdNodes = getNodes(objectiveNode, new String[]{INDICATORS, INDICATOR_ID});
		for (int nodeIndex = 0; nodeIndex < indicatorIdNodes.getLength(); ++nodeIndex) 
		{
			Node indicatorNode = indicatorIdNodes.item(nodeIndex);
			BaseId indicatorId = new BaseId(indicatorNode.getTextContent());
			ORef indicatorRef = new ORef(Indicator.getObjectType(), indicatorId);
			relevantIndicators.add(new RelevancyOverride(indicatorRef, true));
		}
		
		setData(objectiveRef, Objective.TAG_RELEVANT_INDICATOR_SET, relevantIndicators.toString());
	}

	private void importIndicators() throws Exception
	{
		String path = generatePath(new String[] {CONSERVATION_PROJECT, INDICATORS, INDICATOR});
		NodeList indicatorNodeList = getNodes(path);
		for (int nodeIndex = 0; nodeIndex < indicatorNodeList.getLength(); ++nodeIndex) 
		{
			Node indicatorNode = indicatorNodeList.item(nodeIndex);
			String indicatorId = getAttributeValue(indicatorNode, ID);
			ORef indicatorRef = getProject().createObject(Indicator.getObjectType(), new BaseId(indicatorId));
			
			importField(indicatorNode, NAME, indicatorRef, Indicator.TAG_LABEL);
			importMethods(indicatorNode, indicatorRef);
			importCodeField(indicatorNode, PRIORITY, indicatorRef, Indicator.TAG_PRIORITY, getCodeMapHelper().getConProToMiradiRatingMap());
			importProgressReport(indicatorNode, indicatorRef);
			//FIXME finish indicator by importing WHO_MONITORS and checking import methods method			
			importBudgetData(indicatorNode, indicatorRef);
			importField(indicatorNode, COMMENT, indicatorRef, Indicator.TAG_COMMENT);
		}
	}
	
	private void importBudgetData(Node indicatorNode, ORef indicatorRef) throws Exception
	{
		importField(indicatorNode, ANNUAL_COST, indicatorRef, Indicator.TAG_BUDGET_COST_OVERRIDE);
		setData(indicatorRef, Indicator.TAG_BUDGET_COST_MODE, BudgetCostModeQuestion.OVERRIDE_MODE_CODE);	
	}

	private void importProgressReport(Node indicatorNode, ORef indicatorRef) throws Exception
	{
		Node progressStatusNode = getNode(indicatorNode, STATUS);
		if (progressStatusNode == null)
			return;
		
		ORef progressReportRef = getProject().createObject(ProgressReport.getObjectType());
		ORefList progressReportRefs = new ORefList(progressReportRef);
		importCodeField(indicatorNode, STATUS, progressReportRef, ProgressReport.TAG_PROGRESS_STATUS, getCodeMapHelper().getConProToMiradiProgressStatusMap());
		setData(progressReportRef, ProgressReport.TAG_PROGRESS_DATE, getProject().getMetadata().getEffectiveDate());
		setData(indicatorRef, Indicator.TAG_PROGRESS_REPORT_REFS, progressReportRefs.toString());
	}

	private void importMethods(Node indicatorNode, ORef indicatorRef) throws Exception
	{
		ORefList methodRefs = new ORefList();
		Node methodNode = getNode(indicatorNode, METHODS);
		if (methodNode == null)
			return;
		
		String semiColonSeperatedMethods = methodNode.getTextContent();
		String[] methods = semiColonSeperatedMethods.split(";");
		for (int index = 0; index < methods.length; ++index)
		{
			ORef methodRef = getProject().createObject(Task.getObjectType());
			setData(methodRef, Task.TAG_LABEL, methods[index]);
			methodRefs.add(methodRef);
		}
		
		setData(indicatorRef, Indicator.TAG_TASK_IDS, methodRefs, Task.getObjectType());
	}

	private void importThreats() throws Exception
	{
		String path = generatePath(new String[] {CONSERVATION_PROJECT, THREATS, THREAT});
		NodeList threatNodeList = getNodes(path);
		for (int i = 0; i < threatNodeList.getLength(); i++) 
		{
			Node threatNode = threatNodeList.item(i);
			String threatId = getAttributeValue(threatNode, ID);
			ORef threatRef = getProject().createObject(Cause.getObjectType(), new BaseId(threatId));
			
			importField(threatNode, NAME, threatRef, Cause.TAG_LABEL);
			importField(threatNode, THREAT_TAXONOMY_CODE, threatRef, Cause.TAG_TAXONOMY_CODE);			
		}
	}

	private void importViability() throws Exception
	{
		String path = generatePath(new String[] {CONSERVATION_PROJECT, VIABILITY, VIABILITY_ASSESSMENT});
		NodeList keaNodeList = getNodes(path);
		for (int nodeIndex = 0; nodeIndex < keaNodeList.getLength(); ++nodeIndex) 
		{
			Node viabilityAssessmentNode = keaNodeList.item(nodeIndex);
			BaseId targetId = new BaseId(getNode(viabilityAssessmentNode, TARGET_ID).getTextContent());
			ORef targetRef = new ORef(Target.getObjectType(), targetId);
			setData(targetRef, Target.TAG_VIABILITY_MODE, ViabilityModeQuestion.TNC_STYLE_CODE);
			
			BaseId keaId = new BaseId(getNode(viabilityAssessmentNode, KEA_ID).getTextContent());
			ORef keaRef = new ORef(KeyEcologicalAttribute.getObjectType(), keaId);
			IdList keaIds = new IdList(KeyEcologicalAttribute.getObjectType(), new BaseId[]{keaRef.getObjectId()});
			setData(targetRef, Target.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS, keaIds.toString());
			
			BaseId indicatorId = new BaseId(getNode(viabilityAssessmentNode, INDICATOR_ID).getTextContent());
			ORef indicatorRef = new ORef(Indicator.getObjectType(), indicatorId);
			
			IdList allKeaIndicatorIds = new IdList(Indicator.getObjectType(), new BaseId[]{indicatorRef.getObjectId()});
			KeyEcologicalAttribute kea = KeyEcologicalAttribute.find(getProject(), keaRef);
			IdList currentKeaIndicators = kea.getIndicatorIds();
			allKeaIndicatorIds.addAll(currentKeaIndicators);
			setData(keaRef, KeyEcologicalAttribute.TAG_INDICATOR_IDS, allKeaIndicatorIds.toString());
			
			importIndicatorThresholds(viabilityAssessmentNode, indicatorRef);		
			importField(viabilityAssessmentNode, CURRENT_INDICATOR_STATUS_VIABILITY, indicatorRef, Indicator.TAG_FUTURE_STATUS_RATING);
			
			//FIXME finish importing the rest of the fields
			//writeOptionalElement(out, CURRENT_INDICATOR_STATUS_VIABILITY, indicator.getCurrentStatus());
//			writeOptionalRatingCodeElement(out, DESIRED_VIABILITY_RATING,  indicator.getFutureStatusRating());
//			writeOptionalLatestMeasurementValues(out, indicator);
//			writeOptionalElement(out, DESIRED_RATING_DATE,  indicator, Indicator.TAG_FUTURE_STATUS_DATE);
//			writeOptionalElement(out, KEA_AND_INDICATOR_COMMENT, indicator, Indicator.TAG_DETAIL);
//			writeOptionalElement(out, INDICATOR_RATING_COMMENT, indicator, Indicator.TAG_VIABILITY_RATINGS_COMMENT);
//			writeOptionalElement(out, DESIRED_RATING_COMMENT, indicator, Indicator.TAG_FUTURE_STATUS_COMMENT);
//			writeOptionalElement(out, VIABILITY_RECORD_COMMENT, kea, KeyEcologicalAttribute.TAG_DESCRIPTION);
		}
	}

	private void importIndicatorThresholds(Node viabilityAssessmentNode, ORef indicatorRef) throws Exception
	{
		StringMap thresholds = new StringMap();
		String poorThreshold = getNode(viabilityAssessmentNode, INDICATOR_DESCRIPTION_POOR).getTextContent();
		String fairThreshold = getNode(viabilityAssessmentNode, INDICATOR_DESCRIPTION_FAIR).getTextContent();
		String goodThreshold = getNode(viabilityAssessmentNode, INDICATOR_DESCRIPTION_GOOD).getTextContent();
		String veryGoodThreshold = getNode(viabilityAssessmentNode, INDICATOR_DESCRIPTION_VERY_GOOD).getTextContent();
		thresholds.add(StatusQuestion.POOR, poorThreshold);
		thresholds.add(StatusQuestion.FAIR, fairThreshold);
		thresholds.add(StatusQuestion.GOOD, goodThreshold);
		thresholds.add(StatusQuestion.VERY_GOOD, veryGoodThreshold);
		setData(indicatorRef, Indicator.TAG_INDICATOR_THRESHOLD, thresholds.toString());
	}

	private void importKeyEcologicalAttributes() throws Exception
	{
		String path = generatePath(new String[] {CONSERVATION_PROJECT, KEY_ATTRIBUTES, KEY_ATTRIBUTE});
		NodeList keaNodeList = getNodes(path);
		for (int nodeIndex = 0; nodeIndex < keaNodeList.getLength(); ++nodeIndex) 
		{
			Node keaNode = keaNodeList.item(nodeIndex);
			String keaId = getAttributeValue(keaNode, ID);
			ORef keaRef = getProject().createObject(KeyEcologicalAttribute.getObjectType(), new BaseId(keaId));
			
			importField(keaNode, NAME, keaRef, KeyEcologicalAttribute.TAG_LABEL);
			importCodeField(keaNode, CATEGORY, keaRef, KeyEcologicalAttribute.TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE, getCodeMapHelper().getConProToMiradiKeaTypeMap());			
		}
	}

	private Document createDocument(File fileToImport) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		documentFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		
		return documentBuilder.parse(fileToImport);
	}

	private XPath createXPath()
	{
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath  thisXPath = xPathFactory.newXPath();
		thisXPath.setNamespaceContext(new ConProMiradiNameSpaceContext());
		
		return thisXPath;
	}

	private void importProjectSummaryElement() throws Exception
	{
		ORef metadataRef = getProject().getMetadata().getRef();
		Node projectSumaryNode = getNode(generatePath(new String[] {CONSERVATION_PROJECT, PROJECT_SUMMARY,}));
		
		importField(projectSumaryNode, NAME, metadataRef,ProjectMetadata.TAG_PROJECT_NAME);
		importField(projectSumaryNode, START_DATE, metadataRef, ProjectMetadata.TAG_START_DATE);
		importField(projectSumaryNode, AREA_SIZE, metadataRef, ProjectMetadata.TAG_TNC_SIZE_IN_HECTARES);	
		importField(projectSumaryNode, new String[]{GEOSPATIAL_LOCATION, LATITUDE}, metadataRef, ProjectMetadata.TAG_PROJECT_LATITUDE);
		importField(projectSumaryNode, new String[]{GEOSPATIAL_LOCATION, LONGITUDE}, metadataRef, ProjectMetadata.TAG_PROJECT_LONGITUDE);
		importField(projectSumaryNode, DESCRIPTION_COMMENT, metadataRef, ProjectMetadata.TAG_PROJECT_SCOPE);
		importField(projectSumaryNode, GOAL_COMMENT, metadataRef, ProjectMetadata.TAG_PROJECT_VISION);
		importField(projectSumaryNode, PLANNING_TEAM_COMMENT, metadataRef, ProjectMetadata.TAG_TNC_PLANNING_TEAM_COMMENT);
		importField(projectSumaryNode, LESSONS_LEARNED, metadataRef, ProjectMetadata.TAG_TNC_LESSONS_LEARNED);
		
		String[] ecoRegionElementHierarchy = new String[] {CONSERVATION_PROJECT, PROJECT_SUMMARY, ECOREGIONS, ECOREGION_CODE}; 
		String[] allEcoregionCodes = extractNodesAsList(generatePath(ecoRegionElementHierarchy));
		setData(metadataRef, ProjectMetadata.TAG_TNC_TERRESTRIAL_ECO_REGION, extractEcoregions(allEcoregionCodes, TncTerrestrialEcoRegionQuestion.class).toString());
		setData(metadataRef, ProjectMetadata.TAG_TNC_MARINE_ECO_REGION, extractEcoregions(allEcoregionCodes, TncMarineEcoRegionQuestion.class).toString());
		setData(metadataRef, ProjectMetadata.TAG_TNC_FRESHWATER_ECO_REGION, extractEcoregions(allEcoregionCodes, TncFreshwaterEcoRegionQuestion.class).toString());
		
		importCodeListField(generatePath(new String[] {CONSERVATION_PROJECT, PROJECT_SUMMARY, COUNTRIES, COUNTRY_CODE}), metadataRef, ProjectMetadata.TAG_COUNTRIES);
		importCodeListField(generatePath(new String[] {CONSERVATION_PROJECT, PROJECT_SUMMARY, OUS, OU_CODE}), metadataRef, ProjectMetadata.TAG_TNC_OPERATING_UNITS);
	}
	
	public void importTargets() throws Exception
	{
		String path = generatePath(new String[] {CONSERVATION_PROJECT,TARGETS, TARGET});
		NodeList targetNodeList = getNodes(path);
		for (int i = 0; i < targetNodeList.getLength(); i++) 
		{
			Node targetNode = targetNodeList.item(i);
			String targetId = getAttributeValue(targetNode, ID);
			ORef targetRef = getProject().createObject(Target.getObjectType(), new BaseId(targetId));
			
			importField(targetNode, TARGET_NAME, targetRef, Target.TAG_LABEL);
			importField(targetNode, TARGET_DESCRIPTION, targetRef, Target.TAG_TEXT);
			importField(targetNode, TARGET_DESCRIPTION_COMMENT, targetRef, Target.TAG_COMMENT);
			importField(targetNode, TARGET_VIABILITY_COMMENT, targetRef	, Target.TAG_CURRENT_STATUS_JUSTIFICATION);
			importCodeField(targetNode, TARGET_VIABILITY_RANK, targetRef, Target.TAG_TARGET_STATUS, getCodeMapHelper().getConProToMiradiRankingMap());
			importCodeListField(targetNode, HABITAT_TAXONOMY_CODES, HABITAT_TAXONOMY_CODE, targetRef, Target.TAG_HABITAT_ASSOCIATION, getCodeMapHelper().getConProToMiradiHabitiatCodeMap());
			importStresses(targetNode, targetRef);
			//FIXME import stresses threats
			
			importSubTargets(targetNode, targetRef);
			
			//FIXME
			//import SimpleTargetLinkRatings(out, target);
			//import StrategyThreatTargetAssociations(out, target);
		}
	}
		
	private void importSubTargets(Node targetNode, ORef targetRef) throws Exception
	{
		ORefList subTargetRefs = new ORefList();
		NodeList subTargetNodes = getNodes(targetNode, new String[]{NESTED_TARGETS, NESTED_TARGET});
		for (int nodeIndex = 0; nodeIndex < subTargetNodes.getLength(); ++nodeIndex)
		{
			ORef subTargetRef = getProject().createObject(SubTarget.getObjectType());
			Node subTargetNode = subTargetNodes.item(nodeIndex);
			
			importField(subTargetNode, NAME, subTargetRef, SubTarget.TAG_LABEL);
			importField(subTargetNode, COMMENT, subTargetRef, SubTarget.TAG_DETAIL);
			
			subTargetRefs.add(subTargetRef);
		}
		
		setData(targetRef, Target.TAG_SUB_TARGET_REFS, subTargetRefs.toString());
	}

	private void importStresses(Node targetNode, ORef targetRef) throws Exception
	{
		ORefList stressRefs = new ORefList();
		NodeList stressNodes = getNodes(targetNode, new String[]{TARGET_STRESSES, TARGET_STRESS});
		for (int nodeIndex = 0; nodeIndex < stressNodes.getLength(); ++nodeIndex)
		{
			ORef stressRef = getProject().createObject(Stress.getObjectType());
			Node stressNode = stressNodes.item(nodeIndex);
			
			importField(stressNode, NAME, stressRef, Stress.TAG_LABEL); 
			importCodeField(stressNode, STRESS_SEVERITY, stressRef, Stress.TAG_SEVERITY, getCodeMapHelper().getConProToMiradiRatingMap());
			importCodeField(stressNode, STRESS_SCOPE, stressRef, Stress.TAG_SCOPE, getCodeMapHelper().getConProToMiradiRatingMap());
			stressRefs.add(stressRef);
		}
		
		setData(targetRef, Target.TAG_STRESS_REFS, stressRefs.toString());
	}

	private CodeList extractEcoregions(String[] allEcoregionCodes, Class questionClass)
	{
		ChoiceQuestion question = getProject().getQuestion(questionClass);
		CodeList ecoregionCodes = new CodeList();
		for (int i= 0; i < allEcoregionCodes.length; ++i)
		{
			ChoiceItem choiceItem = question.findChoiceByCode(allEcoregionCodes[i]);
			if (choiceItem != null)
				ecoregionCodes.add(allEcoregionCodes[i]);
		}
		
		return ecoregionCodes;
	}

	private void importCodeListField(String path, ORef objectRef, String tag) throws Exception
	{
		CodeList codes = new CodeList(extractNodesAsList(path));
		setData(objectRef, tag, codes.toString());
	}

	private void importField(Node node, String path, ORef ref, String tag) throws Exception
	{
		importField(node, new String[]{path,}, ref, tag);
	}
	
	private void importField(Node node, String[] elements, ORef ref, String tag) throws Exception 
	{
		String generatedPath = generatePath(elements);
		String data = getXPath().evaluate(generatedPath, node);
		setData(ref, tag, data);
	}
	
	private void importCodeField(Node node, String element, ORef ref, String tag, HashMap<String, String> map) throws Exception
	{
		importCodeField(node, new String[]{element}, ref, tag, map);
	}
	
	private void importCodeField(Node node, String[] elements, ORef ref, String tag, HashMap<String, String> map) throws Exception
	{
		String generatedPath = generatePath(elements);
		String rawCode = getXPath().evaluate(generatedPath, node);
		String safeCode = getCodeMapHelper().getSafeXmlCode(map, rawCode);
		setData(ref, tag, safeCode);
	}
	
	private void importCodeListField(Node node, String parentElement, String childElement, ORef ref, String tag, HashMap<String, String> conProToMiradiCodeMap) throws Exception
	{
		CodeList codes = new CodeList();
		NodeList nodes = getNodes(node, new String[]{parentElement, childElement});
		for (int nodeIndex = 0; nodeIndex < nodes.getLength(); ++nodeIndex)
		{
			Node thisNode = nodes.item(nodeIndex);
			String conProCode = thisNode.getTextContent();
			String miradiCode = conProToMiradiCodeMap.get(conProCode);
			if (miradiCode != null)
				codes.add(miradiCode);
		}
		
		setData(ref, tag, codes.toString());
	}
	
	private void setData(ORef ref, String tag, ORefList refList, int type) throws Exception
	{
		setData(ref, tag, refList.convertToIdList(type).toString());
	}
	
	private void setData(ORef ref, String tag, String data) throws Exception
	{
		getProject().setObjectData(ref, tag, data);
	}

	public String generatePath(String[] pathElements)
	{
		String path = "";
		for (int index = 0; index < pathElements.length; ++index)
		{
			path += getPrefixedElement(pathElements[index]);
			if ((index + 1) < pathElements.length)
				path += "/";
		}
		return path;
	}
	
	public String[] extractNodesAsList(String path) throws Exception
	{
		XPathExpression expression = getXPath().compile(path);
		NodeList nodeList = (NodeList) expression.evaluate(getDocument(), XPathConstants.NODESET);
		Vector<String> nodes = new Vector();
		for (int i = 0; i < nodeList.getLength(); i++) 
		{
			nodes.add(nodeList.item(i).getTextContent());
		}
		
		return nodes.toArray(new String[0]);
	}
	
	private String getAttributeValue(Node elementNode, String attributeName)
	{
		NamedNodeMap attributes = elementNode.getAttributes();
		Node attributeNode = attributes.getNamedItem(attributeName);
		return attributeNode.getNodeValue();
	}
		
	private Node getNode(Node node, String element) throws Exception
	{
		String path = generatePath(new String[]{element});
		XPathExpression expression = getXPath().compile(path);
		return (Node) expression.evaluate(node, XPathConstants.NODE);
	}
	
	private Node getNode(String path) throws Exception
	{
		XPathExpression expression = getXPath().compile(path);
		return (Node) expression.evaluate(getDocument(), XPathConstants.NODE);
	}
	
	private NodeList getNodes(String path) throws Exception
	{
		XPathExpression expression = getXPath().compile(path);
		return (NodeList) expression.evaluate(getDocument(), XPathConstants.NODESET);
	}
	
	private NodeList getNodes(Node node, String[] pathElements) throws Exception
	{
		String path = generatePath(pathElements);
		XPathExpression expression = getXPath().compile(path);
		
		return (NodeList) expression.evaluate(node, XPathConstants.NODESET);
	}
	
	private String getPrefixedElement(String elementName)
	{
		return PREFIX + elementName;
	}
	
	private Project getProject()
	{
		return project;
	}
	
	public Document getDocument()
	{
		return document;
	}

	public XPath getXPath()
	{
		return xPath;
	}
	
	private ConProMiradiCodeMapHelper getCodeMapHelper()
	{
		return codeMapHelper;
	}
		
	public static void main(String[] args)
	{
		try
		{
			Project project = new Project();
			project.createOrOpen(new File("c:/temp/devMiradiProject/"));
			new ConProXmlImporter(project).populateProjectFromFile(new File("c:/temp/Conpro.xml"));
			System.out.println("finished importing");
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
	}
	
	private Project project;
	private XPath xPath;
	private Document document;
	private ConProMiradiCodeMapHelper codeMapHelper;
	
	public static final String PREFIX = "cp:";
}
