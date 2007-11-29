/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.project;

import org.conservationmeasures.eam.main.TestCaseWithProject;

public class TestStressBasedThreatFormula extends TestCaseWithProject
{
	public TestStressBasedThreatFormula(String name)
	{
		super(name);
	}
	
	public void testComputeSevertyByScope()
	{
		int[] scope = {4, 3, 2, 1, 0};
		int[] severity = {4, 3, 2, 1, 0};
		int[][] scopeSeverity = 
		{
		/*s      	  s c o p e   */
		/*e 		 4  3  2  1  0*/
		/*v  4*/	{4, 3, 2, 1, 0},
		/*e	 3*/	{3, 3, 2, 1, 0},
		/*r	 2*/	{2, 2, 2, 1, 0},
		/*i	 1*/	{1, 1, 1, 1, 0},
		/*ty 0*/	{0, 0, 0, 0, 0},
		};
		
		StressBasedThreatFormula formula = new StressBasedThreatFormula();
		for (int scopeIndex = 0; scopeIndex < scope.length; ++scopeIndex)
		{
			for (int severityIndex = 0; severityIndex < severity.length; ++severityIndex)
			{
				assertEquals(scopeSeverity[scopeIndex][severityIndex], formula.computeSevertyByScope(scope[scopeIndex], severity[severityIndex]));
			}
		}
		
		try
		{
			formula.computeSevertyByScope(-1, 0);
			formula.computeSevertyByScope(5, 0);
			
			formula.computeSevertyByScope(0, -1);
			formula.computeSevertyByScope(0, 5);
			fail();
		}
		catch(Exception e)
		{
		}
	}
	
	public void testComputeContributionByIrreversibility()
	{
		int[] contribution    = {4, 3, 2, 1, 0};
		int[] irreversibility = {4, 3, 2, 1, 0};
		
		int[][] contributionIrreversibility = 
		{
			/*Ir        Contribution  */	
			/*re   	 	 4  3  2  1  0*/
			/*ve   4 */	{4, 3, 3, 1, 0},	
			/*rs   3 */	{4, 3, 2, 1, 0},
			/*ib   2 */	{3, 2, 2, 1, 0},
			/*il   1 */	{3, 2, 1, 1, 0},
			/*ity  0 */ {0, 0, 0, 0, 0},
		};
		
		StressBasedThreatFormula formula = new StressBasedThreatFormula();
		for (int contributionIndex = 0; contributionIndex < contribution.length; ++contributionIndex)
		{
			for (int irreversibilityIndex = 0; irreversibilityIndex < irreversibility.length; ++irreversibilityIndex)
			{
				int computedValue = formula.computeContributionByIrreversibility(contribution[contributionIndex], irreversibility[irreversibilityIndex]);
				int expectedValue = contributionIrreversibility[contributionIndex][irreversibilityIndex]; 
				assertEquals(expectedValue, computedValue);
			}
		}
		
		try
		{
			formula.computeContributionByIrreversibility(-1, 0);
			formula.computeContributionByIrreversibility(5, 0);
			
			formula.computeContributionByIrreversibility(0, -1);
			formula.computeContributionByIrreversibility(0, 5);
			fail();
		}
		catch(Exception e)
		{
		}
	}
	
	public void testComputeThreatStressRating()
	{
		int[] source = {4, 3, 2, 1, 0};
		int[] stress = {4, 3, 2, 1, 0};
		
		int[][] threatStressRating = 
		{
		 /*        source     */	
	     /*s	 	 4  3  2  1  0*/
		 /*t   4*/	{4, 4, 3, 2, 0},
		 /*r   3*/	{3, 3, 2, 1, 0},
		 /*e   2*/	{2, 2, 1, 1, 0},
		 /*s   1*/	{1, 1, 1, 1, 0},
		 /*s   0*/	{0, 0, 0, 0, 0},	
		};
		
		StressBasedThreatFormula formula = new StressBasedThreatFormula();
		for (int sourceIndex = 0; sourceIndex < source.length; ++sourceIndex)
		{
			for (int stressIndex = 0; stressIndex < stress.length; ++stressIndex)
			{
				int computedValue = formula.computeThreatStressRating(source[sourceIndex], stress[stressIndex]);
				int expectedValue = threatStressRating[sourceIndex][stressIndex];
				assertEquals(expectedValue, computedValue);
			}
		}
		
		try
		{
			formula.computeThreatStressRating(-1, 0);
			formula.computeThreatStressRating(5, 0);
			
			formula.computeThreatStressRating(0, -1);
			formula.computeThreatStressRating(0, 5);
			fail();
		}
		catch(Exception e)
		{
		}
	}
}
