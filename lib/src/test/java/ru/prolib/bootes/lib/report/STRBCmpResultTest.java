package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.difflib.patch.AbstractDelta;

import ru.prolib.aquila.core.utils.Variant;

public class STRBCmpResultTest {
	static STRBHeader header1, header2;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		header1 = new STRBHeader("foo", "bar");
		header2 = new STRBHeader("zoo", "gap");
	}
	
	IMocksControl control;
	AbstractDelta<String> deltaMock1, deltaMock2, deltaMock3;
	List<AbstractDelta<String>> deltas1, deltas2;
	STRBCmpResult service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = EasyMock.createStrictControl();
		deltaMock1 = control.createMock(AbstractDelta.class);
		deltaMock2 = control.createMock(AbstractDelta.class);
		deltaMock3 = control.createMock(AbstractDelta.class);
		deltas1 = Arrays.asList(deltaMock1, deltaMock2);
		deltas2 = Arrays.asList(deltaMock3);
		service = new STRBCmpResult(header1, STRBCmpResultType.REPORT_MISMATCH, "XXX", deltas1);
	}
	
	@Test
	public void testCtor4() {
		assertEquals(header1, service.getHeader());
		assertEquals(STRBCmpResultType.REPORT_MISMATCH, service.getType());
		assertEquals("XXX", service.getDescription());
		assertEquals(deltas1, service.getDeltas());
	}
	
	@Test
	public void testCtor3() {
		service = new STRBCmpResult(header2, STRBCmpResultType.IDENTICAL, "YYY");
		assertEquals(header2, service.getHeader());
		assertEquals(STRBCmpResultType.IDENTICAL, service.getType());
		assertEquals("YYY", service.getDescription());
		assertEquals(Arrays.asList(), service.getDeltas());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(220147909, 91)
				.append(header1)
				.append(STRBCmpResultType.REPORT_MISMATCH)
				.append("XXX")
				.append(deltas1)
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<STRBHeader> v_head = new Variant<>(header1, header2);
		Variant<STRBCmpResultType> v_type = new Variant<STRBCmpResultType>(v_head)
				.add(STRBCmpResultType.REPORT_MISMATCH).add(STRBCmpResultType.IDENTICAL);
		Variant<String> v_desc = new Variant<>(v_type, "XXX", "YYY");
		Variant<List<AbstractDelta<String>>> v_delt = new Variant<>(v_desc, deltas1, deltas2);
		Variant<?> iterator = v_delt;
		int found_cnt = 0;
		STRBCmpResult x, found = null;
		do {
			x = new STRBCmpResult(v_head.get(), v_type.get(), v_desc.get(), v_delt.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(header1, found.getHeader());
		assertEquals(STRBCmpResultType.REPORT_MISMATCH, found.getType());
		assertEquals("XXX", found.getDescription());
		assertEquals(deltas1, found.getDeltas());
	}

}
