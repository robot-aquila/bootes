package ru.prolib.bootes.lib.data.ts;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.TSeries;

public class CMASignalTrigger implements SignalTrigger {
	
	public interface ObjectLocator {
		TSeries<CDecimal> getFast();
		TSeries<CDecimal> getSlow();
	}
	
	public static class ObjectLocatorStub implements ObjectLocator {
		private final TSeries<CDecimal> fast, slow;
		
		public ObjectLocatorStub(TSeries<CDecimal> fast, TSeries<CDecimal> slow) {
			this.fast = fast;
			this.slow = slow;
		}

		@Override
		public TSeries<CDecimal> getFast() {
			return fast;
		}

		@Override
		public TSeries<CDecimal> getSlow() {
			return slow;
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
	public TSignal getSignal(Instant current_time) {
		TSeries<CDecimal> fast = locator.getFast();
		int index = fast.getLength() - 1;
		switch ( math.cross(locator.getFast(), locator.getSlow(), current_time) ) {
			case -1: return new TSignal(current_time, index, SignalType.SELL, null);
			case  1: return new TSignal(current_time, index, SignalType.BUY,  null);
			default: return new TSignal(current_time, index, SignalType.NONE, null);
		}
	}

}
