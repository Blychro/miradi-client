/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.umbrella;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.conservationmeasures.eam.actions.ActionAbout;
import org.conservationmeasures.eam.actions.ActionAboutBenetech;
import org.conservationmeasures.eam.actions.ActionAboutCMP;
import org.conservationmeasures.eam.actions.ActionClose;
import org.conservationmeasures.eam.actions.ActionConfigureExport;
import org.conservationmeasures.eam.actions.ActionCopyProjectTo;
import org.conservationmeasures.eam.actions.ActionDatabasesDemo;
import org.conservationmeasures.eam.actions.ActionExit;
import org.conservationmeasures.eam.actions.ActionExportProjectReportFile;
import org.conservationmeasures.eam.actions.ActionExportZippedProjectFile;
import org.conservationmeasures.eam.actions.ActionHelpAdaptiveManagement;
import org.conservationmeasures.eam.actions.ActionHelpAgileSoftware;
import org.conservationmeasures.eam.actions.ActionHelpButtonExamples;
import org.conservationmeasures.eam.actions.ActionHelpButtonMoreInfo;
import org.conservationmeasures.eam.actions.ActionHelpButtonSupport;
import org.conservationmeasures.eam.actions.ActionHelpButtonWorkshop;
import org.conservationmeasures.eam.actions.ActionHelpCMPStandards;
import org.conservationmeasures.eam.actions.ActionHelpComingAttractions;
import org.conservationmeasures.eam.actions.ActionHelpCredits;
import org.conservationmeasures.eam.actions.ActionImportTncCapWorkbook;
import org.conservationmeasures.eam.actions.ActionImportZippedProjectFile;
import org.conservationmeasures.eam.actions.ActionNewProject;
import org.conservationmeasures.eam.actions.ActionPreferences;
import org.conservationmeasures.eam.actions.ActionRedo;
import org.conservationmeasures.eam.actions.ActionReportsDemo;
import org.conservationmeasures.eam.actions.ActionUndo;
import org.conservationmeasures.eam.actions.ActionWizardNext;
import org.conservationmeasures.eam.actions.ActionWizardPrevious;
import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.actions.jump.ActionJumpActivitiesAndActionPlan;
import org.conservationmeasures.eam.actions.jump.ActionJumpAdaptAndMonitorPlans;
import org.conservationmeasures.eam.actions.jump.ActionJumpAnalyzeData;
import org.conservationmeasures.eam.actions.jump.ActionJumpAnalyzeProjectCapacity;
import org.conservationmeasures.eam.actions.jump.ActionJumpAnalyzeResourcesFeasibilityAndRisk;
import org.conservationmeasures.eam.actions.jump.ActionJumpAnalyzeStrategies;
import org.conservationmeasures.eam.actions.jump.ActionJumpArticulateCoreAssumptions;
import org.conservationmeasures.eam.actions.jump.ActionJumpAssessStakeholders;
import org.conservationmeasures.eam.actions.jump.ActionJumpBudgetFutureDemo;
import org.conservationmeasures.eam.actions.jump.ActionJumpCloseTheLoop;
import org.conservationmeasures.eam.actions.jump.ActionJumpCommunicateResults;
import org.conservationmeasures.eam.actions.jump.ActionJumpCreate;
import org.conservationmeasures.eam.actions.jump.ActionJumpDefineAudiences;
import org.conservationmeasures.eam.actions.jump.ActionJumpDefineTasks;
import org.conservationmeasures.eam.actions.jump.ActionJumpDevelopCharter;
import org.conservationmeasures.eam.actions.jump.ActionJumpDevelopDraftStrategiesStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpDiagramOverviewStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpDiagramWizardDefineTargetsStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpDiagramWizardIdentifyDirectThreatStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpDiagramWizardIdentifyIndirectThreatStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpDiagramWizardLinkDirectThreatsToTargetsStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpDiagramWizardProjectScopeStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpDiagramWizardReviewModelAndAdjustStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpDiagramWizardVisionStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpDocument;
import org.conservationmeasures.eam.actions.jump.ActionJumpEditAllStrategiesStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpFinancialOverviewStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpGroundTruthRevise;
import org.conservationmeasures.eam.actions.jump.ActionJumpImplementPlans;
import org.conservationmeasures.eam.actions.jump.ActionJumpMonitoringPlanOverviewStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpMonitoringWizardDefineIndicatorsStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpMonitoringWizardEditIndicatorsStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpMonitoringWizardFocusStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpMonitoringWizardSelectMethodsStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpPlanDataStorage;
import org.conservationmeasures.eam.actions.jump.ActionJumpRankDraftStrategiesStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpRefinePlans;
import org.conservationmeasures.eam.actions.jump.ActionJumpDiagramWizardResultsChainStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpScheduleOverviewStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpSelectAppropriateMethods;
import org.conservationmeasures.eam.actions.jump.ActionJumpSelectChainStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpShare;
import org.conservationmeasures.eam.actions.jump.ActionJumpShorttermPlans;
import org.conservationmeasures.eam.actions.jump.ActionJumpStrategicPlanDevelopGoalStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpStrategicPlanDevelopObjectivesStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpStrategicPlanHowToConstructStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpStrategicPlanViewAllGoals;
import org.conservationmeasures.eam.actions.jump.ActionJumpStrategicPlanViewAllObjectives;
import org.conservationmeasures.eam.actions.jump.ActionJumpSummaryWizardDefineProjectLeader;
import org.conservationmeasures.eam.actions.jump.ActionJumpSummaryWizardDefineTeamMembers;
import org.conservationmeasures.eam.actions.jump.ActionJumpTargetViability2Step;
import org.conservationmeasures.eam.actions.jump.ActionJumpTargetViability3Step;
import org.conservationmeasures.eam.actions.jump.ActionJumpTeamRoles;
import org.conservationmeasures.eam.actions.jump.ActionJumpThreatMatrixOverviewStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpThreatRatingWizardCheckTotalsStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpWorkPlanAssignResourcesStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpWorkPlanDevelopActivitiesAndTasksStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpWorkPlanDevelopMethodsAndTasksStep;
import org.conservationmeasures.eam.actions.jump.ActionJumpWorkPlanOverviewStep;
import org.conservationmeasures.eam.actions.views.ActionViewBudget;
import org.conservationmeasures.eam.actions.views.ActionViewDiagram;
import org.conservationmeasures.eam.actions.views.ActionViewImages;
import org.conservationmeasures.eam.actions.views.ActionViewMap;
import org.conservationmeasures.eam.actions.views.ActionViewMonitoring;
import org.conservationmeasures.eam.actions.views.ActionViewSchedule;
import org.conservationmeasures.eam.actions.views.ActionViewStrategicPlan;
import org.conservationmeasures.eam.actions.views.ActionViewSummary;
import org.conservationmeasures.eam.actions.views.ActionViewTargetViability;
import org.conservationmeasures.eam.actions.views.ActionViewThreatMatrix;
import org.conservationmeasures.eam.actions.views.ActionViewWorkPlan;
import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandDeleteObject;
import org.conservationmeasures.eam.dialogs.ModelessDialogPanel;
import org.conservationmeasures.eam.dialogs.ModelessDialogWithClose;
import org.conservationmeasures.eam.dialogs.ResourcePropertiesPanel;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.CommandExecutedListener;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.ViewData;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.Doer;
import org.conservationmeasures.eam.views.NullDoer;
import org.conservationmeasures.eam.views.treeViews.TaskTreeTablePanel;
import org.conservationmeasures.eam.wizard.WizardPanel;
import org.martus.swing.UiLabel;
import org.martus.swing.Utilities;

