package ru.prolib.bootes.lib.data;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.bootes.lib.data.PVCluster;
import ru.prolib.bootes.lib.data.WeightedAverageTSeries;

public class WeightedAverageTSeriesTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private TSeries<PVCluster> sourceMock;
	private PVCluster clusterMock;
	private WeightedAverageTSeries service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sourceMock = control.createMock(TSeries.class);
		clusterMock = control.createMock(PVCluster.class);
		service = new WeightedAverageTSeries(sourceMock, "foobar");
	}
	
	@Test
	public void testCtor() {
		assertEquals(sourceMock, service.getSource());
		assertEquals("foobar", service.getId());
	}
	
	@Test
	public void testGet1_T_ClusterNotExists() {
		expect(sourceMock.get(T("2018-12-20T20:09:05Z"))).andReturn(null);
		control.replay();
		
		assertNull(service.get(T("2018-12-20T20:09:05Z")));
		
		control.verify();
	}
	
	@Test
	public void testGet1_T_ClusterExists() {
		expect(sourceMock.get(T("2018-12-20T20:09:05Z"))).andReturn(clusterMock);
		expect(clusterMock.getWeightedAverage()).andReturn(of("34.5678"));
		control.replay();
		
		assertEquals(of("34.5678"), service.get(T("2018-12-20T20:09:05Z")));
		
		control.verify();
	}
	
	@Test
	public void testToIndex() {
		expect(sourceMock.toIndex(T("2018-12-23T20:12:39Z"))).andReturn(204);
		control.replay();
		
		assertEquals(204, service.toIndex(T("2018-12-23T20:12:39Z")));
		
		control.verify();
	}
	
	@Test
	public void testToKey() throws Exception {
		expect(sourceMock.toKey(102)).andReturn(T("2018-12-23T20:10:00Z"));
		control.replay();
		
		assertEquals(T("2018-12-23T20:10:00Z"), service.toKey(102));
		
		control.verify();
	}
	
	@Test
	public void testGetId() {
		assertEquals("foobar", service.getId());
	}
	
	@Test
	public void testGet0_ClusterExists() throws Exception {
		expect(sourceMock.get()).andReturn(clusterMock);
		expect(clusterMock.getWeightedAverage()).andReturn(of("508.217"));
		control.replay();
		
		assertEquals(of("508.217"), service.get());
		
		control.verify();
	}
	
	@Test
	public void testGet0_ClusterNotExists() throws Exception {
		expect(sourceMock.get()).andReturn(null);
		control.replay();
		
		assertNull(service.get());
		
		control.verify();
	}
	
	@Test
	public void testGet1_I_ClusterExists() throws Exception {
		expect(sourceMock.get(504)).andReturn(clusterMock);
		expect(clusterMock.getWeightedAverage()).andReturn(of("107.221"));
		control.replay();
		
		assertEquals(of("107.221"), service.get(504));
		
		control.verify();
	}
	
	@Test
	public void testGet1_I_ClusterNotExists() throws Exception {
		expect(sourceMock.get(504)).andReturn(null);
		control.replay();
		
		assertNull(service.get(504));
		
		control.verify();
	}
	
	@Test
	public void testGetLength() {
		expect(sourceMock.getLength()).andReturn(1000);
		control.replay();
		
		assertEquals(1000, service.getLength());
		
		control.verify();
	}
	
	@Test
	public void testGetLID() {
		LID lidMock = control.createMock(LID.class);
		expect(sourceMock.getLID()).andReturn(lidMock);
		control.replay();
		
		assertSame(lidMock, service.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		sourceMock.lock();
		control.replay();
		
		service.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		sourceMock.unlock();
		control.replay();
		
		service.unlock();
		
		control.verify();
	}

	@Test
	public void testGetTimeFrame() {
		expect(sourceMock.getTimeFrame()).andReturn(ZTFrame.M15MSK);
		control.replay();
		
		assertEquals(ZTFrame.M15MSK, service.getTimeFrame());
		
		control.verify();
	}

}
