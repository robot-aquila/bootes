package ru.prolib.bootes.lib.report.msr2;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.bootes.lib.report.msr2.IBlock;
import ru.prolib.bootes.lib.report.msr2.IReport;

public class ReportUtilsTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private IBlock blockMock1, blockMock2, blockMock3;
	private IReport reportMock;
	private ITimeIndexMapper timMock;
	private ReportUtils service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		blockMock1 = control.createMock(IBlock.class);
		blockMock2 = control.createMock(IBlock.class);
		blockMock3 = control.createMock(IBlock.class);
		reportMock = control.createMock(IReport.class);
		timMock = control.createMock(ITimeIndexMapper.class);
		service = new ReportUtils();
	}
	
	@Test
	public void testGetInstance() {
		ReportUtils actual = ReportUtils.getInstance();
		assertNotNull(actual);
		assertSame(ReportUtils.getInstance(), actual);
		assertSame(ReportUtils.getInstance(), actual);
	}
	
	@Test
	public void testGetAveragePrice() {
		expect(blockMock1.getPrice()).andStubReturn(of("128.934"));
		expect(blockMock2.getPrice()).andStubReturn(of("115.053"));
		expect(blockMock3.getPrice()).andStubReturn(of("130.117"));
		List<IBlock> blocks = new ArrayList<>();
		blocks.add(blockMock1);
		blocks.add(blockMock2);
		blocks.add(blockMock3);
		expect(reportMock.getBlocks()).andStubReturn(blocks);
		control.replay();
		
		CDecimal actual = service.getAveragePrice(reportMock);
		
		control.verify();
		assertEquals(of("124.701"), actual);
	}
	
	@Test
	public void testGetAveragePrice_NoPrice() {
		expect(blockMock1.getPrice()).andStubReturn(null);
		expect(blockMock2.getPrice()).andStubReturn(null);
		expect(blockMock3.getPrice()).andStubReturn(null);
		List<IBlock> blocks = new ArrayList<>();
		blocks.add(blockMock1);
		blocks.add(blockMock2);
		blocks.add(blockMock3);
		expect(reportMock.getBlocks()).andStubReturn(blocks);
		control.replay();
		
		CDecimal actual = service.getAveragePrice(reportMock);
		
		control.verify();
		assertNull(actual);
	}
	
	@Test
	public void testGetAveragePrice_Partial() {
		expect(blockMock1.getPrice()).andStubReturn(of("128.934"));
		expect(blockMock2.getPrice()).andStubReturn(null);
		expect(blockMock3.getPrice()).andStubReturn(of("130.117"));
		List<IBlock> blocks = new ArrayList<>();
		blocks.add(blockMock1);
		blocks.add(blockMock2);
		blocks.add(blockMock3);
		expect(reportMock.getBlocks()).andStubReturn(blocks);
		control.replay();
		
		CDecimal actual = service.getAveragePrice(reportMock);
		
		control.verify();
		assertEquals(of("129.526"), actual);
	}
	
	@Test
	public void testGetAverageIndex2() {
		expect(blockMock1.getTime()).andStubReturn(T("2019-01-30T19:10:00Z"));
		expect(blockMock2.getTime()).andStubReturn(T("2019-01-30T19:15:00Z"));
		expect(blockMock3.getTime()).andStubReturn(T("2019-01-30T19:30:00Z"));
		expect(timMock.toIndex(T("2019-01-30T19:10:00Z"))).andReturn(15);
		expect(timMock.toIndex(T("2019-01-30T19:15:00Z"))).andReturn(16);
		expect(timMock.toIndex(T("2019-01-30T19:30:00Z"))).andReturn(19);
		List<IBlock> blocks = new ArrayList<>();
		blocks.add(blockMock1);
		blocks.add(blockMock2);
		blocks.add(blockMock3);
		expect(reportMock.getBlocks()).andStubReturn(blocks);
		control.replay();
		
		Integer actual = service.getAverageIndex(reportMock, timMock);
		
		control.verify();
		assertEquals(Integer.valueOf(17), actual);
	}
	
	@Test
	public void testGetAverageIndex2_NoTime() {
		expect(blockMock1.getTime()).andStubReturn(null);
		expect(blockMock2.getTime()).andStubReturn(null);
		expect(blockMock3.getTime()).andStubReturn(null);
		List<IBlock> blocks = new ArrayList<>();
		blocks.add(blockMock1);
		blocks.add(blockMock2);
		blocks.add(blockMock3);
		expect(reportMock.getBlocks()).andStubReturn(blocks);
		control.replay();
		
		Integer actual = service.getAverageIndex(reportMock, timMock);
		
		control.verify();
		assertNull(actual);
	}
	
	@Test
	public void testGetAverageIndex2_Partial() {
		expect(blockMock1.getTime()).andStubReturn(T("2019-01-30T19:10:00Z"));
		expect(blockMock2.getTime()).andStubReturn(null);
		expect(blockMock2.getPrice()).andStubReturn(null);
		expect(blockMock3.getTime()).andStubReturn(T("2019-01-30T19:40:00Z"));
		expect(timMock.toIndex(T("2019-01-30T19:10:00Z"))).andReturn(15);
		expect(timMock.toIndex(T("2019-01-30T19:40:00Z"))).andReturn(21);
		List<IBlock> blocks = new ArrayList<>();
		blocks.add(blockMock1);
		blocks.add(blockMock2);
		blocks.add(blockMock3);
		expect(reportMock.getBlocks()).andStubReturn(blocks);
		control.replay();
		
		Integer actual = service.getAverageIndex(reportMock, timMock);
		
		control.verify();
		assertEquals(Integer.valueOf(18), actual);
	}
	
	@Test
	public void testGetAverageIndex3() {
		expect(blockMock1.getTime()).andStubReturn(T("2019-01-30T19:10:00Z"));
		expect(blockMock2.getTime()).andStubReturn(T("2019-01-30T19:15:00Z"));
		expect(blockMock3.getTime()).andStubReturn(T("2019-01-30T19:30:00Z"));
		expect(timMock.toIndex(T("2019-01-30T19:10:00Z"))).andReturn(15);
		expect(timMock.toIndex(T("2019-01-30T19:15:00Z"))).andReturn(16);
		expect(timMock.toIndex(T("2019-01-30T19:30:00Z"))).andReturn(19);
		List<IBlock> blocks = new ArrayList<>();
		blocks.add(blockMock1);
		blocks.add(blockMock2);
		blocks.add(blockMock3);
		expect(reportMock.getBlocks()).andStubReturn(blocks);
		control.replay();
		
		Integer actual = service.getAverageIndex(reportMock, timMock, 18);
		
		control.verify();
		assertEquals(Integer.valueOf(17), actual);
	}
	
	@Test
	public void testGetAverageIndex3_NoTime() {
		expect(blockMock1.getTime()).andStubReturn(null);
		expect(blockMock1.getPrice()).andStubReturn(null);
		expect(blockMock2.getTime()).andStubReturn(null);
		expect(blockMock2.getPrice()).andStubReturn(null);
		expect(blockMock3.getTime()).andStubReturn(null);
		expect(blockMock3.getPrice()).andStubReturn(null);
		List<IBlock> blocks = new ArrayList<>();
		blocks.add(blockMock1);
		blocks.add(blockMock2);
		blocks.add(blockMock3);
		expect(reportMock.getBlocks()).andStubReturn(blocks);
		control.replay();
		
		Integer actual = service.getAverageIndex(reportMock, timMock, 18);
		
		control.verify();
		assertNull(actual);
	}
	
	@Test
	public void testGetAverageIndex3_Partial() {
		expect(blockMock1.getTime()).andStubReturn(null);
		expect(blockMock1.getPrice()).andStubReturn(of("123.456"));
		expect(blockMock2.getTime()).andStubReturn(T("2019-01-30T19:15:00Z"));
		expect(blockMock3.getTime()).andStubReturn(null);
		expect(blockMock3.getPrice()).andStubReturn(null);
		expect(timMock.toIndex(T("2019-01-30T19:15:00Z"))).andReturn(21);
		List<IBlock> blocks = new ArrayList<>();
		blocks.add(blockMock1);
		blocks.add(blockMock2);
		blocks.add(blockMock3);
		expect(reportMock.getBlocks()).andStubReturn(blocks);
		control.replay();
		
		Integer actual = service.getAverageIndex(reportMock, timMock, 18);
		
		control.verify();
		assertEquals(Integer.valueOf(20), actual);
	}

}
