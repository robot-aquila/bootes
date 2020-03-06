package ru.prolib.bootes.lib.report.order;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class OrderReportComparatorTest {

	@Before
	public void setUp() throws Exception {
	}


	@Ignore
	@Test
	public void test() {
		String line = "                        #1 | 2017-01-03 16:35:00.000 | 118940 |   1 |  144389.82968 RUB |  20170103163500#0000000001/s0_c1 ";
		//line = maskExecutionNum(line);
		//line = maskExternalID(line);
		System.out.println("[" + line + "]");
		
		fail("Not yet implemented");
	}

}
