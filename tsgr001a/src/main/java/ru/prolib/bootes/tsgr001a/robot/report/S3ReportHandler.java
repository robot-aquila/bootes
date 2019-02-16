package ru.prolib.bootes.tsgr001a.robot.report;

import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.lib.report.s3rep.IS3Report;
import ru.prolib.bootes.lib.report.s3rep.S3RRecordCreate;
import ru.prolib.bootes.lib.report.s3rep.S3RRecordUpdateLast;
import ru.prolib.bootes.lib.report.s3rep.S3RType;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.RobotStateListener;

public class S3ReportHandler implements RobotStateListener {
	private final RobotState state;
	private final IS3Report report;
	
	public S3ReportHandler(RobotState state, IS3Report report) {
		this.state = state;
		this.report = report;
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
		Speculation spec = getSpeculation();
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
		Speculation spec = getSpeculation();
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
		
	}

}
