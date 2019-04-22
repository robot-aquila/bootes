package ru.prolib.bootes.protos;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.bootes.lib.rm.RMPriceStats;

public class PROTOSPriceStats implements RMPriceStats {
	private final PROTOSRobotState state;
	
	public PROTOSPriceStats(PROTOSRobotState state) {
		this.state = state;
	}

	@Override
	public CDecimal getDailyPriceMove(Instant time) {
		TSeries<CDecimal> x = state.getSessionDataHandler()
				.getSeriesHandlerT1()
				.getSeries()
				.getSeries(PROTOSSetupT1.SID_ATR);
		return x.getFirstBefore(time);
	}

	@Override
	public CDecimal getLocalPriceMove(Instant time) {
		TSeries<CDecimal> x = state.getSessionDataHandler()
				.getSeriesHandlerT0()
				.getSeries()
				.getSeries(PROTOSSetupT0.SID_ATR);
		return x.getFirstBefore(time);
	}

}
