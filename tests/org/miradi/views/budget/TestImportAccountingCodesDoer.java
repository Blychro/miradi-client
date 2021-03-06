/* 
Copyright 2005-2018, Foundations of Success, Bethesda, Maryland
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
package org.miradi.views.budget;

import java.io.BufferedReader;
import java.io.StringReader;

import org.miradi.main.MiradiTestCase;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objectpools.EAMObjectPool;
import org.miradi.objects.AccountingCode;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.project.ProjectForTesting;
import org.miradi.schemas.AccountingCodeSchema;
import org.miradi.views.planning.doers.ImportAccountingCodesDoer;

public class TestImportAccountingCodesDoer extends MiradiTestCase
{

	public TestImportAccountingCodesDoer(String name)
	{
		super(name);
	}

	@Override
	public void setUp() throws Exception
	{
		project = ProjectForTesting.createProjectWithDefaultObjects(getName());
		super.setUp();
	}
	
	@Override
	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
		project = null;
	}
	
	
	public void testTwoRows() throws Exception
	{
		String data = "code1A \t label1A \n code1B \t label1B";
		AccountingCode[] accountingCodes = ImportAccountingCodesDoer.importCodes(new BufferedReader(new StringReader(data)),project);
		assertEquals(2, accountingCodes.length);
		BaseObject object = project.findObject(ObjectType.ACCOUNTING_CODE, accountingCodes[0].getId());
		assertNotNull(object);
		AccountingCode accountingCode = (AccountingCode) object; 
		assertEquals("label1A", accountingCode.getLabel());
		assertEquals("code1A", accountingCode.getData(AccountingCode.TAG_CODE));
	}

	public void testRejectionOfDuplicates() throws Exception
	{
		testTwoRows();
		String data = "code1A \t label1A \n code1B \t label1B";
		ImportAccountingCodesDoer.importCodes(new BufferedReader(new StringReader(data)),project);
		assertEquals(2, project.getPool(ObjectType.ACCOUNTING_CODE).size());
	}
	
	public void testTransaction() throws Exception
	{
		String data = "code1A \t label1A \n code1B \t label1B";
		ImportAccountingCodesDoer.importCodes(new BufferedReader(new StringReader(data)),project);
		getProject().undo();
		EAMObjectPool accountingCodePool = getProject().getPool(AccountingCodeSchema.getObjectType());
		assertEquals("Import wasn't a single transaction?", 0, accountingCodePool.size());
	}
	
	private Project getProject()
	{
		return project;
	}
	
	Project project;
}