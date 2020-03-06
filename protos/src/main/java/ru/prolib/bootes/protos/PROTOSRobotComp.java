package ru.prolib.bootes.protos;

import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.aquila.ui.FastOrder.FastOrderPanel;
import ru.prolib.aquila.ui.form.SecurityListDialog;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig2;
import ru.prolib.bootes.lib.robo.Robot;
import ru.prolib.bootes.lib.robo.s3.S3CommonReports;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListenerComp;
import ru.prolib.bootes.lib.service.UIService;
import ru.prolib.bootes.protos.config.ProtosConfigSection;
import ru.prolib.bootes.protos.ui.PROTOSRobotUI;

public class PROTOSRobotComp implements AppComponent {
	public static final String CONFIG_SECTION_ID = "protos";
	protected final String id;
	protected final AppServiceLocator serviceLocator;
	private Robot<PROTOSRobotState> robot;
	private S3CommonReports reports;
	private FastOrderPanel orderPanel;

	public PROTOSRobotComp(String id, AppServiceLocator serviceLocator) {
		this.id = id;
		this.serviceLocator = serviceLocator;
	}
	
	public S3CommonReports getReports() {
		return reports;
	}
	
	@Override
	public void init() throws Throwable {
		reports = new S3CommonReports(serviceLocator);
		robot = new PROTOSRobotBuilder(serviceLocator).build(id);
		robot.getAutomat().setId(id);
		robot.getAutomat().setDebug(true);
		PROTOSRobotState state = robot.getState();
		S3RobotStateListenerComp stateListener = state.getStateListener();
		reports.registerHandlers(state);
		AppConfig2 app_conf = serviceLocator.getConfig();
		if ( ! app_conf.getBasicConfig().isHeadless() ) {
			stateListener.addListener(new PROTOSRobotUI(serviceLocator, state, reports));
			UIService uis = serviceLocator.getUIService();
			orderPanel = new FastOrderPanel(
					serviceLocator.getTerminal(),
					new SecurityListDialog(uis.getFrame(), SecurityListDialog.TYPE_SELECT, uis.getMessages())
				);
			uis.getTopPanel().add(orderPanel);
		}
		robot.getAutomat().start();
	}

	@Override
	public void startup() throws Throwable {
		// Do not start automat here.
		// All triggers should be registered prior to terminal start.
		if  ( orderPanel != null ) {
			orderPanel.start();
		}
	}

	@Override
	public void shutdown() throws Throwable {
		if ( orderPanel != null ) {
			orderPanel.stop();
			orderPanel = null;
		}
		SMStateMachine automat = robot.getAutomat();
		synchronized ( automat ) {
			if ( ! automat.finished() ) {
				SMStateHandlerEx sh = (SMStateHandlerEx) automat.getCurrentState();
				automat.input(sh.getInterrupt(), null);
			}
		}
		// TODO: Here! Wait for automat finished work.
		AppConfig2 app_conf = serviceLocator.getConfig();
		reports.save(app_conf.getBasicConfig().getReportDirectory(), id + ".report");
	}

	@Override
	public void registerConfig(AppConfigService2 config_service) {
		config_service.addSection(CONFIG_SECTION_ID, new ProtosConfigSection());
	}

}
