package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TextLineReaderBRImplText {
	static final String LN = System.lineSeparator();
	@Rule public ExpectedException eex = ExpectedException.none();
	BufferedReader reader;
	TextLineReaderBRImpl service;

	@Before
	public void setUp() throws Exception {
		
	}
	
	TextLineReaderBRImpl initWithData(String source_text) {
		reader = new BufferedReader(new StringReader(source_text));
		return service = new TextLineReaderBRImpl(reader);
	}
	
	@Test
	public void testCtor() {
		initWithData("");
		
		assertEquals(0, service.getNextLineNo());
		assertEquals(reader, service.getReader());
	}
	
	@Test
	public void testReadLine() throws Exception {
		initWithData(new StringBuilder()
				.append("line 1").append(LN)
				.append("line two").append(LN)
				.append("line #3").append(LN)
				.toString()
			);
		
		assertEquals(0, service.getNextLineNo());
		assertEquals(new TextLine(0, "line 1"), service.readLine());
		assertEquals(1, service.getNextLineNo());
		assertEquals(new TextLine(1, "line two"), service.readLine());
		assertEquals(2, service.getNextLineNo());
		assertEquals(new TextLine(2, "line #3"), service.readLine());
		assertEquals(3, service.getNextLineNo());
		assertNull(service.readLine());
		assertEquals(3, service.getNextLineNo());
		assertNull(service.readLine());
		assertEquals(3, service.getNextLineNo());
		assertNull(service.readLine());
		assertEquals(3, service.getNextLineNo());
	}
	
	@Test
	public void testReadLine_AfterClose() throws Exception {
		initWithData(new StringBuilder()
				.append("line 1").append(LN)
				.append("line two").append(LN)
				.toString()
			);
		service.readLine();
		service.close();
		eex.expect(IOException.class);
		eex.expectMessage("Stream closed");
		
		assertNull(service.readLine());
	}

}
