package ru.prolib.bootes.tsgr001a.mscan;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class MSCANEventImplTest {
	
	Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private MSCANLogEntry entry1, entryMock2, entryMock3;
	private MSCANEventImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		entryMock2 = control.createMock(MSCANLogEntry.class);
		entryMock3 = control.createMock(MSCANLogEntry.class);
		entry1 = new MSCANLogEntryImpl(
				false,
				"TEST",
				T("2015-12-24T00:03:24Z"),
				of("115.340"),
				"foobar"
			);
		service = new MSCANEventImpl(entry1);
	}
	
	@Test
	public void testCtor1() {
		assertFalse(service.isFinished());
		assertEquals(entry1, service.getStart());
		List<MSCANLogEntry> expected = new ArrayList<>();
		expected.add(entry1);
		assertEquals(expected, service.getEntries());
	}
	
	@Test
	public void testCtor1_CloseIfTrigger() {
		entry1 = new MSCANLogEntryImpl(
				true,
				"TEST",
				T("2015-12-24T00:03:24Z"),
				of("115.340"),
				"foobar"
			);
		service = new MSCANEventImpl(entry1);
		assertTrue(service.isFinished());
		assertEquals(entry1, service.getStart());
		List<MSCANLogEntry> expected = new ArrayList<>();
		expected.add(entry1);
		assertEquals(expected, service.getEntries());
		assertEquals(entry1, service.getClose());
	}
	
	@Test
	public void testAddLogEntry_NonTrigger() {
		expect(entryMock2.isTrigger()).andReturn(false);
		control.replay();
		
		service.addLogEntry(entryMock2);
		
		control.verify();
		assertFalse(service.isFinished());
		List<MSCANLogEntry> expected = new ArrayList<>();
		expected.add(entry1);
		expected.add(entryMock2);
		assertEquals(expected, service.getEntries());
	}
	
	@Test
	public void testAddLogEntry_Trigger() {
		expect(entryMock2.isTrigger()).andReturn(true);
		control.replay();
		
		service.addLogEntry(entryMock2);
		
		control.verify();
		assertTrue(service.isFinished());
		assertSame(entryMock2, service.getClose());
		List<MSCANLogEntry> expected = new ArrayList<>();
		expected.add(entry1);
		expected.add(entryMock2);
		assertEquals(expected, service.getEntries());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testAddLogEntry_ThrowsIfClosed() {
		expect(entryMock2.isTrigger()).andReturn(true);
		control.replay();
		service.addLogEntry(entryMock2);
		control.resetToStrict();
		control.replay();
		
		service.addLogEntry(entryMock3);
	}
	
	@Test
	public void testGetStart() {
		control.replay();
		
		assertSame(entry1, service.getStart());
		
		control.verify();
	}
	
	@Test
	public void testGetStartTypeID() {
		assertEquals("TEST", service.getStartTypeID());
	}
	
	@Test
	public void testGetStartTime() {
		assertEquals(T("2015-12-24T00:03:24Z"), service.getStartTime());
	}
	
	@Test
	public void testGetStartValue() {
		assertEquals(of("115.340"), service.getStartValue());
	}
	
	@Test
	public void testGetEntries() {
		expect(entryMock2.isTrigger()).andStubReturn(false);
		expect(entryMock3.isTrigger()).andStubReturn(false);
		control.replay();
		service.addLogEntry(entryMock2);
		service.addLogEntry(entryMock3);
		
		List<MSCANLogEntry> actual = service.getEntries();

		List<MSCANLogEntry> expected = new ArrayList<>();
		expected.add(entry1);
		expected.add(entryMock2);
		expected.add(entryMock3);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIsFinished() {
		assertFalse(service.isFinished());
		
		service.setClose(entryMock2);
		
		assertTrue(service.isFinished());
	}
	
	@Test
	public void testGetClose() {
		service.setClose(entryMock2);
		
		assertSame(entryMock2, service.getClose());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetClose_ThrowsIfNotClosed() {
		service.getClose();
	}
	
	@Test
	public void testGetCloseTypeID() {
		expect(entryMock2.getTypeID()).andReturn("ZULU24");
		control.replay();
		service.setClose(entryMock2);
		
		assertEquals("ZULU24", service.getCloseTypeID());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetCloseTypeID_ThrowsIfNotClosed() {
		service.getCloseTypeID();
	}
	
	@Test
	public void testGetCloseTime() {
		expect(entryMock2.getTime()).andReturn(T("2018-12-26T17:03:58Z"));
		control.replay();
		service.setClose(entryMock2);
		
		assertEquals(T("2018-12-26T17:03:58Z"), service.getCloseTime());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetCloseTime_ThrowsIfNotClosed() {
		service.getCloseTime();
	}
	
	@Test
	public void testGetCloseValue() {
		expect(entryMock2.getValue()).andReturn(of("902.408"));
		control.replay();
		service.setClose(entryMock2);
		
		assertEquals(of("902.408"), service.getCloseValue());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetCloseValue_ThrowsIfNotClosed() {
		service.getCloseValue();
	}
	
	@Test
	public void testHashCode() {
		expect(entryMock2.isTrigger()).andStubReturn(false);
		expect(entryMock3.isTrigger()).andStubReturn(true);
		control.replay();
		service.addLogEntry(entryMock2);
		service.addLogEntry(entryMock3);
		
		int expected = new HashCodeBuilder(244215, 907)
				.append(service.getEntries())
				.append(entry1)
				.append(entryMock3)
				.build();
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testToString() {
		expect(entryMock2.isTrigger()).andStubReturn(false);
		expect(entryMock3.isTrigger()).andStubReturn(true);
		control.replay();
		service.addLogEntry(entryMock2);
		service.addLogEntry(entryMock3);
		
		List<MSCANLogEntry> expectedEntries = new ArrayList<>();
		expectedEntries.add(entry1);
		expectedEntries.add(entryMock2);
		expectedEntries.add(entryMock3);
		
		String expected = new StringBuilder()
				.append("MSCANEventImpl[start=")
				.append(entry1)
				.append(",entries=")
				.append(expectedEntries)
				.append(",close=")
				.append(entryMock3)
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		expect(entryMock2.isTrigger()).andStubReturn(false);
		expect(entryMock3.isTrigger()).andStubReturn(true);
		control.replay();
		service.addLogEntry(entryMock2);
		service.addLogEntry(entryMock3);
		List<MSCANLogEntry> entries1 = new ArrayList<>();
		entries1.add(entry1);
		entries1.add(entryMock2);
		entries1.add(entryMock3);
		List<MSCANLogEntry> entries2 = new ArrayList<>();
		entries2.add(entryMock2);
		entries2.add(entryMock3);
		Variant<List<MSCANLogEntry>> vEntr = new Variant<>(entries1, entries2);
		Variant<Boolean> vCls = new Variant<>(vEntr, true, false);
		Variant<?> iterator = vCls;
		MSCANEventImpl x, found = null;
		int foundCnt = 0;
		do {
			control.resetToStrict();
			List<MSCANLogEntry> dummy = vEntr.get();
			for ( int i = 0; i < dummy.size(); i ++ ) {
				MSCANLogEntry entryMock = dummy.get(i);
				if ( i == dummy.size() - 1 ) {
					expect(entryMock.isTrigger()).andStubReturn(vCls.get());
				} else if ( entryMock != entry1 ) {
					expect(entryMock.isTrigger()).andStubReturn(false);
				}
			}
			control.replay();
			x = new MSCANEventImpl(dummy.get(0));
			for ( int i = 1; i < dummy.size(); i ++ ) {
				x.addLogEntry(dummy.get(i));
			}
			
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(entry1, found.getStart());
		assertEquals(entries1, found.getEntries());
		assertEquals(entryMock3, found.getClose());
	}

}
