package ru.prolib.bootes.tsgr001a;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


// Разбор вариантов решения проблемы паралельной диспетчеризации событий
// В общем виде требование звучит так: отправить максимальное количество событий за минимальное время
// В целях упрощения, опущены некоторые детали
// В целях повышения читабельности некоторые принципы игнорируем
// Примечание: Все пулы на базе LinkedBlockingQueue без аргумента
// Выводы:
// 1) Сегрегация по типам и направление в отдельные экзекьюторы может эффективно решать задачу
// в частных случаях, когда мы знаем как конкретно работают наблюдатели и уверены, что они не создадут проблем.
// В общем случае такой подход сопряжен с множеством рисков: из-за количества типов будет созано большое
// количество экзекьюторов, наиболее длинные будут создавать очереди, ожидая исполнения неоправданно долго,
// может быть достигнут лимит как по тредам, так и по памяти, возможен оверхед из-за большого кол-ва потоков.
// 2) Наиболее эффективным общим решением будет является специальная сортировка по типам с последующим
// контролем завершения и использованием всех имеющихся ресурсов
public class EventDispatcherTest {

	public static class Event {
		final long startTime = System.currentTimeMillis();
	}
	
	public static class TimeStats {
		private long startTime, stopTime;
		private long eventMaxTime, fastTotalTime, slowTotalTime;
		
		public synchronized void start() {
			startTime = System.currentTimeMillis();
		}
		
		public synchronized void stop() {
			stopTime = System.currentTimeMillis();
		}
		
		public synchronized void eventProcessed(Event event, boolean is_fast) {
			long event_time = System.currentTimeMillis() - event.startTime;
			eventMaxTime = Math.max(eventMaxTime, event_time);
			if ( is_fast ) {
				fastTotalTime += event_time;
			} else {
				slowTotalTime += event_time;
			}
		}
		
		@Override
		public synchronized String toString() {
			return new StringBuilder()
				.append(" total time used: ").append(stopTime - startTime).append(System.lineSeparator())
				.append(" fast total time: ").append(fastTotalTime).append(System.lineSeparator())
				.append(" slow total time: ").append(slowTotalTime).append(System.lineSeparator())
				.append("   slowest event: ").append(eventMaxTime)
				.toString();
		}
		
	}

	public interface EventListener {
		void onEvent(Event e);
	}
	
	public interface EventDispatcher extends Closeable {
		void fireEvent(Event e);
		void addListener(EventListener listener);
	}
	
	public static class SimpleListener implements EventListener {
		final long consumedTime;
		final CountDownLatch eventCounter;
		final TimeStats stats;
		final boolean isFast;
		
		SimpleListener(long consumed_time, CountDownLatch event_counter, TimeStats stats, boolean is_fast) {
			this.consumedTime = consumed_time;
			this.eventCounter = event_counter;
			this.stats = stats;
			this.isFast = is_fast;
		}

		@Override
		public void onEvent(Event e) {
			try {
				Thread.sleep(consumedTime);
				stats.eventProcessed(e, isFast);
				eventCounter.countDown();
			} catch ( InterruptedException x ) {
				x.printStackTrace();
			}
		}
		
	}
	
	public static class SimpleListenerFast extends SimpleListener {
		
		SimpleListenerFast(CountDownLatch event_counter, TimeStats stats) {
			super(100, event_counter, stats, true);
		}
		
	}
	
	public static class SimpleListenerSlow extends SimpleListener {
		
		SimpleListenerSlow(CountDownLatch event_counter, TimeStats stats) {
			super(500, event_counter, stats, false);
		}
		
	}
	
	public abstract static class AbstractDispatcher implements EventDispatcher {
		final CopyOnWriteArrayList<EventListener> listeners = new CopyOnWriteArrayList<>();
		
		@Override
		public void addListener(EventListener listener) {
			listeners.add(listener);
		}

	}

	// На вопрос - надо ли ждать подтверждения (завершения) я ответа не получил.
	// По этому рассмотрим оба кейса 

	// Для разбора см. testSeggregateByKnownTypeAndWaitForCompletion_*
	public static class SeggregateByKnownTypeAndWaitForCompletionDispatcher extends AbstractDispatcher {
		final ExecutorService fastExec = Executors.newSingleThreadExecutor();
		final ExecutorService slowExec = Executors.newSingleThreadExecutor();
		
