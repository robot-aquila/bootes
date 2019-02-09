package ru.prolib.bootes.tsgr001a.robot.report;

import ru.prolib.bootes.lib.report.summarep.ISummaryReportTracker;
import ru.prolib.bootes.lib.report.summarep.SREntry;
import ru.prolib.bootes.tsgr001a.mscan.sensors.SignalType;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.RobotStateListener;

public class SummaryReportHandler implements RobotStateListener {
	private final RobotState state;
	private final ISummaryReportTracker tracker;
	
	public SummaryReportHandler(RobotState state, ISummaryReportTracker tracker) {
		this.state = state;
		this.tracker = tracker;
	}
	
	private Speculation getSpeculation() {
		synchronized ( state ) {
			return state.getActiveSpeculation();
		}
	}

	@Override
	public void robotStarted() {
		
	}

	@Override
	public void accountSelected() {
		
	}

	@Override
	public void contractSelected() {
		
	}

	@Override
	public void sessionDataAvailable() {
		
	}

	@Override
	public void riskManagementUpdate() {
		
	}

	@Override
	public void speculationOpened() {
		
	}

	@Override
	public void speculationUpdate() {
		
	}

	@Override
	public void speculationClosed() {
		Speculation spec = getSpeculation();
		SREntry tr = null;
		synchronized ( spec ) {
			tr = new SREntry(
					spec.getTradeSignal().getTime(),
					spec.getExitPoint().getTime(),
					spec.getSignalType() == SignalType.BUY,
					spec.getResult(),
					spec.getExitPoint().getSize()
				);
		}
		synchronized ( tracker ) {
			tracker.add(tr);
		}
	}

	@Override
	public void sessionDataCleanup() {
		
	}

	@Override
	public void robotStopped() {
		
	}

}
