/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.commands;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Vector;

import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.diagram.cells.FactorCell;
import org.conservationmeasures.eam.diagram.cells.LinkCell;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.exceptions.NothingToRedoException;
import org.conservationmeasures.eam.exceptions.NothingToUndoException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.DiagramFactorLinkId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.CommandExecutedListener;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objecthelpers.CreateDiagramFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.CreateDiagramFactorParameter;
import org.conservationmeasures.eam.objecthelpers.CreateFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.CreateTaskParameter;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.DiagramFactor;
import org.conservationmeasures.eam.objects.DiagramFactorLink;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.RatingCriterion;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.project.ProjectForTesting;
import org.conservationmeasures.eam.project.ThreatRatingFramework;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;
import org.conservationmeasures.eam.views.diagram.InsertFactorLinkDoer;
import org.conservationmeasures.eam.views.map.MapView;

public class TestCommands extends EAMTestCase
{
	public TestCommands(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		project = new ProjectForTesting(getName());
		consumeNodeIdZero();
		super.setUp();
	}

	private void consumeNodeIdZero() throws Exception
	{
		project.createFactor(ObjectType.TARGET);
	}
	
	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
	}
	
	public void testCommandSetObjectData_RatingCriterion() throws Exception
	{
		int type = ObjectType.RATING_CRITERION;
		BaseId createdId = project.createObject(type);
		RatingCriterion criterion = project.getThreatRatingFramework().getCriterion(createdId);
		
		String field = RatingCriterion.TAG_LABEL;
		String value = "Blah";
		CommandSetObjectData cmd = new CommandSetObjectData(type, createdId, field, value);
		assertEquals("wrong type?", type, cmd.getObjectType());
		assertEquals("wrong id?", createdId, cmd.getObjectId());
		assertEquals("wrong field?", field, cmd.getFieldTag());
		assertEquals("wrong value?", value, cmd.getDataValue());
		
		project.executeCommand(cmd);
		assertEquals("didn't set value?", value, criterion.getLabel());
		
		CommandSetObjectData badId = new CommandSetObjectData(type, new BaseId(-99), field, value);
		try
		{
			ignoreLogs();
			project.executeCommand(badId);
			fail("Should have thrown for bad id");
		}
		catch (CommandFailedException ignoreExpected)
		{
		}
		finally 
		{
			logToConsole();
		}
		
		CommandSetObjectData badField = new CommandSetObjectData(type, createdId, "bogus", value);
		try
		{
			ignoreLogs();
			project.executeCommand(badField);
			fail("Should have thrown for bad field tag");
		}
		catch (CommandFailedException ignoreExpected)
		{
		}
		finally
		{
			logToConsole();
		}
		
	}

	private void logToConsole()
	{
		EAM.setLogToConsole();
	}

	private void ignoreLogs()
	{
		EAM.setLogToString();
	}
	
	public void testCommandDeleteObject_ThreatRatingValueOption() throws Exception
	{
		int type = ObjectType.VALUE_OPTION;
		BaseId createdId = project.createObject(type);
		
		CommandDeleteObject cmd = new CommandDeleteObject(type, createdId);
		assertEquals("wrong type?", type, cmd.getObjectType());
		assertEquals("wrong id?", createdId, cmd.getObjectId());
		
		project.executeCommand(cmd);
		assertNull("Got deleted object?", project.getThreatRatingFramework().getValueOption(createdId));
		
		project.undo();
		assertNotNull("Didn't undelete?", project.getThreatRatingFramework().getValueOption(createdId));
	}
	
	public void testCommandDeleteObject_ThreatRatingCriterion() throws Exception
	{
		int type = ObjectType.RATING_CRITERION;
		BaseId createdId = project.createObject(type);
		
		CommandDeleteObject cmd = new CommandDeleteObject(type, createdId);
		assertEquals("wrong type?", type, cmd.getObjectType());
		assertEquals("wrong id?", createdId, cmd.getObjectId());
		
		project.executeCommand(cmd);
		assertNull("Got deleted object?", project.getThreatRatingFramework().getCriterion(createdId));
		
		project.undo();
		assertNotNull("Didn't undelete?", project.getThreatRatingFramework().getCriterion(createdId));
	}
	
	public void testCommandCreateObject_ThreatRatingCriterion() throws Exception
	{
		ThreatRatingFramework framework = project.getThreatRatingFramework();
		int type = ObjectType.RATING_CRITERION;
		CommandCreateObject cmd = new CommandCreateObject(type);
		assertEquals("wrong type?", type, cmd.getObjectType());
		assertEquals("created id already set?", BaseId.INVALID, cmd.getCreatedId());

		int oldCount = framework.getCriteria().length;
		project.executeCommand(cmd);
		assertEquals("added to framework?", oldCount, framework.getCriteria().length);
		RatingCriterion criterion = framework.getCriterion(cmd.getCreatedId());
		assertEquals("wrong default label?", BaseObject.DEFAULT_LABEL, criterion.getLabel());
		
		assertNotNull("didn't create?", framework.getCriterion(cmd.getCreatedId()));
		
		project.undo();
		assertEquals("didn't undo?", oldCount, framework.getCriteria().length);
	}
	
	public void testDiagramFactorsMove() throws Exception
	{
		Point moveTo = new Point(25, -68);
		Point zeroZero = new Point(0, 0);

		DiagramFactorId targetId = insertTarget();
		DiagramFactor target = (DiagramFactor) project.findObject(ObjectType.DIAGRAM_FACTOR, targetId);
		Point targetLocation = target.getLocation();
		String previousLocation = EnhancedJsonObject.convertFromPoint(targetLocation);
		String newLocation = EnhancedJsonObject.convertFromPoint(moveTo);
		CommandSetObjectData moveDiagramFactor1 = new CommandSetObjectData(ObjectType.DIAGRAM_FACTOR, target.getDiagramFactorId(), DiagramFactor.TAG_LOCATION, newLocation, previousLocation);
		project.executeCommand(moveDiagramFactor1);
		
		DiagramFactor diagramFactor1 = (DiagramFactor) project.findObject(ObjectType.DIAGRAM_FACTOR, targetId);
		assertEquals("didn't set location?", moveTo, diagramFactor1.getLocation());
		//undo move
		project.undo();
		DiagramFactor diagramFactor2 = (DiagramFactor) project.findObject(ObjectType.DIAGRAM_FACTOR, targetId);
		assertEquals("didn't restore original location?", zeroZero, diagramFactor2.getLocation());

		
		DiagramFactorId factorId = insertContributingFactor().getDiagramFactorId();
		DiagramFactor factor = (DiagramFactor) project.findObject(ObjectType.DIAGRAM_FACTOR, factorId);
		Point factorLocation = factor.getLocation();
		String previousFactorLocation = EnhancedJsonObject.convertFromPoint(factorLocation);
		String newFactorLocation = EnhancedJsonObject.convertFromPoint(moveTo);
		CommandSetObjectData moveDiagramFactor2 = new CommandSetObjectData(ObjectType.DIAGRAM_FACTOR, factor.getDiagramFactorId(), DiagramFactor.TAG_LOCATION, newFactorLocation, previousFactorLocation);
		project.executeCommand(moveDiagramFactor2);
		
		DiagramFactor diagramFactor3 = (DiagramFactor) project.findObject(ObjectType.DIAGRAM_FACTOR, factorId);
		assertEquals("didn't set location?", moveTo, diagramFactor3.getLocation());
		//undo move
		project.undo();
		DiagramFactor diagramFactor4 = (DiagramFactor) project.findObject(ObjectType.DIAGRAM_FACTOR, factorId);
		assertEquals("didn't restore original location?", zeroZero, diagramFactor4.getLocation());


		DiagramFactorId interventionId = insertIntervention();
		DiagramFactor intervention = (DiagramFactor) project.findObject(ObjectType.DIAGRAM_FACTOR, interventionId);
		Point interventionLocation = intervention.getLocation();
		String previousInterventionLocation = EnhancedJsonObject.convertFromPoint(interventionLocation);
		String newInterventionLocation = EnhancedJsonObject.convertFromPoint(moveTo);
		CommandSetObjectData moveDiagramFactor3 = new CommandSetObjectData(ObjectType.DIAGRAM_FACTOR, intervention.getDiagramFactorId(), DiagramFactor.TAG_LOCATION, newInterventionLocation, previousInterventionLocation);
		project.executeCommand(moveDiagramFactor3);
		
		DiagramFactor diagramFactor5 = (DiagramFactor) project.findObject(ObjectType.DIAGRAM_FACTOR, interventionId);
		assertEquals("didn't set location?", moveTo, diagramFactor5.getLocation());
		//undo move
		project.undo();
		DiagramFactor diagramFactor6 = (DiagramFactor) project.findObject(ObjectType.DIAGRAM_FACTOR, interventionId);
		assertEquals("didn't restore original location?", zeroZero, diagramFactor6.getLocation());
	}
	
	public void testCommandSetThreatRating() throws Exception
	{
		FactorId threatId = new FactorId(100);
		FactorId targetId = new FactorId(101);
		BaseId criterionId = new BaseId(102);
		BaseId valueId = new BaseId(103);
		ThreatRatingFramework framework = project.getThreatRatingFramework();
		BaseId defaultValueId = framework.getDefaultValueId();
		
		CommandSetThreatRating cmd = new CommandSetThreatRating(threatId, targetId, criterionId, valueId);
		project.executeCommand(cmd);
		assertEquals("Didn't memorize old value?", defaultValueId, cmd.getPreviousValueId());
		assertEquals("Didn't set new value?", valueId, framework.getBundle(threatId, targetId).getValueId(criterionId));
		
		project.undo();
		assertEquals("Didn't undo?", defaultValueId, framework.getBundle(threatId, targetId).getValueId(criterionId));
	}
	
	public void testCommandNodeResized() throws Exception
	{
		DiagramFactorId id = insertTarget();
		String defaultSize = EnhancedJsonObject.convertFromDimension(new Dimension(120, 60));
		DiagramModel diagramModel = project.getDiagramModel();
		FactorCell node = diagramModel.getFactorCellById(id);
		String originalSize = EnhancedJsonObject.convertFromDimension(node.getSize());
		assertEquals(defaultSize, originalSize);
		
		String newSize = EnhancedJsonObject.convertFromDimension(new Dimension(88, 22));
		
		DiagramFactorId diagramFactorId = node.getDiagramFactorId();
		CommandSetObjectData cmd = new CommandSetObjectData(ObjectType.DIAGRAM_FACTOR, diagramFactorId, DiagramFactor.TAG_SIZE, newSize, originalSize);
		project.executeCommand(cmd);

		DiagramFactor diagramFactor = (DiagramFactor) project.findObject(ObjectType.DIAGRAM_FACTOR, diagramFactorId);
		assertEquals("didn't memorize old size?", originalSize, cmd.getPreviousDataValue());
		assertEquals("didn't change to new size?", newSize, EnhancedJsonObject.convertFromDimension(diagramFactor.getSize()));

		project.undo();
		Dimension size = diagramModel.getFactorCellById(id).getSize();
		String sizeAsString = EnhancedJsonObject.convertFromDimension(size);
		assertEquals("didn't undo?", originalSize, sizeAsString);
	}
	

	public void testCommandAddTarget() throws Exception
	{
		verifyDiagramAddNode(ObjectType.TARGET);
	}

	public void testCommandInsertFactor() throws Exception
	{
		verifyDiagramAddNode(ObjectType.CAUSE);
	}

	public void testCommandInsertIntervention() throws Exception
	{
		verifyDiagramAddNode(ObjectType.STRATEGY);
	}

	private void verifyDiagramAddNode(int type) throws Exception, CommandFailedException
	{
		FactorId factorId = project.createFactor(type);
		CreateDiagramFactorParameter extraInfo = new CreateDiagramFactorParameter(factorId);
		CommandCreateObject createDiagramFactorCommand = new CommandCreateObject(ObjectType.DIAGRAM_FACTOR, extraInfo);
		project.executeCommand(createDiagramFactorCommand);
		
		DiagramFactorId diagramFactorId = (DiagramFactorId) createDiagramFactorCommand.getCreatedId();
		CommandDiagramAddFactor add = new CommandDiagramAddFactor(diagramFactorId);
		project.executeCommand(add);

		DiagramFactorId insertedId = add.getInsertedId();
		FactorCell node = project.getDiagramModel().getFactorCellById(insertedId);
		assertEquals("type not right?", type, node.getWrappedType());
		assertNotEquals("already have an id?", BaseId.INVALID, node.getDiagramFactorId());

		verifyUndoDiagramAddNode(add);
	}

	private void verifyUndoDiagramAddNode(CommandDiagramAddFactor cmd) throws CommandFailedException
	{
		DiagramFactorId insertedId = cmd.getInsertedId();
		project.undo();
		try
		{
			EAM.setLogToString();
			project.getDiagramModel().getFactorCellById(insertedId);
			fail("Should have thrown because node didn't exist");
		}
		catch(Exception ignoreExpected)
		{
		}
	}

	public void testCommandDiagramAddLinkage() throws Exception
	{
		DiagramModel model = project.getDiagramModel();

		DiagramFactorId from = insertNode(ObjectType.CAUSE).getDiagramFactorId();
		DiagramFactorId to = insertTarget();
		FactorId fromId = model.getFactorCellById(from).getWrappedId();
		FactorId toId = model.getFactorCellById(to).getWrappedId();
		
		CreateFactorLinkParameter extraInfo = new CreateFactorLinkParameter(fromId, toId);
		CommandCreateObject createModelLinkage = new CommandCreateObject(ObjectType.FACTOR_LINK, extraInfo);
		project.executeCommand(createModelLinkage);
		
		FactorLinkId modelLinkageId = (FactorLinkId)createModelLinkage.getCreatedId();
		DiagramFactorId fromDiagramFactorId = project.getDiagramModel().getFactorCellByWrappedId(fromId).getDiagramFactorId();
		DiagramFactorId toDiagramFactorId = project.getDiagramModel().getFactorCellByWrappedId(toId).getDiagramFactorId();
		CreateDiagramFactorLinkParameter diagramLinkExtraInfo = new CreateDiagramFactorLinkParameter(modelLinkageId, fromDiagramFactorId, toDiagramFactorId);
		
		CommandCreateObject createDiagramLinkCommand =  new CommandCreateObject(ObjectType.DIAGRAM_LINK, diagramLinkExtraInfo);
    	project.executeCommand(createDiagramLinkCommand);
    	
    	BaseId createdId = createDiagramLinkCommand.getCreatedId();
		DiagramFactorLinkId diagramFactorLinkId = new DiagramFactorLinkId(createdId.asInt());
    	CommandDiagramAddFactorLink addLinkageCommand = new CommandDiagramAddFactorLink(diagramFactorLinkId);
		project.executeCommand(addLinkageCommand);
		
		DiagramFactorLink inserted = model.getDiagramFactorLinkbyWrappedId(modelLinkageId);
		LinkCell cell = model.findLinkCell(inserted);
		DiagramFactorId fromNodeId = cell.getFrom().getDiagramFactorId();
		assertEquals("wrong source?", from, fromNodeId);
		DiagramFactorId toNodeId = cell.getTo().getDiagramFactorId();
		assertEquals("wrong dest?", to, toNodeId);

		assertTrue("linkage not created?", project.getDiagramModel().areLinked(fromNodeId, toNodeId));
		project.undo();
		
		project.undo();
		assertFalse("didn't remove linkage?", project.getDiagramModel().areLinked(fromNodeId, toNodeId));
		
		project.undo();
		assertNull("didn't delete linkage from pool?", project.getFactorLinkPool().find(modelLinkageId));
	}
	
	public void testDeleteLinkage() throws Exception
	{
		DiagramModel model = project.getDiagramModel();

		DiagramFactorId fromId = insertIntervention();
		DiagramFactor from = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, fromId));
		DiagramFactor to = insertContributingFactor();
		DiagramFactorId toId = to.getDiagramFactorId();

		CommandDiagramAddFactorLink addLinkageCommand = InsertFactorLinkDoer.createModelLinkageAndAddToDiagramUsingCommands(project, from.getWrappedId(), to.getWrappedId());
		DiagramFactorLinkId linkageId = addLinkageCommand.getDiagramFactorLinkId();
	
		CommandDiagramRemoveFactorLink cmd = new CommandDiagramRemoveFactorLink(linkageId);
		assertEquals("model id not invalid?", BaseId.INVALID, cmd.getFactorLinkId());
		project.executeCommand(cmd);
		assertEquals("model id not set?", addLinkageCommand.getFactorLinkId(), cmd.getFactorLinkId());

		assertFalse("linkage not deleted?", model.areLinked(fromId, toId));
		project.undo();
		assertTrue("didn't restore link?", model.areLinked(fromId, toId));
	}

	public void testDeleteNode() throws Exception
	{
		DiagramFactorId id = insertTarget();
		FactorId modelNodeId = project.getDiagramModel().getFactorCellById(id).getWrappedId();
		
		CommandDiagramRemoveFactor cmd = new CommandDiagramRemoveFactor(id);
		assertEquals("modelNodeId not invalid?", BaseId.INVALID, cmd.getFactorId());
		project.executeCommand(cmd);
		
		assertEquals("modelNodeId not set by execute?", modelNodeId, cmd.getFactorId());
		
		project.undo();
		assertEquals("didn't undo delete?", Factor.TYPE_TARGET, project.getDiagramModel().getFactorCellById(id).getUnderlyingFactorType());
	}
	
	public void testBeginTransaction() throws Exception
	{
		CommandBeginTransaction cmd = new CommandBeginTransaction();
		assertTrue(cmd.isBeginTransaction());
		assertFalse(cmd.isEndTransaction());
		project.executeCommand(cmd);

		EAM.setLogToConsole();
	}

	public void testEndTransaction() throws Exception
	{
		CommandEndTransaction cmd = new CommandEndTransaction();
		assertTrue(cmd.isEndTransaction());
		assertFalse(cmd.isBeginTransaction());
		project.executeCommand(cmd);

		EAM.setLogToConsole();
	}

	public void testUndoWhenNothingToUndo() throws Exception
	{
		Project emptyProject = new ProjectForTesting(getName());
		try
		{
			EAM.setLogToString();
			emptyProject.undo();
			fail("Should have thrown");
		}
		catch(NothingToUndoException ignoreExpected)
		{
		}
		EAM.setLogToConsole();
		emptyProject.close();
	}

	public void testRedo() throws Exception
	{
		DiagramFactorId insertedId = insertTarget();
		project.undo();
		project.redo();
		
		FactorCell inserted = project.getDiagramModel().getFactorCellById(insertedId);
		assertTrue("wrong node?", inserted.isTarget());
		
	}
	
	public void testRedoWhenNothingToRedo() throws Exception
	{
		Project emptyProject = new ProjectForTesting(getName());
		try
		{
			EAM.setLogToString();
			emptyProject.redo();
			fail("Should have thrown");
		}
		catch(NothingToRedoException ignoreExpected)
		{
		}
		EAM.setLogToConsole();
		emptyProject.close();
	}
	
	public void testCommandSwitchView() throws Exception
	{
		String originalViewName = project.getCurrentView();

		CommandSwitchView toMap = new CommandSwitchView(MapView.getViewName());
		project.executeCommand(toMap);
		assertEquals("didn't switch?", toMap.getDestinationView(), project.getCurrentView());
		assertEquals("didn't set from?", originalViewName, toMap.getPreviousView());
		
		project.undo();
		assertEquals("didn't switch back?", originalViewName, project.getCurrentView());
	}
	
	static class UndoListener implements CommandExecutedListener
	{
		public UndoListener()
		{
			undoneCommands = new Vector();
		}
		
		public void commandExecuted(CommandExecutedEvent event)
		{
			undoneCommands.add(event.getCommand());
			
		}

		Vector undoneCommands;
	}
	
	public void testUndoFiresCommandUndone() throws Exception
	{
		UndoListener undoListener = new UndoListener();
		project.addCommandExecutedListener(undoListener);
		CommandCreateObject cmd = new CommandCreateObject(ObjectType.TASK, getTaskExtraInfo());
		project.executeCommand(cmd);
		project.undo();
		assertEquals("didn't undo one command?", 2, undoListener.undoneCommands.size());
		assertEquals("didn't fire proper undo?", cmd.toString(), undoListener.undoneCommands.get(0).toString());
		project.removeCommandExecutedListener(undoListener);
	}
	
	private DiagramFactorId insertTarget() throws Exception
	{
		return insertNode(ObjectType.TARGET).getDiagramFactorId();
	}
	
	private DiagramFactor insertContributingFactor() throws Exception
	{
		return insertNode(ObjectType.CAUSE);
	}

	private DiagramFactorId insertIntervention() throws Exception
	{
		return insertNode(ObjectType.STRATEGY).getDiagramFactorId();
	}

	private DiagramFactor insertNode(int type) throws Exception
	{
		FactorId factorId = project.createFactor(type);
		CreateDiagramFactorParameter extraInfo = new CreateDiagramFactorParameter(factorId);
		CommandCreateObject createDiagramFactorCommand = new CommandCreateObject(ObjectType.DIAGRAM_FACTOR, extraInfo);
		project.executeCommand(createDiagramFactorCommand);
		
		DiagramFactorId diagramFactorId = (DiagramFactorId) createDiagramFactorCommand.getCreatedId();
		CommandDiagramAddFactor add = new CommandDiagramAddFactor(diagramFactorId);
		project.executeCommand(add);
		
		DiagramFactorId insertedId = add.getInsertedId();
		return (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, insertedId));
	}
	
	private CreateTaskParameter getTaskExtraInfo()
	{
		ORef parentRef = new ORef(ObjectType.FACTOR, new BaseId(45));
		CreateTaskParameter extraInfo = new CreateTaskParameter(parentRef);
		return extraInfo;
	}
	
	ProjectForTesting project;
}
