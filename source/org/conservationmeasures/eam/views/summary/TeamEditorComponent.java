/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.summary;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.conservationmeasures.eam.actions.ActionModifyResource;
import org.conservationmeasures.eam.actions.ActionTeamCreateMember;
import org.conservationmeasures.eam.actions.ActionTeamRemoveMember;
import org.conservationmeasures.eam.actions.ActionViewPossibleTeamMembers;
import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.dialogs.ObjectPoolTable;
import org.conservationmeasures.eam.dialogs.ObjectTableModel;
import org.conservationmeasures.eam.dialogs.ObjectTablePanel;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.ProjectResource;
import org.conservationmeasures.eam.project.Project;
import org.martus.swing.UiButton;

public class TeamEditorComponent extends ObjectTablePanel implements KeyListener
{
	public TeamEditorComponent(Project projectToUse, Actions actionsToUse)
	{
		super(projectToUse, ObjectType.PROJECT_RESOURCE, new ObjectPoolTable(new TeamModel(projectToUse)));
		addDoubleClickAction(actionsToUse.get(ActionModifyResource.class));
		createButtonBar(actionsToUse);
		getTable().addKeyListener(this);
		actions = actionsToUse;
	}
	
	public void dispose()
	{
		super.dispose();
	}
	
	void createButtonBar(Actions actionsToUse)
	{
		addButton(new UiButton(actionsToUse.get(ActionTeamCreateMember.class)));
		addButton(actionsToUse.getObjectsAction(ActionTeamRemoveMember.class));
		addButton(actionsToUse.getObjectsAction(ActionModifyResource.class));
		addButton(new UiButton(actionsToUse.get(ActionViewPossibleTeamMembers.class)));
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);
		notifyTableRoleCodeChange(event);
	}

	public void commandUndone(CommandExecutedEvent event)
	{
		super.commandUndone(event);
		notifyTableRoleCodeChange(event);
	}
	
	private void notifyTableRoleCodeChange(CommandExecutedEvent event)
	{
		if (!event.getCommandName().equals(CommandSetObjectData.COMMAND_NAME))
			return;
		
		if (((CommandSetObjectData) event.getCommand()).getObjectType() != ObjectType.PROJECT_RESOURCE)
			return;
		
		if (((CommandSetObjectData) event.getCommand()).getFieldTag().equals(ProjectResource.TAG_ROLE_CODES))
			((ObjectTableModel) getTable().getModel()).rowsWereAddedOrRemoved();
	}

	//TODO: Enter key should not cause a traversal event when used in this way....
	public void keyPressed(KeyEvent keyEvent)
	{
		if (keyEvent.getKeyCode() ==KeyEvent.VK_ENTER)
		{
			if (getTable().getSelectedRow()<0) 
				return;
			try
			{
				actions.get(ActionModifyResource.class).doAction();
			}
			catch (Exception e)
			{
				EAM.logException(e);
			}
		}
	}

	public void keyReleased(KeyEvent arg0)
	{
	}

	public void keyTyped(KeyEvent arg0)
	{
	}
	
	Actions actions;
}