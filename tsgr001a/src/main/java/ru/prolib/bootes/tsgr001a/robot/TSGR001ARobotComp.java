package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.tsgr001a.TSGR001AInstConfig;
import ru.prolib.bootes.tsgr001a.robot.ui.RobotUIService;

public class TSGR001ARobotComp implements AppComponent {
	private final AppConfig appConfig;
	private final AppServiceLocator serviceLocator;
	private final TSGR001AInstConfig config;
	private TSGR001AReports reports;
	private Robot robot;
	
	public TSGR001ARobotComp(AppConfig appConfig,
							 AppServiceLocator serviceLocator,
							 TSGR001AInstConfig config)
	{
		this.appConfig = appConfig;
		this.serviceLocator = serviceLocator;
		this.config = config;
	}

	@Override
	public void init() throws Throwable {
		reports = new TSGR001AReports(serviceLocator);
		robot = new TSGR001ARobotBuilder(serviceLocator, reports, config)
				.build(appConfig.getBasicConfig().isNoOrders());
		RobotState state = robot.getState();
		reports.registerHandlers(state);
		if ( ! appConfig.getBasicConfig().isHeadless() ) {
			state.getStateListener().addListener(new RobotUIService(
					serviceLocator,
					reports,
					state,
					config.getTitle()
				));
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

		// TODO: Move to better place
		// TODO: Safe title in filename
		reports.save(
				appConfig.getBasicConfig().getReportsDirectory(),
				config.getTitle() + ".report"
			);
	}

}
