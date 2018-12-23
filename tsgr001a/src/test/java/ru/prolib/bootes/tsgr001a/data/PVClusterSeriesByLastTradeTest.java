package ru.prolib.bootes.tsgr001a.data;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.DataProviderStub;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;

public class PVClusterSeriesByLastTradeTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private static Symbol symbol = new Symbol("XXX");
	private IMocksControl control;
	private PVClusterAggregator aggregatorMock;
	private TSeriesImpl<PVCluster> series;
	private EditableTerminal terminal;
	private PVClusterSeriesByLastTrade service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		aggregatorMock = control.createMock(PVClusterAggregator.class);
		series = new TSeriesImpl<>(ZTFrame.M5);
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		terminal.getEditableSecurity(symbol);
		service = new PVClusterSeriesByLastTrade(series, terminal, symbol, aggregatorMock);
	}
	
	@Test
	public void testCtor4() throws Exception {
		assertSame(terminal, service.getTerminal());
		assertSame(series, service.getSeries());
		assertSame(aggregatorMock, service.getAggregator());
		assertEquals(symbol, service.getSymbol());
		assertNull(service.getSecurity());
	}
	
	@Test
	public void testCtor3() throws Exception {
		service = new PVClusterSeriesByLastTrade(series, terminal, symbol);
		assertSame(terminal, service.getTerminal());
		assertSame(series, service.getSeries());
		assertSame(PVClusterAggregator.getInstance(), service.getAggregator());
		assertEquals(symbol, service.getSymbol());
		assertNull(service.getSecurity());
	}
	
	@Test
	public void testStartListening() {
		Security securityMock = control.createMock(Security.class);
		EventType typeMock = control.createMock(EventType.class);
		expect(securityMock.onLastTrade()).andReturn(typeMock);
		typeMock.addListener(service);
		control.replay();
		
		service.startListening(securityMock);
		
		control.verify();
	}
	
	@Test
	public void testStopListening() {
		Security securityMock = control.createMock(Security.class);
		EventType typeMock = control.createMock(EventType.class);
		expect(securityMock.onLastTrade()).andReturn(typeMock);
		typeMock.removeListener(service);
		control.replay();
		
		service.stopListening(securityMock);
		
		control.verify();
	}

	@Test
	public void testProcessEvent() {
		Security securityMock = control.createMock(Security.class);
		EventType typeMock = control.createMock(EventType.class);
		Tick tick = Tick.ofTrade(T("2018-12-23T05:12:37Z"), of("12.34"), of(100L));
		SecurityTickEvent event = new SecurityTickEvent(
				typeMock,
				securityMock,
				T("2018-12-23T05:12:38Z"),
				tick
			);
		aggregatorMock.aggregate(series, tick);
		control.replay();
		
		service.processEvent(event);
		
		control.verify();
	}

}
