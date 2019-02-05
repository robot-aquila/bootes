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
	
	/**
	 * Calculate average index of all blocks of report.
	 * <p>
	 * @param report - report instance
	 * @param tim - time-index mapper
	 * @param null_time_index - index to use for blocks with defined price and undefined time
	 * @return average index
	 */
	public Integer getAverageIndex(IReport report, ITimeIndexMapper tim, int null_time_index) {
		Integer ix = null;
		int in = 0, index;
		for ( IBlock block : report.getBlocks() ) {
			Instant btx = block.getTime();
			if ( btx != null ) {
				index = tim.toIndex(btx);
			} else if ( block.getPrice() != null ) {
				index = null_time_index;
			} else {
				continue;
			}
			ix = ix == null ? index : ix + index;
			in ++;
		}
		return in > 0 ? Math.round((float)ix / (float)in) : null;
	}

}
