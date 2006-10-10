/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.project;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.Vector;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandDeleteNode;
import org.conservationmeasures.eam.commands.CommandDiagramMove;
import org.conservationmeasures.eam.commands.CommandInsertNode;
import org.conservationmeasures.eam.commands.CommandLinkNodes;
import org.conservationmeasures.eam.commands.CommandSetNodeSize;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.commands.CommandSwitchView;
import org.conservationmeasures.eam.database.ProjectServer;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.diagram.EAMGraphCell;
import org.conservationmeasures.eam.diagram.nodes.DiagramLinkage;
import org.conservationmeasures.eam.diagram.nodes.DiagramNode;
import org.conservationmeasures.eam.diagram.nodetypes.NodeType;
import org.conservationmeasures.eam.diagram.nodetypes.NodeTypeFactor;
import org.conservationmeasures.eam.diagram.nodetypes.NodeTypeIntervention;
import org.conservationmeasures.eam.diagram.nodetypes.NodeTypeTarget;
import org.conservationmeasures.eam.exceptions.AlreadyInThatViewException;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdAssigner;
import org.conservationmeasures.eam.ids.IndicatorId;
import org.conservationmeasures.eam.ids.ModelNodeId;
import org.conservationmeasures.eam.ids.ObjectiveIds;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.TransferableEamList;
import org.conservationmeasures.eam.objecthelpers.CreateModelLinkageParameter;
import org.conservationmeasures.eam.objecthelpers.CreateModelNodeParameter;
import org.conservationmeasures.eam.objecthelpers.DirectThreatSet;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objectpools.LinkagePool;
import org.conservationmeasures.eam.objects.ConceptualModelFactor;
import org.conservationmeasures.eam.objects.ConceptualModelLinkage;
import org.conservationmeasures.eam.objects.ConceptualModelNode;
import org.conservationmeasures.eam.objects.ConceptualModelNodeSet;
import org.conservationmeasures.eam.objects.ViewData;
import org.conservationmeasures.eam.testall.EAMTestCase;
import org.conservationmeasures.eam.views.diagram.DiagramView;
import org.martus.util.DirectoryUtils;

