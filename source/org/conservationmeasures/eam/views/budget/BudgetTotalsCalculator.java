/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.budget;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.ids.TaskId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.DateRangeEffortList;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Assignment;
import org.conservationmeasures.eam.objects.EAMObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.objects.ProjectResource;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.DateRange;
import org.conservationmeasures.eam.views.TreeTableNode;

public class BudgetTotalsCalculator
{
	public BudgetTotalsCalculator(Project projectToUse)
	{
		project = projectToUse;
	}
	
	public double getTotalUnits(IdList assignmentIdList, DateRange dateRange) throws Exception
	{
		double totalDateRangeUnits = 0.0;
		for (int i = 0 ; i < assignmentIdList.size(); i++)
			totalDateRangeUnits += getTotalUnits(assignmentIdList.get(i), dateRange);
		
		return totalDateRangeUnits;
	}
	
	public double getTotalUnits(BaseId assignmentId, DateRange dateRange) throws Exception
	{
		Assignment assignment = (Assignment)project.findObject(ObjectType.ASSIGNMENT, assignmentId);
		return getTotalUnits(assignment, dateRange);
	}
	
	public double getTotalUnits(Assignment assignment, DateRange dateRange) throws Exception
	{
		DateRangeEffortList effortList = getDateRangeEffortList(assignment);
		return effortList.getTotalUnitQuantity(dateRange);
	}
	
	private DateRangeEffortList getDateRangeEffortList(Assignment assignment) throws Exception
	{
		String effortListAsString = assignment.getData(Assignment.TAG_DATERANGE_EFFORTS);
		return new DateRangeEffortList(effortListAsString);
	}
	
	public double getTotalCost(IdList assignmentIds, DateRange dateRange) throws Exception
	{
		double totalDateRangeCost = 0.0;
		for (int i = 0; i < assignmentIds.size(); i++)
			totalDateRangeCost += getTotalCost(assignmentIds.get(i), dateRange);
		
		return totalDateRangeCost;
	}
	
	public double getTotalCost(BaseId assignmentId, DateRange dateRange) throws Exception
	{
		Assignment assignment = (Assignment)project.findObject(ObjectType.ASSIGNMENT, assignmentId);
		return getTotalCost(assignment, dateRange);
	}
	
	public double getTotalCost(Assignment assignment, DateRange dateRange) throws Exception
	{
		ProjectResource resource = getProjectResource(assignment.getId());
		if (resource == null)
			return 0.0;
	
		double totalUnits = getTotalUnits(assignment, dateRange);
		double costPerUnit = resource.getCostPerUnit();
	
		return totalUnits * costPerUnit;
	}
	
	public ProjectResource getProjectResource(BaseId assignmentId)
	{
		String stringId = project.getObjectData(ObjectType.ASSIGNMENT, assignmentId, Assignment.TAG_ASSIGNMENT_RESOURCE_ID);
		BaseId resourceId = new BaseId(stringId);
		ProjectResource resource = (ProjectResource)project.findObject(ObjectType.PROJECT_RESOURCE, resourceId);
		
		return resource;
	}
	
