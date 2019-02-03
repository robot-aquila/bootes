package ru.prolib.bootes.lib.report.msr2.swing;

import static org.easymock.EasyMock.*;
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
import ru.prolib.bootes.lib.report.msr2.IReport;
import ru.prolib.bootes.lib.report.msr2.ITimeIndexMapper;
import ru.prolib.bootes.lib.report.msr2.ReportUtils;

public class ReportRendererElemTest {
	private IMocksControl control;
	private CategoryAxisDisplayMapper camStub;
	private ValueAxisDisplayMapper vamStub;
	private ReportUtils ruMock;
	private Graphics2D deviceMock;
	private IReport reportMock;
	private ITimeIndexMapper timMock;
	private BCDisplayContext context;
	private ReportRendererElem service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		camStub = new CategoryAxisDisplayMapperHR(0, 100, 10, of("8.00"));
		vamStub = new ValueAxisDisplayMapperVUV(0, 100, new Range<>(of("100.00"), of("200.00")));
		ruMock = control.createMock(ReportUtils.class);
		deviceMock = control.createMock(Graphics2D.class);
		reportMock = control.createMock(IReport.class);
		timMock = control.createMock(ITimeIndexMapper.class);
		context = new BCDisplayContextImpl(camStub, vamStub, null, null);
		service = new ReportRendererElem(ruMock);
	}

	@Test (expected=IllegalStateException.class)
	public void testPaintReport_ThrowsIfUnsupportedAxisDirection() {
		expect(camStub.getAxisDirection()).andStubReturn(AxisDirection.UP);
		control.replay();
		
		service.paintReport(context, deviceMock, reportMock, timMock);
	}
	
	@Test
	public void testPaintReport_IfNoTimeThenIndexAtCenter() {
		expect(ruMock.getAverageIndex(reportMock, timMock)).andReturn(null);
		expect(ruMock.getAveragePrice(reportMock)).andReturn(of("120.94"));
		deviceMock.setColor(Color.BLACK);
		deviceMock.fillOval(44, 79, 5, 5);
		control.replay();
		
		service.paintReport(context, deviceMock, reportMock, timMock);
		
		control.verify();
	}
	
	@Test
	public void testPaintReport_UseMaxIndexIfCalculatedIsGtMax() {
		expect(ruMock.getAverageIndex(reportMock, timMock)).andReturn(130);
		expect(ruMock.getAveragePrice(reportMock)).andReturn(of("120.94"));
		deviceMock.setColor(Color.BLACK);
		deviceMock.fillOval(76, 79, 5, 5);
		control.replay();
		
		service.paintReport(context, deviceMock, reportMock, timMock);
		
		control.verify();
	}
	
	@Test
	public void testPaintReport_UseMinIndexIfCalculatedIsLeMin() {
		expect(ruMock.getAverageIndex(reportMock, timMock)).andReturn(95);
		expect(ruMock.getAveragePrice(reportMock)).andReturn(of("120.94"));
		deviceMock.setColor(Color.BLACK);
		deviceMock.fillOval(4, 79, 5, 5);
		control.replay();
		
		service.paintReport(context, deviceMock, reportMock, timMock);
		
		control.verify();
	}
	
	@Test
	public void testPaintReport_IfNoPriceThenAtValueCenter() {
		expect(ruMock.getAverageIndex(reportMock, timMock)).andReturn(101);
		expect(ruMock.getAveragePrice(reportMock)).andReturn(null);
		deviceMock.setColor(Color.BLACK);
		deviceMock.fillOval(12, 49, 5, 5);
		control.replay();
		
		service.paintReport(context, deviceMock, reportMock, timMock);
		
		control.verify();
	}
	
	@Test
	public void testPaintReport_UseMaxValueIfCalculatedIsGtMax() {
		expect(ruMock.getAverageIndex(reportMock, timMock)).andReturn(101);
		expect(ruMock.getAveragePrice(reportMock)).andReturn(of("245.20"));
		deviceMock.setColor(Color.BLACK);
		deviceMock.fillOval(12, 0, 5, 5);
		control.replay();
		
		service.paintReport(context, deviceMock, reportMock, timMock);
		
		control.verify();
	}
	
	@Test
	public void testPaintReport_UseMinValueIfCalculatedIsLtMin() {
		expect(ruMock.getAverageIndex(reportMock, timMock)).andReturn(101);
		expect(ruMock.getAveragePrice(reportMock)).andReturn(of("99.75"));
		deviceMock.setColor(Color.BLACK);
		deviceMock.fillOval(12, 99, 5, 5);
		control.replay();
		
		service.paintReport(context, deviceMock, reportMock, timMock);
		
		control.verify();
	}
	
	@Test
	public void testPaintReport() {
		expect(ruMock.getAverageIndex(reportMock, timMock)).andReturn(103);
		expect(ruMock.getAveragePrice(reportMock)).andReturn(of("115.29"));
		deviceMock.setColor(Color.BLACK);
		deviceMock.fillOval(28, 84, 5, 5);
		control.replay();
		
		service.paintReport(context, deviceMock, reportMock, timMock);
		
		control.verify();
	}

}
