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

package org.miradi.project;

import org.miradi.diagram.PersistentDiagramModel;
import org.miradi.main.TestCaseWithProject;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.TimePeriodCosts;
import org.miradi.objecthelpers.TimePeriodCostsMap;
import org.miradi.objects.BaseObject;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.Indicator;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.Strategy;

public class TestProjectTotalCalculator extends TestCaseWithProject
{
	public TestProjectTotalCalculator(String name)
	{
		super(name);
	}
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		ORef resultsChainRef = getProject().createObject(ResultsChainDiagram.getObjectType());
		ResultsChainDiagram resultsChain = ResultsChainDiagram.find(getProject(), resultsChainRef);
		resultsChainDiagramModel = new PersistentDiagramModel(getProject());
		resultsChainDiagramModel.fillFrom(resultsChain);
		
		fred = getProject().createAndPopulateProjectResource();
	}
	
	public void testProjectTotalCalculation() throws Exception
	{
		
		assertEquals("no results chains created?", 1, getProject().getResultsChainDiagramPool().size());
		ProjectTotalCalculator calculator = new ProjectTotalCalculator(getProject());
		assertEquals("Empty project had non-zero totals data?", 0, calculator.calculateProjectTotals().size());
		
		DiagramFactor conceptualModelStrategy = getProject().createDiagramFactorAndAddToDiagram(Strategy.getObjectType());
		getProject().addResourceAssignment(conceptualModelStrategy.getWrappedFactor(), fred, 10, YEAR_2008, YEAR_2009);
		assertEquals("ConceptualModel Strategy included in project totals?", 0, calculator.calculateProjectTotals().size());
		
		DiagramFactor conceptualModelDraftStrategy = getProject().createDiagramFactorAndAddToDiagram(Strategy.getObjectType());
		getProject().turnOnDraft((Strategy)conceptualModelDraftStrategy.getWrappedFactor());
		getProject().addResourceAssignment(conceptualModelDraftStrategy.getWrappedFactor(), fred, 10, YEAR_2008, YEAR_2009);
		assertEquals("ConceptualModel Draft Strategy included in project totals?", 0, calculator.calculateProjectTotals().size());
		
		Indicator indicatorWithResourceAssignment = getProject().createAndPopulateIndicator();
		addResourceAssignment(indicatorWithResourceAssignment);
		TimePeriodCostsMap totalsWithIndicator = calculator.calculateProjectTotals();
		assertEquals("did not include indicator in totals?", 1, totalsWithIndicator.size());
		DateUnit dateUnit = getProject().createDateUnit(YEAR_2008, YEAR_2009);
		TimePeriodCosts indictorTimePeriodCosts = totalsWithIndicator.getTimePeriodCostsForSpecificDateUnit(dateUnit);
		assertEquals("wrong resources total units calculation with indicator?", 10.0, indictorTimePeriodCosts.calculateResourcesTotalUnits().getValue());
		
		DiagramFactor resultsChainNonDraftStrategy = getProject().createAndAddFactorToDiagram(resultsChainDiagramModel.getDiagramObject(), Strategy.getObjectType());
		addResourceAssignment(resultsChainNonDraftStrategy.getWrappedFactor());
		TimePeriodCostsMap totalsWithNonDraftStrategy = calculator.calculateProjectTotals();
		TimePeriodCosts strategyTimePeriodCosts = totalsWithNonDraftStrategy.getTimePeriodCostsForSpecificDateUnit(dateUnit);
		assertEquals("did not include strategy inside results chain?", 20.0, strategyTimePeriodCosts.calculateResourcesTotalUnits().getValue());
		
		DiagramFactor resultsChainDraftStrategy = getProject().createAndAddFactorToDiagram(resultsChainDiagramModel.getDiagramObject(), Strategy.getObjectType());
		getProject().turnOnDraft((Strategy)conceptualModelDraftStrategy.getWrappedFactor());
		TimePeriodCostsMap totalsWithDraftStrategy = calculator.calculateProjectTotals();
		TimePeriodCosts draftStrategyTimePeriodCosts = totalsWithDraftStrategy.getTimePeriodCostsForSpecificDateUnit(dateUnit);
		addResourceAssignment(resultsChainDraftStrategy.getWrappedFactor());
		assertEquals("Results chain Draft Strategy included in project totals?", 20.0, draftStrategyTimePeriodCosts.calculateResourcesTotalUnits().getValue());
	}

	private void addResourceAssignment(BaseObject wrappedFactor) throws Exception
	{
		getProject().addResourceAssignment(wrappedFactor, fred, 10, YEAR_2008, YEAR_2009);
	}
	
	private static final int YEAR_2008 = 2008;
	private static final int YEAR_2009 = 2009;
	
	private PersistentDiagramModel resultsChainDiagramModel;
	private ProjectResource fred; 
}
