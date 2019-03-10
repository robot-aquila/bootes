package ru.prolib.bootes.tsgr001a.rm;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;

public class RMContractStrategyTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	private static Instant TIME = T("2018-12-14T05:30:39Z");
	private static Account account = new Account("JBT-1251");
	private static Symbol symbol = new Symbol("FAKE");
	
	private IMocksControl control;
	private EditableTerminal terminal;
	private EditableSecurity security;
	private EditablePortfolio portfolio;
	private RMPriceStats psMock;
	private RMContractStrategyParams params;
	private RMContractStrategy service;
	
	private void setupRTS_1() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of(10L))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("6.8645"))
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("11627.63"))
				.buildUpdate());
	}

	private void setupRTS_2() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of(10L))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("13.23588"))
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB5("17710.50000"))
				.buildUpdate());
	}

	private void setupBR() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("0.01"))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("6.66046"))
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("3425.15"))
				.buildUpdate());
	}
	
	private void setupEu() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of(1L))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("1.00000"))
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("1533.89"))
				.buildUpdate());
	}
	
	private void setupGOLD() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("0.1"))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("6.61794"))
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("2925.61"))
				.buildUpdate());
	}
	
	private void setupSi() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of(1L))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("1.00000"))
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB2("1554.00"))
				.buildUpdate());
	}
	
	private RMContractStrategyParams commonParamsWithSlippage(int slippageStp, CDecimal strategy_cap_share) {
		return params = new RMContractStrategyParams(
				CDecimalBD.of("0.075"),
				CDecimalBD.of("0.012"),
				CDecimalBD.of("0.60"),
				CDecimalBD.of("1.05"),
				slippageStp,
				strategy_cap_share
			);
	}
	
	private RMContractStrategyParams commonParamsWithSlippage(int slippage_stp) {
		return commonParamsWithSlippage(slippage_stp, of("1"));
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		psMock = control.createMock(RMPriceStats.class);
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		security = terminal.getEditableSecurity(symbol);
		portfolio = terminal.getEditablePortfolio(account);
		portfolio.consume(new DeltaUpdateBuilder()
				.withToken(PortfolioField.FREE_MARGIN, ofRUB2("1044780.17"))
				.buildUpdate());
		service = new RMContractStrategy();
		service.setStrategyParams(commonParamsWithSlippage(3));
		service.setPortfolio(portfolio);
		service.setSecurity(security);
		service.setPriceStats(psMock);
	}
	
	@Test
	public void testCtor() {
		assertSame(params, service.getStrategyParams());
		assertSame(portfolio, service.getPortfolio());
		assertSame(security, service.getSecurity());
		assertSame(psMock, service.getPriceStats());
	}
	
	@Test
	public void testPriceToMoney() {
		setupRTS_2(); // tick size = 10, tick value = 13.23588
		assertEquals(ofRUB2("198922.04"), service.priceToMoney(of("150290")));
		assertEquals(ofRUB2("198922.04"), service.priceToMoney(of("150294")));
		assertEquals(ofRUB2("198922.04"), service.priceToMoney(of("150291.592504256")));
		assertEquals(ofRUB2("198935.28"), service.priceToMoney(of("150297")));
		assertEquals(ofRUB2("198935.28"), service.priceToMoney(of("150297.278634646")));
		
		setupBR(); // tick size = 0.01, tick value = 6.66046
		assertEquals(ofRUB2("16597.87"), service.priceToMoney(of("24.92")));
		
		setupEu(); // tick size = 1, tick value = 1.00000
		assertEquals(ofRUB2("69279.00"), service.priceToMoney(of("69279")));
		
		setupGOLD(); // tick size = 0.1, tick value = 6.61794
		assertEquals(ofRUB2("132405.13"), service.priceToMoney(of("2000.7")));
		
		setupSi(); // tick size = 1, tick value = 1.00000
		assertEquals(ofRUB2("67928.00"), service.priceToMoney(of("67928")));
	}
	
	@Test
	public void testPriceToMoney_ZeroTickSize() {
		setupRTS_2();
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("0.00000"))
				.buildUpdate());
		
		assertEquals(ofRUB2("0.00"), service.priceToMoney(of("150290")));
	}
	
	@Test
	public void testMoneyToPrice() {
		setupRTS_2();
		assertEquals(of("76145770"), service.moneyToPrice(ofRUB5("100785632.62345")));
		assertEquals(of("58822620"), service.moneyToPrice(ofRUB2("77856912.12")));
		
		setupBR();
		assertEquals(of("107.49"), service.moneyToPrice(ofRUB5("71590.54331")));
		assertEquals(of("18.02"), service.moneyToPrice(ofRUB2("12000")));
		
		setupEu();
		assertEquals(of("78002632"), service.moneyToPrice(ofRUB5("78002631.99751")));
		assertEquals(of("655521"), service.moneyToPrice(ofRUB2("655521.1")));
		
		setupGOLD();
		assertEquals(of("152427.2"), service.moneyToPrice(ofRUB5("10087542.4461")));
		assertEquals(of("8919.1"), service.moneyToPrice(ofRUB2("590261")));
		
		setupSi();
		assertEquals(of("90085313"), service.moneyToPrice(ofRUB5("90085312.5002")));
	}
	
	@Test
	public void testMoneyToPrice_ZeroTickValue() {
		setupRTS_2();
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_VALUE, ofRUB5("0"))
				.buildUpdate());
		
		assertEquals(of("0"), service.moneyToPrice(ofRUB2("77856912.12")));
	}
	
	@Test
	public void testGetPositionParams_RTS_1() {
		service.setStrategyParams(commonParamsWithSlippage(3));
		setupRTS_1();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("3659.02861"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("115.47992"));
		control.replay();
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				51,
				of("2200"),
				of("330"),
				of(30L), // slippage points
				ofRUB2("78358.51"),
				ofRUB2("12537.36"),
				of("3659.02861"),
				of("115.47992")
			);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetPositionParams_RTS_2() {
		service.setStrategyParams(commonParamsWithSlippage(3));
		setupRTS_2();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("3300.01726"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("130.50992"));
		control.replay();
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				29,
				of("1980"),
				of("300"), // 10 * 12537.36 / 29 / 13.23588 - 10 * 3 = 300
				of(30L),
				ofRUB2("78358.51"),
				ofRUB2("12537.36"),
				of("3300.01726"),
				of("130.50992")
			);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetPositionParams_Si_1() {
		service.setStrategyParams(commonParamsWithSlippage(5));
		setupSi();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("289.02761"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("52.40061"));
		control.replay();
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				452,
				of("173"),
				of("23"),
				of("5"),
				ofRUB2("78358.51"),
				ofRUB2("12537.36"),
				of("289.02761"),
				of("52.40061")
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_Si_2() {
		service.setStrategyParams(commonParamsWithSlippage(5));
		setupSi();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("879.71002"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("76.11972"));
		control.replay();
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				148,
				of("528"),
				of("80"), // 1 * 12537.36 / 148 / 1 - 1 * 5 = 80
				of("5"),
				ofRUB2("78358.51"),
				ofRUB2("12537.36"),
				of("879.71002"),
				of("76.11972")
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_BR_1() {
		service.setStrategyParams(commonParamsWithSlippage(1));
		setupBR();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("2.9801562"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("0.136678"));
		control.replay();
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				65,
				of("1.79"),
				of("0.28"), // 0.01 * 12537.36 / 66 / 6.66046 - 1 * 0.01 = 0.28520 - 0.01 = 0.28
				of("0.01"),
				ofRUB2("78358.51"),
				ofRUB2("12537.36"),
				of("2.9801562"),
				of("0.136678")
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_Eu_1() {
		service.setStrategyParams(commonParamsWithSlippage(2));
		setupEu();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("895.40182"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("49.76512"));
		control.replay();
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				145,
				of("537"),
				of("84"), // 1 * 12537.36 / 146 / 1 - 2 * 1 = 86 - 2 = 84
				of("2"),
				ofRUB2("78358.51"),
				ofRUB2("12537.36"),
				of("895.40182"),
				of("49.76512")
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_GOLD_1() {
		service.setStrategyParams(commonParamsWithSlippage(3));
		setupGOLD();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("10.801772"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("3.99012"));
		control.replay();
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				182,
				of("6.5"),
				of("0.7"), // 0.1 / 12537.36 / 182 / 6.61794 - 3 * 0.1 = 1.0 - 0.3 = 0.7
				of("0.3"),
				ofRUB2("78358.51"),
				ofRUB2("12537.36"),
				of("10.801772"),
				of("3.99012")
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_SpecialCase_ZeroFreeMargin() {
		service.setStrategyParams(commonParamsWithSlippage(5));
		setupRTS_2();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("3300.01726"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("250.86612"));
		control.replay();
		portfolio.consume(new DeltaUpdateBuilder()
				.withToken(PortfolioField.FREE_MARGIN, ofRUB2("0.00"))
				.buildUpdate());
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				0,
				of(0L),
				of(0L),
				of(0L),
				ofRUB2("0.00"),
				ofRUB2("0.00"),
				of(0L),
				of(0L)
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_SpecialCase_ZeroTradeGoalCapPer() {
		service.setStrategyParams(new RMContractStrategyParams(
				CDecimalBD.of("0.000"),
				CDecimalBD.of("0.012"),
				CDecimalBD.of("0.60"),
				CDecimalBD.of("1.05"),
				5,
				of(1L)
			));
		setupRTS_2();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("3300.01726"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("450.88210"));
		control.replay();
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				0,
				of(0L),
				of(0L),
				of(0L),
				ofRUB2("0.00"),
				ofRUB2("0.00"),
				of(0L),
				of(0L)
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_SpecialCase_ZeroLossGoalCapPer() {
		service.setStrategyParams(new RMContractStrategyParams(
				CDecimalBD.of("0.075"),
				CDecimalBD.of("0.000"),
				CDecimalBD.of("0.60"),
				CDecimalBD.of("1.05"),
				5,
				of(1L)
			));
		setupRTS_2();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("3300.01726"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("450.88210"));
		control.replay();
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				0,
				of(0L),
				of(0L),
				of(0L),
				ofRUB2("0.00"),
				ofRUB2("0.00"),
				of(0L),
				of(0L)
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_SpecialCase_ZeroPriceTickSize() {
		// Normally this never should happen because tick size shall not be zero
		service.setStrategyParams(commonParamsWithSlippage(5));
		setupRTS_2();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("3300.01726"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("450.88210"));
		control.replay();
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of(0L))
				.buildUpdate());
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				0,
				of(0L),
				of(0L),
				of(0L),
				ofRUB2("0.00"),
				ofRUB2("0.00"),
				of(0L),
				of(0L)
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_SpecialCase_ZeroPriceTickValue() {
		// Normally this never should happen because tick value shall not be zero
		service.setStrategyParams(commonParamsWithSlippage(5));
		setupRTS_2();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("3300.01726"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("450.88210"));
		control.replay();
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_VALUE, ofRUB5("0.00000"))
				.buildUpdate());
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				0,
				of(0L),
				of(0L),
				of(0L),
				ofRUB2("0.00"),
				ofRUB2("0.00"),
				of(0L),
				of(0L)
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_SpecialCase_ExpDailyPriceMovePer() {
		service.setStrategyParams(new RMContractStrategyParams(
				CDecimalBD.of("0.075"),
				CDecimalBD.of("0.012"),
				CDecimalBD.of("0.00"),
				CDecimalBD.of("1.05"),
				5,
				of(1L)
			));
		setupRTS_2();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("3300.01726"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("450.88210"));
		control.replay();
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				0,
				of(0L),
				of(0L),
				of(0L),
				ofRUB2("0.00"),
				ofRUB2("0.00"),
				of(0L),
				of(0L)
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_SpecialCase_ExpLocalPriceMovePer() {
		service.setStrategyParams(new RMContractStrategyParams(
				CDecimalBD.of("0.075"),
				CDecimalBD.of("0.012"),
				CDecimalBD.of("0.60"),
				CDecimalBD.of("0.00"),
				5,
				of(1L)
			));
		setupRTS_2();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("3300.01726"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("450.88210"));
		control.replay();
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				0,
				of(0L),
				of(0L),
				of(0L),
				ofRUB2("0.00"),
				ofRUB2("0.00"),
				of(0L),
				of(0L)
			);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetPositionParams_SpecialCase_ZeroTakeProfitPts() {
		testGetPositionParams_SpecialCase_ExpDailyPriceMovePer();
	}
	
	@Test
	public void testGetPositionParams_SpecialCase_ZeroNumContracts() {
		testGetPositionParams_SpecialCase_ExpLocalPriceMovePer();
	}
	
	@Test
	public void testGetPositionParams_StrategyCapitalShare() {
		service.setStrategyParams(commonParamsWithSlippage(3, of("0.4")));
		setupRTS_2();
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("3300.01726"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("130.50992"));
		control.replay();
		
		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);

		// strategy cap = 1044780.17 * 0.4 = 417912.06800
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				11, 				// contracts: 10 * 31343.41 / 13.23588 / 1980 = 11.95992 = 11 
				of("1980"),			// TP: daily price move * 0.6 
				of("310"), 			// SL: 10 * 5014.94 / 11 / 13.23588 - 10 * 3 = 310
				of(30L),			// slippage points
				ofRUB2("31343.41"), // goal cap: 417912.06800 * 0.075 = 31343.40510 
				ofRUB2( "5014.95"), // loss cap: 417912.06800 * 0.012 =  5014.94482 = 5014.95
									//     because loss cap percentage has scale of 3 ^^^^^^^
				of("3300.01726"),	// daily price move
				of("130.50992")		// local price move
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_MaxContractsLimit() {
		service.setStrategyParams(commonParamsWithSlippage(3, of("0.4")));
		setupRTS_2();
		security.consume(new L1UpdateBuilder(symbol)
				.withTrade()
				.withTime("2019-03-09T07:51:00Z")
				.withPrice("117920")
				.withSize(1L)
				.buildL1Update()); // TODO: ???
		expect(psMock.getDailyPriceMove(TIME)).andReturn(of("800.72651"));
		expect(psMock.getLocalPriceMove(TIME)).andReturn(of("130.50992"));
		control.replay();

		RMContractStrategyPositionParams actual = service.getPositionParams(TIME);
		
		// strategy cap = 1044780.17 * 0.4 = 417912.06800
		// max contracts 417912.06800 / 17710.50000 = 23
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				23, 				// contracts: 10 * 31343.41 / 13.23588 / 480.43591 = 49 so use max = 23
				of("480"),			// TP: 800.72651 * 0.6 = 480.43591 
				of("130"), 			// SL: 10 * 5014.94 / 23 / 13.23588 - 10 * 3 = 130
				of(30L),			// slippage points
				ofRUB2("31343.41"), // goal cap: 417912.06800 * 0.075 = 31343.40510 
				ofRUB2( "5014.95"), // loss cap: 417912.06800 * 0.012 =  5014.94482 = 5014.95
				of("800.72651"),	// daily price move
				of("130.50992")		// local price move
			);
		assertEquals(expected, actual);
	}

}
