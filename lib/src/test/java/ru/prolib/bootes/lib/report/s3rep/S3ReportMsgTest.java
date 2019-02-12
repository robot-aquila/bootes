package ru.prolib.bootes.lib.report.s3rep;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.bootes.lib.report.s3rep.ui.S3ReportMsg;

public class S3ReportMsgTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testConstants() {
		assertEquals(new MsgID("S3Report", "RECORD_ID"), S3ReportMsg.RECORD_ID);
		assertEquals(new MsgID("S3Report", "RECORD_TYPE"), S3ReportMsg.RECORD_TYPE);
		assertEquals(new MsgID("S3Report", "DATE"), S3ReportMsg.DATE);
		assertEquals(new MsgID("S3Report", "ENTRY_TIME"), S3ReportMsg.ENTRY_TIME);
		assertEquals(new MsgID("S3Report", "ENTRY_PRICE"), S3ReportMsg.ENTRY_PRICE);
		assertEquals(new MsgID("S3Report", "QTY"), S3ReportMsg.QTY);
		assertEquals(new MsgID("S3Report", "TAKE_PROFIT"), S3ReportMsg.TAKE_PROFIT);
		assertEquals(new MsgID("S3Report", "STOP_LOSS"), S3ReportMsg.STOP_LOSS);
		assertEquals(new MsgID("S3Report", "BREAK_EVEN"), S3ReportMsg.BREAK_EVEN);
		assertEquals(new MsgID("S3Report", "EXIT_TIME"), S3ReportMsg.EXIT_TIME);
		assertEquals(new MsgID("S3Report", "EXIT_PRICE"), S3ReportMsg.EXIT_PRICE);
		assertEquals(new MsgID("S3Report", "PROFIT_AND_LOSS"), S3ReportMsg.PROFIT_AND_LOSS);		
	}

}
