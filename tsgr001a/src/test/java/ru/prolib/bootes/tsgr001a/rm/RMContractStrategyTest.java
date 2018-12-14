package ru.prolib.bootes.tsgr001a.rm;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;

public class RMContractStrategyTest {
	private static Instant TIME = Instant.EPOCH;
	private static Account account = new Account("JBT-1251");
	private static Symbol symbol = new Symbol("FAKE");
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private EditableTerminal terminal;
	private EditableSecurity security;
	private EditablePortfolio portfolio;
	private TSeriesImpl<CDecimal> d10ATR, m5ATR;
	private RMContractStrategyParams params;
	private RMContractStrategy service;
	
	private void setupRTS_1() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of(10L))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("6.8645"))
				.buildUpdate());
	}

	private void setupRTS_2() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of(10L))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("13.23588"))
				.buildUpdate());
	}

	private void setupBR() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("0.01"))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("6.66046"))
				.buildUpdate());
	}
	
	private void setupEu() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of(1L))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("1.00000"))
				.buildUpdate());
	}
	
	private void setupGOLD() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("0.1"))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("6.61794"))
				.buildUpdate());
	}
	
	private void setupSi() {
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of(1L))
				.withToken(SecurityField.TICK_VALUE, ofRUB5("1.00000"))
				.buildUpdate());
	}
	
	private RMContractStrategyParams commonParamsWithSlippage(int slippageStp) {
		return params = new RMContractStrategyParams(
				CDecimalBD.of("0.075"),
				CDecimalBD.of("0.012"),
				CDecimalBD.of("0.60"),
				CDecimalBD.of("1.05"),
				slippageStp
			);
	}

	@Before
	public void setUp() throws Exception {
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		security = terminal.getEditableSecurity(symbol);
		portfolio = terminal.getEditablePortfolio(account);
		portfolio.consume(new DeltaUpdateBuilder()
				.withToken(PortfolioField.BALANCE, ofRUB2("1044780.17"))
				.buildUpdate());
		d10ATR = new TSeriesImpl<>(ZTFrame.D1MSK);
		m5ATR = new TSeriesImpl<>(ZTFrame.M5MSK);
		service = new RMContractStrategy();
		service.setStrategyParams(commonParamsWithSlippage(3));
		service.setPortfolio(portfolio);
		service.setSecurity(security);
		service.setAvgDailyPriceMove(d10ATR);
		service.setAvgLocalPriceMove(m5ATR);
	}
	
	@Test
	public void testCtor() {
		assertSame(params, service.getStrategyParams());
		assertSame(portfolio, service.getPortfolio());
		assertSame(security, service.getSecurity());
		assertSame(d10ATR, service.getAvgDailyPriceMove());
		assertSame(m5ATR, service.getAvgLocalPriceMove());
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
		d10ATR.set(TIME, of("3659.02861"));
		
		RMContractStrategyPositionParams actual = service.getPositionParams();
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				52,
				of("2200"),
				of("320"),
				ofRUB2("78358.51"),
				ofRUB2("12537.36")
			);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetPositionParams_RTS_2() {
		service.setStrategyParams(commonParamsWithSlippage(3));
		setupRTS_2();
		d10ATR.set(TIME, of("3300.01726"));
		
		RMContractStrategyPositionParams actual = service.getPositionParams();
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				30,
				of("1980"),
				of("290"), // 10 * 12537.36 / 30 / 13.23588 - 10 * 3 = 315.74 - 30 = 290
				ofRUB2("78358.51"),
				ofRUB2("12537.36")
			);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetPositionParams_Si_1() {
		service.setStrategyParams(commonParamsWithSlippage(5));
		setupSi();
		d10ATR.set(TIME, of("289.02761"));
		
		RMContractStrategyPositionParams actual = service.getPositionParams();
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				453,
				of("173"),
				of("23"),
				ofRUB2("78358.51"),
				ofRUB2("12537.36")
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_Si_2() {
		service.setStrategyParams(commonParamsWithSlippage(5));
		setupSi();
		d10ATR.set(TIME, of("879.71002"));
		
		RMContractStrategyPositionParams actual = service.getPositionParams();
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				148,
				of("528"),
				of("80"), // 1 * 12537.36 / 148 / 1 - 1 * 5 = 80
				ofRUB2("78358.51"),
				ofRUB2("12537.36")
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_BR_1() {
		service.setStrategyParams(commonParamsWithSlippage(1));
		setupBR();
		d10ATR.set(TIME, of("2.9801562"));
		
		RMContractStrategyPositionParams actual = service.getPositionParams();
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				66,
				of("1.79"),
				of("0.28"), // 0.01 * 12537.36 / 66 / 6.66046 - 1 * 0.01 = 0.28520 - 0.01 = 0.28
				ofRUB2("78358.51"),
				ofRUB2("12537.36")
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_Eu_1() {
		service.setStrategyParams(commonParamsWithSlippage(2));
		setupEu();
		d10ATR.set(TIME, of("895.40182"));
		
		RMContractStrategyPositionParams actual = service.getPositionParams();
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				146,
				of("537"),
				of("84"), // 1 * 12537.36 / 146 / 1 - 2 * 1 = 86 - 2 = 84
				ofRUB2("78358.51"),
				ofRUB2("12537.36")
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_GOLD_1() {
		service.setStrategyParams(commonParamsWithSlippage(3));
		setupGOLD();
		d10ATR.set(TIME, of("10.801772"));
		
		RMContractStrategyPositionParams actual = service.getPositionParams();
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				182,
				of("6.5"),
				of("0.7"), // 0.1 / 12537.36 / 182 / 6.61794 - 3 * 0.1 = 1.0 - 0.3 = 0.7
				ofRUB2("78358.51"),
				ofRUB2("12537.36")
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_SpecialCase_ZeroBalance() {
		service.setStrategyParams(commonParamsWithSlippage(5));
		setupRTS_2();
		d10ATR.set(TIME, of("3300.01726"));
		portfolio.consume(new DeltaUpdateBuilder()
				.withToken(PortfolioField.BALANCE, ofRUB2("0.00"))
				.buildUpdate());
		
		RMContractStrategyPositionParams actual = service.getPositionParams();
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				0,
				of("0"),
				of("0"),
				ofRUB2("0.00"),
				ofRUB2("0.00")
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
				5
			));
		setupRTS_2();
		d10ATR.set(TIME, of("3300.01726"));
		
		RMContractStrategyPositionParams actual = service.getPositionParams();
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				0,
				of("0"),
				of("0"),
				ofRUB2("0.00"),
				ofRUB2("0.00")
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
				5
			));
		setupRTS_2();
		d10ATR.set(TIME, of("3300.01726"));
		
		RMContractStrategyPositionParams actual = service.getPositionParams();
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				0,
				of("0"),
				of("0"),
				ofRUB2("0.00"),
				ofRUB2("0.00")
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_SpecialCase_ZeroPriceTickSize() {
		// Normally this never should happen because tick size shall not be zero
		service.setStrategyParams(commonParamsWithSlippage(5));
		setupRTS_2();
		d10ATR.set(TIME, of("3300.01726"));
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("0"))
				.buildUpdate());
		
		RMContractStrategyPositionParams actual = service.getPositionParams();
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				0,
				of("0"),
				of("0"),
				ofRUB2("0.00"),
				ofRUB2("0.00")
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPositionParams_SpecialCase_ZeroPriceTickValue() {
		// Normally this never should happen because tick value shall not be zero
		service.setStrategyParams(commonParamsWithSlippage(5));
		setupRTS_2();
		d10ATR.set(TIME, of("3300.01726"));
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_VALUE, of("0.00000"))
				.buildUpdate());
		
		RMContractStrategyPositionParams actual = service.getPositionParams();
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				0,
				of("0"),
				of("0"),
				of("0.00"),
				of("0.00")
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
				5
			));
		setupRTS_2();
		d10ATR.set(TIME, of("3300.01726"));
		
		RMContractStrategyPositionParams actual = service.getPositionParams();
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				0,
				of("0"),
				of("0"),
				ofRUB2("0.00"),
				ofRUB2("0.00")
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
				5
			));
		setupRTS_2();
		d10ATR.set(TIME, of("3300.01726"));
		
		RMContractStrategyPositionParams actual = service.getPositionParams();
		
		RMContractStrategyPositionParams expected = new RMContractStrategyPositionParams(
				0,
				of("0"),
				of("0"),
				ofRUB2("0.00"),
				ofRUB2("0.00")
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

}