		@Override
		public void fireEvent(Event e) {
			// Изменение количества потоков не решает проблему кардинально, для простоты используем по 1
			// Этот кейс про ожидание завершения
			CountDownLatch finished = new CountDownLatch(listeners.size()); //just dumbest solution
			for ( EventListener listener : listeners ) {
				if ( listener instanceof SimpleListenerFast ) {
					fastExec.execute(() -> { listener.onEvent(e); finished.countDown(); });
				} else if ( listener instanceof SimpleListenerSlow ) {
					slowExec.execute(() -> { listener.onEvent(e); finished.countDown(); });
				} else {
					throw new IllegalStateException();
				}
			}
			try {
				finished.await();
			} catch ( Exception x ) {
				throw new IllegalStateException(x);
			}
			// Дополнительно, какие в этом коде проблемы:
			// Здесь нарушен принцип Dependency Inversion, так как идет связывание с типами наблюдателей
			// Для того, что бы остаться в рамках DIP мы должны расширить интерфейс EventListener методом
			// информирования о продолжительности задачи в каком либо виде. Тогда мы сможем оперировать
			// любыми типами без привязки к реализации. Код аналогичный этому методу нельзя пускать
			// в продакшен, так как это мина замедленного действия, хотя и способен решить задачу более
			// эффективно, чем универсальных подход через карту тип -> экзекьютор. При использовании карты
			// свои тонкости. В данном кейсе мы можем просчитать максимальную эффективность, выделив на каждый
			// пул количество потоков равное количеству ядер. Тогда мы получим максимальную эффективность
			// для случая когда в списке один тип и оверхед на переключении потоков на время обработки
			// быстрых, когда у нас два типа. Оверхед будет до тех пор, пока не останутся в подвешенном
			// состоянии только медленные. Эффективно подогнать под железо становится невозможно, а
			// оверхед увеличится. В среднем, данная реализация "не справедлива" ко всем. Она наказывает
			// быстрые тем, что задерживает следующее событие и наказывает медленные тем, что не дает им
			// второй экзекьютор. В случае общего решения через карты, ситуация усугубляется.
		}
		
		@Override
		public void close() {
			fastExec.shutdown();
			slowExec.shutdown();
		}
		
	}
	
	// Для разбора см. testSeggregateByKnownTypeSendAndForget_*
	public static class SeggregateByKnownTypeSendAndForget extends AbstractDispatcher {
		final ExecutorService fastExec = Executors.newSingleThreadExecutor();
		final ExecutorService slowExec = Executors.newSingleThreadExecutor();

		@Override
		public void fireEvent(Event e) {
			for ( EventListener listener : listeners ) {
				if ( listener instanceof SimpleListenerFast ) {
					fastExec.execute(() -> listener.onEvent(e)); 
				} else if ( listener instanceof SimpleListenerSlow ) {
					slowExec.execute(() -> listener.onEvent(e));
				} else {
					throw new IllegalStateException();
				}
			}
			// Эта реализация все так же несправедлива к медленным.
			// Медленные не только не выполняются как можно быстрее, они создают узкое место.
		}
		
		@Override
		public void close() {
			fastExec.shutdown();
			slowExec.shutdown();
		}

	}
	
	// Для разбора см. testSortByTypeSendAndWaitUsingAllThreadsWaitForCompletion
	public static class SortByTypeSendAndWaitUsingAllThreadsWaitForCompletion extends AbstractDispatcher {
		// Просто объединим два экзекьютора по 1 потоку в 1 пул из двух потоков
		final ExecutorService exec = Executors.newFixedThreadPool(2);
		
		@Override
		public void fireEvent(Event e) {	
			// don't care how sorting is effective. doesn't matter for this case. we can do it better for sure
			List<EventListener> sorted = new ArrayList<>(),
				fast_list = listeners.stream().filter(l -> l instanceof SimpleListenerFast)
					.collect(Collectors.toList()),
				slow_list = listeners.stream().filter(l -> l instanceof SimpleListenerSlow)
					.collect(Collectors.toList());
			assertEquals(fast_list.size(), slow_list.size()); // don't care it's a test
			for ( int i = 0; i < slow_list.size(); i ++ ) {
				sorted.add(fast_list.get(i));
				sorted.add(slow_list.get(i));
			}
			CountDownLatch finished = new CountDownLatch(fast_list.size() + slow_list.size()); // just dumb solution
			for ( EventListener listener : sorted ) {
				exec.execute(() -> { listener.onEvent(e); finished.countDown(); });
			}
			finish(finished);
		}
		
