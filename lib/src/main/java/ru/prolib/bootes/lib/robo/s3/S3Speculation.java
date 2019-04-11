package ru.prolib.bootes.lib.robo.s3;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;

public class S3Speculation {
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
	
	private final S3TradeSignal signal;
	private Tick entry;
	private Tick exit;
	private int flags = SF_NEW;
	private CDecimal result, tp, sl, be;
	
	public S3Speculation(S3TradeSignal signal) {
		this.signal = signal;
	}
	
	public void setEntryPoint(Tick data) {
		this.entry = data;
	}
	
	public void setExitPoint(Tick data) {
		this.exit = data;
	}
	
	public void setFlags(int flags) {
		this.flags = flags;
	}

	public void setResult(CDecimal result) {
		this.result = result;
	}
	
	public void setTakeProfit(CDecimal price) {
		this.tp = price;
	}
	
	public void setStopLoss(CDecimal price) {
		this.sl = price;
	}
	
	public void setBreakEven(CDecimal price) {
		this.be = price;
	}

	public S3TradeSignal getTradeSignal() {
		return signal;
	}
	
	public SignalType getSignalType() {
		return signal.getType();
	}
	
	public Tick getEntryPoint() {
		return entry;
	}
	
	public Tick getExitPoint() {
		return exit;
	}
	
	public int getFlags() {
		return flags;
	}
	
	public CDecimal getResult() {
		return result;
	}
	
	public CDecimal getTakeProfit() {
		return tp;
	}
	
	public CDecimal getStopLoss() {
		return sl;
	}
	
	public CDecimal getBreakEven() {
		return be;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