public class TestProject extends EAMTestCase
{
	public TestProject(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		project = new ProjectForTesting(getName());
		idAssigner = project.getNodeIdAssigner();
		chainManager = new ChainManager(project);
		super.setUp();
	}
	
	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
	}
	
	public void testUndoRedoSaveInfoAndDiagram() throws Exception
	{
		CommandInsertNode cmd = new CommandInsertNode(new NodeTypeFactor());
		project.executeCommand(cmd);
		DiagramModel model = new DiagramModel(project);
		project.getDatabase().readDiagram(model);
		assertEquals("not one node?", 1, model.getAllNodes().size());
		
		project.undo();
		project.getDatabase().readDiagram(model);
		assertEquals("node not removed?", 0, model.getAllNodes().size());

		project.redo();
		project.getDatabase().readDiagram(model);
		assertEquals("node not re-added?", 1, model.getAllNodes().size());
	}
	
	public void testGetViewData() throws Exception
	{
		String viewName1 = DiagramView.getViewName();
		ViewData diagramData = project.getViewData(viewName1);
		assertEquals("gave back wrong data?", viewName1, diagramData.getLabel());
		ViewData repeat = project.getViewData(viewName1);
		assertEquals("didn't return same data?", diagramData.getId(), repeat.getId());
		
		assertNotNull("didn't create arbitrary data?", project.getViewData("iweflijjfliej"));
	}
	
	public void testCreateAndDeleteModelLinkage() throws Exception
	{
		ModelNodeId threatId = (ModelNodeId)project.createObject(ObjectType.MODEL_NODE, BaseId.INVALID, new CreateModelNodeParameter(new NodeTypeFactor()));
		ModelNodeId targetId = (ModelNodeId)project.createObject(ObjectType.MODEL_NODE, BaseId.INVALID, new CreateModelNodeParameter(new NodeTypeTarget()));
		ConceptualModelFactor factor = (ConceptualModelFactor)project.findNode(threatId);
		assertFalse("already direct threat?", factor.isDirectThreat());
		CreateModelLinkageParameter parameter = new CreateModelLinkageParameter(threatId, targetId);
		BaseId createdId = project.createObject(ObjectType.MODEL_LINKAGE, BaseId.INVALID, parameter);
		BaseId linkageId = createdId;
		assertTrue("didn't become direct threat?", factor.isDirectThreat());
		project.deleteObject(ObjectType.MODEL_LINKAGE, linkageId);
		assertFalse("still a direct threat?", factor.isDirectThreat());
	}
	
	public void testGetSnapped() throws Exception
	{
		Point zeroZero = new Point(0, 0);
		assertEquals("moved zero zero?", zeroZero, project.getSnapped(zeroZero));
		
		Point defaultPlus = new Point(Project.DEFAULT_GRID_SIZE, Project.DEFAULT_GRID_SIZE);
		assertEquals("moved default plus?", defaultPlus, project.getSnapped(defaultPlus));
		
		Point defaultMinus = new Point(-Project.DEFAULT_GRID_SIZE, -Project.DEFAULT_GRID_SIZE);
		assertEquals("moved default minus?", defaultMinus, project.getSnapped(defaultMinus));

		Point oneThirdPlus = new Point(Project.DEFAULT_GRID_SIZE/3, Project.DEFAULT_GRID_SIZE/3);
		assertEquals("didn't snap one third?", zeroZero, project.getSnapped(oneThirdPlus));
		
		Point oneThirdMinus = new Point(-Project.DEFAULT_GRID_SIZE/3, -Project.DEFAULT_GRID_SIZE/3);
		assertEquals("didn't snap minus one third?", zeroZero, project.getSnapped(oneThirdMinus));
		
		Point twoThirdsPlus = new Point(2*Project.DEFAULT_GRID_SIZE/3, 2*Project.DEFAULT_GRID_SIZE/3);
		assertEquals("didn't snap two thirds?", defaultPlus, project.getSnapped(twoThirdsPlus));
		
		Point twoThirdsMinus = new Point(-2*Project.DEFAULT_GRID_SIZE/3, -2*Project.DEFAULT_GRID_SIZE/3);
		assertEquals("didn't snap minus two thirds?", defaultMinus, project.getSnapped(twoThirdsMinus));
		
		
	}
	
	public void testIsValidProjectFilename() throws Exception
	{
		assertTrue("AlphaNumericDotDashSpace", Project.isValidProjectFilename("AZaz09.- "));
		assertFalse("allowed really long name?", Project.isValidProjectFilename("1234567890123456789012345678901234567890"));
		assertFalse("Other Punct", Project.isValidProjectFilename("$"));
		final char ACCENT_A_LOWER = 0xE1;
		assertTrue("Foreign", Project.isValidProjectFilename(new String(new char[] {ACCENT_A_LOWER})));
	}
	
	public void testViewChanges() throws Exception
	{
		assertEquals("didn't start in summary view?", Project.SUMMARY_VIEW_NAME, project.getCurrentView());
		String destination = Project.MAP_VIEW_NAME;
		CommandSwitchView toMap = new CommandSwitchView(destination);
		project.executeCommand(toMap);
		assertEquals("didn't update project?", destination, project.getCurrentView());
		try
		{
			project.executeCommand(toMap);
			fail("should have thrown for switch to current view");
		}
		catch(AlreadyInThatViewException ignoreExpected)
		{
		}
		
		Command illegalView = new CommandSwitchView("Not a legal view name");
		try
		{
			project.executeCommand(illegalView);
			fail("should have thrown for switch to bogus view name");
		}
		catch(CommandFailedException ignoreExpected)
		{
		}
	}

	public void testGetAllSelectedCellsWithLinkages() throws Exception
	{
		DiagramNode node1 = createNode(DiagramNode.TYPE_TARGET);
		DiagramNode node2 =  createNode(DiagramNode.TYPE_INTERVENTION);
		DiagramNode node3 =  createNode(DiagramNode.TYPE_FACTOR);
		
		DiagramLinkage linkage1 = createLinkage(idAssigner.takeNextId(), node1.getWrappedId(), node2.getWrappedId());
		DiagramLinkage linkage2 = createLinkage(idAssigner.takeNextId(), node1.getWrappedId(), node3.getWrappedId());
		
		EAMGraphCell[] selectedCells = {linkage1};
		Vector selectedItems = project.getAllSelectedCellsWithLinkages(selectedCells);
		assertEquals(1, selectedItems.size());
		assertContains(linkage1, selectedItems);
		
		selectedCells[0] = node2;
		selectedItems = project.getAllSelectedCellsWithLinkages(selectedCells);
		assertEquals(2, selectedItems.size());
		assertContains(node2, selectedItems);
		assertContains(linkage1, selectedItems);
		
		selectedCells[0] = node1;
		selectedItems = project.getAllSelectedCellsWithLinkages(selectedCells);
		assertEquals(3, selectedItems.size());
		assertContains(node1, selectedItems);
		assertContains(linkage1, selectedItems);
		assertContains(linkage2, selectedItems);
	}

	public void testGetAllSelectedNodes() throws Exception
	{
		DiagramNode node1 = createNode(DiagramNode.TYPE_TARGET);
		DiagramNode node2 =  createNode(DiagramNode.TYPE_INTERVENTION);
		
		DiagramLinkage linkage1 = createLinkage(idAssigner.takeNextId(), node1.getWrappedId(), node2.getWrappedId());
		
		EAMGraphCell[] selectedCells = {linkage1};
		EAMGraphCell[] selectedItems = project.getOnlySelectedNodes(selectedCells);
		assertEquals(0, selectedItems.length);
		
		selectedCells[0] = node2;
		selectedItems = project.getOnlySelectedNodes(selectedCells);
		assertEquals(1, selectedItems.length);
		assertEquals(node2, selectedItems[0]);
		
		EAMGraphCell[] selectedCellsTwo = {node2,linkage1,node1};
		selectedItems = project.getOnlySelectedNodes(selectedCellsTwo);
		assertEquals(2, selectedItems.length);
	}
	
	public void TestPasteNodesAndLinksIntoProject() throws Exception
	{
		DiagramModel model = project.getDiagramModel();

		DiagramNode node1 = createNode(DiagramNode.TYPE_TARGET);
		DiagramNode node2 =  createNode(DiagramNode.TYPE_INTERVENTION);
		DiagramNode node3 =  createNode(DiagramNode.TYPE_FACTOR);
		
		createLinkage(idAssigner.takeNextId(), node1.getWrappedId(), node2.getWrappedId());
		createLinkage(idAssigner.takeNextId(), node1.getWrappedId(), node3.getWrappedId());
		
		Vector cellVector = project.getAllSelectedCellsWithLinkages(new EAMGraphCell[]{node1});
		Object[] selectedCells = cellVector.toArray(new EAMGraphCell[0]);
		TransferableEamList transferableList = new TransferableEamList(selectedCells);
		assertEquals(3, model.getAllNodes().size());
		assertEquals(2, model.getLinkages(node1).size());
		assertEquals(1, model.getLinkages(node2).size());
		assertEquals(1, model.getLinkages(node3).size());
		
		new NodeCommandHelper(project).pasteNodesAndLinksIntoProject(transferableList, new Point(5,5));
		Vector nodes = model.getAllNodes();
		assertEquals(4, nodes.size());
		assertEquals(4, model.getAllLinkages().size());
		for(int i = 0; i < nodes.size(); ++i)
		{
			assertEquals(2, model.getLinkages((DiagramNode)nodes.get(i)).size());
		}
		
		//Test when a pasted item has linkages to a previously deleted node
		model.deleteNode(node2);
		new NodeCommandHelper(project).pasteNodesAndLinksIntoProject(transferableList, new Point(5,5));
		assertEquals(2, model.getLinkages(node1).size());
		assertEquals(3, model.getLinkages(node3).size());
	}

	public void testDiagramMoveOnly() throws Exception
	{
		DiagramNode node1 = createNode(DiagramNode.TYPE_TARGET);
		node1.setPreviousLocation(new Point(0,0));
		node1.setLocation(new Point(0,0));
		node1.setPreviousSize(node1.getSize());

		BaseId[] noNodesMoved = new BaseId[1];
		noNodesMoved[0] = node1.getDiagramNodeId();
	
		new NodeMoveHandler(project).nodesWereMovedOrResized(0, 0, noNodesMoved);
		try
		{
			project.getLastCommand();
			fail("Should have thrown a null pointer since command should not have been recorded for a node which wasn't moved");
		}
		catch(Exception expected)
		{
		}
		
		
		int deltaX = 55;
		int deltaY = 88;
		node1.setLocation(new Point(deltaX,deltaY));
		
		DiagramNode node2 =  createNode(DiagramNode.TYPE_FACTOR);
		node2.setPreviousLocation(new Point(10,10));
		node2.setLocation(new Point(20,30));
		
		BaseId[] ids = new BaseId[2];
		ids[0] = node1.getDiagramNodeId();
		ids[1] = node2.getDiagramNodeId();
		
		
		new NodeMoveHandler(project).nodesWereMovedOrResized(deltaX, deltaY, ids);
		project.getLastCommand(); //End Transaction
		CommandDiagramMove commandDiagramMoveRecorded = (CommandDiagramMove)project.getLastCommand();
		assertEquals(deltaX, commandDiagramMoveRecorded.getDeltaX());
		assertEquals(deltaY, commandDiagramMoveRecorded.getDeltaY());
		assertEquals(node1.getDiagramNodeId(), commandDiagramMoveRecorded.getIds()[0]);
		assertEquals(node2.getDiagramNodeId(), commandDiagramMoveRecorded.getIds()[1]);
		project.getLastCommand(); //begin Transaction
		
	}
	
	public void testResizeNodesOnly() throws Exception
	{
		DiagramNode node1 =  createNode(DiagramNode.TYPE_INTERVENTION);
		node1.setSize(new Dimension(5,10));
		node1.setPreviousSize((new Dimension(55, 80)));
		node1.setPreviousLocation(new Point(0,0));
		node1.setLocation(new Point(0,0));

		DiagramNode node2 =  createNode(DiagramNode.TYPE_FACTOR);
		node2.setSize(new Dimension(15,15));
		node2.setPreviousSize((new Dimension(52, 33)));
		node2.setPreviousLocation(new Point(0,0));
		node2.setLocation(new Point(0,0));
		
		BaseId[] ids = new BaseId[2];
		ids[0] = node1.getDiagramNodeId();
		ids[1] = node2.getDiagramNodeId();
		
		new NodeMoveHandler(project).nodesWereMovedOrResized(0, 0, ids);
		project.getLastCommand(); //End Transaction
		CommandSetNodeSize commandSetNodeSize2Recorded = (CommandSetNodeSize)project.getLastCommand();
		assertEquals(node2.getSize(), commandSetNodeSize2Recorded.getCurrentSize());
		assertEquals(node2.getPreviousSize(), commandSetNodeSize2Recorded.getPreviousSize());
		CommandSetNodeSize commandSetNodeSize1Recorded = (CommandSetNodeSize)project.getLastCommand();
		assertEquals(node1.getSize(), commandSetNodeSize1Recorded.getCurrentSize());
		assertEquals(node1.getPreviousSize(), commandSetNodeSize1Recorded.getPreviousSize());
		project.getLastCommand(); //begin Transaction
	}

	public void testResizeAndMoveNodes() throws Exception
	{
		int x = 5;
		int y = 10;
		int deltaX = 20;
		int deltaY = 30;
		Dimension position1 = new Dimension(45, 65);
		Dimension position2 = new Dimension(95, 88);
		
		
		DiagramNode nodeResizedAndMoved =  createNode(DiagramNode.TYPE_INTERVENTION);
		nodeResizedAndMoved.setSize(position1);
		nodeResizedAndMoved.setPreviousSize(position2);
		nodeResizedAndMoved.setPreviousLocation(new Point(x,y));
		nodeResizedAndMoved.setLocation(new Point(x+deltaX, y+deltaY));

		DiagramNode nodeMovedOnly =  createNode(DiagramNode.TYPE_FACTOR);
		nodeMovedOnly.setSize(position1);
		nodeMovedOnly.setPreviousSize(position1);
		nodeMovedOnly.setPreviousLocation(new Point(x,y));
		nodeMovedOnly.setLocation(new Point(x+deltaX, y+deltaY));
		
		DiagramNode nodeResizedOnly = createNode(DiagramNode.TYPE_FACTOR);
		nodeResizedOnly.setSize(position1);
		nodeResizedOnly.setPreviousSize(position2);
		nodeResizedOnly.setPreviousLocation(new Point(x,y));
		nodeResizedOnly.setLocation(new Point(x,y));

		DiagramNode nodeNotMovedOrResized =  createNode(DiagramNode.TYPE_FACTOR);
		nodeNotMovedOrResized.setSize(position1);
		nodeNotMovedOrResized.setPreviousSize(position1);
		nodeNotMovedOrResized.setPreviousLocation(new Point(x,y));
		nodeNotMovedOrResized.setLocation(new Point(x,y));

		
		BaseId[] ids = new BaseId[4];
		ids[0] = nodeResizedAndMoved.getDiagramNodeId();
		ids[1] = nodeMovedOnly.getDiagramNodeId();
		ids[2] = nodeResizedOnly.getDiagramNodeId();
		ids[3] = nodeNotMovedOrResized.getDiagramNodeId();

		new NodeMoveHandler(project).nodesWereMovedOrResized(deltaX, deltaY, ids);
		
		project.getLastCommand(); //End Transaction
		
		CommandDiagramMove commandDiagramMoveRecorded = (CommandDiagramMove)project.getLastCommand();
		assertEquals(deltaX, commandDiagramMoveRecorded.getDeltaX());
		assertEquals(deltaY, commandDiagramMoveRecorded.getDeltaY());
		assertEquals(nodeResizedAndMoved.getDiagramNodeId(), commandDiagramMoveRecorded.getIds()[0]);
		assertEquals(nodeMovedOnly.getDiagramNodeId(), commandDiagramMoveRecorded.getIds()[1]);

		CommandSetNodeSize commandNodeResizedOnlyRecorded = (CommandSetNodeSize)project.getLastCommand();
		assertEquals(nodeResizedOnly.getSize(), commandNodeResizedOnlyRecorded.getCurrentSize());
		assertEquals(nodeResizedOnly.getPreviousSize(), commandNodeResizedOnlyRecorded.getPreviousSize());
		
		CommandSetNodeSize commandNodeResizedAndMovedRecorded = (CommandSetNodeSize)project.getLastCommand();
		assertEquals(nodeResizedAndMoved.getSize(), commandNodeResizedAndMovedRecorded.getCurrentSize());
		assertEquals(nodeResizedAndMoved.getPreviousSize(), commandNodeResizedAndMovedRecorded.getPreviousSize());
		
		project.getLastCommand(); //begin Transaction
	}

	public void testPasteNodesOnlyIntoProject() throws Exception
	{
		DiagramModel model = project.getDiagramModel();

		DiagramNode node1 = createNode(DiagramNode.TYPE_TARGET);
		DiagramNode node2 = createNode(DiagramNode.TYPE_INTERVENTION);
		DiagramNode node3 = createNode(DiagramNode.TYPE_FACTOR);
		
		createLinkage(idAssigner.takeNextId(), node1.getWrappedId(), node2.getWrappedId());
		createLinkage(idAssigner.takeNextId(), node1.getWrappedId(), node3.getWrappedId());
		
		Vector cellVector = project.getAllSelectedCellsWithLinkages(new EAMGraphCell[]{node1});
		Object[] selectedCells = cellVector.toArray(new EAMGraphCell[0]);
		TransferableEamList transferableList = new TransferableEamList(selectedCells);
		assertEquals(3, model.getAllNodes().size());
		assertEquals(2, model.getLinkages(node1).size());
		assertEquals(1, model.getLinkages(node2).size());
		assertEquals(1, model.getLinkages(node3).size());
		
		new NodeCommandHelper(project).pasteNodesOnlyIntoProject(transferableList, new Point(5,5));
		Vector nodes = model.getAllNodes();
		assertEquals(4, nodes.size());
		assertEquals(2, model.getAllLinkages().size());
	}
	
	public void testCutAndPaste() throws Exception
	{
		assertEquals("objects already in the pool?", 0, project.getNodePool().size());
		DiagramModel model = project.getDiagramModel();

		DiagramNode node1 = createNode(DiagramNode.TYPE_TARGET);

		Object[] selectedCells = new DiagramNode[] {node1};
		TransferableEamList transferableList = new TransferableEamList(selectedCells);
		
		CommandDeleteNode.deleteNode(project, node1.getDiagramNodeId());
		assertEquals("objects still in the pool?", 0, project.getNodePool().size());

		Point pastePoint = new Point(5,5);
		new NodeCommandHelper(project).pasteNodesAndLinksIntoProject(transferableList, pastePoint);
		Vector nodes = model.getAllNodes();
		assertEquals(1, nodes.size());
		DiagramNode pastedNode = (DiagramNode)nodes.get(0);
		assertEquals("didn't paste correct size?", node1.getSize(), pastedNode.getSize());
		assertNotEquals("didn't change id?", node1.getDiagramNodeId(), pastedNode.getDiagramNodeId());
		assertEquals("didn't snap?", project.getSnapped(pastePoint), pastedNode.getLocation());

		assertEquals("not just one object in the pool?", 1, project.getNodePool().size());
	}

	public void testCloseClearsCurrentView() throws Exception
	{
		assertEquals("not starting on summary view?", Project.SUMMARY_VIEW_NAME, project.getCurrentView());
		String sampleViewName = Project.MAP_VIEW_NAME;
		project.switchToView(sampleViewName);
		assertEquals("didn't switch?", sampleViewName, project.getCurrentView());
		project.close();
		assertEquals("didn't reset view?", Project.NO_PROJECT_VIEW_NAME, project.getCurrentView());
	}
	
	public void testExecuteCommandWritesDiagram() throws Exception
	{
		CommandInsertNode cmd = new CommandInsertNode(new NodeTypeTarget());
		project.executeCommand(cmd);
		DiagramModel copyOfModel = new DiagramModel(project);
		project.getDatabase().readDiagram(copyOfModel);
		assertEquals("didn't read back our one node?", 1, copyOfModel.getAllNodes().size());
	}
	
	public void testNodesGetWritten() throws Exception
	{
		ProjectServerForTesting database = project.getTestDatabase();
		int existingCalls = database.callsToWriteObject;
		
		CommandInsertNode targetCommand = new CommandInsertNode(new NodeTypeTarget());
		project.executeCommand(targetCommand);
		assertEquals(1 + existingCalls, database.callsToWriteObject);
		ModelNodeId targetId = targetCommand.getId();
		
		CommandInsertNode factorCommand = new CommandInsertNode(new NodeTypeFactor());
		project.executeCommand(factorCommand);
		assertEquals(2 + existingCalls, database.callsToWriteObject);
		ModelNodeId factorId = factorCommand.getId();
		DiagramNode factor = project.getDiagramModel().getNodeById(factorId);
		
		int type = factor.getUnderlyingObject().getType();
		String tag = ConceptualModelNode.TAG_LABEL;
		CommandSetObjectData setData = new CommandSetObjectData(type, factor.getWrappedId(), tag, "woo");
		project.executeCommand(setData);
		assertEquals(3 + existingCalls, database.callsToWriteObject);
		
		project.undo();
		assertEquals(4 + existingCalls, database.callsToWriteObject);
		
		project.redo();
		assertEquals(5 + existingCalls, database.callsToWriteObject);

		project.executeCommand(new CommandDiagramMove(9, 9, new BaseId[] {targetId, factorId} ));
		assertEquals(5 + existingCalls, database.callsToWriteObject);
		
		Dimension oldDimension = factor.getSize();
		project.executeCommand(new CommandSetNodeSize(factorId, new Dimension(50, 75), oldDimension));
		assertEquals(5 + existingCalls, database.callsToWriteObject);
		
	}
	
	public void testInsertDuplicateNodes() throws Exception
	{
		BaseId id = new BaseId(3023);
		BaseId gotIdFirst = CommandInsertNode.createNode(project, DiagramNode.TYPE_FACTOR, id);
		assertEquals("Didn't get our id?", id, gotIdFirst);
		try
		{
			CommandInsertNode.createNode(project, DiagramNode.TYPE_FACTOR, id);
			fail("Should have thrown for inserting a duplicate id");
		}
		catch(RuntimeException ignoreExpected)
		{
		}
		
		CommandInsertNode cmd = new CommandInsertNode(DiagramNode.TYPE_FACTOR);
		project.executeCommand(cmd);
		try
		{
			EAM.setLogToString();
			cmd.execute(project);
			fail("Should have thrown for replaying an insert over an existing node");
		}
		catch(CommandFailedException ignoreExpected)
		{
		}
		finally
		{
			EAM.setLogToConsole();
		}
		
	}
	
	public void testLinkagePool() throws Exception
	{
		DiagramNode nodeA = createNode(new NodeTypeFactor());
		DiagramNode nodeB = createNode(new NodeTypeTarget());
		ModelNodeId idA = nodeA.getWrappedId();
		ModelNodeId idB = nodeB.getWrappedId();
		CreateModelLinkageParameter parameter = new CreateModelLinkageParameter(idA, idB);
		BaseId createdId = project.createObject(ObjectType.MODEL_LINKAGE, idAssigner.takeNextId(), parameter);
		BaseId linkageId = createdId;
		LinkagePool linkagePool = project.getLinkagePool();
		assertEquals("not in pool?", 1, linkagePool.size());
		ConceptualModelLinkage cmLinkage = linkagePool.find(linkageId);
		assertEquals("wrong from?", nodeA.getDiagramNodeId(), cmLinkage.getFromNodeId());
		assertEquals("wrong to?", nodeB.getDiagramNodeId(), cmLinkage.getToNodeId());
		assertTrue("not linked?", project.isLinked(nodeA.getDiagramNodeId(), nodeB.getDiagramNodeId()));
		
		project.deleteObject(ObjectType.MODEL_LINKAGE, linkageId);
		assertEquals("Didn't remove from pool?", 0, linkagePool.size());
		assertFalse("still linked?", project.isLinked(nodeA.getDiagramNodeId(), nodeB.getDiagramNodeId()));
	}

	public void testFindNodesThatUseThisObjective() throws Exception
	{
		DiagramNode nodeA = createNode(new NodeTypeTarget());
		DiagramNode nodeB = createNode(new NodeTypeTarget());
		createNode(new NodeTypeTarget());
		
		BaseId objectiveId1 = project.createObject(ObjectType.OBJECTIVE);
		BaseId objectiveId2 = project.createObject(ObjectType.OBJECTIVE);

		ObjectiveIds objectiveId = new ObjectiveIds();
		objectiveId.add(objectiveId1);
		
		nodeA.getUnderlyingObject().setObjectives(objectiveId);
		nodeB.getUnderlyingObject().setObjectives(objectiveId);
		
		ConceptualModelNodeSet foundNodes = chainManager.findNodesThatUseThisObjective(objectiveId1);
				
		assertEquals("didn't find both nodes?", 2, foundNodes.size());
		assertContains("missing nodeA? ", nodeA.getUnderlyingObject(), foundNodes);
		assertContains("missing nodeB?", nodeB.getUnderlyingObject(), foundNodes);

		ConceptualModelNodeSet noNodes = chainManager.findNodesThatUseThisObjective(objectiveId2);
		
		assertEquals("found a node?", 0, noNodes.size());
		
	}
	
	public void testFindAllNodesRelatedToThisObjective() throws Exception
	{
		DiagramNode nodeIndirectFactor = createNode(new NodeTypeFactor());
		DiagramNode nodeDirectThreat = createNode(new NodeTypeFactor());
		
		BaseId objectiveId1 = project.createObject(ObjectType.OBJECTIVE);
		
		ObjectiveIds objectiveId = new ObjectiveIds();
		objectiveId.add(objectiveId1);

		nodeIndirectFactor.getUnderlyingObject().setObjectives(objectiveId);
		
		createLinkage(BaseId.INVALID, nodeIndirectFactor.getWrappedId(), nodeDirectThreat.getWrappedId());
		
		ConceptualModelNodeSet foundNodes = chainManager.findAllNodesRelatedToThisObjective(objectiveId1);
		
		assertEquals("didn't find anything?", 2, foundNodes.size());
		assertContains("missing direct threat?", nodeDirectThreat.getUnderlyingObject(), foundNodes);
		assertContains("missing indirect factor?", nodeIndirectFactor.getUnderlyingObject(), foundNodes);
		
		
	}
	
	public void testFindNodesThatUseThisIndicator() throws Exception
	{
		
		DiagramNode nodeA = createNode(new NodeTypeTarget());
		DiagramNode nodeB = createNode(new NodeTypeTarget());
		createNode(new NodeTypeTarget());
		
		IndicatorId indicatorId1 = (IndicatorId)project.createObject(ObjectType.INDICATOR);
		IndicatorId indicatorId2 = (IndicatorId)project.createObject(ObjectType.INDICATOR);
		
		nodeA.getUnderlyingObject().setIndicatorId(indicatorId1);
		nodeB.getUnderlyingObject().setIndicatorId(indicatorId1);
		
		ConceptualModelNodeSet foundNodes = chainManager.findNodesThatUseThisIndicator(indicatorId1);
				
		assertEquals("didn't find both nodes?", 2, foundNodes.size());
		assertContains("missing nodeA? ", nodeA.getUnderlyingObject(), foundNodes);
		assertContains("missing nodeB?", nodeB.getUnderlyingObject(), foundNodes);

		ConceptualModelNodeSet noNodes = chainManager.findNodesThatUseThisIndicator(indicatorId2);
		
		assertEquals("found a node?", 0, noNodes.size());
	}
	
	public void testFindAllNodesRelatedToThisIndicator() throws Exception
	{
		DiagramNode nodeIndirectFactor = createNode(new NodeTypeFactor());
		DiagramNode nodeDirectThreat = createNode(new NodeTypeFactor());
		
		IndicatorId indicatorId1 = (IndicatorId)project.createObject(ObjectType.INDICATOR);
		nodeIndirectFactor.getUnderlyingObject().setIndicatorId(indicatorId1);
		
		createLinkage(BaseId.INVALID, nodeIndirectFactor.getWrappedId(), nodeDirectThreat.getWrappedId());
		
		ConceptualModelNodeSet foundNodes = chainManager.findAllNodesRelatedToThisIndicator(indicatorId1);
		
		assertEquals("didn't find anything?", 2, foundNodes.size());
		assertContains("missing direct threat?", nodeDirectThreat.getUnderlyingObject(), foundNodes);
		assertContains("missing indirect factor?", nodeIndirectFactor.getUnderlyingObject(), foundNodes);
		
		
	}
	
	public void testDirectThreatSet() throws Exception
	{
		DiagramNode nodeIndirectFactor = createNode(new NodeTypeFactor());
		DiagramNode nodeDirectThreatA = createNode(new NodeTypeFactor());	
		((ConceptualModelFactor)nodeDirectThreatA.getUnderlyingObject()).increaseTargetCount();
		DiagramNode nodeDirectThreatB = createNode(new NodeTypeFactor());
		((ConceptualModelFactor)nodeDirectThreatB.getUnderlyingObject()).increaseTargetCount();
		
		ConceptualModelNodeSet allNodes = new ConceptualModelNodeSet();
		allNodes.attemptToAdd(nodeIndirectFactor.getUnderlyingObject());
		allNodes.attemptToAdd(nodeDirectThreatA.getUnderlyingObject());
		allNodes.attemptToAdd(nodeDirectThreatB.getUnderlyingObject());	
		
		DirectThreatSet foundNodes = new DirectThreatSet(allNodes);
		
		assertEquals("didn't find both nodes?", 2, foundNodes.size());
		assertContains("missing nodeDirectThreatA? ", nodeDirectThreatA.getUnderlyingObject(), foundNodes);
		assertContains("missing nodeDirectThreatB?", nodeDirectThreatB.getUnderlyingObject(), foundNodes);
	}
	
	public void testOpenProject() throws Exception
	{
		ModelNodeId factorId;
		File tempDir = createTempDirectory();
		Project diskProject = new Project(new ProjectServer());
		diskProject.createOrOpen(tempDir);
		try
		{
			
			CommandInsertNode cmdNode1 = new CommandInsertNode(new NodeTypeFactor());
			diskProject.executeCommand(cmdNode1);
			factorId = cmdNode1.getId();
			CommandInsertNode cmdNode2 = new CommandInsertNode(new NodeTypeTarget());
			diskProject.executeCommand(cmdNode2);
			ModelNodeId targetId = cmdNode2.getId();
			CommandLinkNodes cmdLinkage = new CommandLinkNodes(factorId, targetId);
			diskProject.executeCommand(cmdLinkage);
			CommandInsertNode cmdNode3 = new CommandInsertNode(new NodeTypeIntervention());
			diskProject.executeCommand(cmdNode3);
			CommandDeleteNode cmdDelete = new CommandDeleteNode(cmdNode3.getId());
			diskProject.executeCommand(cmdDelete);
		}
		finally
		{
			diskProject.close();
		}
		
		Project loadedProject = new Project(new ProjectServer());
		loadedProject.createOrOpen(tempDir);
		try
		{
			assertEquals("didn't read node pool?", 2, loadedProject.getNodePool().size());
			assertEquals("didn't read linkage pool?", 1, loadedProject.getLinkagePool().size());
			assertEquals("didn't populate diagram?", 2, loadedProject.getDiagramModel().getNodeCount());
			assertEquals("didn't preserve next node id?", diskProject.getNodeIdAssigner().takeNextId(), loadedProject.getNodeIdAssigner().takeNextId());
			BaseId expectedAnnotationId = diskProject.getAnnotationIdAssigner().takeNextId();
			assertEquals("didn't preserve next annotation id?", expectedAnnotationId, loadedProject.getAnnotationIdAssigner().takeNextId());
			ConceptualModelFactor factor = (ConceptualModelFactor)loadedProject.findNode(factorId);
			assertTrue("didn't update factor target count?", factor.isDirectThreat());
		}
		finally
		{
			loadedProject.close();
			DirectoryUtils.deleteEntireDirectoryTree(tempDir);
		}
		
		int highestAnnotationIdBeforeClearing = diskProject.getAnnotationIdAssigner().getHighestAssignedId();
		
		File emptyDir = createTempDirectory();
		diskProject.createOrOpen(emptyDir);
		try
		{
			assertEquals("didn't clear node pool?", 0, diskProject.getNodePool().size());
			assertEquals("didn't clear linkage pool?", 0, diskProject.getLinkagePool().size());
			assertEquals("didn't clear diagram?", 0, diskProject.getDiagramModel().getNodeCount());
			assertEquals("didn't clear next node id?", new BaseId(0), diskProject.getNodeIdAssigner().takeNextId());
			assertTrue("didn't clear next annotation id?", diskProject.getAnnotationIdAssigner().getHighestAssignedId() < highestAnnotationIdBeforeClearing);
		}
		finally
		{
			diskProject.close();
			DirectoryUtils.deleteEntireDirectoryTree(emptyDir);
		}
		
	}
	
	public void testCreateNewProject() throws Exception
	{
		File tempDir = createTempDirectory();
		Project newProject = new Project(new ProjectServer());
		newProject.createOrOpen(tempDir);
		
		try
		{
			assertEquals("default criterion not created?", 3, newProject.getThreatRatingFramework().getCriteria().length);
			assertEquals("default valueoptions not created?", 5, newProject.getThreatRatingFramework().getValueOptions().length);
		}
		finally
		{
			newProject.close();
			DirectoryUtils.deleteEntireDirectoryTree(tempDir);
		}
	}
	
	private DiagramNode createNode(NodeType nodeType) throws Exception
	{
		BaseId insertedId = CommandInsertNode.createNode(project, nodeType, BaseId.INVALID);
		return project.getDiagramModel().getNodeById(insertedId);
	}
	
	private DiagramLinkage createLinkage(BaseId id, ModelNodeId fromId, ModelNodeId toId) throws Exception
	{
		BaseId insertedId = CommandLinkNodes.createLinkage(project, id, fromId, toId);
		return project.getDiagramModel().getLinkageById(insertedId);
	}


	ProjectForTesting project;
	IdAssigner idAssigner;
	ChainManager chainManager;
}