		protected void finish(CountDownLatch finished) {
			try {
				finished.await();
			} catch ( Exception x ) {
				throw new IllegalStateException(x);
			}
			// Так гораздо лучше, но он недостаточно справедлив для быстрых 
		}
		
		@Override
		public void close() {
			exec.shutdown();
		}
		
	}
	
	// Для разбора см. testSortByTypeSendAndForgetUsingAllThreads_*
	public static class SortByTypeSendAndForgetUsingAllThreads
		extends SortByTypeSendAndWaitUsingAllThreadsWaitForCompletion // можно быстрее, но не суть
	{
		
		@Override
		protected void finish(CountDownLatch finished) {
			// Do nothing
		}
		
	}
	
	// Для разбора см. testSortByTypeDetectFasterAndSendByPriorityWaitForCompletion_*
	public static class SortByTypeDetectFasterAndSendByPriorityWaitForCompletion extends AbstractDispatcher {
		final ExecutorService exec = Executors.newFixedThreadPool(2);
		
		@Override
		public void fireEvent(Event e) {
			// Don't care of sorting. In general solution it should be a map by type
			final LinkedList<EventListener> type1_list = new LinkedList<>(listeners.stream()
					.filter(l -> l instanceof SimpleListenerFast)
					.collect(Collectors.toList()));
			final LinkedList<EventListener> type2_list = new LinkedList<>(listeners.stream()
					.filter(l -> l instanceof SimpleListenerSlow)
					.collect(Collectors.toList()));
			CompletableFuture<Void> pal1 = null, pal2 = null;
			assertEquals(type1_list.size(), type2_list.size()); // don't care of differences
			assertThat(type1_list.size(), Matchers.is(greaterThanOrEqualTo(1)));
			
			// Фаза 1: каждый идет в свой поток
			while ( type1_list.size() > 0 && type2_list.size() > 0 ) {
				if ( pal1 == null || pal1.isDone() ) {
					pal1 = CompletableFuture.runAsync(() -> { type1_list.removeFirst().onEvent(e); }, exec);
				}
				if ( pal2 == null || pal2.isDone() ) {
					pal2 = CompletableFuture.runAsync(() -> { type2_list.removeFirst().onEvent(e); }, exec);
				}
				CompletableFuture.anyOf(pal1, pal2).join();
			}
			// Фаза 2: есть какой то остаток. Надо раскидать по всем доступным потокам
			List<EventListener> rest = type1_list.size() == 0 ? type2_list : type1_list;
			CountDownLatch finished = new CountDownLatch(rest.size());
			for ( EventListener listener : rest ) {
				exec.execute(() -> { listener.onEvent(e); finished.countDown(); });
			}
			finish(finished);
		}
		
		protected void finish(CountDownLatch finished) {
			try {
				finished.await();
			} catch ( Exception x ) {
				throw new IllegalStateException(x);
			}
		}
		
		@Override
		public void close() {
			exec.shutdown();
		}
		
	}

	TimeStats stats;
	EventDispatcher dispatcher;
	List<EventListener> list;
	
	@Before
	public void setUp() throws Exception {
		stats = new TimeStats();
		list = new ArrayList<>();
		dispatcher = null;
	}
	
	@After
	public void tearDown() throws Exception {
		if ( dispatcher != null ) {
			dispatcher.close();
			dispatcher = null;
		}
	}
	
	void subscribeInRandomOrder(List<EventListener> listeners) {
		assertNotEquals(0, listeners.size());
		Collections.shuffle(listeners); // ensure it is unordered
		for ( EventListener listener : listeners ) {
			dispatcher.addListener(listener);
		}
	}
	
	void subscribeInRandomOrder() {
		subscribeInRandomOrder(list);
	}
	
	@Test
	public void testSeggregateByKnownTypeAndWaitForCompletion_SinglePass() throws Exception {
		CountDownLatch finished = new CountDownLatch(10); // должно прийти суммарно 10 уведомлений
		dispatcher = new SeggregateByKnownTypeAndWaitForCompletionDispatcher();
		for ( int i = 0; i < 5; i ++ ) {
			list.add(new SimpleListenerFast(finished, stats));
			list.add(new SimpleListenerSlow(finished, stats));
		}
		subscribeInRandomOrder();
		
		stats.start();
		dispatcher.fireEvent(new Event());
		assertTrue(finished.await(1, TimeUnit.MINUTES));
		stats.stop();
		
		System.out.println("testSeggregateByKnownTypeAndWaitForCompletion_SinglePass");
		System.out.println(stats);
		// total time used: 2539 - плохо
		// fast total time: 1688
		// slow total time: 7692 / 5 = 1538,4 - плохо
		//   slowest event: 2539
	}
	