abstract public class UmbrellaView extends JPanel implements CommandExecutedListener
{
	public UmbrellaView(MainWindow mainWindowToUse)
	{
		super(new BorderLayout());
		mainWindow = mainWindowToUse;
		nullDoer = new NullDoer();
		actionToDoerMap = new HashMap();
		addUmbrellaDoersToMap();
	}
	
	abstract public String cardName();
	
	public WizardPanel getWizardPanel()
	{
		return wizardPanel;
	}
	
	public void jump(Class stepMarker) throws Exception
	{
		mainWindow.getWizardManager().setStep(stepMarker);
	}

	public void becomeActive() throws Exception
	{
		existingCommandListenerCount = getProject().getCommandListenerCount();
		if(isActive)
			EAM.logWarning("UmbrellaView.becomeActive was already active: " + getClass().getName());
		getProject().addCommandExecutedListener(this);
		isActive = true;
	}
	
	public void becomeInactive() throws Exception
	{
		if(!isActive)
			EAM.logWarning("UmbrellaView.becomeActive was not active: " + getClass().getName());
		getProject().removeCommandExecutedListener(this);
		isActive = false;
		removeAll();
		if(getProject().getCommandListenerCount() != existingCommandListenerCount)
		{
			EAM.logError("CommandListener orphaned by " + getClass());
			getProject().logCommandListeners(System.err);
		}
	}
	
