package ru.prolib.bootes.tsgr001a.mscan.sensors;

import java.time.Instant;

public interface SignalTrigger {
	
	SignalType getSignal(Instant currentTime);

}
