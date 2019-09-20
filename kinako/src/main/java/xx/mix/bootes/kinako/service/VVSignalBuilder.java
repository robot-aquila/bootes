package xx.mix.bootes.kinako.service;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class VVSignalBuilder {
	private Instant time;
	private final List<VVOrderRecom> recommendations;
	
	VVSignalBuilder(List<VVOrderRecom> recommendations) {
		this.recommendations = recommendations;
		this.time = Instant.now();
	}
	
	public VVSignalBuilder() {
		this(new ArrayList<>());
	}
	
	public VVSignalBuilder withTime(Instant time) {
		this.time = time;
		return this;
	}
	
	public VVSignalBuilder withTime(String time_string) {
		return withTime(Instant.parse(time_string));
	}
	
	public VVSignalBuilder addOrderRecom(VVOrderType type, long volume, String symbol) {
		recommendations.add(new VVOrderRecom(type, of(volume), symbol));
		return this;
	}
	
	public VVSignal build() {
		if ( recommendations.size() == 0 ) {
			throw new IllegalStateException("No recommendations");
		}
		return new VVSignal(time, new ArrayList<>(recommendations));
	}

}