	public MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	public Project getProject()
	{
		return getMainWindow().getProject();
	}
	
	public Actions getActions()
	{
		return getMainWindow().getActions();
	}
	
	abstract public JComponent createToolBar();
	
	public BufferedImage getImage()
	{
		throw new RuntimeException("This view doesn't support getImage");
	}
	
	public JComponent getPrintableComponent()
	{
		throw new RuntimeException("This view doesn't support getPrintableComponent");
	}
	
	
	public void showResourcePropertiesDialog(BaseObject object) throws Exception
	{
		ModelessDialogPanel propertiesPanel = createPanelForDialog(object);
		if(propertiesPanel == null)
			return;
		
		ModelessDialogWithClose dlg = new ModelessDialogWithClose(mainWindow, propertiesPanel, propertiesPanel.getPanelDescription());
		dlg.addWindowListener(new ObjectPropertiesDialogWindowEventHandler());
		showFloatingPropertiesDialog(dlg);
	}
	
	public BaseObject getSelectedObject()
	{
		return null;
	}
	
	public TaskTreeTablePanel getTaskTreeTablePanel()
	{
		return null;
	}
	
	class ObjectPropertiesDialogWindowEventHandler extends WindowAdapter
	{

		public void windowClosed(WindowEvent e)
		{
			super.windowClosed(e);
			closeActivePropertiesDialog();
		}

	}
	
	private ModelessDialogPanel createPanelForDialog(BaseObject object) throws Exception
	{	
		return new ResourcePropertiesPanel(getProject(), object.getId());
	}

	public void showFloatingPropertiesDialog(ModelessDialogWithClose newDialog)
	{
		closeActivePropertiesDialog();
		
		activePropertiesDlg = newDialog;
		activePropertiesPanel = (ModelessDialogPanel)newDialog.getWrappedPanel();
		activePropertiesDlg.pack();
		Utilities.centerDlg(activePropertiesDlg);
		activePropertiesDlg.setVisible(true);
	}
	
	protected UiLabel createScreenShotLabel()
	{
		UiLabel label = new UiLabel("Demo Screen Shot");
		label.setBorder(new LineBorder(Color.BLACK));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		return label;
	}

	////////////////////////////////////////////////////////////
	// these doers are available in this class
	
