package ru.prolib.bootes.tsgr001a.data;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.EditableTSeries;

public class PVClusterAggregator {
	private static final PVClusterAggregator instance;
	
	static {
		instance = new PVClusterAggregator();
	}
	
	public static PVClusterAggregator getInstance() {
		return instance;
	}
	
	public void aggregate(EditableTSeries<PVCluster> series, Tick tick) {
		Instant time = tick.getTime();
		PVCluster cluster = series.get(time);
		if ( cluster == null ) {
			cluster = new PVCluster();
			series.set(time, cluster);
		}
		cluster.addTrade(tick.getPrice(), tick.getSize());
	}

}
