/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.project;

import java.util.Vector;

import org.conservationmeasures.eam.objects.ProjectMetadata;
import org.conservationmeasures.eam.utils.DateRange;
import org.martus.util.MultiCalendar;
import org.martus.util.TestCaseEnhanced;

public class TestProjectCalendar extends TestCaseEnhanced
{
	public TestProjectCalendar(String name)
	{
		super(name);
	}

	public void testBasics() throws Exception
	{
		Project project = new ProjectForTesting(getName());
		ProjectCalendar pc = project.getProjectCalendar();
		try
		{
			project.getMetadata().setData(ProjectMetadata.TAG_START_DATE, "2006-01-01");
			project.getMetadata().setData(ProjectMetadata.TAG_EXPECTED_END_DATE, "2007-12-31");
			pc.rebuildProjectDateRanges();
			{
				Vector yearlyRanges = pc.getYearlyDateRanges();
				assertEquals(2, yearlyRanges.size());
				assertEquals("FY06", pc.getDateRangeName((DateRange)yearlyRanges.get(0)));
				assertEquals("FY07", pc.getDateRangeName((DateRange)yearlyRanges.get(1)));
			}

			project.getMetadata().setData(ProjectMetadata.TAG_FISCAL_YEAR_START, "7");
			pc.rebuildProjectDateRanges();
			{
				Vector yearlyRanges = pc.getYearlyDateRanges();
				assertEquals(3, yearlyRanges.size());
				assertEquals("FY06", pc.getDateRangeName((DateRange)yearlyRanges.get(0)));
				assertEquals("FY07", pc.getDateRangeName((DateRange)yearlyRanges.get(1)));
				assertEquals("FY08", pc.getDateRangeName((DateRange)yearlyRanges.get(2)));
			}

			project.getMetadata().setData(ProjectMetadata.TAG_START_DATE, "2006-01-02");
			project.getMetadata().setData(ProjectMetadata.TAG_EXPECTED_END_DATE, "2007-01-02");
			project.getMetadata().setData(ProjectMetadata.TAG_FISCAL_YEAR_START, "1");
			pc.rebuildProjectDateRanges();
			{
				Vector yearlyRanges = pc.getYearlyDateRanges();
				assertEquals(2, yearlyRanges.size());
				assertEquals("FY06", pc.getDateRangeName((DateRange)yearlyRanges.get(0)));
				assertEquals("FY07", pc.getDateRangeName((DateRange)yearlyRanges.get(1)));
			}
		}
		finally
		{
			project.close();
		}
	}

	public void testGetFiscalYearQuarterName() throws Exception
	{
		verifyFiscalQuarterName("FY06", "2006-01-01", "2006-12-31", 1);
		verifyFiscalQuarterName("Q1 FY06", "2006-01-01", "2006-03-31", 1);
		verifyFiscalQuarterName("Q2 FY06", "2006-04-01", "2006-06-30", 1);
		verifyFiscalQuarterName("Q3 FY06", "2006-07-01", "2006-09-30", 1);
		verifyFiscalQuarterName("Q4 FY06", "2006-10-01", "2006-12-31", 1);
		
		verifyFiscalQuarterName("FY06", "2006-04-01", "2007-03-31", 4);
		verifyFiscalQuarterName("Q1 FY06", "2006-04-01", "2006-06-30", 4);
		verifyFiscalQuarterName("Q2 FY06", "2006-07-01", "2006-09-30", 4);
		verifyFiscalQuarterName("Q3 FY06", "2006-10-01", "2006-12-31", 4);
		verifyFiscalQuarterName("Q4 FY06", "2007-01-01", "2007-03-31", 4);

		verifyFiscalQuarterName("FY06", "2005-07-01", "2006-06-30", 7);
		verifyFiscalQuarterName("Q1 FY06", "2005-07-01", "2005-09-30", 7);
		verifyFiscalQuarterName("Q2 FY06", "2005-10-01", "2005-12-31", 7);
		verifyFiscalQuarterName("Q3 FY06", "2006-01-01", "2006-03-31", 7);
		verifyFiscalQuarterName("Q4 FY06", "2006-04-01", "2006-06-30", 7);

		verifyFiscalQuarterName("FY06", "2005-10-01", "2006-09-30", 10);
		verifyFiscalQuarterName("Q1 FY06", "2005-10-01", "2005-12-31", 10);
		verifyFiscalQuarterName("Q2 FY06", "2006-01-01", "2006-03-31", 10);
		verifyFiscalQuarterName("Q3 FY06", "2006-04-01", "2006-06-30", 10);
		verifyFiscalQuarterName("Q4 FY06", "2006-07-01", "2006-09-30", 10);

		verifyFiscalQuarterName("2006", "2006-01-01", "2006-12-31", 10);
	}

	
	private void verifyFiscalQuarterName(String expectedName, String beginDate, String endDate, int fiscalYearFirstMonth) throws Exception
	{
		MultiCalendar begin = MultiCalendar.createFromIsoDateString(beginDate);
		MultiCalendar end = MultiCalendar.createFromIsoDateString(endDate);
		DateRange dateRange = new DateRange(begin, end);
		String result = ProjectCalendar.getFiscalYearQuarterName(dateRange, fiscalYearFirstMonth);
		assertEquals(expectedName, result);
	}

}
