package ru.prolib.bootes.tsgr001a;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ru.prolib.bootes.lib.report.ReportComparator;
import ru.prolib.bootes.lib.report.STRCmpResult;

public class TSGR001A_IT {
	static final File dataDir = new File("./../shared/canned-data");
	static final File reportDir = new File("tmp/it-reports");
	
	static void assertReports(File expected, File actual) throws Exception {
		STRCmpResult result = ReportComparator.getInstance().compare(expected, actual);
		assertTrue(result.toString(), result.identical());
	}
	
	static String[] args(String... args) {
		List<String> arg_list = new ArrayList<>(Arrays.asList(args));
		return arg_list.toArray(new String[0]);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if ( reportDir.exists() ) {
			FileUtils.forceDelete(reportDir);
		}
		reportDir.mkdirs();
	}

	@Before
	public void setUp() throws Exception {
		
	}
	
	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testPass_QF_2017HY_4Inst_LM0_LegSDS_Headless() throws Throwable {
		File my_reports = new File(reportDir, "4inst-lm0-legsds-headless");
		new TSGR001A().run(args(
				"--data-dir=" + dataDir,
				"--driver=qforts",
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-06-01T00:00:00Z",
				"--probe-auto-start",
				"--probe-auto-shutdown",
				"--report-dir=" + my_reports,
				"--tsgr001a-inst-config=fixture/4inst.ini",
				"--qforts-liquidity-mode=0",
				"--qforts-legacy-sds",
				"--headless"
			));
		// Compare in same order as in ini file
		assertReports(new File("fixture/4inst-lm0-NOF.report"), new File(my_reports, "TSGR001A-NOF.report"));
		assertReports(new File("fixture/4inst-lm0-MINF-DUP1.report"), new File(my_reports, "TSGR001A-MINF-DUP1.report"));
		assertReports(new File("fixture/4inst-lm0-ALLF.report"), new File(my_reports, "TSGR001A-ALLF.report"));
		assertReports(new File("fixture/4inst-lm0-MINF-DUP2.report"), new File(my_reports, "TSGR001A-MINF-DUP2.report"));
	}
	
	@Ignore
	@Test
	public void testModernSDS() {
		fail();
	}
	
	@Ignore
	@Test
	public void testLiquidityModes() {
		fail();
	}

	@Ignore
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
