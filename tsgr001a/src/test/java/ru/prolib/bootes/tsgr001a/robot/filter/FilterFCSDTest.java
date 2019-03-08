package ru.prolib.bootes.tsgr001a.robot.filter;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.DataProviderStub;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.tseries.SCDHSetupStub;
import ru.prolib.aquila.core.data.tseries.SecurityChartDataHandler;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class FilterFCSDTest {
	private static Symbol SYMBOL = new Symbol("AQLA");
	private static ZTFrame TFRAME = ZTFrame.M5;
	
	static S3TradeSignal ofType(SignalType type, Instant time) {
		return new S3TradeSignal(
				type,
				time,
				of("100.00"),
				of(1000L),
				of("50.00"),
				of("10.00"),
				of("5.00")
			);
	}
	
	static S3TradeSignal ofTypeBuy(Instant time) {
		return ofType(SignalType.BUY, time);
	}
	
	static S3TradeSignal ofTypeBuy(String timeString) {
		return ofTypeBuy(T(timeString));
	}
	
	static S3TradeSignal ofTypeBuy() {
		return ofTypeBuy(Instant.EPOCH);
	}
	
	static S3TradeSignal ofTypeSell(Instant time) {
		return ofType(SignalType.SELL, time);
	}
	
	static S3TradeSignal ofTypeSell(String timeString) {
		return ofTypeSell(T(timeString));
	}
	
	static S3TradeSignal ofTypeSell() {
		return ofTypeSell(Instant.EPOCH);
	}
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private EditableTerminal terminal;
	private SecurityChartDataHandler dh;
	private RobotState state;
	private EditableTSeries<Candle> ohlc;
	private FilterFCSD service;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
	public void setUp() throws Exception {
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		dh = new SecurityChartDataHandler(new SCDHSetupStub(SYMBOL, TFRAME, terminal));
		dh.initialize();
		dh.startDataHandling();
		EditableTSeries x = (EditableTSeries) dh.getSeries().getSeries(SCDHSetupStub.SID_OHLC_MUTATOR);
		ohlc = (EditableTSeries<Candle>) x;
		state = new RobotState(null);
		state.setSeriesHandlerT0(dh);

		service = new FilterFCSD(state);
	}
	
	@After
	public void tearDown() throws Exception {
		dh.stopDataHandling();
	}
	
	void addOHLC(String timeString, String open, String high, String low, String close) {
		Instant time = T(timeString);
		Interval interval = TFRAME.getInterval(time);
		ohlc.set(interval.getStart(), new CandleBuilder()
				.withTimeFrame(TFRAME)
				.withTime(interval.getStart())
				.withOpenPrice(open)
				.withHighPrice(high)
				.withLowPrice(low)
				.withClosePrice(close)
				.buildCandle());
	}
	
	void addOHLC(String timeString, String open, String close) {
		addOHLC(timeString, open, open, close, close);
	}
	
	@Test
	public void testCtor1() {
		assertEquals("FCSD", service.getID());
		assertEquals(3, service.getNumberOfCandles());
	}
	
	@Test
	public void testCtor2() {
		service = new FilterFCSD(state, 5);
		assertEquals("FCSD", service.getID());
		assertEquals(5, service.getNumberOfCandles());
	}
	
	@Test
	public void testApprove_HandlerNotDefined() throws Exception {
		state.setSeriesHandlerT0(null);
		
		assertFalse(service.approve(ofTypeBuy()));
	}
	
	@Test
	public void testApprove_NoIndexForSignalTime() throws Exception {
		addOHLC("2019-03-08T08:45:00Z", "56.19", "57.25");
		addOHLC("2019-03-08T08:50:00Z", "57.26", "58.80");
		addOHLC("2019-03-08T08:55:00Z", "58.80", "59.95");
		//addOHLC("2019-03-08T09:00:00Z", "59.94", "59.99");
		
		assertFalse(service.approve(ofTypeBuy("2019-03-08T09:00:00Z")));
	}
	
	@Test
	public void testApprove_NotEnoughElements() throws Exception {
		addOHLC("2019-03-08T08:50:00Z", "57.26", "58.80");
		addOHLC("2019-03-08T08:55:00Z", "58.80", "59.95");
		addOHLC("2019-03-08T09:00:00Z", "59.94", "59.99");
		
		assertFalse(service.approve(ofTypeBuy("2019-03-08T09:00:00Z")));
	}
	
	@Test
	public void testApprove_Buy_NotAllCandlesAreBullish() throws Exception {
		addOHLC("2019-03-08T08:45:00Z", "56.19", "57.25");
		addOHLC("2019-03-08T08:50:00Z", "57.26", "51.80");
		addOHLC("2019-03-08T08:55:00Z", "51.80", "59.95");
		addOHLC("2019-03-08T09:00:00Z", "59.94", "59.99");
		
		assertFalse(service.approve(ofTypeBuy("2019-03-08T09:00:00Z")));
	}
	
	@Test
	public void testApprove_Buy_AllCandlesAreBullish() throws Exception {
		addOHLC("2019-03-08T08:45:00Z", "56.19", "57.25");
		addOHLC("2019-03-08T08:50:00Z", "57.26", "58.80");
		addOHLC("2019-03-08T08:55:00Z", "58.80", "59.95");
		addOHLC("2019-03-08T09:00:00Z", "59.94", "59.99");
		
		assertTrue(service.approve(ofTypeBuy("2019-03-08T09:00:00Z")));
	}
	
	@Test
	public void testApprove_Sell_NotCandlesAreBearish() throws Exception {
		addOHLC("2019-03-08T08:45:00Z", "56.19", "55.25");
		addOHLC("2019-03-08T08:50:00Z", "55.25", "58.80");
		addOHLC("2019-03-08T08:55:00Z", "58.80", "51.95");
		addOHLC("2019-03-08T09:00:00Z", "51.94", "50.99");
		
		assertFalse(service.approve(ofTypeSell("2019-03-08T09:00:00Z")));
	}

	@Test
	public void testApprove_Sell_AllCandlesAreBearish() throws Exception {
		addOHLC("2019-03-08T08:45:00Z", "56.19", "55.25");
		addOHLC("2019-03-08T08:50:00Z", "55.25", "53.80");
		addOHLC("2019-03-08T08:55:00Z", "53.80", "51.95");
		addOHLC("2019-03-08T09:00:00Z", "51.94", "50.99");
		
		assertTrue(service.approve(ofTypeSell("2019-03-08T09:00:00Z")));
	}

}
