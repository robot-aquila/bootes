package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class AppConfigTest {
	private IMocksControl control;
	private BasicConfig basicConfigMock1, basicConfigMock2;
	private SchedulerConfig schedulerConfigMock1, schedulerConfigMock2;
	private OHLCHistoryConfig ohlcHistoryConfigMock1, ohlcHistoryConfigMock2;
	private TerminalConfig terminalConfigMock1, terminalConfigMock2;
	private AppConfig service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		basicConfigMock1 = control.createMock(BasicConfig.class);
		basicConfigMock2 = control.createMock(BasicConfig.class);
		schedulerConfigMock1 = control.createMock(SchedulerConfig.class);
		schedulerConfigMock2 = control.createMock(SchedulerConfig.class);
		ohlcHistoryConfigMock1 = control.createMock(OHLCHistoryConfig.class);
		ohlcHistoryConfigMock2 = control.createMock(OHLCHistoryConfig.class);
		terminalConfigMock1 = control.createMock(TerminalConfig.class);
		terminalConfigMock2 = control.createMock(TerminalConfig.class);
		service = new AppConfig(basicConfigMock1, schedulerConfigMock1, ohlcHistoryConfigMock1, terminalConfigMock1);
	}
	
	@Test
	public void testCtor() {
		assertSame(basicConfigMock1, service.getBasicConfig());
		assertSame(schedulerConfigMock1, service.getSchedulerConfig());
		assertSame(ohlcHistoryConfigMock1, service.getOHLCHistoryConfig());
		assertSame(terminalConfigMock1, service.getTerminalConfig());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<BasicConfig> vBC = new Variant<>(basicConfigMock1, basicConfigMock2);
		Variant<SchedulerConfig> vSC = new Variant<>(vBC, schedulerConfigMock1, schedulerConfigMock2);
		Variant<OHLCHistoryConfig> vHC = new Variant<>(vSC, ohlcHistoryConfigMock1, ohlcHistoryConfigMock2);
		Variant<TerminalConfig> vTC = new Variant<>(vHC, terminalConfigMock1, terminalConfigMock2);
		Variant<?> iterator = vTC;
		int foundCnt = 0;
		AppConfig x, found = null;
		do {
			x = new AppConfig(vBC.get(), vSC.get(), vHC.get(), vTC.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(basicConfigMock1, found.getBasicConfig());
		assertSame(schedulerConfigMock1, found.getSchedulerConfig());
		assertSame(ohlcHistoryConfigMock1, found.getOHLCHistoryConfig());
		assertSame(terminalConfigMock1, found.getTerminalConfig());
	}

	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(127539, 39751)
			.append(basicConfigMock1)
			.append(schedulerConfigMock1)
			.append(ohlcHistoryConfigMock1)
			.append(terminalConfigMock1)
			.toHashCode();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testToString() {
		String expected = "AppConfig[basic=" + basicConfigMock1
				+ ",scheduler=" + schedulerConfigMock1
				+ ",ohlcHistory=" + ohlcHistoryConfigMock1
				+ ",terminal=" + terminalConfigMock1
				+ "]";
		
		assertEquals(expected, service.toString());
	}

}
