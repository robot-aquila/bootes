package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.tsgr001a.robot.report.BlockReportHandler;
import ru.prolib.bootes.tsgr001a.robot.report.S3ReportHandler;
import ru.prolib.bootes.tsgr001a.robot.report.SummaryReportDumpAtShutdown;
import ru.prolib.bootes.tsgr001a.robot.report.SummaryReportHandler;
import ru.prolib.bootes.tsgr001a.robot.sh.CommonHandler;
import ru.prolib.bootes.tsgr001a.robot.ui.RobotUIService;

public class TSGR001ARobotComp implements AppComponent {
	private final AppConfig appConfig;
	private final AppServiceLocator serviceLocator;
	private final RoboServiceLocator roboServices;
	private Robot robot;
	
	public TSGR001ARobotComp(AppConfig appConfig, AppServiceLocator serviceLocator) {
		this.appConfig = appConfig;
		this.serviceLocator = serviceLocator;
		this.roboServices = new RoboServiceLocator();
	}

	@Override
	public void init() throws Throwable {
		RobotStateListenerComp stateListener = new RobotStateListenerComp();
		robot = new TSGR001ARobotBuilder(serviceLocator, roboServices).build(stateListener);
		RobotState state = robot.getState();
		stateListener.addListener(new SummaryReportHandler(state, roboServices.getSummaryReportTracker()));
		stateListener.addListener(new SummaryReportDumpAtShutdown(roboServices.getSummaryReportTracker()));
		stateListener.addListener(new S3ReportHandler(state, roboServices.getS3Report()));
		stateListener.addListener(new S3ReportHandler(state, roboServices.getS3ReportShortDuration()));
		if ( ! appConfig.getBasicConfig().isHeadless() ) {
			stateListener.addListener(new BlockReportHandler(state, roboServices.getBlockReportStorage()));
			stateListener.addListener(new RobotUIService(serviceLocator, roboServices, state));
		}
		robot.getAutomat().start();
	}

	@Override
	public void startup() throws Throwable {
		// Do not start automat here.
		// All triggers should be registered prior to terminal start.
	}

	@Override
	public void shutdown() throws Throwable {
		SMStateMachine automat = robot.getAutomat();
		if ( ! automat.finished() ) {
			CommonHandler sh = (CommonHandler) automat.getCurrentState();
			automat.input(sh.getInputOfInterruption(), null);
		}
	}

}
