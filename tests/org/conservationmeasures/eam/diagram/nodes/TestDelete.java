/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram.nodes;

import org.conservationmeasures.eam.commands.CommandDeleteLinkage;
import org.conservationmeasures.eam.commands.CommandDeleteNode;
import org.conservationmeasures.eam.commands.CommandInsertNode;
import org.conservationmeasures.eam.commands.CommandLinkNodes;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.ProjectForTesting;
import org.conservationmeasures.eam.testall.EAMTestCase;

public class TestDelete extends EAMTestCase
{
	public TestDelete(String name)
	{
		super(name);
	}
	
	public void testBasics() throws Exception
	{
		ProjectForTesting project = new ProjectForTesting(getName());
		DiagramModel model = project.getDiagramModel();
		
		CommandInsertNode insertIntervention = new CommandInsertNode(Node.TYPE_INTERVENTION);
		CommandInsertNode insertFactor = new CommandInsertNode(Node.TYPE_FACTOR);
		insertIntervention.execute(project);
		Node intervention = model.getNodeById(insertIntervention.getId());
		insertFactor.execute(project);
		Node factor = model.getNodeById(insertFactor.getId());
		int interventionId = intervention.getId();
		int factorId = factor.getId();
		CommandLinkNodes link = new CommandLinkNodes(interventionId, factorId);
		link.execute(project);
		int linkageId = link.getLinkageId();
		
		assertTrue("linkage not found?", model.hasLinkage(intervention, factor));
		
		CommandDeleteLinkage delete = new CommandDeleteLinkage(linkageId);
		delete.execute(project);
		assertFalse("linkage not deleted?", model.hasLinkage(intervention, factor));
		
		EAM.setLogToString();
		try
		{
			delete.execute(project);
			fail("should have thrown for deleting non-existant linkage");
		}
		catch(CommandFailedException ignoreExpected)
		{
		}
		EAM.setLogToConsole();
		
		CommandDeleteNode deleteNode = new CommandDeleteNode(factorId);
		deleteNode.execute(project);
		assertFalse("node not deleted?", model.isNodeInProject(factor));

		EAM.setLogToString();
		try
		{
			deleteNode.execute(project);
			fail("should have thrown for deleting non-existant linkage");
		}
		catch(CommandFailedException ignoreExpected)
		{
		}
		EAM.setLogToConsole();
	}
}
