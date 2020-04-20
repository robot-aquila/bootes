package ru.prolib.bootes.protos;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;
import static ru.prolib.bootes.protos.SOSTestUtils.*;

import java.io.File;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.SimpleCounter;
import ru.prolib.bootes.protos.sos.*;

public class ComplexSystemIT {
	static final File dataDir = new File("./../shared/canned-data");
	static final File reportDir = new File("tmp/it-reports");

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testCase_TwoConsecutiveOrdersInTheMorning() throws Throwable {
		OrderDefinitionBuilder odb = new OrderDefinitionBuilder()
				.withAccount(new Account("SOS-TEST-ACCOUNT"))
				.withSymbol(new Symbol("RTS-3.17"))
				.withMaxExecutionTime(120000L);
		OrderDefinitionProvider signal_provider = new OrderDefinitionProviderStub(
			new CloseableIteratorStub<OrderDefinition>(Arrays.asList(
				odb.withLimitBuy(of(1L), of(116470L)).withPlacementTime(ZT("2017-01-03T10:15:00")).buildDefinition(),
				odb.withLimitSell(of(1L), of(116570L)).withPlacementTime(ZT("2017-01-03T10:35:00")).buildDefinition()
			))
		);
		File report_dir = new File(reportDir, "sos_2coitm");
		SOSApp app = new SOSApp(signal_provider);
		SimpleCounter score = new SimpleCounter();
		app.onInit(at("2017-01-03T10:14:59.999", testPositionObjectNotExists(score)
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("1000000.0"), score))
				.and(testPortfolioFreeMargin(ofRUB5("1000000.0"), score))
				.and(testPortfolioUsedMargin(ofRUB5("0.0"), score))
				.and(testPortfolioPnL(ofRUB5("0.0"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		app.onInit(at("2017-01-03T10:15:00.001", testPositionCurrentVolume(of(1L), score)
				// recalculated with each execution, according to the last one the price was 116470
				.and(testPositionCurrentPrice(of(116470L), score)) 
				.and(testPositionOpenPrice(of(116470L), score))
				.and(testPositionUsedMargin(ofRUB5("21244.42"), score))
				.and(testPositionPnL(ofRUB5("0.0"), score))
				.and(testPositionVarMargin(ofRUB5("0.0"), score))
				.and(testPositionVarMarginInter(ofRUB5("0.0"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(ofRUB5("12.20892"), score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("1000000.0"), score))
				.and(testPortfolioFreeMargin(ofRUB5("978755.58"), score))
				.and(testPortfolioUsedMargin(ofRUB5("21244.42"), score))
				.and(testPortfolioPnL(ofRUB5("0.0"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		app.onInit(at("2017-01-03T10:15:05.001", testPositionCurrentVolume(of(1L), score)
				.and(testPositionCurrentPrice(of(116510L), score))
				.and(testPositionOpenPrice(of(116470L), score))
				.and(testPositionUsedMargin(ofRUB5("21244.42"), score))
				.and(testPositionPnL(ofRUB5("48.83568"), score))
				.and(testPositionVarMargin(ofRUB5("48.83568"), score))
				.and(testPositionVarMarginInter(ofRUB5("0.0"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(ofRUB5("12.20892"), score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("1000048.83568"), score))
				.and(testPortfolioFreeMargin(ofRUB5("978804.41568"), score))
				.and(testPortfolioUsedMargin(ofRUB5("21244.42"), score))
				.and(testPortfolioPnL(ofRUB5("48.83568"), score))
				.and(testPortfolioVarMargin(ofRUB5("48.83568"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		app.onInit(at("2017-01-03T10:25:00.001", testPositionCurrentVolume(of(1L), score)
				.and(testPositionCurrentPrice(of(116380L), score))
				.and(testPositionOpenPrice(of(116470L), score))
				.and(testPositionUsedMargin(ofRUB5("21244.42"), score))
				.and(testPositionPnL(ofRUB5("-109.88028"), score))
				.and(testPositionVarMargin(ofRUB5("-109.88028"), score))
				.and(testPositionVarMarginInter(ofRUB5("0.0"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(ofRUB5("12.20892"), score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("999890.11972"), score))
				.and(testPortfolioFreeMargin(ofRUB5("978645.69972"), score))
				.and(testPortfolioUsedMargin(ofRUB5("21244.42"), score))
				.and(testPortfolioPnL(ofRUB5("-109.88028"), score))
				.and(testPortfolioVarMargin(ofRUB5("-109.88028"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		// The second order shouldn't be executed exactly when placed at 10:35:00
		// because of the price 116570 when first tick comes at 10:35:04
		SOSExtension test_set;
		app.onInit(at("2017-01-03T10:35:04.001", test_set = testPositionCurrentVolume(of(0L), score)
				.and(testPositionCurrentPrice(of(0L), score))
				.and(testPositionOpenPrice(of(0L), score))
				.and(testPositionUsedMargin(ofRUB5("0.0"), score))
				.and(testPositionPnL(ofRUB5("122.08920"), score))
				.and(testPositionVarMargin(ofRUB5("0.0"), score))
				.and(testPositionVarMarginInter(ofRUB5("0.0"), score))
				// 100pts = 10steps = 10 * 12.20892 = 122.08920
				.and(testPositionVarMarginClose(ofRUB5("122.08920"), score))
				.and(testPositionTickValue(null, score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("1000122.08920"), score))
				.and(testPortfolioFreeMargin(ofRUB5("1000122.08920"), score))
				.and(testPortfolioUsedMargin(ofRUB5("0.0"), score))
				.and(testPortfolioPnL(ofRUB5("122.08920"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("122.08920"), score))
			));
		// Test that nothing is changed after regular portfolio updates
		app.onInit(at("2017-01-03T10:35:10.001", test_set));
		app.onInit(at("2017-01-03T13:59:59.999", test_set));
		// After mid clearing
		app.onInit(at("2017-01-03T14:05:00.000", testPositionCurrentVolume(of(0L), score)
				.and(testPositionCurrentPrice(of(0L), score))
				.and(testPositionOpenPrice(of(0L), score))
				.and(testPositionUsedMargin(ofRUB5("0.0"), score))
				.and(testPositionPnL(ofRUB5("122.08920"), score))
				.and(testPositionVarMargin(ofRUB5("0.0"), score))
				.and(testPositionVarMarginInter(ofRUB5("122.08920"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(null, score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("1000122.08920"), score))
				.and(testPortfolioFreeMargin(ofRUB5("1000122.08920"), score))
				.and(testPortfolioUsedMargin(ofRUB5("0.0"), score))
				.and(testPortfolioPnL(ofRUB5("122.08920"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("122.08920"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		// After main clearing
		app.onInit(at("2017-01-03T19:00:00.000", testPositionCurrentVolume(of(0L), score)
				.and(testPositionCurrentPrice(of(0L), score))
				.and(testPositionOpenPrice(of(0L), score))
				.and(testPositionUsedMargin(ofRUB5("0.0"), score))
				.and(testPositionPnL(ofRUB5("0.0"), score))
				.and(testPositionVarMargin(ofRUB5("0.0"), score))
				.and(testPositionVarMarginInter(ofRUB5("0.0"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(null, score))
				.and(testPortfolioBalance(ofRUB5("1000122.08920"), score))
				.and(testPortfolioEquity(ofRUB5("1000122.08920"), score))
				.and(testPortfolioFreeMargin(ofRUB5("1000122.08920"), score))
				.and(testPortfolioUsedMargin(ofRUB5("0.0"), score))
				.and(testPortfolioPnL(ofRUB5("0.0"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		
		app.run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-03T00:00:00Z",
				"--probe-stop-time=2017-01-04T00:00:00Z",
				"--probe-auto-shutdown",
				"--headless",
				"--probe-auto-start",
				"--qforts-order-exec-trigger-mode=0"
			));
		
		assertEquals(145, score.get());
	}

	@Test
	public void testCase_TwoOrdersGoThruMidClearing() throws Throwable {
		OrderDefinitionBuilder odb = new OrderDefinitionBuilder()
				.withAccount(new Account("SOS-TEST-ACCOUNT"))
				.withSymbol(new Symbol("RTS-3.17"))
				.withMaxExecutionTime(120000L);
		OrderDefinitionProvider signal_provider = new OrderDefinitionProviderStub(
			new CloseableIteratorStub<OrderDefinition>(Arrays.asList(
				odb.withPlacementTime(ZT("2017-01-03T12:00:00")).withLimitSell(of(5L), of(116880L)).buildDefinition(),
				odb.withPlacementTime(ZT("2017-01-03T14:45:02")).withLimitBuy( of(5L), of(119100L)).buildDefinition()
			))
		);
		File report_dir = new File(reportDir, "sos_2ogtmc");
		SOSApp app = new SOSApp(signal_provider);
		SimpleCounter score = new SimpleCounter();
		app.onInit(at("2017-01-03T11:59:59.999", testPositionObjectNotExists(score)
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("1000000.0"), score))
				.and(testPortfolioFreeMargin(ofRUB5("1000000.0"), score))
				.and(testPortfolioUsedMargin(ofRUB5("0.0"), score))
				.and(testPortfolioPnL(ofRUB5("0.0"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		// at this time just 2 contracts filled
		app.onInit(at("2017-01-03T12:00:00.001", testPositionCurrentVolume(of(-2L), score)
				.and(testPositionCurrentPrice(of(-233760L), score))
				.and(testPositionOpenPrice(of(-233760L), score))
				.and(testPositionUsedMargin(ofRUB5("42488.84"), score)) // 21244.42 * 2 = 42488.84 
				.and(testPositionPnL(ofRUB5("0.0"), score)) // not yet calculated
				.and(testPositionVarMargin(ofRUB5("0.0"), score))
				.and(testPositionVarMarginInter(ofRUB5("0.0"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(ofRUB5("12.20892"), score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("1000000.0"), score))
				.and(testPortfolioFreeMargin(ofRUB5("957511.16"), score))
				.and(testPortfolioUsedMargin(ofRUB5("42488.84"), score))
				.and(testPortfolioPnL(ofRUB5("0.0"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		app.onInit(at("2017-01-03T12:00:03.000", testPositionCurrentVolume(of(-5L), score)
				.and(testPositionCurrentPrice(of(-584400L), score))
				.and(testPositionOpenPrice(of(-584400L), score))
				.and(testPositionUsedMargin(ofRUB5("106222.1"), score))
				.and(testPositionPnL(ofRUB5("0.0"), score))
				.and(testPositionVarMargin(ofRUB5("0.0"), score))
				.and(testPositionVarMarginInter(ofRUB5("0.0"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(ofRUB5("12.20892"), score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("1000000.0"), score))
				.and(testPortfolioUsedMargin(ofRUB5("106222.1"), score))
				.and(testPortfolioFreeMargin(ofRUB5("893777.9"), score))
				.and(testPortfolioPnL(ofRUB5("0.0"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		// Before clearing. It was at 13:59:55 because QF consider 14:00 as a part of intermediate clearing
		// Moving 14:00 to session phase make no sense because security property update scheduled at 14:00
		// thus any work will take into account already updated values
		app.onInit(at("2017-01-03T13:59:55.001", testPositionCurrentVolume(of(-5L), score)
				.and(testPositionCurrentPrice(of(-594000L), score)) // 118800
				.and(testPositionOpenPrice(of(-584400L), score))
				.and(testPositionUsedMargin(ofRUB5("106222.1"), score))
				.and(testPositionPnL(ofRUB5("-11720.5632"), score))
				.and(testPositionVarMargin(ofRUB5("-11720.5632"), score))
				.and(testPositionVarMarginInter(ofRUB5("0.0"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(ofRUB5("12.20892"), score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("988279.4368"), score))
				.and(testPortfolioUsedMargin(ofRUB5("106222.1"), score))
				.and(testPortfolioFreeMargin(ofRUB5("882057.3368"), score))
				.and(testPortfolioPnL(ofRUB5("-11720.5632"), score))
				.and(testPortfolioVarMargin(ofRUB5("-11720.5632"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		// After clearing
		app.onInit(at("2017-01-03T14:04:00.001", testPositionCurrentVolume(of(-5L), score)
				.and(testPositionCurrentPrice(of(-594050L), score)) // 118810
				.and(testPositionOpenPrice(of(-594050L), score)) // -9650pts = -965 steps = -11781.6078 
				.and(testPositionUsedMargin(ofRUB5("108859.05"), score)) // 21771.81 * 5 = 108859.05
				.and(testPositionPnL(ofRUB5("-11781.6078"), score))
				.and(testPositionVarMargin(ofRUB5("0.0"), score))
				.and(testPositionVarMarginInter(ofRUB5("-11781.6078"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(ofRUB5("12.13972"), score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("988218.3922"), score))
				.and(testPortfolioUsedMargin(ofRUB5("108859.05"), score))
				.and(testPortfolioFreeMargin(ofRUB5("879359.3422"), score))
				.and(testPortfolioPnL(ofRUB5("-11781.6078"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("-11781.6078"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		// Before closing
		app.onInit(at("2017-01-03T14:45:00.001", testPositionCurrentVolume(of(-5L), score)
				.and(testPositionCurrentPrice(of(-595600L), score)) // 119120
				.and(testPositionOpenPrice(of(-594050L), score))
				.and(testPositionUsedMargin(ofRUB5("108859.05"), score))
				.and(testPositionPnL(ofRUB5("-13663.2644"), score)) // -1550pts = -155 steps = -1881.6566 + -11781.6078
				.and(testPositionVarMargin(ofRUB5("-1881.6566"), score))
				.and(testPositionVarMarginInter(ofRUB5("-11781.6078"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(ofRUB5("12.13972"), score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("986336.7356"), score))
				.and(testPortfolioUsedMargin(ofRUB5("108859.05"), score))
				.and(testPortfolioFreeMargin(ofRUB5("877477.6856"), score))
				.and(testPortfolioPnL(ofRUB5("-13663.2644"), score))
				.and(testPortfolioVarMargin(ofRUB5("-1881.6566"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("-11781.6078"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		// 14:45:03 closed 2x 119090
		app.onInit(at("2017-01-03T14:45:03.001", testPositionCurrentVolume(of(-3L), score)
				.and(testPositionCurrentPrice(of(-357270L), score)) // 119090 * 3 = 357270
				.and(testPositionOpenPrice(of(-356430L), score)) // 118810 * 3
				.and(testPositionUsedMargin(ofRUB5("65315.43"), score)) // 21771.81 * 3 = 65315.43
				.and(testPositionPnL(ofRUB5("-13481.1686"), score))
				.and(testPositionVarMargin(ofRUB5("-1019.73648"), score))
				.and(testPositionVarMarginInter(ofRUB5("-11781.6078"), score))
				.and(testPositionVarMarginClose(ofRUB5("-679.82432"), score))
				.and(testPositionTickValue(ofRUB5("12.13972"), score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("986518.8314"), score))
				.and(testPortfolioUsedMargin(ofRUB5("65315.43"), score))
				.and(testPortfolioFreeMargin(ofRUB5("921203.4014"), score))
				.and(testPortfolioPnL(ofRUB5("-13481.1686"), score))
				.and(testPortfolioVarMargin(ofRUB5("-1019.73648"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("-11781.6078"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("-679.82432"), score))
			));
		// 14:45:04 closed 3x 119100
		SOSExtension test_set;
		app.onInit(at("2017-01-03T14:45:04.001", test_set = testPositionCurrentVolume(of(0L), score)
				.and(testPositionCurrentPrice(of(0L), score))
				.and(testPositionOpenPrice(of(0L), score))
				.and(testPositionUsedMargin(ofRUB5("0.0"), score))
				.and(testPositionPnL(ofRUB5("-13517.58776"), score))
				.and(testPositionVarMargin(ofRUB5("0.0"), score))
				.and(testPositionVarMarginInter(ofRUB5("-11781.6078"), score))
				.and(testPositionVarMarginClose(ofRUB5("-1735.97996"), score))
				.and(testPositionTickValue(null, score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("986482.41224"), score))
				.and(testPortfolioUsedMargin(ofRUB5("0.0"), score))
				.and(testPortfolioFreeMargin(ofRUB5("986482.41224"), score))
				.and(testPortfolioPnL(ofRUB5("-13517.58776"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("-11781.6078"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("-1735.97996"), score))
			));
		app.onInit(at("2017-01-03T18:45:00.000", test_set));
		app.onInit(at("2017-01-03T19:00:00.000", testPositionCurrentVolume(of(0L), score)
				.and(testPositionCurrentPrice(of(0L), score))
				.and(testPositionOpenPrice(of(0L), score))
				.and(testPositionUsedMargin(ofRUB5("0.0"), score))
				.and(testPositionPnL(ofRUB5("0.0"), score))
				.and(testPositionVarMargin(ofRUB5("0.0"), score))
				.and(testPositionVarMarginInter(ofRUB5("0.0"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(null, score))
				.and(testPortfolioBalance(ofRUB5("986482.41224"), score))
				.and(testPortfolioEquity(ofRUB5("986482.41224"), score))
				.and(testPortfolioUsedMargin(ofRUB5("0.0"), score))
				.and(testPortfolioFreeMargin(ofRUB5("986482.41224"), score))
				.and(testPortfolioPnL(ofRUB5("0.0"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));

		app.run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-03T00:00:00Z",
				"--probe-stop-time=2017-01-04T00:00:00Z",
				"--probe-auto-shutdown",
				"--headless",
				"--probe-auto-start",
				"--qforts-order-exec-trigger-mode=0"
			));
		
		assertEquals(162, score.get());
	}

	@Test
	public void testCase_TwoOrdersGoThruEveningClearing() throws Throwable {
		OrderDefinitionBuilder odb = new OrderDefinitionBuilder()
				.withAccount(new Account("SOS-TEST-ACCOUNT"))
				.withSymbol(new Symbol("RTS-3.17"))
				.withMaxExecutionTime(120000L);
		OrderDefinitionProvider signal_provider = new OrderDefinitionProviderStub(
			new CloseableIteratorStub<OrderDefinition>(Arrays.asList(
				odb.withPlacementTime(ZT("2017-01-03T15:08:32")).withLimitBuy( of(2L), of(118920L)).buildDefinition(),
				odb.withPlacementTime(ZT("2017-01-03T19:15:00")).withLimitSell(of(2L), of(118670L)).buildDefinition()
			))
		);
		File report_dir = new File(reportDir, "sos_2ogtec");
		SOSApp app = new SOSApp(signal_provider);
		SimpleCounter score = new SimpleCounter();
		app.onInit(at("2017-01-03T15:08:31.999", testPositionObjectNotExists(score)
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("1000000.0"), score))
				.and(testPortfolioFreeMargin(ofRUB5("1000000.0"), score))
				.and(testPortfolioUsedMargin(ofRUB5("0.0"), score))
				.and(testPortfolioPnL(ofRUB5("0.0"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		app.onInit(at("2017-01-03T15:08:37.001", testPositionCurrentVolume(of(2L), score)
				.and(testPositionCurrentPrice(of(237840L), score)) // 118920 * 2 = 237840
				.and(testPositionOpenPrice(of(237840L), score))
				.and(testPositionUsedMargin(ofRUB5("43543.62"), score)) // 21771.81 * 2 = 43543.62
				.and(testPositionPnL(ofRUB5("0.0"), score))
				.and(testPositionVarMargin(ofRUB5("0.0"), score))
				.and(testPositionVarMarginInter(ofRUB5("0.0"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(ofRUB5("12.13972"), score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("1000000.0"), score))
				.and(testPortfolioUsedMargin(ofRUB5("43543.62"), score))
				.and(testPortfolioFreeMargin(ofRUB5("956456.38"), score))
				.and(testPortfolioPnL(ofRUB5("0.0"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		app.onInit(at("2017-01-03T16:00:00.001", testPositionCurrentVolume(of(2L), score)
				.and(testPositionCurrentPrice(of(238260L), score)) // 119130 * 2 = 238260
				.and(testPositionOpenPrice(of(237840L), score))
				.and(testPositionUsedMargin(ofRUB5("43543.62"), score))
				.and(testPositionPnL(ofRUB5("509.86824"), score))
				.and(testPositionVarMargin(ofRUB5("509.86824"), score))
				.and(testPositionVarMarginInter(ofRUB5("0.0"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(ofRUB5("12.13972"), score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("1000509.86824"), score))
				.and(testPortfolioUsedMargin(ofRUB5("43543.62"), score))
				.and(testPortfolioFreeMargin(ofRUB5("956966.24824"), score))
				.and(testPortfolioPnL(ofRUB5("509.86824"), score))
				.and(testPortfolioVarMargin(ofRUB5("509.86824"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		app.onInit(at("2017-01-03T18:44:55.001", testPositionCurrentVolume(of(2L), score)
				.and(testPositionCurrentPrice(of(238000L), score)) // 119000 * 2 = 238000
				.and(testPositionOpenPrice(of(237840L), score))
				.and(testPositionUsedMargin(ofRUB5("43543.62"), score))
				.and(testPositionPnL(ofRUB5("194.23552"), score))
				.and(testPositionVarMargin(ofRUB5("194.23552"), score))
				.and(testPositionVarMarginInter(ofRUB5("0.0"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(ofRUB5("12.13972"), score))
				.and(testPortfolioBalance(ofRUB5("1000000.0"), score))
				.and(testPortfolioEquity(ofRUB5("1000194.23552"), score))
				.and(testPortfolioUsedMargin(ofRUB5("43543.62"), score))
				.and(testPortfolioFreeMargin(ofRUB5("956650.61552"), score))
				.and(testPortfolioPnL(ofRUB5("194.23552"), score))
				.and(testPortfolioVarMargin(ofRUB5("194.23552"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		app.onInit(at("2017-01-03T18:59:59.999", testPositionCurrentVolume(of(2L), score)
				.and(testPositionCurrentPrice(of(238000L), score)) // 119000 * 2 = 238000
				.and(testPositionOpenPrice(of(238000L), score))
				.and(testPositionUsedMargin(ofRUB5("43453.64"), score)) // 21726.82 * 2 = 43453.64
				.and(testPositionPnL(ofRUB5("0.0"), score))
				.and(testPositionVarMargin(ofRUB5("0.0"), score))
				.and(testPositionVarMarginInter(ofRUB5("0.0"), score))
				.and(testPositionVarMarginClose(ofRUB5("0.0"), score))
				.and(testPositionTickValue(ofRUB5("12.09928"), score))
				.and(testPortfolioBalance(ofRUB5("1000194.23552"), score))
				.and(testPortfolioEquity(ofRUB5("1000194.23552"), score))
				.and(testPortfolioUsedMargin(ofRUB5("43453.64"), score))
				.and(testPortfolioFreeMargin(ofRUB5("956740.59552"), score))
				.and(testPortfolioPnL(ofRUB5("0.0"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("0.0"), score))
			));
		SOSExtension test_set;
		// 19:15 @118680L
		app.onInit(at("2017-01-03T19:15:01.001", test_set = testPositionCurrentVolume(of(0L), score)
				.and(testPositionCurrentPrice(of(0L), score))
				.and(testPositionOpenPrice(of(0L), score))
				.and(testPositionUsedMargin(ofRUB5("0.0"), score))
				.and(testPositionPnL(ofRUB5("-774.35392"), score))
				.and(testPositionVarMargin(ofRUB5("0.0"), score))
				.and(testPositionVarMarginInter(ofRUB5("0.0"), score))
				.and(testPositionVarMarginClose(ofRUB5("-774.35392"), score))
				.and(testPositionTickValue(null, score))
				.and(testPortfolioBalance(ofRUB5("1000194.23552"), score))
				.and(testPortfolioEquity(ofRUB5("999419.8816"), score))
				.and(testPortfolioUsedMargin(ofRUB5("0.0"), score))
				.and(testPortfolioFreeMargin(ofRUB5("999419.8816"), score))
				.and(testPortfolioPnL(ofRUB5("-774.35392"), score))
				.and(testPortfolioVarMargin(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginInter(ofRUB5("0.0"), score))
				.and(testPortfolioVarMarginClose(ofRUB5("-774.35392"), score))
			));
		app.onInit(at("2017-01-03T23:50:50.000", test_set));
		app.onInit(at("2017-01-04T10:00:00.001", test_set));
		
		app.run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-03T00:00:00Z",
				"--probe-stop-time=2017-01-04T08:00:00Z",
				"--probe-auto-shutdown",
				"--headless",
				"--probe-auto-start",
				"--qforts-order-exec-trigger-mode=0"
			));
		
		assertEquals(128, score.get());
	}

}
