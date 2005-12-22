/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram.nodes;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandInsertNode;
import org.conservationmeasures.eam.commands.CommandSetFactorType;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.diagram.nodetypes.NodeType;
import org.conservationmeasures.eam.project.ProjectForTesting;
import org.martus.util.TestCaseEnhanced;

public class TestSetFactorType extends TestCaseEnhanced
{
	public TestSetFactorType(String name)
	{
		super(name);
	}

	public void testSetFactorType() throws Exception
	{
		ProjectForTesting project = new ProjectForTesting(getName());
		DiagramModel model = project.getDiagramModel();

		CommandInsertNode insertCommand = new CommandInsertNode(DiagramNode.TYPE_STRESS);
		insertCommand.execute(project);
		int id = insertCommand.getId();

		NodeType newType = DiagramNode.TYPE_DIRECT_THREAT;
		Command setTextCommand = new CommandSetFactorType(id, newType);
		setTextCommand.execute(project);

		DiagramNode found = model.getNodeById(id);
		assertEquals("wrong type?", newType, found.getType());
		
		project.close();
	}

}
