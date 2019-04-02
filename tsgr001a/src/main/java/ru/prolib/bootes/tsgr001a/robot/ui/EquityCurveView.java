package ru.prolib.bootes.tsgr001a.robot.ui;

import java.awt.GridLayout;

import javax.swing.JPanel;

import ru.prolib.bootes.lib.ui.EquityCurveChartPanel;
import ru.prolib.bootes.tsgr001a.robot.RoboServiceLocator;

public class EquityCurveView extends JPanel {
	private static final long serialVersionUID = 1L;
	private EquityCurveChartPanel equityPanel;
	
	public EquityCurveView(RoboServiceLocator roboServices) {
		super(new GridLayout(1, 1));
		equityPanel = new EquityCurveChartPanel();
		add(equityPanel.create());
		equityPanel.update(roboServices.getEquityCurveReportL());
	}

}
