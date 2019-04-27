package ru.prolib.bootes.protos;

import static org.junit.Assert.*;

import java.io.File;
import java.time.Instant;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.threeten.extra.Interval;

public class PROTOS_IT {
	static Interval INTERVAL;
	
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
		FileUtils.forceDelete(tempDir);
	}

	@Test
	public void test_() throws Throwable {
		File rd_pass1 = new File(tempDir, "pass1");
		String[] args_pass1 = {
			"--data-dir=fixture",
			"--probe-initial-time=" + INTERVAL.getStart(),
			"--probe-stop-time=" + INTERVAL.getEnd(),
			"--probe-auto-shutdown",
			"--probe-auto-start",
			"--reports-dir=" + rd_pass1,
			};
		new PROTOS().run(args_pass1);
		
		File rd_pass2 = new File(tempDir, "pass2");
		String[] args_pass2 = {
			"--data-dir=fixture",
			"--probe-initial-time=" + INTERVAL.getStart(),
			"--probe-stop-time=" + INTERVAL.getEnd(),
			"--probe-auto-shutdown",
			"--probe-auto-start",
			"--reports-dir=" + rd_pass2,
			"--headless",
		};
		new PROTOS().run(args_pass2);
		
		File expected = new File("fixture", "expected-protos.rep");
		assertTrue(FileUtils.contentEqualsIgnoreEOL(expected, new File(rd_pass1, "protos.report"), "UTF-8"));
		assertTrue(FileUtils.contentEqualsIgnoreEOL(expected, new File(rd_pass2, "protos.report"), "UTF-8"));
	}

}