	private void addUmbrellaDoersToMap()
	{
		addDoerToMap(ActionAbout.class, new AboutDoer());
		addDoerToMap(ActionAboutBenetech.class, new HelpButtonDoer());
		addDoerToMap(ActionAboutCMP.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpComingAttractions.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpAdaptiveManagement.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpAgileSoftware.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpCMPStandards.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpCredits.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpButtonExamples.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpButtonMoreInfo.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpButtonWorkshop.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpButtonSupport.class, new HelpButtonDoer());
		
		addDoerToMap(ActionNewProject.class, new NewProject());
		addDoerToMap(ActionCopyProjectTo.class, new CopyProjectToDoer());
		addDoerToMap(ActionClose.class, new Close());
		addDoerToMap(ActionExit.class, new Exit());
		addDoerToMap(ActionUndo.class, new Undo());
		addDoerToMap(ActionRedo.class, new Redo());
		addDoerToMap(ActionPreferences.class, new Preferences());
		addDoerToMap(ActionExportZippedProjectFile.class, new ExportZippedProjectFileDoer());
		addDoerToMap(ActionImportZippedProjectFile.class, new ImportZippedProjectFileDoer());
		addDoerToMap(ActionImportTncCapWorkbook.class, new ImportTncCapWorkbookDoer());
		addDoerToMap(ActionExportProjectReportFile.class, new ExportProjectReportFileDoer());
		addDoerToMap(ActionConfigureExport.class, new HelpButtonDoer());
		addDoerToMap(ActionDatabasesDemo.class, new HelpButtonDoer());
		addDoerToMap(ActionReportsDemo.class, new HelpButtonDoer());
		
		
		addDoerToMap(ActionViewSummary.class, new ViewSummary());
		addDoerToMap(ActionViewDiagram.class, new ViewDiagram());
		addDoerToMap(ActionViewThreatMatrix.class, new ViewThreatMatrix());
		addDoerToMap(ActionViewBudget.class, new ViewBudget());
		addDoerToMap(ActionViewWorkPlan.class, new ViewWorkPlan());
		addDoerToMap(ActionViewMap.class, new ViewMap());
		addDoerToMap(ActionViewImages.class, new ViewImages());
		addDoerToMap(ActionViewSchedule.class, new ViewSchedule());
		addDoerToMap(ActionViewStrategicPlan.class, new ViewStrategicPlan());
		addDoerToMap(ActionViewMonitoring.class, new ViewMonitoring());
		addDoerToMap(ActionViewTargetViability.class, new ViewTargetViability());
		
		addDoerToMap(ActionWizardNext.class, new WizardNextDoer());
		addDoerToMap(ActionWizardPrevious.class, new WizardPreviousDoer());
		
		addJumpDoerToMap(ActionJumpSummaryWizardDefineTeamMembers.class);
		addJumpDoerToMap(ActionJumpSummaryWizardDefineProjectLeader.class);
		addJumpDoerToMap(ActionJumpDevelopCharter.class);
		
		addJumpDoerToMap(ActionJumpDiagramWizardProjectScopeStep.class);
		addJumpDoerToMap(ActionJumpDiagramWizardVisionStep.class);
		addJumpDoerToMap(ActionJumpDiagramWizardDefineTargetsStep.class);
		addJumpDoerToMap(ActionJumpTargetViability2Step.class);
		addJumpDoerToMap(ActionJumpTargetViability3Step.class);
		
		addJumpDoerToMap(ActionJumpDiagramWizardIdentifyDirectThreatStep.class);
		addJumpDoerToMap(ActionJumpDiagramWizardLinkDirectThreatsToTargetsStep.class);
		addJumpDoerToMap(ActionJumpThreatMatrixOverviewStep.class);
		addJumpDoerToMap(ActionJumpDiagramWizardIdentifyIndirectThreatStep.class);
		addJumpDoerToMap(ActionJumpAssessStakeholders.class);
		addJumpDoerToMap(ActionJumpAnalyzeProjectCapacity.class);
		
		addJumpDoerToMap(ActionJumpArticulateCoreAssumptions.class);
		addJumpDoerToMap(ActionJumpDiagramWizardReviewModelAndAdjustStep.class);
		addJumpDoerToMap(ActionJumpGroundTruthRevise.class);
		
		addJumpDoerToMap(ActionJumpStrategicPlanDevelopGoalStep.class);
		addJumpDoerToMap(ActionJumpSelectChainStep.class);
		addJumpDoerToMap(ActionJumpStrategicPlanDevelopObjectivesStep.class);
		
		addJumpDoerToMap(ActionJumpRankDraftStrategiesStep.class);
		addJumpDoerToMap(ActionJumpDiagramWizardResultsChainStep.class);
		addJumpDoerToMap(ActionJumpEditAllStrategiesStep.class);
		addJumpDoerToMap(ActionJumpActivitiesAndActionPlan.class);
		addJumpDoerToMap(ActionJumpAnalyzeResourcesFeasibilityAndRisk.class);
		
		addJumpDoerToMap(ActionJumpMonitoringWizardFocusStep.class);
		addJumpDoerToMap(ActionJumpDefineAudiences.class);
		
		addJumpDoerToMap(ActionJumpMonitoringWizardDefineIndicatorsStep.class);
		addJumpDoerToMap(ActionJumpMonitoringWizardEditIndicatorsStep.class);
		addJumpDoerToMap(ActionJumpSelectAppropriateMethods.class);
		addJumpDoerToMap(ActionJumpPlanDataStorage.class);
		
		addJumpDoerToMap(ActionJumpMonitoringPlanOverviewStep.class);
		addJumpDoerToMap(ActionJumpMonitoringWizardSelectMethodsStep.class);
		
		addJumpDoerToMap(ActionJumpShorttermPlans.class);
		addJumpDoerToMap(ActionJumpScheduleOverviewStep.class);
		addJumpDoerToMap(ActionJumpDefineTasks.class);
		addJumpDoerToMap(ActionJumpFinancialOverviewStep.class);
		addJumpDoerToMap(ActionJumpTeamRoles.class);
		addJumpDoerToMap(ActionJumpRefinePlans.class);
		addJumpDoerToMap(ActionJumpImplementPlans.class);
		
		addJumpDoerToMap(ActionJumpAnalyzeData.class);
		addJumpDoerToMap(ActionJumpAnalyzeStrategies.class);
		addJumpDoerToMap(ActionJumpCommunicateResults.class);
		
		addJumpDoerToMap(ActionJumpAdaptAndMonitorPlans.class);
		
		addJumpDoerToMap(ActionJumpDocument.class);
		addJumpDoerToMap(ActionJumpShare.class);
		addJumpDoerToMap(ActionJumpCreate.class);
		
		addJumpDoerToMap(ActionJumpCloseTheLoop.class);
		
		addJumpDoerToMap(ActionJumpStrategicPlanViewAllGoals.class);
		addJumpDoerToMap(ActionJumpStrategicPlanViewAllObjectives.class);
		addJumpDoerToMap(ActionJumpStrategicPlanHowToConstructStep.class);
		
		addJumpDoerToMap(ActionJumpDevelopDraftStrategiesStep.class);
		
		
		addJumpDoerToMap(ActionJumpWorkPlanAssignResourcesStep.class);
		addJumpDoerToMap(ActionJumpDiagramWizardReviewModelAndAdjustStep.class);
		addJumpDoerToMap(ActionJumpThreatRatingWizardCheckTotalsStep.class);

		addJumpDoerToMap(ActionJumpWorkPlanDevelopActivitiesAndTasksStep.class);
		addJumpDoerToMap(ActionJumpWorkPlanDevelopMethodsAndTasksStep.class);
		addJumpDoerToMap(ActionJumpWorkPlanAssignResourcesStep.class);
		addJumpDoerToMap(ActionJumpScheduleOverviewStep.class);
		addJumpDoerToMap(ActionJumpFinancialOverviewStep.class);
		addJumpDoerToMap(ActionJumpBudgetFutureDemo.class);
		addJumpDoerToMap(ActionJumpDiagramOverviewStep.class);
		addJumpDoerToMap(ActionJumpWorkPlanOverviewStep.class);
		
		
	}
	
