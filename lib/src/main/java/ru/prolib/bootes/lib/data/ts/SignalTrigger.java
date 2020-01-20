package ru.prolib.bootes.lib.data.ts;

import java.time.Instant;

public interface SignalTrigger {
	
	TSignal getSignal(Instant currentTime);

}
