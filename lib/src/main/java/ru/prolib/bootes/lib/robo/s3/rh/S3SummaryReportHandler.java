package ru.prolib.bootes.lib.robo.s3.rh;

import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.lib.report.summarep.ISummaryReportTracker;
import ru.prolib.bootes.lib.report.summarep.SREntry;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListener;
import ru.prolib.bootes.lib.robo.s3.S3Speculation;
import ru.prolib.bootes.lib.robo.s3.statereq.IS3Speculative;

public class S3SummaryReportHandler implements S3RobotStateListener {
	private final IS3Speculative state;
	private final ISummaryReportTracker tracker;
	
	public S3SummaryReportHandler(IS3Speculative state,
								ISummaryReportTracker tracker)
	{
		this.state = state;
		this.tracker = tracker;
	}
	
	private S3Speculation getSpeculation() {
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
		S3Speculation spec = getSpeculation();
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
