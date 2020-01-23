package ru.prolib.bootes.protos;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.threeten.extra.Interval;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

public class PROTOS_IT {
	static Interval INTERVAL;
	static File EXPECTED = new File("fixture", "expected-protos.rep");
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		INTERVAL = Interval.of(T("2017-01-01T00:00:00Z"), T("2017-06-01T00:00:00Z"));
	}
	
	private File tempDir;

	@Before
	public void setUp() throws Exception {
		tempDir = new File(FileUtils.getTempDirectory(), "protos-temp-" + System.nanoTime());
	}
	
	@After
	public void tearDown() throws Exception {
		if ( tempDir.exists() ) {
			FileUtils.forceDelete(tempDir);
		}
	}
	
	private static List<String> removeSysInfoReport(List<String> lines) {
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
	
	private static void assertReportFiles(File expected, File actual) throws Exception {
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
	
//	@Test
//	public void testAssertReportFiles() throws Exception {
//		File base_dir = new File("D:/work/_test_reports/202001_replay_optim");
//		
//		File expected = new File(base_dir, "20200106044814/TSGR001A-ALLF.report");
//		File actual = new File(base_dir, "20200107204333-ctrl1/TSGR001A-ALLF.report");
//		assertReportFiles(expected, actual);
//	}

	@Test
	public void testPass1() throws Throwable {
		File rd_pass1 = new File(tempDir, "pass1");
		String[] args_pass1 = {
			"--data-dir=fixture",
			"--probe-initial-time=" + INTERVAL.getStart(),
			"--probe-stop-time=" + INTERVAL.getEnd(),
			"--probe-auto-shutdown",
			"--probe-auto-start",
			"--report-dir=" + rd_pass1,
		};
		new PROTOS().run(args_pass1);
		assertReportFiles(EXPECTED, new File(rd_pass1, "protos1.report"));
	}
	
	@Test
	public void testPass2() throws Throwable {
		File rd_pass2 = new File(tempDir, "pass2");
		String[] args_pass2 = {
			"--data-dir=fixture",
			"--probe-initial-time=" + INTERVAL.getStart(),
			"--probe-stop-time=" + INTERVAL.getEnd(),
			"--probe-auto-shutdown",
			"--probe-auto-start",
			"--report-dir=" + rd_pass2,
			"--headless",
		};
		new PROTOS().run(args_pass2);
		assertReportFiles(EXPECTED, new File(rd_pass2, "protos1.report"));
	}
	
	@Test
	public void testPass3() throws Throwable {
		File rd_pass3 = new File(tempDir, "pass3");
		String[] args_pass3 = {
			"--data-dir=fixture",
			"--probe-initial-time=" + INTERVAL.getStart(),
			"--probe-stop-time=" + INTERVAL.getEnd(),
			"--probe-auto-shutdown",
			"--probe-auto-start",
			"--report-dir=" + rd_pass3,
			"--headless",
			"--qforts-liquidity-mode=1"
		};
		new PROTOS(3).run(args_pass3);
		assertReportFiles(EXPECTED, new File(rd_pass3, "protos1.report"));
		assertReportFiles(EXPECTED, new File(rd_pass3, "protos2.report"));
		assertReportFiles(EXPECTED, new File(rd_pass3, "protos3.report"));
	}

}
