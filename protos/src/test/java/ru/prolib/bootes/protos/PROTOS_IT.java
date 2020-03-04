package ru.prolib.bootes.protos;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

import ru.prolib.bootes.lib.report.ReportComparator;
import ru.prolib.bootes.lib.report.STRCmpResult;

public class PROTOS_IT {
	static final File dataDir = new File("./../shared/canned-data");
	static final File reportDir = new File("tmp/it-reports");
	static final File EXPECTED_LONG = new File("fixture", "protos-long.rep");
	static final File EXPECTED_SHORT = new File("fixture", "protos-short.rep");
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if ( reportDir.exists() ) {
			FileUtils.forceDelete(reportDir);
		}
		reportDir.mkdirs();
	}
	
	@After
	public void tearDown() throws Exception {

	}
	
	public String[] args(String... args) {
		List<String> arg_list = new ArrayList<>(Arrays.asList(args));
		return arg_list.toArray(new String[0]);
	}
	
	static List<String> removeSysInfoReport(List<String> lines) {
		for ( int i = 0; i < lines.size(); i ++ ) {
			String line = lines.get(i);
			if ( line.startsWith("# ReportID=SysInfoReport_") ) {
				lines.remove(i); // header
				lines.remove(i); // job started
				lines.remove(i); // job finished
				lines.remove(i); // time spent
				lines.remove(i); // empty line
			}
		}
		return lines;
	}
	
	static void assertReportFiles_V1(File expected, File actual) throws Exception {
		List<String> expected_lines = removeSysInfoReport(Files.readAllLines(expected.toPath()));
		List<String> actual_lines = removeSysInfoReport(Files.readAllLines(actual.toPath()));
		Patch<String> patch = DiffUtils.diff(expected_lines, actual_lines);
		
		List<String> deltas = new ArrayList<>();
		for ( AbstractDelta<String> delta : patch.getDeltas() ) {
			deltas.add(delta.toString());
		}
		String found_deltas = StringUtils.join(deltas, "," + System.lineSeparator());
		assertEquals("Reports are differ: ", "", found_deltas);
	}
	
	static void assertReportFiles_V2(File expected, File actual) throws Exception {
		STRCmpResult result = ReportComparator.getInstance().compare(expected, actual);
		assertTrue(String.format("exp: %s\nact: %s\n%s", expected, actual, result.toString()), result.identical());		
	}
	
	static void assertReports(File expected, File actual) throws Exception {
		assertReportFiles_V2(expected, actual);
	}
	
	@Test
	public void testPass1_OldOrderExecTriggerMode() throws Throwable {
		File rd_pass1 = new File(reportDir, "pass1_old-oetm");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + rd_pass1,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--qforts-order-exec-trigger-mode=0"
			));
		assertReports(EXPECTED_SHORT, new File(rd_pass1, "protos1.report"));
	}

	@Test
	public void testPass1_NewOrderExecTriggerMode() throws Throwable {
		File rd_pass1 = new File(reportDir, "pass1_new-oetm");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + rd_pass1,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--qforts-order-exec-trigger-mode=1"
			));
		assertReports(EXPECTED_SHORT, new File(rd_pass1, "protos1.report"));
	}

	@Test
	public void testPass1_OldOrderExecTriggerMode_ohlc() throws Throwable {
		File rd_pass1 = new File(reportDir, "pass1_old-oetm_ohlc");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + rd_pass1,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--qforts-order-exec-trigger-mode=0",
				"--protos-use-ohlc-provider"
			));
		assertReports(EXPECTED_SHORT, new File(rd_pass1, "protos1.report"));
	}

	@Test
	public void testPass1_NewOrderExecTriggerMode_ohlc() throws Throwable {
		File rd_pass1 = new File(reportDir, "pass1_new-oetm_ohlc");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + rd_pass1,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--qforts-order-exec-trigger-mode=1",
				"--protos-use-ohlc-provider"
			));
		assertReports(EXPECTED_SHORT, new File(rd_pass1, "protos1.report"));
	}

	
	
	
	
	@Ignore
	@Test
	public void testPass1_WithLegacySDS() throws Throwable {
		File report_dir = new File(reportDir, "pass1_legacy_sds");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--qforts-legacy-sds"
			));
		assertReports(EXPECTED_SHORT, new File(report_dir, "protos1.report"));
	}
	
	@Ignore
	@Test
	public void testPass2() throws Throwable {
		File rd_pass2 = new File(reportDir, "pass2");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + rd_pass2,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless"
			));
		assertReports(EXPECTED_SHORT, new File(rd_pass2, "protos1.report"));
	}
	
	@Ignore
	@Test
	public void testPass2_WithLegacySDS() throws Throwable {
		File report_dir = new File(reportDir, "pass2_legacy_sds");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless",
				"--qforts-legacy-sds"
			));
		assertReports(EXPECTED_SHORT, new File(report_dir, "protos1.report"));
	}
	
	@Ignore
	@Test
	public void testPass3() throws Throwable {
		File rd_pass3 = new File(reportDir, "pass3");
		new PROTOS(3).run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + rd_pass3,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless",
				"--qforts-liquidity-mode=1"
			));
		assertReports(EXPECTED_SHORT, new File(rd_pass3, "protos1.report"));
		assertReports(EXPECTED_SHORT, new File(rd_pass3, "protos2.report"));
		assertReports(EXPECTED_SHORT, new File(rd_pass3, "protos3.report"));
	}
	
	@Ignore
	@Test
	public void testPass3_WithLegacySDS() throws Throwable {
		File report_dir = new File(reportDir, "pass3_legacy_sds");
		new PROTOS(3).run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless",
				"--qforts-liquidity-mode=1",
				"--qforts-legacy-sds"
			));
		assertReports(EXPECTED_SHORT, new File(report_dir, "protos1.report"));
		assertReports(EXPECTED_SHORT, new File(report_dir, "protos2.report"));
		assertReports(EXPECTED_SHORT, new File(report_dir, "protos3.report"));
	}

	@Ignore
	@Test
	public void testPass4_Long() throws Throwable {
		File report_dir = new File(reportDir, "pass4_long");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-06-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless"
			));
		assertReports(EXPECTED_LONG, new File(report_dir, "protos1.report"));
	}
	
	@Ignore
	@Test
	public void testPass4_Long_WithLegacySDS() throws Throwable {
		File report_dir = new File(reportDir, "pass4_long_legacy_sds");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-06-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless",
				"--qforts-legacy-sds"
			));
		assertReports(EXPECTED_LONG, new File(report_dir, "protos1.report"));
	}
	
	@Ignore
	@Test
	public void testPass5_OhlcProviderProducer_WithLegacySDS() throws Throwable {
		File report_dir = new File(reportDir, "pass5_ohlc_prov_prod");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless",
				"--qforts-legacy-sds",
				"--protos-use-ohlc-provider"
			));
		assertReports(EXPECTED_SHORT, new File(report_dir, "protos1.report"));
	}

}
