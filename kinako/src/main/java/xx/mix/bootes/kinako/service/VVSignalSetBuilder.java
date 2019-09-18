package xx.mix.bootes.kinako.service;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class VVSignalSetBuilder {
	private Instant time;
	private final List<VVSignal> signals;
	
	VVSignalSetBuilder(List<VVSignal> signals) {
		this.signals = signals;
		this.time = Instant.now();
	}
	
	public VVSignalSetBuilder() {
		this(new ArrayList<>());
	}
	
	public VVSignalSetBuilder withTime(Instant time) {
		this.time = time;
		return this;
	}
	
	public VVSignalSetBuilder withTime(String time_string) {
		return withTime(Instant.parse(time_string));
	}
	
	public VVSignalSetBuilder addSignal(VVSignalType type, long volume, String symbol) {
		signals.add(new VVSignal(type, of(volume), symbol));
		return this;
	}
	
	public VVSignalSet build() {
		if ( signals.size() == 0 ) {
			throw new IllegalStateException("No signals");
		}
		return new VVSignalSet(time, new ArrayList<>(signals));
	}

}
