package ru.prolib.bootes.tsgr001a.robot;

import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.sh.CommonHandler;

public class TSGR001ARobotComp implements AppComponent {
	private final AppServiceLocator serviceLocator;
	private Robot robot;
	private RobotUIService uis;
	
	public TSGR001ARobotComp(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	@Override
	public void init() throws Throwable {
		uis = new RobotUIService(serviceLocator);
		// TODO: headless mode
		robot = new TSGR001ARobotBuilder(serviceLocator).build(uis);
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				uis.initialize(robot.getState());
			}
		});
		
	}

	@Override
	public void startup() throws Throwable {
		robot.getAutomat().start();
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
