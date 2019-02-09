package ru.prolib.bootes.lib.report.blockrep;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.bootes.lib.report.blockrep.TimeIndexMapperTS;

public class TimeIndexMapperTSTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private TSeriesImpl<Instant> basis;
	private TimeIndexMapperTS service;
	
	public void addEntry(String timeString) {
		basis.set(T(timeString), T(timeString));
	}

	@Before
	public void setUp() throws Exception {
		basis = new TSeriesImpl<>(ZTFrame.M5);
		service = new TimeIndexMapperTS(basis);
		addEntry("2019-02-01T00:00:00Z"); // # 0
		addEntry("2019-02-01T03:00:00Z"); // # 1
		addEntry("2019-02-01T03:05:00Z"); // # 2
		addEntry("2019-02-01T03:10:00Z"); // # 3
		addEntry("2019-02-01T03:15:00Z"); // # 4
		addEntry("2019-02-01T03:20:00Z"); // # 5
		addEntry("2019-02-01T05:05:00Z"); // # 6
		addEntry("2019-02-01T05:15:00Z"); // # 7
		addEntry("2019-02-01T08:45:00Z"); // # 8
		addEntry("2019-02-01T08:50:00Z"); // # 9
		addEntry("2019-02-01T08:55:00Z"); // #10
		addEntry("2019-02-01T09:00:00Z"); // #11
		addEntry("2019-02-01T09:05:00Z"); // #12
		addEntry("2019-02-01T09:10:00Z"); // #13
		addEntry("2019-02-01T12:00:00Z"); // #14
	}
	
	@Test
	public void testCtor1() {
		assertSame(basis, service.getBasis());
	}
	
	@Test
	public void testToIntervalStart() {
		assertEquals(T("2019-02-01T03:05:00Z"), service.toIntervalStart( 2));
		assertEquals(T("2019-02-01T05:15:00Z"), service.toIntervalStart( 7));
		assertEquals(T("2019-02-01T09:10:00Z"), service.toIntervalStart(13));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testToIntervalStart_ThrowsIfOutOfRange() {
		service.toIntervalStart(20);
	}
	
	@Test
	public void testToIntervalEnd() {
		assertEquals(T("2019-02-01T03:10:00Z"), service.toIntervalEnd( 2));
		assertEquals(T("2019-02-01T05:20:00Z"), service.toIntervalEnd( 7));
		assertEquals(T("2019-02-01T09:15:00Z"), service.toIntervalEnd(13));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testToIntervalEnd_ThrowsIfOutOfRange() {
		service.toIntervalEnd(20);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testToIndex_ThrowsIfNoElements() {
		basis.clear();
		
		service.toIndex(T("2019-05-13T05:28:46Z"));
	}
	
	@Test
	public void testToIndex_FirstIfSingleElement() {
		basis.clear();
		addEntry("2019-01-29T21:55:19Z");
		
		assertEquals(0, service.toIndex(T("1998-05-12T13:04:35Z")));
		assertEquals(0, service.toIndex(T("2546-03-15T19:48:24Z")));
	}
	
	@Test
	public void testToIndex_FirstIfBeforeFirst() {
		assertEquals( 0, service.toIndex(T("2019-01-01T00:00:00Z")));
	}
	
	@Test
	public void testToIndex_LastIfAfterLast() {
		assertEquals(14, service.toIndex(T("2019-02-12T23:59:59Z")));
	}
	
	@Test
	public void testToIndex_FirstIfInFirst() {
		assertEquals( 0, service.toIndex(T("2019-02-01T00:03:19Z")));
	}
	
	@Test
	public void testToIndex_LastIfInLast() {
		assertEquals(14, service.toIndex(T("2019-02-01T12:02:15Z")));
	}
	
	@Test
	public void testToIndex_ExactAtElement() {
		assertEquals( 3, service.toIndex(T("2019-02-01T03:10:00Z")));
		
		assertEquals( 4, service.toIndex(T("2019-02-01T03:15:00Z")));
		assertEquals( 4, service.toIndex(T("2019-02-01T03:17:23Z")));
		assertEquals( 4, service.toIndex(T("2019-02-01T03:19:59.999Z")));
		
		assertEquals(12, service.toIndex(T("2019-02-01T09:05:00Z")));
		assertEquals(12, service.toIndex(T("2019-02-01T09:08:09Z")));
		assertEquals(12, service.toIndex(T("2019-02-01T09:09:59.999Z")));
	}
	
	@Test
	public void testToIndex_CloserToEarlierThanLater() {
		assertEquals( 0, service.toIndex(T("2019-02-01T01:15:39Z")));
		assertEquals( 0, service.toIndex(T("2019-02-01T01:30:00Z")));
		assertEquals( 0, service.toIndex(T("2019-02-01T01:32:29.999Z")));
		
		assertEquals( 3, service.toIndex(T("2019-02-01T03:14:59.999Z")));
		
		assertEquals(13, service.toIndex(T("2019-02-01T09:11:00Z")));
	}
	
	@Test
	public void testToIndex_CloserToLaterThanEarlier() {
		assertEquals( 1, service.toIndex(T("2019-02-01T01:32:30Z")));
		assertEquals( 1, service.toIndex(T("2019-02-01T01:32:31Z")));
		assertEquals( 1, service.toIndex(T("2019-02-01T01:35:00Z")));

		assertEquals( 4, service.toIndex(T("2019-02-01T03:15:00Z")));
		
		assertEquals(14, service.toIndex(T("2019-02-01T11:00:00Z")));
	}
	
	@Test
	public void testToIndex_BtwSiblings() {
		assertEquals(7, service.toIndex(T("2019-02-01T06:12:47Z")));
		assertEquals(8, service.toIndex(T("2019-02-01T07:41:12Z")));
	}

}
