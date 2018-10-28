package ru.prolib.bootes.lib.service.ars;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class ARSHandlerBuilderTest {
	private IMocksControl control;
	private Runnable rMock1, rMock2;
	private ARSAction aMock1, aMock2;
	private ARSHandlerBuilder service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rMock1 = control.createMock(Runnable.class);
		rMock2 = control.createMock(Runnable.class);
		aMock1 = control.createMock(ARSAction.class);
		aMock2 = control.createMock(ARSAction.class);
		service = new ARSHandlerBuilder();
	}

	@Test
	public void testBuild() {
		assertSame(service, service.withID("foobar"));
		assertSame(service, service.addStartupAction(aMock1));
		assertSame(service, service.addStartupAction(rMock1));
		assertSame(service, service.addShutdownAction(aMock2));
		assertSame(service, service.addShutdownAction(rMock2));
		
		ARSHandler actual = service.build();
		
		List<ARSAction> expectedStartupActions = new ArrayList<>();
		expectedStartupActions.add(aMock1);
		expectedStartupActions.add(new ARSActionR(rMock1));
		List<ARSAction> expectedShutdownActions = new ArrayList<>();
		expectedShutdownActions.add(aMock2);
		expectedShutdownActions.add(new ARSActionR(rMock2));
		ARSHandler expected = new ARSCompositeHandler("foobar", expectedStartupActions, expectedShutdownActions);
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testBuild_ThrowsIfNoHandlerID() {
		service.addStartupAction(aMock1);
		service.addStartupAction(rMock1);
		service.addShutdownAction(aMock2);
		service.addShutdownAction(rMock2);
		
		service.build();
	}

}
