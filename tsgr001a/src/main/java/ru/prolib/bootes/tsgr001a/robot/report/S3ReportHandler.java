package ru.prolib.bootes.tsgr001a.robot.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.lib.report.s3rep.IS3Report;
import ru.prolib.bootes.lib.report.s3rep.S3RRecord;
import ru.prolib.bootes.lib.report.s3rep.S3RRecordCreate;
import ru.prolib.bootes.lib.report.s3rep.S3RRecordUpdateLast;
import ru.prolib.bootes.lib.report.s3rep.S3RType;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListener;
import ru.prolib.bootes.lib.robo.s3.S3Speculation;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class S3ReportHandler implements S3RobotStateListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(S3ReportHandler.class);
	}
	
	private final RobotState state;
	private final IS3Report report;
	private final boolean dumpResult;
	
	public S3ReportHandler(RobotState state, IS3Report report, boolean dumpResult) {
		this.state = state;
		this.report = report;
		this.dumpResult = dumpResult;
	}
	
	public S3ReportHandler(RobotState state, IS3Report report) {
		this(state, report, false);
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
		S3Speculation spec = getSpeculation();
		S3RRecordCreate request;
		synchronized ( spec ) {
			Tick en_p = spec.getEntryPoint();
			request = new S3RRecordCreate(
					spec.getSignalType() == SignalType.BUY ? S3RType.LONG : S3RType.SHORT,
					en_p.getTime(),
					en_p.getPrice(),
					en_p.getSize(),
					spec.getTakeProfit(),
					spec.getStopLoss(),
					spec.getBreakEven()
				);
		}
		report.create(request);
	}

	@Override
	public void speculationClosed() {
		S3Speculation spec = getSpeculation();
		S3RRecordUpdateLast request;
		synchronized ( spec ) {
			Tick ex_p = spec.getExitPoint();
			request = new S3RRecordUpdateLast(
					ex_p.getTime(),
					ex_p.getPrice(),
					spec.getResult()
				);
		}
		report.update(request);
	}

	@Override
	public void sessionDataCleanup() {
		
	}

	@Override
	public void robotStopped() {
		if ( ! dumpResult ) {
			return;
		}

		String r_sep = " ", l_sep = System.lineSeparator();
		StringBuilder sb = new StringBuilder()
				.append(l_sep)
				.append("S3 Trades Report -----------------------------------------")
				.append(l_sep);
		synchronized ( report ) {
			int record_count = report.getRecordCount();
			for ( int i = 0; i < record_count; i ++ ) {
				S3RRecord rec = report.getRecord(i);
				sb.append(String.format("%06d", rec.getID())).append(r_sep)
					.append(rec.getType()).append(r_sep)
					.append(rec.getEntryTime()).append(" -> ").append(rec.getExitTime()).append(r_sep)
					.append(rec.getProfitAndLoss())
					.append(l_sep);
			}
		}
		logger.debug(sb.toString());
	}

}
