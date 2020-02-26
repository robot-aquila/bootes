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
import org.junit.Test;
import org.threeten.extra.Interval;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

import ru.prolib.bootes.lib.report.ReportComparator;
import ru.prolib.bootes.lib.report.STRCmpResult;

public class PROTOS_IT {
	static final File dataDir = new File("./../shared/canned-data");
	static final File reportDir = new File("tmp/it-reports");
	static Interval INTERVAL;
	static File EXPECTED = new File("fixture", "expected-protos.rep");
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		INTERVAL = Interval.of(T("2017-01-01T00:00:00Z"), T("2017-06-01T00:00:00Z"));
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
	
	static void assertReportFiles(File expected, File actual) throws Exception {
		assertReportFiles_V2(expected, actual);
	}
	
	@Test
	public void testPass1() throws Throwable {
		File rd_pass1 = new File(reportDir, "pass1");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--probe-initial-time=" + INTERVAL.getStart(),
				"--probe-stop-time=" + INTERVAL.getEnd(),
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--report-dir=" + rd_pass1
			));
		assertReportFiles(EXPECTED, new File(rd_pass1, "protos1.report"));
	}
	
	@Test
	public void testPass1_WithLegacySDS() throws Throwable {
		File report_dir = new File(reportDir, "pass1_legacy_sds");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--probe-initial-time=" + INTERVAL.getStart(),
				"--probe-stop-time=" + INTERVAL.getEnd(),
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--report-dir=" + report_dir,
				"--qforts-legacy-sds"
			));
		assertReportFiles(EXPECTED, new File(report_dir, "protos1.report"));
	}
	
	@Test
	public void testPass2() throws Throwable {
		File rd_pass2 = new File(reportDir, "pass2");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--probe-initial-time=" + INTERVAL.getStart(),
				"--probe-stop-time=" + INTERVAL.getEnd(),
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--report-dir=" + rd_pass2,
				"--headless"
			));
		assertReportFiles(EXPECTED, new File(rd_pass2, "protos1.report"));
	}
	
	@Test
	public void testPass2_WithLegacySDS() throws Throwable {
		File report_dir = new File(reportDir, "pass2");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--probe-initial-time=" + INTERVAL.getStart(),
				"--probe-stop-time=" + INTERVAL.getEnd(),
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--report-dir=" + report_dir,
				"--headless",
				"--qforts-legacy-sds"
			));
		assertReportFiles(EXPECTED, new File(report_dir, "protos1.report"));
	}
	
	@Test
	public void testPass3() throws Throwable {
		File rd_pass3 = new File(reportDir, "pass3");
		new PROTOS(3).run(args(
				"--data-dir=" + dataDir,
				"--probe-initial-time=" + INTERVAL.getStart(),
				"--probe-stop-time=" + INTERVAL.getEnd(),
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--report-dir=" + rd_pass3,
				"--headless",
				"--qforts-liquidity-mode=1"
			));
		assertReportFiles(EXPECTED, new File(rd_pass3, "protos1.report"));
		assertReportFiles(EXPECTED, new File(rd_pass3, "protos2.report"));
		assertReportFiles(EXPECTED, new File(rd_pass3, "protos3.report"));
	}
	
	@Test
	public void testPass3_WithLegacySDS() throws Throwable {
		File report_dir = new File(reportDir, "pass3");
		new PROTOS(3).run(args(
				"--data-dir=" + dataDir,
				"--probe-initial-time=" + INTERVAL.getStart(),
				"--probe-stop-time=" + INTERVAL.getEnd(),
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--report-dir=" + report_dir,
				"--headless",
				"--qforts-liquidity-mode=1",
				"--qforts-legacy-sds"
			));
		assertReportFiles(EXPECTED, new File(report_dir, "protos1.report"));
		assertReportFiles(EXPECTED, new File(report_dir, "protos2.report"));
		assertReportFiles(EXPECTED, new File(report_dir, "protos3.report"));
	}

}
