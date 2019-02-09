package ru.prolib.bootes.lib.report.blockrep.ui;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.awt.Color;
import java.awt.Graphics2D;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContextImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapperHR;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapperVUV;
import ru.prolib.bootes.lib.report.blockrep.IBlockReport;
import ru.prolib.bootes.lib.report.blockrep.ITimeIndexMapper;
import ru.prolib.bootes.lib.report.blockrep.BlockReportUtils;
import ru.prolib.bootes.lib.report.blockrep.ui.BlockReportRendererElem;
import ru.prolib.bootes.lib.ui.swing.GPrim;

public class BlockReportRendererElemTest {
	private static Color FILL_COLOR = new Color(127, 255, 212);
	private static Color BORDER_COLOR = new Color(25, 25, 112);
	
	private IMocksControl control;
	private CategoryAxisDisplayMapper camStub;
	private ValueAxisDisplayMapper vamStub;
	private BlockReportUtils ruMock;
	private GPrim gprimMock;
	private Graphics2D deviceMock;
	private IBlockReport reportMock;
	private ITimeIndexMapper timMock;
	private BCDisplayContext context;
	private BlockReportRendererElem service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		camStub = new CategoryAxisDisplayMapperHR(0, 100, 10, of("8.00"));
		vamStub = new ValueAxisDisplayMapperVUV(0, 100, new Range<>(of("100.00"), of("200.00")));
		ruMock = control.createMock(BlockReportUtils.class);
		gprimMock = control.createMock(GPrim.class);
		deviceMock = control.createMock(Graphics2D.class);
		reportMock = control.createMock(IBlockReport.class);
		timMock = control.createMock(ITimeIndexMapper.class);
		context = new BCDisplayContextImpl(camStub, vamStub, null, null);
		service = new BlockReportRendererElem(ruMock, gprimMock);
	}

	@Test (expected=IllegalStateException.class)
	public void testPaintReport_ThrowsIfUnsupportedAxisDirection() {
		expect(camStub.getAxisDirection()).andStubReturn(AxisDirection.UP);
		control.replay();
		
		service.paintReport(context, deviceMock, reportMock, timMock);
	}
	
	@Test
	public void testPaintReport_IfNoTimeThenIndexAtCenter() {
		expect(ruMock.getAverageIndex(reportMock, timMock, 109)).andReturn(null);
		expect(ruMock.getAveragePrice(reportMock)).andReturn(of("120.94"));
		gprimMock.drawCircle(deviceMock, 44, 79, 6, FILL_COLOR, BORDER_COLOR);
		control.replay();
		
		assertTrue(service.paintReport(context, deviceMock, reportMock, timMock));
		
		control.verify();
	}
	
	@Test
	public void testPaintReport_UseMaxIndexIfCalculatedIsGtMax() {
		expect(ruMock.getAverageIndex(reportMock, timMock, 109)).andReturn(130);
		expect(ruMock.getAveragePrice(reportMock)).andReturn(of("120.94"));
		gprimMock.drawCircle(deviceMock, 76, 79, 6, FILL_COLOR, BORDER_COLOR);
		control.replay();
		
		assertTrue(service.paintReport(context, deviceMock, reportMock, timMock));
		
		control.verify();
	}
	
	@Test
	public void testPaintReport_UseMinIndexIfCalculatedIsLeMin() {
		expect(ruMock.getAverageIndex(reportMock, timMock, 109)).andReturn(95);
		expect(ruMock.getAveragePrice(reportMock)).andReturn(of("120.94"));
		gprimMock.drawCircle(deviceMock, 4, 79, 6, FILL_COLOR, BORDER_COLOR);
		control.replay();
		
		assertTrue(service.paintReport(context, deviceMock, reportMock, timMock));
		
		control.verify();
	}
	
	@Test
	public void testPaintReport_IfNoPriceThenAtValueCenter() {
		expect(ruMock.getAverageIndex(reportMock, timMock, 109)).andReturn(101);
		expect(ruMock.getAveragePrice(reportMock)).andReturn(null);
		gprimMock.drawCircle(deviceMock, 12, 49, 6, FILL_COLOR, BORDER_COLOR);
		control.replay();
		
		assertTrue(service.paintReport(context, deviceMock, reportMock, timMock));
		
		control.verify();
	}
	
	@Test
	public void testPaintReport_UseMaxValueIfCalculatedIsGtMax() {
		expect(ruMock.getAverageIndex(reportMock, timMock, 109)).andReturn(101);
		expect(ruMock.getAveragePrice(reportMock)).andReturn(of("245.20"));
		gprimMock.drawCircle(deviceMock, 12, 0, 6, FILL_COLOR, BORDER_COLOR);
		control.replay();
		
		assertTrue(service.paintReport(context, deviceMock, reportMock, timMock));
		
		control.verify();
	}
	
	@Test
	public void testPaintReport_UseMinValueIfCalculatedIsLtMin() {
		expect(ruMock.getAverageIndex(reportMock, timMock, 109)).andReturn(101);
		expect(ruMock.getAveragePrice(reportMock)).andReturn(of("99.75"));
		gprimMock.drawCircle(deviceMock, 12, 99, 6, FILL_COLOR, BORDER_COLOR);
		control.replay();
		
		assertTrue(service.paintReport(context, deviceMock, reportMock, timMock));
		
		control.verify();
	}
	
	@Test
	public void testPaintReport() {
		expect(ruMock.getAverageIndex(reportMock, timMock, 109)).andReturn(103);
		expect(ruMock.getAveragePrice(reportMock)).andReturn(of("115.29"));
		gprimMock.drawCircle(deviceMock, 28, 84, 6, FILL_COLOR, BORDER_COLOR);
		control.replay();
		
		assertTrue(service.paintReport(context, deviceMock, reportMock, timMock));
		
		control.verify();
	}

}
