package ru.prolib.bootes.lib.service.ars;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class ARSCompositeHandlerTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private IMocksControl control;
	private ARSAction actMock1, actMock2, actMock3, actMock4, actMock5, actMock6;
	private List<ARSAction> actList1, actList2, actList3, actList4;
	private ARSCompositeHandler service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		actMock1 = control.createMock(ARSAction.class);
		actMock2 = control.createMock(ARSAction.class);
		actMock3 = control.createMock(ARSAction.class);
		actMock4 = control.createMock(ARSAction.class);
		actMock5 = control.createMock(ARSAction.class);
		actMock6 = control.createMock(ARSAction.class);
		actList1 = new ArrayList<>();
		actList1.add(actMock1);
		actList1.add(actMock2);
		actList1.add(actMock3);
		actList2 = new ArrayList<>();
		actList2.add(actMock4);
		actList2.add(actMock5);
		actList2.add(actMock6);
		actList3 = new ArrayList<>();
		actList3.add(actMock4);
		actList3.add(actMock1);
		actList4 = new ArrayList<>();
		actList4.add(actMock2);
		actList4.add(actMock5);
		actList4.add(actMock3);
		service = new ARSCompositeHandler("MySERVICE", actList1, actList2);
	}
	
	@Test
	public void testCtor() {
		assertEquals("MySERVICE", service.getHanderID());
		assertSame(actList1, service.getStartupActions());
		assertSame(actList2, service.getShutdownActions());
	}

	@Test
	public void testStartup() throws Throwable {
		actMock1.run();
		actMock2.run();
		actMock3.run();
		control.replay();
		
		service.startup();
		
		control.verify();
	}
	
	@Test
	public void testStartup_OnError() throws Throwable {
		Exception exception = new Exception("Test error");
		actMock1.run();
		actMock2.run();
		expectLastCall().andThrow(exception);
		control.replay();
		
		try {
			service.startup();
			fail("Expected: " + Exception.class.getSimpleName());
		} catch ( Exception e ) {
			assertSame(exception, e);
		}
		control.verify();
	}
	
	@Test
	public void testShutdown() throws Throwable {
		actMock4.run();
		actMock5.run();
		actMock6.run();
		control.replay();
		
		service.shutdown();
		
		control.verify();
	}
	
	@Test
	public void testShutdown_OnError() throws Throwable {
		Exception exception = new Exception("Test error");
		actMock4.run();
		actMock5.run();
		expectLastCall().andThrow(exception);
		control.replay();
		
		try {
			service.shutdown();
			fail("Expected: " + Exception.class.getSimpleName());
		} catch ( Exception e ) {
			assertSame(exception, e);
		}
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new ARSCompositeHandler("MySERVICE", actList1, actList2)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<String> vHID = new Variant<>("MySERVICE", "foobar");
		Variant<List<ARSAction>> vSTAL = new Variant<>(vHID, actList1, actList3),
				vSHUL = new Variant<>(vSTAL, actList2, actList4);
		Variant<?> iterator = vSHUL;
		int foundCnt = 0;
		ARSCompositeHandler x, found = null;
		do {
			x = new ARSCompositeHandler(vHID.get(), vSTAL.get(), vSHUL.get());
			if ( service.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("MySERVICE", found.getHanderID());
		assertEquals(actList1, found.getStartupActions());
		assertEquals(actList2, found.getShutdownActions());
	}

}
