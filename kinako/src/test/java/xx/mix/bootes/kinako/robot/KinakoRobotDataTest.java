package xx.mix.bootes.kinako.robot;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import xx.mix.bootes.kinako.service.VVSignal;

public class KinakoRobotDataTest {
	@Rule
	public ExpectedException eex = ExpectedException.none();
	
	private IMocksControl control;
	private KinakoRobotData service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service = new KinakoRobotData();
	}
	
	@Test
	public void testGetCurrentSignal_ThrowsIfNotDefined() {
		eex.expect(NullPointerException.class);
		eex.expectMessage("Current signal was not defined");
		
		service.getCurrentSignal();
	}

	@Test
	public void testGetCurrentSignal() {
		VVSignal signalMock = control.createMock(VVSignal.class);
		assertSame(service, service.setCurrentSignal(signalMock));
		assertSame(signalMock, service.getCurrentSignal());
	}

}