	@Test
	public void testSeggregateByKnownTypeAndWaitForCompletion_TwoPass() throws Exception {
		CountDownLatch finished = new CountDownLatch(20); // должно прийти суммарно 20! уведомлений
		dispatcher = new SeggregateByKnownTypeAndWaitForCompletionDispatcher();
		for ( int i = 0; i < 5; i ++ ) {
			list.add(new SimpleListenerFast(finished, stats));
			list.add(new SimpleListenerSlow(finished, stats));
		}
		subscribeInRandomOrder();
		
		stats.start();
		dispatcher.fireEvent(new Event());
		dispatcher.fireEvent(new Event());
		assertTrue(finished.await(1, TimeUnit.MINUTES));
		stats.stop();
		
		System.out.println("testSeggregateByKnownTypeAndWaitForCompletion_TwoPass");
		System.out.println(stats);
		// total time used: 5038 - очень плохо
		// fast total time: 3180 / 10 = 318
		// slow total time: 15182 / 10 = 1518,2
		//   slowest event: 2536
	}
	
	@Test
	public void testSeggregateByKnownTypeSendAndForget_SinglePass() throws Exception {
		CountDownLatch finished = new CountDownLatch(10); // должно прийти суммарно 10 уведомлений
		dispatcher = new SeggregateByKnownTypeSendAndForget();
		for ( int i = 0; i < 5; i ++ ) {
			list.add(new SimpleListenerFast(finished, stats));
			list.add(new SimpleListenerSlow(finished, stats));
		}
		subscribeInRandomOrder();
		
		stats.start();
		dispatcher.fireEvent(new Event());
		assertTrue(finished.await(1, TimeUnit.MINUTES));
		stats.stop();
		
		System.out.println("testSeggregateByKnownTypeSendAndForget_SinglePass");
		System.out.println(stats);
		// total time used: 2534
		// fast total time: 1665
		// slow total time: 7660
		//   slowest event: 2533
	}
	
	@Test
	public void testSeggregateByKnownTypeSendAndForget_TwoPass() throws Exception {
		CountDownLatch finished = new CountDownLatch(20); // должно прийти суммарно 20! уведомлений
		dispatcher = new SeggregateByKnownTypeSendAndForget();
		for ( int i = 0; i < 5; i ++ ) {
			list.add(new SimpleListenerFast(finished, stats));
			list.add(new SimpleListenerSlow(finished, stats));
		}
		subscribeInRandomOrder();
		
		stats.start();
		dispatcher.fireEvent(new Event());
		dispatcher.fireEvent(new Event());
		assertTrue(finished.await(1, TimeUnit.MINUTES));
		stats.stop();
		
		System.out.println("testSeggregateByKnownTypeSendAndForget_TwoPass");
		System.out.println(stats);
		// total time used: 5038
		// fast total time: 5754
		// slow total time: 27689 <- забили, максимальное время оработки (=ожидание в пуле) 5 секунд!
		//   slowest event: 5003
	}
	
	@Test
	public void testSortByTypeSendAndWaitUsingAllThreadsWaitForCompletion_SinglePass() throws Exception {
		CountDownLatch finished = new CountDownLatch(10);
		dispatcher = new SortByTypeSendAndWaitUsingAllThreadsWaitForCompletion();
		for ( int i = 0; i < 5; i ++ ) {
			list.add(new SimpleListenerFast(finished, stats));
			list.add(new SimpleListenerSlow(finished, stats));
		}
		subscribeInRandomOrder();
		
		stats.start();
		dispatcher.fireEvent(new Event());
		assertTrue(finished.await(1, TimeUnit.MINUTES));
		stats.stop();
		
		System.out.println("testSortByTypeSendAndWaitUsingAllThreadsWaitForCompletion_SinglePass");
		System.out.println(stats);
		// total time used: 1740 - гораздо лучше
		// fast total time: 3096 / 5 = 619,2 - очень плохо
		// slow total time: 5498 / 5 = 1099,6 - лучше
		//   slowest event: 1740
	}
	
