package xx.mix.bootes.kinako.service;

import static org.junit.Assert.*;

import java.io.File;
import java.time.Instant;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class VVSignalParserTest {
	private static final Instant SOME_TIME = T("2019-09-20T00:51:00Z");
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private VVSignalParser service;

	@Before
	public void setUp() throws Exception {
		service = new VVSignalParser();
	}
	
	@Test
	public void testParse() throws Exception {
		String text = FileUtils.readFileToString(new File("fixture/vvsignalparser1.txt"), "UTF8");

		VVSignal actual = service.parse(text, SOME_TIME);

		VVSignal expected = new VVSignalBuilder()
				.withTime(SOME_TIME)
				.addOrderRecom(VVOrderType.SELL_LONG, 58, "MKTX")
				.addOrderRecom(VVOrderType.SELL_SHORT, 54, "ULTA")
				.addOrderRecom(VVOrderType.BUY_LONG, 10, "AAPL")
				.addOrderRecom(VVOrderType.COVER_SHORT, 2, "MSFT")
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testParse_ThrowsIfNoSignals() throws Exception {
		eex.expect(VVSignalParseException.class);
		eex.expectMessage("No signal found");
		String text= FileUtils.readFileToString(new File("fixture/vvsignalparser2.txt"), "UTF8");
		
		service.parse(text, SOME_TIME);
	}
	
	@Test
	public void testParse_CanParseFortsSymbols() throws Exception {
		String text = FileUtils.readFileToString(new File("fixture/vvsignalparser3.txt"), "UTF8");
		
		VVSignal actual = service.parse(text,  SOME_TIME);
		
		VVSignal expected = new VVSignalBuilder()
				.withTime(SOME_TIME)
				.addOrderRecom(VVOrderType.SELL_LONG, 12, "RTS-12.19")
				.addOrderRecom(VVOrderType.BUY_LONG, 10, "Si-9.19")
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testParse_CanParseAquilaSymbolFormat() throws Exception {
		String text = FileUtils.readFileToString(new File("fixture/vvsignalparser4.txt"), "UTF8");
		
		VVSignal actual = service.parse(text,  SOME_TIME);
		
		VVSignal expected = new VVSignalBuilder()
				.withTime(SOME_TIME)
				.addOrderRecom(VVOrderType.SELL_LONG, 58, "S:AAPL@NASDAQ:USD")
				.build();
		assertEquals(expected, actual);
	}

}
