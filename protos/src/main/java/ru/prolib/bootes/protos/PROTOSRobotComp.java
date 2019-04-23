package ru.prolib.bootes.protos;

import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.robo.Robot;
import ru.prolib.bootes.lib.robo.s3.S3CommonReports;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListenerComp;
import ru.prolib.bootes.protos.ui.PROTOSRobotUI;

public class PROTOSRobotComp implements AppComponent {
	protected final AppConfig appConfig;
	protected final AppServiceLocator serviceLocator;
	private Robot<PROTOSRobotState> robot;
	private S3CommonReports reports;

	public PROTOSRobotComp(AppConfig appConfig, AppServiceLocator serviceLocator) {
		this.appConfig = appConfig;
		this.serviceLocator = serviceLocator;
	}
	
	@Override
	public void init() throws Throwable {
		reports = new S3CommonReports(serviceLocator);
		robot = new PROTOSRobotBuilder(serviceLocator).build();
		robot.getAutomat().setId("PROTOS");
		robot.getAutomat().setDebug(true);
		PROTOSRobotState state = robot.getState();
		S3RobotStateListenerComp stateListener = state.getStateListener();
		reports.registerHandlers(state);
		if ( ! appConfig.getBasicConfig().isHeadless() ) {
			stateListener.addListener(new PROTOSRobotUI(serviceLocator, state, reports));
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
		synchronized ( automat ) {
			if ( ! automat.finished() ) {
				SMStateHandlerEx sh = (SMStateHandlerEx) automat.getCurrentState();
				automat.input(sh.getInterrupt(), null);
			}
		}
		// TODO: Here! Wait for automat finished work.
	}

}