	@Test
	public void testSortByTypeSendAndWaitUsingAllThreadsWaitForCompletion_TwoPass() throws Exception {
		CountDownLatch finished = new CountDownLatch(20);
		dispatcher = new SortByTypeSendAndWaitUsingAllThreadsWaitForCompletion();
		for ( int i = 0; i < 5; i ++ ) {
			list.add(new SimpleListenerFast(finished, stats));
			list.add(new SimpleListenerSlow(finished, stats));
		}
		subscribeInRandomOrder();
		
		stats.start();
		dispatcher.fireEvent(new Event());
		dispatcher.fireEvent(new Event());
		assertTrue(finished.await(1, TimeUnit.MINUTES));
		stats.stop();
		
		System.out.println("testSortByTypeSendAndWaitUsingAllThreadsWaitForCompletion_TwoPass");
		System.out.println(stats);

		// одход дает лучшее распределение времени между задачами
		// total time used: 3442 - есть прогресс
		// fast total time: 6002 / 10 = 600,2 - очень плохо
		// slow total time: 10806 / 10 = 1080,6
		//   slowest event: 1738
	}
	
	@Test
	public void testSortByTypeSendAndForgetUsingAllThreads_SinglePass() throws Exception {
		CountDownLatch finished = new CountDownLatch(10);
		dispatcher = new SortByTypeSendAndForgetUsingAllThreads();
		for ( int i = 0; i < 5; i ++ ) {
			list.add(new SimpleListenerFast(finished, stats));
			list.add(new SimpleListenerSlow(finished, stats));
		}
		subscribeInRandomOrder();
		
		stats.start();
		dispatcher.fireEvent(new Event());
		assertTrue(finished.await(1, TimeUnit.MINUTES));
		stats.stop();
		
		System.out.println("testSortByTypeSendAndForgetUsingAllThreads_SinglePass");
		System.out.println(stats);
		// Относительно без изменений
		// total time used: 1739
		// fast total time: 3093
		// slow total time: 5494
		//   slowest event: 1739
	}
	
	@Test
	public void testSortByTypeSendAndForgetUsingAllThreads_TwoPass() throws Exception {
		CountDownLatch finished = new CountDownLatch(20);
		dispatcher = new SortByTypeSendAndForgetUsingAllThreads();
		for ( int i = 0; i < 5; i ++ ) {
			list.add(new SimpleListenerFast(finished, stats));
			list.add(new SimpleListenerSlow(finished, stats));
		}
		subscribeInRandomOrder();

		stats.start();
		dispatcher.fireEvent(new Event());
		dispatcher.fireEvent(new Event()); 
		assertTrue(finished.await(1, TimeUnit.MINUTES));
		stats.stop();
		
		System.out.println("testSortByTypeSendAndForgetUsingAllThreads_TwoPass");
		System.out.println(stats);
		
		// получили более ровное распределение, но вариант плох тем, что на втором
		// событии быстрый попадает  в пул, забитый медленным, оставшимся с предыдущего события
		// total time used: 3144
		// fast total time: 13319
		// slow total time: 18224
		//   slowest event: 3106
	}

	@Test
	public void testSortByTypeDetectFasterAndSendByPriorityWaitForCompletion_SinglePass() throws Exception {
		CountDownLatch finished = new CountDownLatch(10);
		dispatcher = new SortByTypeDetectFasterAndSendByPriorityWaitForCompletion();
		for ( int i = 0; i < 5; i ++ ) {
			list.add(new SimpleListenerFast(finished, stats));
			list.add(new SimpleListenerSlow(finished, stats));
		}
		subscribeInRandomOrder();

		stats.start();
		dispatcher.fireEvent(new Event()); 
		assertTrue(finished.await(1, TimeUnit.MINUTES));
		stats.stop();
		
		System.out.println("testSortByTypeDetectFasterAndSendByPriorityWaitForCompletion_SinglePass");
		System.out.println(stats);

		// total time used: 1550
		// fast total time: 1728
		// slow total time: 5745
		//   slowest event: 1550
	}
	
	@Test
	public void testSortByTypeDetectFasterAndSendByPriorityWaitForCompletion_TwoPass() throws Exception {
		CountDownLatch finished = new CountDownLatch(20);
		dispatcher = new SortByTypeDetectFasterAndSendByPriorityWaitForCompletion();
		for ( int i = 0; i < 5; i ++ ) {
			list.add(new SimpleListenerFast(finished, stats));
			list.add(new SimpleListenerSlow(finished, stats));
		}
		subscribeInRandomOrder();

		stats.start();
		dispatcher.fireEvent(new Event());
		dispatcher.fireEvent(new Event()); 
		assertTrue(finished.await(1, TimeUnit.MINUTES));
		stats.stop();
		
		System.out.println("testSortByTypeDetectFasterAndSendByPriorityWaitForCompletion_TwoPass");
		System.out.println(stats);
		// total time used: 3054
		// fast total time: 3240
		// slow total time: 11260
		//   slowest event: 1552
		
		// сравним цифры с сегрегацией тип -> пул
		
		// total time used: 5038 - очень плохо
		// fast total time: 3180 / 10 = 318
		// slow total time: 15182 / 10 = 1518,2
		//   slowest event: 2536
		
		// Ниже тесты для большего количества событий, что бы показать,
		// как ситуация будет развиваться при росте нагрузки
	}
	
