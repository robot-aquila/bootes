package ru.prolib.bootes.tsgr001a.robot.ui;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.data.tseries.STSeries;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.ui.SecurityChartPanel;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.RobotStateListener;

public class RobotUIService implements RobotStateListener {
	private final AppServiceLocator serviceLocator;
	private RobotState state;
	private JPanel rootPanel;
	private SecurityChartPanel t0,t1,t2;
	private StrategyConfigPanel cfg;
	
	public RobotUIService(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
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
	
	private void updateConfigPanel() {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateConfigPanel();
				}
			});
		} else {
			cfg.updateView();
		}
	}
	
	public void initialize(RobotState state) {
		this.state = state;
		rootPanel = new JPanel(new GridLayout(0, 2));
		t0 = new ChartT0();
		t1 = new ChartT1();
		t2 = new ChartT2();
		cfg = new StrategyConfigPanel(serviceLocator.getMessages(), state);
		rootPanel.add(t0.create());
		rootPanel.add(t1.create());
		rootPanel.add(t2.create());
		rootPanel.add(cfg);
		
		serviceLocator.getUIService().getTabPanel().addTab("Test", rootPanel);
	}

	@Override
	public void robotStarted() {
		
	}
	
	@Override
	public void accountSelected() {
		
	}

	@Override
	public void contractSelected() {
		updateConfigPanel();
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
			updateConfigPanel();
			updateChart(t0, state.getSeriesHandlerT0().getSeries());
			updateChart(t1, state.getSeriesHandlerT1().getSeries());
			updateChart(t2, state.getSeriesHandlerT2().getSeries());
		}
	}
	
	@Override
	public void limitsUpdated() {
		updateConfigPanel();
	}

	@Override
	public void sessionDataCleanup() {
		updateConfigPanel();
		// Do not clear to avoid blinking
		// Do not repaint to keep chart data on screen
	}

	@Override
	public void robotStopped() {

	}

}
