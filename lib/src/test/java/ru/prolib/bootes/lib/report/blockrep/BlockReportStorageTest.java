package ru.prolib.bootes.lib.report.blockrep;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.bootes.lib.report.blockrep.Block;
import ru.prolib.bootes.lib.report.blockrep.IBlockReport;
import ru.prolib.bootes.lib.report.blockrep.BlockReport;
import ru.prolib.bootes.lib.report.blockrep.BlockReportStorage;

public class BlockReportStorageTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IBlockReport report1, report2, report3, report4, report5;
	private BlockReportStorage service;

	@Before
	public void setUp() throws Exception {
		report1 = new BlockReport(new Block("foo", of("12.03"), T("2019-02-01T23:03:00Z")));
		report1.setBlock(new Block("bar", of("12.06"), null));
		report1.setBlock(new Block("buz", of("12.07"), T("2019-02-01T23:45:19Z")));
		report2 = new BlockReport(new Block("foo", of("11.15"), T("2019-02-01T23:15:00Z")));
		report3 = new BlockReport(new Block("foo", of("10.12"), T("2019-02-01T23:30:00Z")));
		report4 = new BlockReport(new Block("foo", of("11.26"), T("2019-02-01T23:49:00Z")));
		report4.setBlock(new Block("bar", of("11.45"), T("2019-02-01T23:55:00Z")));
		report4.setBlock(new Block("buz", of("11.92"), T("2019-02-02T01:00:00Z")));
		report5 = new BlockReport(new Block("foo", of("10.81"), T("2019-02-02T03:19:01Z")));
		service = new BlockReportStorage();
		service.addReport(report1);
		service.addReport(report2);
		service.addReport(report3);
		service.addReport(report4);
		service.addReport(report5);
	}

	@Test
	public void testGetReports_Case1() {
		
		List<IBlockReport> actual = service.getReports(Interval.of(T("2019-01-01T00:00:00Z"), T("2020-01-01T00:00:00Z")));
		
		List<IBlockReport> expected = new ArrayList<>();
		expected.add(report1);
		expected.add(report2);
		expected.add(report3);
		expected.add(report4);
		expected.add(report5);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetReports_Case2() {
		
		List<IBlockReport> actual = service.getReports(Interval.of(T("2019-02-01T23:47:00Z"), T("2020-01-01T00:00:00Z")));
		
		List<IBlockReport> expected = new ArrayList<>();
		expected.add(report4);
		expected.add(report5);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetReports_Case3() {
		
		List<IBlockReport> actual = service.getReports(Interval.of(T("2019-02-01T23:30:00Z"), T("2019-02-02T00:00:00Z")));
		
		List<IBlockReport> expected = new ArrayList<>();
		expected.add(report1);
		expected.add(report3);
		expected.add(report4);
		assertEquals(expected, actual);
	}

}
