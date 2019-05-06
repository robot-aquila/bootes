package ru.prolib.bootes.tsgr001a.robot.filter;

import java.time.Duration;

import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.IFilter;
import ru.prolib.bootes.lib.data.ts.filter.impl.CooldownFilter;
import ru.prolib.bootes.lib.report.s3rep.utils.S3RLastSpeculationEndTime;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.TSGR001AReports;

public class S3TSFilterFactory implements IS3TSFilterFactory {
	private final RobotState state;
	private final TSGR001AReports reports;
	
	public S3TSFilterFactory(TSGR001AReports reports, RobotState state) {
		this.state = state;
		this.reports = reports;
	}

	/* (non-Javadoc)
	 * @see ru.prolib.bootes.tsgr001a.robot.filter.IS3TSFilterFactory#produce(java.lang.String)
	 */
	@Override
	public IFilter<S3TradeSignal> produce(String code) {
		switch ( code ) {
		case "CoolDown30":
			return new CooldownFilter(new S3RLastSpeculationEndTime(
					reports.getTradesReport()),
					Duration.ofMinutes(30)
				);
			
		case "SLgtATR":
			return new StopLossGtATR(state.getSessionDataHandler());
			
		case "MADevLim":
			return new MADevLimit(state);
			
		case "ByTrendT1":
			return new ByTrendT1(state);
			
		case "FCSD":
			return new FilterFCSD(state.getSessionDataHandler());
			
		default:
			throw new IllegalArgumentException("Unsupported code: " + code);
		}
	}
	
}
