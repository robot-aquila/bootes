package ru.prolib.bootes.protos;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.probe.datasim.l1.L1UpdateReaderFactory;
import ru.prolib.aquila.web.utils.finam.data.FinamData;
import ru.prolib.bootes.lib.report.ReportComparator;
import ru.prolib.bootes.lib.report.STRCmpResult;
import ru.prolib.bootes.lib.report.order.OrderExecInfo;
import ru.prolib.bootes.lib.report.order.OrderInfo;
import ru.prolib.bootes.lib.report.order.OrderReport;

public class PROTOS_IT {
	static final File dataDir = new File("./../shared/canned-data");
	static final File reportDir = new File("tmp/it-reports");
	static final File EXPECTED_LONG = new File("fixture", "protos-long.rep");
	static final File EXPECTED_SHORT = new File("fixture", "protos-short.rep");
	static L1UpdateReaderFactory l1uReaderFactory;
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if ( reportDir.exists() ) {
			FileUtils.forceDelete(reportDir);
		}
		reportDir.mkdirs();
		l1uReaderFactory = new FinamData().createUpdateReaderFactory(dataDir);
	}
	
	static void assertOrderHasNoExecutionsInThePast(OrderReport report) {
		for ( OrderInfo order_info : report.getOrders() ) {
			for ( OrderExecInfo exec_info : order_info.getExecutions() ) {
				assertThat(new StringBuilder()
						.append("Execution #")
						.append(exec_info.getNum())
						.append(" of order #")
						.append(order_info.getNum())
						.append(" time in the past")
						.toString(),
						exec_info.getTime(), greaterThanOrEqualTo(order_info.getTime()));
			}
		}
	}
	
	static void assertAllOrdersExecutedCompletely(OrderReport report) {
		for ( OrderInfo order_info : report.getOrders() ) {
			CDecimal total_qty = ZERO;
			for ( OrderExecInfo exec_info : order_info.getExecutions() ) {
				total_qty = exec_info.getQty().add(total_qty);
			}
			assertEquals(new StringBuilder()
					.append("Order #")
					.append(order_info.getNum())
					.append(" does not executed completely")
					.toString(), order_info.getQty(), total_qty);
		}
	}
	
	private static int getTickNumber(OrderExecInfo exec_info) {
		Pattern p = Pattern.compile("#0*(\\d+)/");
		//Matcher m = p.matcher("20170125130000#0000000002/s0_c1");
		Matcher m = p.matcher(exec_info.getExternalID());
		if ( m.find() ) {
			return Integer.parseInt(m.group(1));
		}
		throw new IllegalStateException("Cannot parse ext.ID: " + exec_info.getExternalID());
	}
	
	static void assertFirstExecutionDoesNotStartFromFirstTick(OrderReport report) {
		for ( OrderInfo order_info : report.getOrders() ) {
			List<OrderExecInfo> execs = new ArrayList<>(order_info.getExecutions());
			if ( execs.size() > 0 ) {
				OrderExecInfo exec_info = execs.get(0);
				if ( order_info.getTime().equals(exec_info.getTime())) {
					int tick_number = getTickNumber(exec_info);
					if ( tick_number == 1 ) {
						LocalTime order_time = order_info.getTime().atZone(ZoneId.of("Europe/Moscow")).toLocalTime();
						boolean r = order_time.equals(LocalTime.of(13, 50)) || order_time.equals(LocalTime.of(18, 30));
						assertTrue(new StringBuilder()
								.append("First execution of order #")
								.append(order_info.getNum())
								.append(" based on a first tick: ")
								.append(exec_info.getExternalID())
								.toString(), r);
					} else {
						assertThat(new StringBuilder()
								.append("First execution of order #")
								.append(order_info.getNum())
								.append(" based on wrong tick: ")
								.append(exec_info.getExternalID())
								.toString(), tick_number, greaterThanOrEqualTo(2));
					}
				}
			}
		}
	}
	
	@After
	public void tearDown() throws Exception {

	}
	
	public String[] args(String... args) {
		List<String> arg_list = new ArrayList<>(Arrays.asList(args));
		return arg_list.toArray(new String[0]);
	}
	
	static void assertReportFiles_V2(File expected, File actual) throws Exception {
		STRCmpResult result = ReportComparator.getInstance().compare(expected, actual);
		assertTrue(String.format("exp: %s\nact: %s\n%s", expected, actual, result.toString()), result.identical());		
	}
	
	static void assertReports(File expected, File actual) throws Exception {
		assertReportFiles_V2(expected, actual);
	}
	
	static Tick searchSuitableTick(CloseableIterator<L1Update> it, OrderAction action, CDecimal price) throws Exception {
		for ( ;; ) {
			assertTrue("No more ticks", it.next());
			Tick tick = it.item().getTick();
			if ( action == OrderAction.BUY || action == OrderAction.COVER ) {
				if ( price.compareTo(tick.getPrice()) >= 0 ) {
					//System.out.println("For action " + action + " and price " + price + " found tick: " + tick);
					return tick;
				}
			}
			if ( action == OrderAction.SELL || action == OrderAction.SELL_SHORT ) {
				if ( price.compareTo(tick.getPrice()) <= 0 ) {
					//System.out.println("For action " + action + " and price " + price + " found tick: " + tick);
					return tick;
				}
			}
		}
	}
	
	@Test
	public void testPass1_OldOrderExecTriggerMode_L1AGGR() throws Throwable {
		File rd_pass1 = new File(reportDir, "pass1_old-oetm");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + rd_pass1,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless",
				"--qforts-order-exec-trigger-mode=0"
			));
		assertReports(EXPECTED_SHORT, new File(rd_pass1, "protos1.report"));
	}

	@Test
	public void testPass1_NewOrderExecTriggerMode_L1AGGR() throws Throwable {
		// Здесь разница с OLD OETM в том, что срабатывает на втором тике.
		// Поскольку OHLC провайдер здесь на базе тиков L1, то на момент
		// выставления заявки, при наличии тиков на момент выставления заявки,
		// симуляция первого тика из серии уже выполнена. Именно она привела
		// к срабатыванию триггера и генерации сигнала.
		//----------------------------------
		// Почему первый тик в OLD OETM попадает как в OHLC агрегацию, так и
		// успевает послужить основанием для сделки по выставленной заявке?
		// OHLC агрегатор подписывается на событие инструмента, а реактор QFORTS
		// подписывается на событие терминала. События терминала - это
		// альтернативный тип для соответствующего типа событий в инструменте.
		// Процессинг событий сначала диспатчик события прямым наблюдателям типа,
		// а только потом это событие транслируется дальше по иерархии наблюдателям
		// альтернативных типов. Регистрация заявки происходит моментально в момент
		// ее подачи. И когда событие доходит до QFORTS реактора, заявка уже
		// зарегистрирована и готова к исполнению. Таким образом, это скорее
		// побочный эффект, который дал корректный лишь на первый взгляд результат.
		//----------------------------------
		// Из вышеуказанного следует, что все заявки должны исполняться как минимум
		// начиная со второго тика в последовательности тиков единой временной точки.
		// Также последовательность исполнений может начать более поздний тик набора
		// в том случае, если предыдущие не удовлетворяли условию цены. Это справедливо
		// для временных точек, выровненных по границе таймфрейма (в данном случае M5).
		// То есть, если первая сделка по ордеру ровно в 0, 5, 10, 15, 20 etc минут,
		// то она должна начинаться со второго тика. Но на границе свечи может и не
		// быть подходящих по цене данных. По этому внутри свечи открывать
		// последовательность может и первый тик.
		//----------------------------------
		// Есть одно исключение из вышесказанного. Заявка может быть на границе свечи
		// и иметь исполнения начиная с первого тика. Это касается заявок закрытия
		// позы при выходе за пределы торгового расписания. В этом случае триггером
		// является переход к определенной временной точке. Этот переход запланирован
		// (зашедулен) в момент входа в состояние трекинга позы. Между подачи данной
		// задачи планировщику и ее исполнением обычно следует множество тиков.
		// Поскольку реплей тика подается планировщику позже чем выход из позы по
		// таймауту, выход по таймауту срабатывает прежде чем симулируется тик на
		// границе свечи. Или выражаясь конкретно, наше расписание содержит
		// принудительное закрытие позы в 18:30. И исполнение этой задачи инициируется
		// раньше, чем симуляция тика на 18:30. Таким образом, при выходе по таймауту
		// новая заявка успевает получить первое исполнение первым тиком на 18:30.
		// Сейчас расписание 10:30-13:50 и 14:10-18:30. Это значит что с первого тика
		// могут начинаться заявки, выставлнные в 13:50 и 18:30.
		//----------------------------------
		// Вышесказанное справедливо для комбинации NEW OrderExecTriggerMode и
		// L1 OHLC aggregator, при котором L1 поступают в течение всего периода торгов,
		// поскольку данные L1 затребованы OHLC агрегатором с начала сессии и до
		// закрытия. При использовании иного агрегатора ситуация, вероятно, будет иной.
		//----------------------------------
		// Итого: Какие дополнительные тесты нужны?
		// По заявкам и исполнениям:
		// 1) Заявки датированные 13:50 и 18:30 обязан иметь первый экзекьюшен
		// основанный на первом тике последовательности тиков данной временной
		// точки. Будем считать, что обязан, так как это про фьюч RTS, чья ликвидность
		// высока.
		// 2) Для всех остальных, если первое исполнение совпадает по времени с
		// временем заявки, то первое исполнение должно указывать идентификатором на
		// второй тик последовательности тиков.
		// 3) Все заявки должны быть полностью исполнены
		// 4) Время экзекьюшена не должно быть в прошлом относительно времени заявки
		
		File rd_pass1 = new File(reportDir, "pass1_new-oetm_l1aggr");
		PROTOS protos = new PROTOS();
		protos.run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + rd_pass1,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless",
				"--qforts-order-exec-trigger-mode=1"
			));
		
		OrderReport report = protos.getReports("protos1").getOrderReport();
		assertOrderHasNoExecutionsInThePast(report);
		assertAllOrdersExecutedCompletely(report);
		assertFirstExecutionDoesNotStartFromFirstTick(report);
				
		assertReports(new File("fixture", "protos-short_new-oetm_l1aggr.rep"), new File(rd_pass1, "protos1.report"));
	}
	
	@Test
	public void testPass1_NewOrderExecTriggerMode_OHLC() throws Throwable {
		File rd_pass1 = new File(reportDir, "pass1_new-oetm_ohlc");
		PROTOS protos = new PROTOS();
		protos.run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + rd_pass1,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless",
				"--qforts-order-exec-trigger-mode=1",
				"--protos-use-ohlc-provider"
			));
		
		OrderReport report = protos.getReports("protos1").getOrderReport();
		assertOrderHasNoExecutionsInThePast(report);
		assertAllOrdersExecutedCompletely(report);
		// first execution should start from first tick
		List<OrderInfo> orders = new ArrayList<>(report.getOrders());
		for ( int j = 0; j < orders.size(); j ++ ) {
			if ( j % 2 != 0 ) {
				// Нет смысла проверять закрывающую заявку, так как в большинстве случаев ее выставление
				// является результатом анализа последовательности тиков. Условие выхода из позиции
				// может сработать на любом тике последовательности, не обязательно на первом (скорее обязательно
				// не на первом). Проверять можно только четные, открывающие  заявки, так как они всегда выровнены
				// по границе свечи.
				continue;
			}
			OrderInfo order = orders.get(j);
			try ( CloseableIterator<L1Update> it = l1uReaderFactory.createReader(order.getSymbol(), order.getTime()) ) {
				List<OrderExecInfo> executions = new ArrayList<>(order.getExecutions());
				for ( int i = 0; i < executions.size(); i ++ ) {
					boolean is_last_exec = i == executions.size() - 1;
					OrderExecInfo exec = executions.get(i);
					Tick tick = searchSuitableTick(it, order.getAction(), order.getPrice());
					assertEquals(new StringBuilder()
							.append("Order ")
							.append(j + 1)
							.append(" execution ")
							.append(i + 1)
							.append(" price mismatch")
							.toString(), exec.getPrice(), tick.getPrice());
					if ( is_last_exec ) {
						//assertThat(tick.getSize().compareTo(exec.getQty()), greaterThanOrEqualTo(0));
						assertThat(tick.getSize(), greaterThanOrEqualTo(exec.getQty()));
					} else {
						assertEquals(tick.getSize(), exec.getQty());
					}
				}
			}
		}
		
		assertReports(new File("fixture", "protos-short_new-oetm_ohlc.rep"), new File(rd_pass1, "protos1.report"));
	}
	
	@Test
	public void testPass3_OldOETM_SevaralRobots_LiquidityMode1() throws Throwable {
		File rd_pass3 = new File(reportDir, "pass3_old-oetm_sev-robots_lm1");
		new PROTOS(3).run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + rd_pass3,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless",
				"--qforts-liquidity-mode=1",
				"--qforts-order-exec-trigger-mode=0"
			));
		assertReports(EXPECTED_SHORT, new File(rd_pass3, "protos1.report"));
		assertReports(EXPECTED_SHORT, new File(rd_pass3, "protos2.report"));
		assertReports(EXPECTED_SHORT, new File(rd_pass3, "protos3.report"));
	}

