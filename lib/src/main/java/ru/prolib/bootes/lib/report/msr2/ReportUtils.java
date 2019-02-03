package ru.prolib.bootes.lib.report.msr2;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class ReportUtils {
	private static final ReportUtils instance = new ReportUtils();
	
	public static ReportUtils getInstance() {
		return instance;
	}
	
	public CDecimal getAveragePrice(IReport report) {
		CDecimal px = null;
		int pn = 0;
		for ( IBlock block : report.getBlocks() ) {
			CDecimal bpx = block.getPrice();
			if ( bpx != null ) {
				px = px == null ? bpx : px.add(bpx);
				pn ++;
			}
		}
		return pn > 0 ? px.divide((long) pn) : null;
	}
	
	public Integer getAverageIndex(IReport report, ITimeIndexMapper tim) {
		Integer ix = null;
		int in = 0;
		for ( IBlock block : report.getBlocks() ) {
			Instant btx = block.getTime();
			if ( btx != null ) {
				int index = tim.toIndex(btx);
				ix = ix == null ? index : ix + index;
				in ++;
			}
		}
		return in > 0 ? Math.round((float)ix / (float)in) : null;
	}

}
