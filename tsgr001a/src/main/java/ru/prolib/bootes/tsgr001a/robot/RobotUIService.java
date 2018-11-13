package ru.prolib.bootes.tsgr001a.robot;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.tseries.SuperTSeries;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.ui.ChartT0;


public class RobotUIService implements RobotStateListener {
	private final AppServiceLocator serviceLocator;
	private RobotState state;
	private JPanel rootPanel;
	private ChartT0 t0;
	
	public RobotUIService(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}
	
	public void initialize(RobotState state) {
		this.state = state;
		rootPanel = new JPanel(new GridLayout(1, 1));
		t0 = new ChartT0();
		rootPanel.add(t0.create());
		
		serviceLocator.getUIService().getTabPanel().addTab("Test", rootPanel);
	}

	@Override
	public void robotStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contractSelected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionDataAvailable() {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					sessionDataAvailable();
				}
			});
		} else {
			Security security = state.getSecurity();
			SuperTSeries ss = state.getSeriesHandlerT0().getSuperSeries();
			if ( ss != null ) {
				t0.update(ss, security);
			} else {
				t0.clear();
			}
		}
	}

	@Override
	public void sessionDataCleanup() {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					sessionDataCleanup();
				}
			});
		} else {
			t0.clear();
		}
		
	}

	@Override
	public void robotStopped() {
		// TODO Auto-generated method stub
		
	}

}
