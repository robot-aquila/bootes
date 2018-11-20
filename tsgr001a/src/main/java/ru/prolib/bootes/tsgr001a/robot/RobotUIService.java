package ru.prolib.bootes.tsgr001a.robot;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.tseries.STSeries;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.ui.SecurityChartPanel;
import ru.prolib.bootes.tsgr001a.robot.ui.ChartT0;
import ru.prolib.bootes.tsgr001a.robot.ui.ChartT1;
import ru.prolib.bootes.tsgr001a.robot.ui.ChartT2;


public class RobotUIService implements RobotStateListener {
	private final AppServiceLocator serviceLocator;
	private RobotState state;
	private JPanel rootPanel;
	private SecurityChartPanel t0,t1,t2;
	
	public RobotUIService(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}
	
	public void initialize(RobotState state) {
		this.state = state;
		rootPanel = new JPanel(new GridLayout(0, 2));
		t0 = new ChartT0();
		t1 = new ChartT1();
		t2 = new ChartT2();
		rootPanel.add(t0.create());
		rootPanel.add(t1.create());
		rootPanel.add(t2.create());
		
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
	
	private void updateChart(SecurityChartPanel panel, STSeries ss) {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateChart(panel, ss);
				}
			});
		} else {
			if ( ss != null ) {
				panel.update(ss, state.getSecurity());
			} else {
				panel.clear();
			}
			panel.paint();
		}
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
			updateChart(t0, state.getSeriesHandlerT0().getSeries());
			updateChart(t1, state.getSeriesHandlerT1().getSeries());
			updateChart(t2, state.getSeriesHandlerT2().getSeries());
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
			t2.clear();
			t1.clear();
			t0.clear();
			// Do not repaint to keep chart data on screen
		}
	}

	@Override
	public void robotStopped() {
		// TODO Auto-generated method stub
		
	}

}
