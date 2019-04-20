package ru.prolib.bootes.tsgr001a.robot;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.bootes.lib.rm.RMPriceStats;

public class TSGR001APriceStats implements RMPriceStats {
	private final RobotState state;
	
	public TSGR001APriceStats(RobotState state) {
		this.state = state;
	}

	@Override
	public CDecimal getDailyPriceMove(Instant time) {
		TSeries<CDecimal> x = state.getSessionDataHandler()
				.getSeriesHandlerT2()
				.getSeries()
				.getSeries(SetupT2.SID_ATR);
		return x.getFirstBefore(time);
	}

	@Override
	public CDecimal getLocalPriceMove(Instant time) {
		TSeries<CDecimal> x = state.getSessionDataHandler()
				.getSeriesHandlerT0()
				.getSeries()
				.getSeries(SetupT0.SID_ATR);
		return x.getFirstBefore(time);
	}

}
