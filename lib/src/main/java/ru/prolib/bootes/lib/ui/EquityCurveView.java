package ru.prolib.bootes.lib.ui;

import java.awt.GridLayout;

import javax.swing.JPanel;

import ru.prolib.aquila.core.data.OHLCScalableSeries;

public class EquityCurveView extends JPanel {
	private static final long serialVersionUID = 1L;
	private EquityCurveChartPanel equityPanel;
	
	public EquityCurveView(OHLCScalableSeries report) {
		super(new GridLayout(1, 1));
		equityPanel = new EquityCurveChartPanel();
		add(equityPanel.create());
		equityPanel.update(report);
	}

}