	public void testSortByTypeDetectFasterAndSendByPriorityAndForget() throws Exception {
		// Этого кейса нет, так как потребуется более сложная логика определения быстрого:
		// при последовательной подаче событий пул будет забит предыдущими
		// и такая простая провеки как в реализации с подтверждением не сработает
		// По этому, для финального теста мы используем реализации с подтверждениями
	}
	
	@Test
	public void testSeggregateByKnownTypeAndWaitForCompletion_MultiPass() throws Exception {
		int num_events = 50, num_listeners_each_type = 5;
		dispatcher = new SeggregateByKnownTypeAndWaitForCompletionDispatcher();
		CountDownLatch finished = new CountDownLatch(num_listeners_each_type * 2 * num_events);
		for ( int i = 0; i < num_listeners_each_type; i ++ ) {
			list.add(new SimpleListenerFast(finished, stats));
			list.add(new SimpleListenerSlow(finished, stats));
		}
		subscribeInRandomOrder();

		stats.start();
		for ( int i = 0; i < num_events; i ++ ) {
			dispatcher.fireEvent(new Event());
		}
		assertTrue(finished.await(5, TimeUnit.MINUTES));
		stats.stop();
		
		System.out.println("testSeggregateByKnownTypeAndWaitForCompletion_MultiPass");
		System.out.println(stats);
		
		// total time used: 125168 <- 5 * 500 * 50 = 125000 <- макс производительность по медленным
		// fast total time: 75494
		// slow total time: 375548
		//   slowest event: 2534

	}
	
	@Test
	public void testSortByTypeDetectFasterAndSendByPriorityWaitForCompletion_MultiPass() throws Exception {
		int num_events = 50, num_listeners_each_type = 5;
		dispatcher = new SortByTypeDetectFasterAndSendByPriorityWaitForCompletion();
		CountDownLatch finished = new CountDownLatch(num_listeners_each_type * 2 * num_events);
		for ( int i = 0; i < num_listeners_each_type; i ++ ) {
			list.add(new SimpleListenerFast(finished, stats));
			list.add(new SimpleListenerSlow(finished, stats));
		}
		subscribeInRandomOrder();

		stats.start();
		for ( int i = 0; i < num_events; i ++ ) {
			dispatcher.fireEvent(new Event());
		}
		assertTrue(finished.await(1, TimeUnit.MINUTES));
		stats.stop();
		
		System.out.println("testSortByTypeDetectFasterAndSendByPriorityWaitForCompletion_MultiPass");
		System.out.println(stats);
		
		// total time used: 75204 <- (5 * 100 + 5 * 500) * 50 = 150000 / 2 = 75000 <- производительность по ресурсам
		// fast total time: 75558 
		// slow total time: 275661
		//   slowest event: 1547
	}
	
	@Test
	public void testSeggregateByKnownTypeSendAndForget_MultiPass() throws Exception {
		int num_events = 50, num_listeners_each_type = 5;
		dispatcher = new SeggregateByKnownTypeSendAndForget();
		CountDownLatch finished = new CountDownLatch(num_listeners_each_type * 2 * num_events);
		for ( int i = 0; i < num_listeners_each_type; i ++ ) {
			list.add(new SimpleListenerFast(finished, stats));
			list.add(new SimpleListenerSlow(finished, stats));
		}
		subscribeInRandomOrder();
		
		stats.start();
		for ( int i = 0; i < num_events; i ++ ) {
			dispatcher.fireEvent(new Event());
		}
		assertTrue(finished.await(5, TimeUnit.MINUTES));
		stats.stop();
		
		System.out.println("testSeggregateByKnownTypeSendAndForget_MultiPass");
		System.out.println(stats);

		// Этот тест просто для демонстрации: даже в самом эффективном
		// виде простая сегрегация проблему не решает, а создает

		// total time used: 125156 <--
		// fast total time: 3153338
		// slow total time: 15702638
		//   slowest event: 125123
	}

}
