package ru.prolib.bootes.lib.report.hello;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

public class HelloBlockPrinterTest {
	private HelloBlockPrinter service;

	@Before
	public void setUp() throws Exception {
		service = new HelloBlockPrinter("Header", "Hello, world!");
	}
	
	@Test
	public void testCtor1() {
		service = new HelloBlockPrinter("Wookie");
		assertEquals("Default", service.getTitle());
		assertEquals("Hello_v0.1.0", service.getReportID());
	}
	
	@Test
	public void testCtor2() {
		assertEquals("Header", service.getTitle());
		assertEquals("Hello_v0.1.0", service.getReportID());
	}

	@Test
	public void testPrint() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		service.print(new PrintStream(baos));

		String expected = new StringBuilder()
				.append("Hello, world!")
				.append(System.lineSeparator())
				.toString();
		assertEquals(expected, baos.toString());
	}

}