	public double calculateTotalCost(TreeTableNode node)
	{
		try
		{
			ORef oRef = node.getObjectReference();
			int type = node.getObjectReference().getObjectType();

			if (type == ObjectType.INDICATOR)
				return getTotalIndicatorCost(oRef);
			
			if (type == ObjectType.FACTOR)
				return getTotalFactorCost(getFactor(oRef));

			if (oRef.getObjectType() == ObjectType.TASK)
				return getTotalTaskCost((TaskId)oRef.getObjectId());
			
			if (oRef.getObjectType() == ObjectType.FAKE)
				return getTotalFakeCost(node);				
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
		return  0.0;
	}

	private Factor getFactor(ORef oRef)
	{
		return (Factor)project.findObject(oRef.getObjectType(), oRef.getObjectId());
	}

	
	public double getTotalIndicatorsCost(IdList idList) throws Exception
	{
		double totalIndicatorsCost = 0.0;
		for (int i = 0; i < idList.size(); i++)
			totalIndicatorsCost += getTotalIndicatorCost(new ORef(ObjectType.INDICATOR, idList.get(i)));
		
		return totalIndicatorsCost;
	}
	
	public double getTotalIndicatorCost(ORef oRef) throws Exception
	{
		Indicator indicator = (Indicator)project.findObject(oRef.getObjectType(), oRef.getObjectId());
		return getTotalTasksCost(indicator.getTaskIdList());
	}
	
	public double getTotalFactorCost(Factor factor) throws Exception
	{
		if (factor.getIndicators().size() > 0)
			return getIndicatorTotal(factor);
		
		return getTotalStrategyCost(factor);
	}
	
	public double getTotalStrategyCost(Factor factor) throws Exception
	{
		double totalStrategyCost = 0.0;
		if (!factor.isStrategy())
			return totalStrategyCost;
		
		Strategy strategy = (Strategy)factor;
		IdList idList = strategy.getActivityIds();
		for (int i = 0; i < idList.size(); i++)
		{
			Task task = (Task)project.findObject(ObjectType.TASK, idList.get(i));
			totalStrategyCost += getTotalCost(task);	
		}
		
		return totalStrategyCost;
	}
	
	private double getIndicatorTotal(Factor factor) throws Exception
	{
		double totalIndicatorCost = 0.0;
		IdList idList = factor.getIndicators();
		for (int i = 0; i < idList.size(); i++)
		{
			Indicator indicator = (Indicator)project.findObject(ObjectType.INDICATOR, idList.get(i));
			IdList taskIds = indicator.getTaskIdList();
			totalIndicatorCost += getTotalTasksCost(taskIds);
		}
		return totalIndicatorCost;
	}

	private double getTotalTasksCost(IdList taskIds) throws Exception
	{
		double totalTaskCost = 0.0;
		for (int i = 0; i < taskIds.size(); i++)
		{
			Task task = (Task)project.findObject(ObjectType.TASK, taskIds.get(i));
			totalTaskCost += getTotalCost(task);
		}
		return totalTaskCost;
	}

	public double getTotalTaskCost(TaskId taskId) throws Exception 
	{
		Task task = (Task)project.findObject(ObjectType.TASK, taskId);
		return getTotalCost(task);
	}
	
	public double getTotalCost(Task task) throws Exception
	{
		totalCost = 0.0;
		calculateTotalAssignment(task);
		return totalCost;
	}
	
	public double getTotalFakeCost(TreeTableNode node) throws Exception
	{
		TreeTableNode child = node.getChild(0);
		if (child == null)
			return 0.0;
		
		int type = child.getObjectReference().getObjectType();
		if (type == ObjectType.FACTOR)
			return getFactorTotal(node);
		
		return 0.0;
	}
	
	private double getFactorTotal(TreeTableNode node) throws Exception
	{
		ORef nodeRef = node.getObjectReference();
		if(nodeRef.equals(EAM.WORKPLAN_STRATEGY_ROOT))
			return getStrategiesChildrenTotal(node);
		else if(nodeRef.equals(EAM.WORKPLAN_MONITORING_ROOT))
			return getFactorTotals(node);

		throw new RuntimeException("Unexpected tree node root: " + nodeRef);
	}
	
	private double getStrategiesChildrenTotal(TreeTableNode node) throws Exception
	{
		double childrenTotal = 0.0;
		for (int i = 0; i < node.getChildCount(); i++)
		{
			EAMObject object = node.getChild(i).getObject();
			Strategy strategy = (Strategy)object;
			childrenTotal += getTotalStrategyCost(strategy);
		}
		return childrenTotal;
	}

	private double getFactorTotals(TreeTableNode node) throws Exception
	{
		double childrenTotal = 0.0;
		for (int i = 0; i < node.getChildCount(); i++)
		{
			ORef ref = node.getChild(i).getObjectReference();
			IdList indicatorIds = new IdList(project.getObjectData(ref.getObjectType(), ref.getObjectId(), Factor.TAG_INDICATOR_IDS));
			
			childrenTotal += getTotalIndicatorsCost(indicatorIds);
		}
		return childrenTotal;
	}
	
	private void calculateTotalAssignment(Task task) throws Exception
	{
		sumTotals(task);
		int subTaskCount = task.getSubtaskCount();
		for (int index = 0; index < subTaskCount; index++)
		{
			BaseId subTaskId = task.getSubtaskId(index);
			Task  subTask = (Task)project.findObject(ObjectType.TASK, subTaskId);
			calculateTotalAssignment(subTask);
		}
	}

	private void sumTotals(Task task) throws Exception
	{
		IdList idList = task.getAssignmentIdList();
		for (int i = 0; i < idList.size(); i++)
		{
			Assignment assignment = (Assignment)project.findObject(ObjectType.ASSIGNMENT, idList.get(i));
			ProjectResource resource = getProjectResource(idList.get(i));
			if (resource != null)
			{
				String effortListAsString = assignment.getData(Assignment.TAG_DATERANGE_EFFORTS);
				DateRangeEffortList effortList = new DateRangeEffortList(effortListAsString);
				double totalCostPerAssignment = (effortList.getTotalUnitQuantity() * resource.getCostPerUnit());
				totalCost += totalCostPerAssignment;
			}
		}
	}
	
	double totalCost;
	private Project project;
}