	private void addJumpDoerToMap(Class actionClass)
	{
		addDoerToMap(actionClass, new JumpDoer(actionClass));
	}
	
	public void addDoerToMap(Class actionClass, Doer doer)
	{
		actionToDoerMap.put(actionClass, doer);
	}
	
	public Doer getDoer(Class actionClass)
	{
		Doer doer = (Doer)actionToDoerMap.get(actionClass);
		if(doer == null)
			doer = nullDoer;
		
		doer.setView(this);
		doer.setProject(getProject());
		return doer;
	}
	
	protected ViewData getViewData() throws Exception
	{
		ViewData ourViewData = getProject().getViewData(cardName());
		return ourViewData;
	}

	public void commandExecuted(CommandExecutedEvent event)
	{
		closeActivePropertiesDialogIfWeDeletedItsObject(event.getCommand());
	}

	void closeActivePropertiesDialogIfWeDeletedItsObject(Command rawCommand)
	{
		if(activePropertiesDlg == null)
			return;
		
		if(!rawCommand.getCommandName().equals(CommandDeleteObject.COMMAND_NAME))
			return;
		
		CommandDeleteObject cmd = (CommandDeleteObject)rawCommand;
		BaseObject objectBeingEdited = activePropertiesPanel.getObject();
		if(objectBeingEdited == null)
			return;
		if(cmd.getObjectType() != objectBeingEdited.getType())
			return;
		if(cmd.getObjectId() != objectBeingEdited.getId())
			return;
		
		closeActivePropertiesDialog();
	}
	
	public void closeActivePropertiesDialog()
	{
		if(activePropertiesPanel != null && activePropertiesDlg.isDisplayable())
			activePropertiesDlg.dispose();
		activePropertiesPanel = null;
		activePropertiesDlg = null;
	}

	private MainWindow mainWindow;
	private NullDoer nullDoer;
	private HashMap actionToDoerMap;
	private boolean isActive;
	
	private int existingCommandListenerCount;
	
	private ModelessDialogPanel activePropertiesPanel;
	private ModelessDialogWithClose activePropertiesDlg;
	protected WizardPanel wizardPanel;
 
}
