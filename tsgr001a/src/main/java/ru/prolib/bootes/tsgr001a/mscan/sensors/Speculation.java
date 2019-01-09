package ru.prolib.bootes.tsgr001a.mscan.sensors;

import java.time.Instant;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.TStampedVal;
import ru.prolib.aquila.core.BusinessEntities.Tick;

public class Speculation {
	public static final int SF_NEW				= 0x00;
	
	/**
	 * If bit enabled then speculation closed otherwise is open.
	 */
	public static final int SF_STATUS_CLOSED	= 0x01;
	
	/**
	 * If bit enabled then speculation was closed with profit otherwise it was
	 * closed with loss.
	 */
	public static final int SF_RESULT_PROFIT	= 0x02;
	
	/**
	 * If bit enabled then speculation in break-even mode.
	 */
	public static final int SF_BREAK_EVEN		= 0x04;
	
	/**
	 * If bit enabled then speculation was closed by timeout. 
	 */
	public static final int SF_TIMEOUT			= 0x08;
	
	private final TradeSignal signal;
	private Tick entry;
	private CDecimal tp, sl, be;
	private Tick exit;
	private TStampedVal<CDecimal> high, low;
	private int flags = SF_NEW;
	
	public Speculation(TradeSignal signal) {
		this.signal = signal;
	}
	
	public void setEntryPoint(Tick data) {
		this.entry = data;
	}
	
	public void setTakeProfitAt(CDecimal value) {
		this.tp = value;
	}
	
	public void setStopLossAt(CDecimal value) {
		this.sl = value;
	}
	
	public void setBreakEvenAt(CDecimal value) {
		this.be = value;
	}
	
	public void setExitPoint(Tick data) {
		this.exit = data;
	}
	
	public void setHighestPrice(Instant time, CDecimal value) {
		this.high = new TStampedVal<>(time, value);
	}
	
	public void setLowestPrice(Instant time, CDecimal value) {
		this.low = new TStampedVal<>(time, value);
	}
	
	public void setFlags(int flags) {
		this.flags = flags;
	}
	
	public TradeSignal getTradeSignal() {
		return signal;
	}
	
	public Tick getEntryPoint() {
		return entry;
	}
	
	public CDecimal getTakeProfitAt() {
		return tp;
	}
	
	public CDecimal getStopLossAt() {
		return sl;
	}
	
	public CDecimal getBreakEvenAt() {
		return be;
	}
	
	public Tick getExitPoint() {
		return exit;
	}
	
	public TStampedVal<CDecimal> getHighestPrice() {
		return high;
	}
	
	public TStampedVal<CDecimal> getLowestPrice() {
		return low;
	}
	
	public int getFlags() {
		return flags;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
