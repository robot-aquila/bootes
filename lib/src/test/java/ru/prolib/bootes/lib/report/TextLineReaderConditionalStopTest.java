package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;

import java.io.IOException;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;

public class TextLineReaderConditionalStopTest {
	@Rule public ExpectedException eex = ExpectedException.none();
	IMocksControl control;
	Validator<TextLine> conditionMock;
	TextLineReader readerMock;
	TextLineReaderConditionalStop service;
	TextLine anyLine1 = new TextLine(-1, "xxx"),
			anyLine2 = new TextLine(100, "yyy"),
			anyLine3 = new TextLine(200, "zzz");

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		conditionMock = control.createMock(Validator.class);
		readerMock = control.createMock(TextLineReader.class);
		service = new TextLineReaderConditionalStop(readerMock, conditionMock);
	}
	
	@Test
	public void testClose() throws Exception {
		readerMock.close();
		control.replay();
		assertFalse(service.stopped());
		
		service.close();
		
		control.verify();
		assertTrue(service.stopped());
	}
	
	@Test
	public void testGetNextLineNo() {
		expect(readerMock.getNextLineNo()).andReturn(86);
		control.replay();
		
		assertEquals(86, service.getNextLineNo());
		
		control.verify();
	}
	
	@Test
	public void testReadLine_SkipIfStopped() throws Exception {
		expect(readerMock.readLine()).andReturn(anyLine1);
		expect(conditionMock.validate(anyLine1)).andReturn(true);
		control.replay();
		assertFalse(service.stopped());
		
		assertNull(service.readLine()); assertTrue(service.stopped());
		assertNull(service.readLine()); assertTrue(service.stopped());
		assertNull(service.readLine()); assertTrue(service.stopped());
		
		control.verify();
	}
	
	@Test
	public void testReadLine_IfEndOfStream() throws Exception {
		expect(readerMock.readLine()).andReturn(null);
		control.replay();
		assertFalse(service.stopped());
		
		assertNull(service.readLine());
		
		control.verify();
		assertTrue(service.stopped());
	}
	
	@Test
	public void testReadLine_ValidatorException() throws Exception{
		eex.expect(IOException.class);
		eex.expectMessage("Validation failed");
		expect(readerMock.readLine()).andReturn(anyLine1);
		expect(conditionMock.validate(anyLine1)).andThrow(new ValidatorException("Test error"));
		control.replay();
		
		service.readLine();
	}

	@Test
	public void testReadLine() throws Exception {
		expect(readerMock.readLine()).andReturn(anyLine1);
		expect(conditionMock.validate(anyLine1)).andReturn(false);
		control.replay();
		assertFalse(service.stopped());
		
		assertEquals(anyLine1, service.readLine());
		
		control.verify();
		assertFalse(service.stopped());
	}
	
	@Test
	public void testSkipUntilStopped_AlreadyStopped() throws Exception {
		expect(readerMock.readLine()).andReturn(anyLine1);
		expect(conditionMock.validate(anyLine1)).andReturn(true);
		control.replay();
		service.readLine();
		control.resetToStrict();
		control.replay();
		assertTrue(service.stopped());
		
		service.skipUntilStopped();
		
		control.verify();
	}
	
	@Test
	public void testSkipUntilStopped_Stopped() throws Exception {
		expect(readerMock.readLine()).andReturn(anyLine1);
		expect(conditionMock.validate(anyLine1)).andReturn(false);
		expect(readerMock.readLine()).andReturn(anyLine2);
		expect(conditionMock.validate(anyLine2)).andReturn(false);
		expect(readerMock.readLine()).andReturn(anyLine3);
		expect(conditionMock.validate(anyLine3)).andReturn(true);
		control.replay();
		
		service.skipUntilStopped();
		
		control.verify();
		assertTrue(service.stopped());
	}
	
	@Test
	public void testSkipUntilStopped_EndOfData() throws Exception {
		expect(readerMock.readLine()).andReturn(anyLine1);
		expect(conditionMock.validate(anyLine1)).andReturn(false);
		expect(readerMock.readLine()).andReturn(anyLine2);
		expect(conditionMock.validate(anyLine2)).andReturn(false);
		expect(readerMock.readLine()).andReturn(anyLine3);
		expect(conditionMock.validate(anyLine3)).andReturn(false);
		expect(readerMock.readLine()).andReturn(null);
		control.replay();
		
		service.skipUntilStopped();
		
		control.verify();
		assertTrue(service.stopped());
	}

}
