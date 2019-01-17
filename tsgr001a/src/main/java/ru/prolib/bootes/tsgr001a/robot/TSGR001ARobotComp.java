package ru.prolib.bootes.tsgr001a.robot;

import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.tsgr001a.robot.sh.CommonHandler;
import ru.prolib.bootes.tsgr001a.robot.ui.RobotUIService;

public class TSGR001ARobotComp implements AppComponent {
	private final AppConfig appConfig;
	private final AppServiceLocator serviceLocator;
	private Robot robot;
	private RobotUIService uis;
	
	public TSGR001ARobotComp(AppConfig appConfig, AppServiceLocator serviceLocator) {
		this.appConfig = appConfig;
		this.serviceLocator = serviceLocator;
	}

	@Override
	public void init() throws Throwable {
		RobotStateListenerComp stateListener = new RobotStateListenerComp();
		boolean headless = appConfig.getBasicConfig().isHeadless();
		if ( ! headless ) {
			uis = new RobotUIService(serviceLocator);
			stateListener.addListener(uis);
		}
		robot = new TSGR001ARobotBuilder(serviceLocator).build(stateListener);
		stateListener.addListener(new RobotStateListenerStats(robot.getState()));
		robot.getAutomat().start();
		if ( ! headless ) {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					uis.initialize(robot.getState());
				}
			});
		}
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
