package ru.prolib.bootes.lib.robo.s3.rh;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.bootes.lib.report.blockrep.Block;
import ru.prolib.bootes.lib.report.blockrep.BlockReport;
import ru.prolib.bootes.lib.report.blockrep.IBlockReportStorage;
import ru.prolib.bootes.lib.robo.s3.S3RobotState;
import ru.prolib.bootes.lib.robo.s3.S3Speculation;

public class S3BlockReportHandlerTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private S3RobotState state;
	private IBlockReportStorage brsMock;
	private S3BlockReportHandler service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		brsMock = control.createMock(IBlockReportStorage.class);
		state = new S3RobotState();
		service = new S3BlockReportHandler(state, brsMock);
	}
	
	@Test
	public void testRobotStarted() {
		control.replay();
		
		service.robotStarted();
		
		control.verify();
	}
	
	@Test
	public void testAccountSelected() {
		control.replay();
		
		service.accountSelected();
		
		control.verify();
	}
	
	@Test
	public void testContractSelected() {
		control.replay();
		
		service.contractSelected();
		
		control.verify();
	}
	
	@Test
	public void testSessionDataAvailable() {
		control.replay();
		
		service.sessionDataAvailable();
		
		control.verify();
	}
	
	@Test
	public void testRiskManagementUpdate() {
		control.replay();
		
		service.riskManagementUpdate();
		
		control.verify();
	}
	
	@Test
	public void testSpeculationOpened() {
		S3Speculation spec = new S3Speculation(null);
		spec.setEntryPoint(Tick.ofTrade(T("2019-04-22T09:19:40Z"), of("120.05"), of(10L)));
		BlockReport expected = new BlockReport(new Block("OPEN", of("120.05"), T("2019-04-22T09:19:40Z")));
		brsMock.addReport(expected);
		control.replay();
		state.setActiveSpeculation(spec);
		assertNull(service.getCurrReport());
		
		service.speculationOpened();
		
		control.verify();
		assertEquals(expected, service.getCurrReport());
	}
	
	@Test
	public void testSpeculationUpdate() {
		S3Speculation spec = new S3Speculation(null);
		spec.setTakeProfit(of("128261.972"));
		spec.setStopLoss(of("127828.767"));
		spec.setBreakEven(of("72621.662"));
		service.setCurrReport(new BlockReport(new Block("OPEN", of("120.05"), T("2019-04-22T09:19:40Z"))));
		control.replay();
		state.setActiveSpeculation(spec);
		
		service.speculationUpdate();
		
		control.verify();
		BlockReport expected = new BlockReport(new Block("OPEN", of("120.05"), T("2019-04-22T09:19:40Z")));
		expected.setBlock(new Block("TAKE_PROFIT", of("128261.972"), null));
		expected.setBlock(new Block("STOP_LOSS", of("127828.767"), null));
		expected.setBlock(new Block("BREAK_EVEN", of("72621.662"), null));
		assertEquals(expected, service.getCurrReport());
	}
	
	@Test
	public void testSpeculationClosed() {
		S3Speculation spec = new S3Speculation(null);
		spec.setExitPoint(Tick.ofTrade(T("2019-04-22T10:00:00Z"), of("1202.92"), of(1L)));
		spec.setTakeProfit(of("1282.05"));
		spec.setStopLoss(  of("1278.76"));
		spec.setBreakEven( of("1262.62"));
		BlockReport actual = new BlockReport(new Block("OPEN", of("1220.05"), T("2019-04-22T09:19:40Z")));
		service.setCurrReport(actual);
		control.replay();
		state.setActiveSpeculation(spec);
		
		service.speculationClosed();
		
		control.verify();
		assertNull(service.getCurrReport());
		
		BlockReport expected = new BlockReport(new Block("OPEN", of("1220.05"), T("2019-04-22T09:19:40Z")));
		expected.setBlock(new Block("TAKE_PROFIT", of("1282.05"), T("2019-04-22T10:00:00Z")));
		expected.setBlock(new Block("STOP_LOSS",   of("1278.76"), T("2019-04-22T10:00:00Z")));
		expected.setBlock(new Block("BREAK_EVEN",  of("1262.62"), T("2019-04-22T10:00:00Z")));
		expected.setBlock(new Block("CLOSE",       of("1202.92"), T("2019-04-22T10:00:00Z")));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSessionDataCleanup() {
		control.replay();
		
		service.sessionDataCleanup();
		
		control.verify();
	}

	@Test
	public void testRobotStopped() {
		control.replay();
		
		service.robotStopped();
		
		control.verify();
	}
	
	@Test
	public void testOrderFinished() {
		Order orderMock = control.createMock(Order.class);
		control.replay();
		
		service.orderFinished(orderMock);
		
		control.verify();
	}

}
