package ru.prolib.bootes.tsgr001a.data;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;

public class PVClusterAggregatorTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private TSeriesImpl<PVCluster> series;
	private PVClusterAggregator service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		series = new TSeriesImpl<>(ZTFrame.M5);
		service = new PVClusterAggregator();
	}
	
	@Test
	public void testGetInstance() {
		PVClusterAggregator actual = PVClusterAggregator.getInstance();
		
		assertNotNull(actual);
		assertSame(PVClusterAggregator.getInstance(), actual);
		assertSame(PVClusterAggregator.getInstance(), actual);
	}
	
	@Test
	public void testAggregate_ExistingCluster() {
		PVCluster clusterMock = control.createMock(PVCluster.class);
		clusterMock.addTrade(of("10.03"), of(12L));
		series.set(T("2018-12-22T23:00:00Z"), clusterMock);
		control.replay();
		
		service.aggregate(series, Tick.ofTrade(T("2018-12-22T23:02:13Z"), of("10.03"), of(12L)));
		
		control.verify();
	}

	@Test
	public void testAggregate_NewCluster() {
		
		service.aggregate(series, Tick.ofTrade(T("2018-12-22T15:08:12Z"), of("13.40"), of(10L)));
		
		PVCluster actual = series.get(T("2018-12-22T15:05:00Z"));
		assertNotNull(actual);
		assertEquals(of("13.40000000"), actual.getWeightedAverage());
		assertEquals(of(10L), actual.getTotalVolume());
	}

}
