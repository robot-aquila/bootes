package ru.prolib.bootes.lib.data.ts.filter.impl;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.TStamped;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;

public class CooldownFilterTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private TStamped getterMock;
	private S3TradeSignal signalMock;
	private CooldownFilter service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		getterMock = control.createMock(TStamped.class);
		signalMock = control.createMock(S3TradeSignal.class);
		service = new CooldownFilter("foo", getterMock, Duration.ofHours(3));
	}
	
	@Test
	public void testCtor3() {
		assertEquals("foo", service.getID());
		assertSame(getterMock, service.getTimeGetter());
		assertEquals(Duration.ofHours(3), service.getDuration());
	}
	
	@Test
	public void testCtor2() {
		service = new CooldownFilter(getterMock, Duration.ofMinutes(15));
		assertEquals("COOLDOWN", service.getID());
		assertSame(getterMock, service.getTimeGetter());
		assertEquals(Duration.ofMinutes(15), service.getDuration());
	}
	
	@Test
	public void testApprove_Approve() {
		expect(getterMock.getTime()).andReturn(T("2019-02-18T07:08:19Z"));
		expect(signalMock.getTime()).andReturn(T("2019-02-18T12:01:34Z"));
		control.replay();
		
		assertTrue(service.approve(signalMock));
		
		control.verify();
	}
	
	@Test
	public void testApprove_Decline() {
		expect(getterMock.getTime()).andReturn(T("2019-02-18T07:08:19Z"));
		expect(signalMock.getTime()).andReturn(T("2019-02-18T08:00:00Z"));
		control.replay();
		
		assertFalse(service.approve(signalMock));
		
		control.verify();
	}
	
	@Test
	public void testApprove_IfGetterReturnsNull() {
		expect(getterMock.getTime()).andReturn(null);
		control.replay();
		
		assertTrue(service.approve(signalMock));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<String> vID = new Variant<>("foo", "bar");
		Variant<TStamped> vGTR = new Variant<>(vID, getterMock, control.createMock(TStamped.class));
		Variant<Duration> vDUR = new Variant<>(vGTR, Duration.ofHours(3), Duration.ofMillis(5));
		Variant<?> iterator = vDUR;
		int foundCnt = 0;
		CooldownFilter x, found = null;
		do {
			x = new CooldownFilter(vID.get(), vGTR.get(), vDUR.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foo", found.getID());
		assertSame(getterMock, found.getTimeGetter());
		assertEquals(Duration.ofHours(3), found.getDuration());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("CooldownFilter[getter=").append(getterMock).append(",duration=PT3H,id=foo]")
				.toString();
		
		assertEquals(expected, service.toString());
	}

}
