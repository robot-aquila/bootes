package ru.prolib.bootes.tsgr001a.config;

import static org.junit.Assert.*;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class XTest {

	@Before
	public void setUp() throws Exception {
	}

	@Ignore
	@Test
	public void testX() {
		Options options = new Options();
		options.addOption(Option.builder()
				.longOpt("foo")
				.build());
		options.addOption(Option.builder()
				.longOpt("foo")
				.build());
		
		fail("Not yet implemented");
	}

}