/*
	@Test
	public void testPass1_OldOrderExecTriggerMode_ohlc() throws Throwable {
		File rd_pass1 = new File(reportDir, "pass1_old-oetm_ohlc");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + rd_pass1,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--qforts-order-exec-trigger-mode=0",
				"--protos-use-ohlc-provider"
			));
		assertReports(EXPECTED_SHORT, new File(rd_pass1, "protos1.report"));
	}
	
	
	
	
	@Ignore
	@Test
	public void testPass1_WithLegacySDS() throws Throwable {
		File report_dir = new File(reportDir, "pass1_legacy_sds");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--qforts-legacy-sds"
			));
		assertReports(EXPECTED_SHORT, new File(report_dir, "protos1.report"));
	}
	
	@Ignore
	@Test
	public void testPass2() throws Throwable {
		File rd_pass2 = new File(reportDir, "pass2");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + rd_pass2,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless"
			));
		assertReports(EXPECTED_SHORT, new File(rd_pass2, "protos1.report"));
	}
	
	@Ignore
	@Test
	public void testPass2_WithLegacySDS() throws Throwable {
		File report_dir = new File(reportDir, "pass2_legacy_sds");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless",
				"--qforts-legacy-sds"
			));
		assertReports(EXPECTED_SHORT, new File(report_dir, "protos1.report"));
	}
	
	@Ignore
	@Test
	public void testPass3_WithLegacySDS() throws Throwable {
		File report_dir = new File(reportDir, "pass3_legacy_sds");
		new PROTOS(3).run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless",
				"--qforts-liquidity-mode=1",
				"--qforts-legacy-sds"
			));
		assertReports(EXPECTED_SHORT, new File(report_dir, "protos1.report"));
		assertReports(EXPECTED_SHORT, new File(report_dir, "protos2.report"));
		assertReports(EXPECTED_SHORT, new File(report_dir, "protos3.report"));
	}

	@Ignore
	@Test
	public void testPass4_Long() throws Throwable {
		File report_dir = new File(reportDir, "pass4_long");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-06-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless"
			));
		assertReports(EXPECTED_LONG, new File(report_dir, "protos1.report"));
	}
	
	@Ignore
	@Test
	public void testPass4_Long_WithLegacySDS() throws Throwable {
		File report_dir = new File(reportDir, "pass4_long_legacy_sds");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-06-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless",
				"--qforts-legacy-sds"
			));
		assertReports(EXPECTED_LONG, new File(report_dir, "protos1.report"));
	}
	
	@Ignore
	@Test
	public void testPass5_OhlcProviderProducer_WithLegacySDS() throws Throwable {
		File report_dir = new File(reportDir, "pass5_ohlc_prov_prod");
		new PROTOS().run(args(
				"--data-dir=" + dataDir,
				"--report-dir=" + report_dir,
				"--probe-initial-time=2017-01-01T00:00:00Z",
				"--probe-stop-time=2017-02-01T00:00:00Z",
				"--probe-auto-shutdown",
				"--probe-auto-start",
				"--headless",
				"--qforts-legacy-sds",
				"--protos-use-ohlc-provider"
			));
		assertReports(EXPECTED_SHORT, new File(report_dir, "protos1.report"));
	}
	*/
}
