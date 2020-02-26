package ru.prolib.bootes.lib.data.ts;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.TSeries;

public class CMASignalTrigger implements SignalTrigger {
	static final Logger logger = LoggerFactory.getLogger(CMASignalTrigger.class);
	
	public interface ObjectLocator {
		TSeries<CDecimal> getFast();
		TSeries<CDecimal> getSlow();
		TSeries<CDecimal> getPrice();
	}
	
	public static class ObjectLocatorStub implements ObjectLocator {
		private final TSeries<CDecimal> fast, slow, price;
		
		public ObjectLocatorStub(TSeries<CDecimal> fast, TSeries<CDecimal> slow, TSeries<CDecimal> price) {
			this.fast = fast;
			this.slow = slow;
			this.price = price;
		}

		@Override
		public TSeries<CDecimal> getFast() {
			return fast;
		}

		@Override
		public TSeries<CDecimal> getSlow() {
			return slow;
		}

		@Override
		public TSeries<CDecimal> getPrice() {
			return price;
		}
	}
	
	private final TAMath math;
	private final ObjectLocator locator;
	
	public CMASignalTrigger(TAMath math, ObjectLocator locator) {
		this.math = math;
		this.locator = locator;
	}
	
	public CMASignalTrigger(ObjectLocator locator) {
		this(TAMath.getInstance(), locator);
	}
	
	@Override
	public TSignal getSignal(Instant time) {
		TSeries<CDecimal> fast = locator.getFast(), slow = locator.getSlow(), price = locator.getPrice();
		int fast_index = fast.getFirstIndexBefore(time);
		CDecimal price_val = price.getFirstBefore(time);
		SignalType type = SignalType.NONE;
		if ( price_val != null ) {
			switch ( math.cross(fast, slow, time) ) {
				case -1: type = SignalType.SELL; break;
				case  1: type = SignalType.BUY; break;
			}
		}
		return new TSignal(time, fast_index, type, price_val);
	}

}
