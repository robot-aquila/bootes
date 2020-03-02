package ru.prolib.bootes.lib.robo.s3.rh;

import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.lib.report.summarep.ISummaryReportTracker;
import ru.prolib.bootes.lib.report.summarep.SREntry;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListenerStub;
import ru.prolib.bootes.lib.robo.s3.S3Speculation;
import ru.prolib.bootes.lib.robo.s3.statereq.IS3Speculative;

public class S3SummaryReportHandler extends S3RobotStateListenerStub {
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

}
