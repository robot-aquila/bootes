package ru.prolib.bootes.lib.report.msr2.swing;

import java.awt.Graphics2D;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContextImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.bootes.lib.report.msr2.IReport;
import ru.prolib.bootes.lib.report.msr2.IReportStorage;
import ru.prolib.bootes.lib.report.msr2.ITimeIndexMapper;
import ru.prolib.bootes.lib.report.msr2.TimeIndexMapperTS;

public class MSR2LayerTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private ITimeIndexMapper timMock;
	private IReportStorage storageMock;
	private ReportRenderer rendererMock;
	private IReport reportMock1, reportMock2, reportMock3;
	private Graphics2D deviceMock;
	private CategoryAxisDisplayMapper cadmMock;
	private BCDisplayContext context;
	private MSR2Layer service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		timMock = control.createMock(ITimeIndexMapper.class);
		storageMock = control.createMock(IReportStorage.class);
		rendererMock = control.createMock(ReportRenderer.class);
		reportMock1 = control.createMock(IReport.class);
		reportMock2 = control.createMock(IReport.class);
		reportMock3 = control.createMock(IReport.class);
		deviceMock = control.createMock(Graphics2D.class);
		cadmMock = control.createMock(CategoryAxisDisplayMapper.class);
		service = new MSR2Layer("foo", storageMock, timMock, rendererMock);
		context = new BCDisplayContextImpl(cadmMock, null, null, null);
	}
	
	@Test
	public void testCtor4_TIM() {
		assertEquals("foo", service.getId());
		assertSame(storageMock, service.getStorage());
		assertSame(rendererMock, service.getRenderer());
		assertSame(timMock, service.getTimeIndexMapper());
	}
	
	@Test
	public void testCtor4_DefaultTIM() {
		TSeries<?> basis = control.createMock(TSeries.class);
		service = new MSR2Layer("bar", storageMock, basis, rendererMock);
		
		assertEquals("bar", service.getId());
		assertSame(storageMock, service.getStorage());
		assertSame(rendererMock, service.getRenderer());
		TimeIndexMapperTS tim = (TimeIndexMapperTS) service.getTimeIndexMapper();
		assertSame(basis, tim.getBasis());
	}
	
	@Test
	public void testCtor3_DefaultRenderer() {
		TSeries<?> basis = control.createMock(TSeries.class);
		service = new MSR2Layer("bar", storageMock, basis);
		
		assertEquals("bar", service.getId());
		assertSame(storageMock, service.getStorage());
		ReportRendererElem renderer = (ReportRendererElem) service.getRenderer();
		assertNotNull(renderer);
		TimeIndexMapperTS tim = (TimeIndexMapperTS) service.getTimeIndexMapper();
		assertSame(basis, tim.getBasis());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testPaint_ThrowsIfUnsupportedDirection() throws Exception {
		expect(cadmMock.getAxisDirection()).andReturn(AxisDirection.UP);
		control.replay();
		
		service.paint(context, deviceMock);
	}

	@Test
	public void testPaint() {
		expect(cadmMock.getAxisDirection()).andStubReturn(AxisDirection.RIGHT);
		expect(cadmMock.getFirstVisibleCategory()).andStubReturn(10);
		expect(cadmMock.getLastVisibleCategory()).andStubReturn(14);
		expect(timMock.toIntervalStart(10)).andReturn(T("2019-01-28T09:40:00Z"));
		expect(timMock.toIntervalEnd(14)).andReturn(T("2019-01-28T10:05:00Z"));
		List<IReport> reports = new ArrayList<>();
		reports.add(reportMock1);
		reports.add(reportMock2);
		reports.add(reportMock3);
		expect(storageMock.getReports(Interval.of(
				T("2019-01-28T09:40:00Z"),
				T("2019-01-28T10:05:00Z")
			))).andReturn(reports);
		expect(rendererMock.paintReport(context, deviceMock, reportMock1, timMock)).andReturn(true);
		expect(rendererMock.paintReport(context, deviceMock, reportMock2, timMock)).andReturn(true);
		expect(rendererMock.paintReport(context, deviceMock, reportMock3, timMock)).andReturn(true);
		control.replay();
		
		service.paint(context, deviceMock);
		
		control.verify();
	}

}
