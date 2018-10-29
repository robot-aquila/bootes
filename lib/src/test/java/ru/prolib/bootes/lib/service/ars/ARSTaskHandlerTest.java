package ru.prolib.bootes.lib.service.ars;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ARSTaskHandlerTest {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(ARSTaskHandlerTest.class);
	}
	
	private IMocksControl control;
	private ARSAction actionMock;
	private ARSTaskHandler service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		actionMock = control.createMock(ARSAction.class);
		service = new ARSTaskHandler(actionMock, "foobar");
	}
	
	@Test
	public void testCtor() {
		assertEquals("foobar", service.getTaskID());
		assertSame(actionMock, service.getAction());
		assertEquals(ARSTaskState.PENDING, service.getCurrentState());
		assertNull(service.getThrownException());
	}
	
	@Test
	public void testCancel_DoNotRunAction() {
		assertEquals(ARSTaskState.PENDING, service.getCurrentState());
		
		service.cancel();
		
		assertEquals(ARSTaskState.CANCELLED, service.getCurrentState());
		control.replay();
		service.run();
		control.verify();
	}
	
	@Test
	public void testCancel_HasNoEffectWhileExecuting() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), proceed = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				started.countDown();
				proceed.await(1, TimeUnit.SECONDS);
				return null;
			}
		});
		control.replay();
		Thread t = new Thread(service);
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		assertEquals(ARSTaskState.EXECUTING, service.getCurrentState());
		service.cancel();
		assertEquals(ARSTaskState.EXECUTING, service.getCurrentState());
		started.countDown();
	}
	
	@Test
	public void testWaitForStateChange0_ImmediatelyIfCancelled() throws Throwable {
		service.cancel();
		long start = System.currentTimeMillis();
		
		ARSTaskState actual = service.waitForStateChange();
		
		long diff = System.currentTimeMillis() - start;
		assertEquals(ARSTaskState.CANCELLED, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
	}

	@Test
	public void testWaitForStateChange0_ImmediatelyIfExecuted() throws Throwable {
		actionMock.run();
		control.replay();
		service.run();
		long start = System.currentTimeMillis();
		
		ARSTaskState actual = service.waitForStateChange();
		
		long diff = System.currentTimeMillis() - start;
		assertEquals(ARSTaskState.EXECUTED, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
	}
	
	@Test
	public void testWaitForStateChange0_ImmediatelyIfFailed() throws Throwable {
		actionMock.run();
		expectLastCall().andThrow(new Exception("Test error"));
		control.replay();
		service.run();
		long start = System.currentTimeMillis();
		
		ARSTaskState actual = service.waitForStateChange();
		
		long diff = System.currentTimeMillis() - start;
		assertEquals(ARSTaskState.FAILED, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
	}
	
	@Test
	public void testWaitForStateChange0_ImmediatelyIfTimeout() throws Throwable {
		actionMock.run();
		expectLastCall().andThrow(new InterruptedException("Test error"));
		control.replay();
		service.run();
		long start = System.currentTimeMillis();
		
		ARSTaskState actual = service.waitForStateChange();
		
		long diff = System.currentTimeMillis() - start;
		assertEquals(ARSTaskState.TIMEOUT, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
	}

	@Test
	public void testWaitForStateChange0_FromPendingToCancelled() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.CANCELLED, service.waitForStateChange());
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.cancel();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForStateChange0_FromPendingToExecuting() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				assertTrue(finished.await(1, TimeUnit.SECONDS));
				return null;
			}
		});
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.EXECUTING, service.waitForStateChange());
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForStateChange0_FromExecutingToExecuted() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		actionMock.run();
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						ARSTaskState s = service.waitForStateChange();
						if ( s == ARSTaskState.EXECUTING ) {
							s = service.waitForStateChange();
						}
						assertEquals(ARSTaskState.EXECUTED, service.waitForStateChange());
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForStateChange0_FromExecutingToFailed() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andThrow(new Exception("Test error"));
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						ARSTaskState s = service.waitForStateChange();
						if ( s == ARSTaskState.EXECUTING ) {
							s = service.waitForStateChange();
						}
						assertEquals(ARSTaskState.FAILED, service.waitForStateChange());
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForStateChange0_FromExecutingToTimeout() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andThrow(new InterruptedException("Test error"));
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.EXECUTING, service.waitForStateChange());
						assertEquals(ARSTaskState.TIMEOUT, service.waitForStateChange());
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForStateChange0_SkipInsignificantSignals() throws Throwable {
		CountDownLatch
			started = new CountDownLatch(1), // indicates that main monitor thread is waiting for state change
			finished = new CountDownLatch(1), // indicates that main monitor thread is finished
			s_finished = new CountDownLatch(1); // indicates that noise thread finished
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				s_finished.await(1, TimeUnit.SECONDS);
				return null;
			}
		});
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.EXECUTING, service.waitForStateChange());
						started.countDown();						
						assertEquals(ARSTaskState.EXECUTED, service.waitForStateChange());
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		Thread t_s = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					started.await(1, TimeUnit.SECONDS);
					for ( int i = 0; i < 15; i ++ ) {
						synchronized ( service ) {
							service.notifyAll();
						}
					}
					s_finished.countDown();
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t_s.start();
		
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForCompletion0_ImmediatelyIfCancelled() throws Throwable {
		service.cancel();
		control.replay();
		long start = System.currentTimeMillis();
		
		ARSTaskState actual = service.waitForCompletion();
		
		long diff = System.currentTimeMillis() - start;
		assertEquals(ARSTaskState.CANCELLED, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
		control.verify();
	}
	
	@Test
	public void testWaitForCompletion0_ImmediatelyIfExecuted() throws Throwable {
		actionMock.run();
		control.replay();
		service.run();
		long start = System.currentTimeMillis();
		
		ARSTaskState actual = service.waitForCompletion();
		
		long diff = System.currentTimeMillis() - start;
		assertEquals(ARSTaskState.EXECUTED, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
		control.verify();
	}
	
	@Test
	public void testWaitForCompletion0_ImmediatelyIfFailed() throws Throwable {
		actionMock.run();
		expectLastCall().andThrow(new Exception("Test error"));
		control.replay();
		service.run();
		long start = System.currentTimeMillis();
		
		ARSTaskState actual = service.waitForCompletion();
		
		long diff = System.currentTimeMillis() - start;
		assertEquals(ARSTaskState.FAILED, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
		control.verify();
	}
	
	@Test
	public void testWaitForCompletion0_ImmediatelyIfTimeout() throws Throwable {

	}
	
	@Test
	public void testWaitForCompletion0_FromPendingToCancelled() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.CANCELLED, service.waitForCompletion());
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.cancel();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}

	@Test
	public void testWaitForCompletion0_FromPendingToExecuted() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		actionMock.run();
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.EXECUTED, service.waitForCompletion());
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}

	@Test
	public void testWaitForCompletion0_FromPendingToFailed() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andThrow(new Exception("Test error"));
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.FAILED, service.waitForCompletion());
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForCompletion0_FromPendingToTimeout() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andThrow(new InterruptedException("Test error"));
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.TIMEOUT, service.waitForCompletion());
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForCompletion0_FromExecutingToExecuted() throws Throwable {
		CountDownLatch m_started = new CountDownLatch(1), t_started = new CountDownLatch(1),
				finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				t_started.countDown();
				m_started.await(1, TimeUnit.SECONDS);
				return null;
			}
		});
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					t_started.await(1, TimeUnit.SECONDS);
					synchronized ( service ) {
						assertEquals(ARSTaskState.EXECUTING, service.getCurrentState());
						m_started.countDown();
						assertEquals(ARSTaskState.EXECUTED, service.waitForCompletion());
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}

	@Test
	public void testWaitForCompletion0_FromExecutingToFailed() throws Throwable {
		CountDownLatch m_started = new CountDownLatch(1), t_started = new CountDownLatch(1),
				finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				t_started.countDown();
				m_started.await(1, TimeUnit.SECONDS);
				throw new Exception("Test error");
			}
		});
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					t_started.await(1, TimeUnit.SECONDS);
					synchronized ( service ) {
						assertEquals(ARSTaskState.EXECUTING, service.getCurrentState());
						m_started.countDown();
						assertEquals(ARSTaskState.FAILED, service.waitForCompletion());
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForCompletion0_FromExecutingToTimeout() throws Throwable {
		CountDownLatch m_started = new CountDownLatch(1), t_started = new CountDownLatch(1),
				finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				t_started.countDown();
				m_started.await(1, TimeUnit.SECONDS);
				throw new InterruptedException("Test error");
			}
		});
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					t_started.await(1, TimeUnit.SECONDS);
					synchronized ( service ) {
						assertEquals(ARSTaskState.EXECUTING, service.getCurrentState());
						m_started.countDown();
						assertEquals(ARSTaskState.TIMEOUT, service.waitForCompletion());
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}

	@Test
	public void testWaitForCompletion0_SkipInsignificantSignals() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1),
				s_finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				s_finished.await(1, TimeUnit.SECONDS);
				return null;
			}
		});
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.EXECUTED, service.waitForCompletion());
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		Thread t_s = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					started.await(1, TimeUnit.SECONDS);
					for ( int i = 0; i < 15; i ++ ) {
						synchronized ( service ) {
							service.notifyAll();
						}
					}
					s_finished.countDown();
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t_s.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForStateChange1_ImmediatelyIfCancelled() throws Throwable {
		control.replay();
		service.cancel();
		long started = System.currentTimeMillis();
		
		ARSTaskState actual = service.waitForStateChange(1000L);
		
		long diff = System.currentTimeMillis() - started;
		assertEquals(ARSTaskState.CANCELLED, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
		control.verify();
	}

	@Test
	public void testWaitForStateChange1_ImmediatelyIfExecuted() throws Throwable {
		actionMock.run();
		control.replay();
		service.run();
		long started = System.currentTimeMillis();
		
		ARSTaskState actual = service.waitForStateChange(1000L);
		
		long diff = System.currentTimeMillis() - started;
		assertEquals(ARSTaskState.EXECUTED, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
		control.verify();
	}
	
	@Test
	public void testWaitForStateChange1_ImmediatelyIfFailed() throws Throwable {
		actionMock.run();
		expectLastCall().andThrow(new Exception("Test error"));
		control.replay();
		service.run();
		long started = System.currentTimeMillis();
		
		ARSTaskState actual = service.waitForStateChange(1000L);
		
		long diff = System.currentTimeMillis() - started;
		assertEquals(ARSTaskState.FAILED, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
		control.verify();
	}
	
	@Test
	public void testWaitForStateChange1_ImmediatelyIfTimeout() throws Throwable {
		actionMock.run();
		expectLastCall().andThrow(new InterruptedException("Test error"));
		control.replay();
		service.run();
		long started = System.currentTimeMillis();
		
		ARSTaskState actual = service.waitForStateChange(1000L);
		
		long diff = System.currentTimeMillis() - started;
		assertEquals(ARSTaskState.TIMEOUT, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
		control.verify();
	}
	
	@Test
	public void testWaitForStateChange1_Timeout() throws Throwable {
		CountDownLatch finished = new CountDownLatch(1);
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					service.waitForStateChange(100L);
				} catch ( TimeoutException e ) {
					finished.countDown();
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(finished.await(150L, TimeUnit.MILLISECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForStateChange1_FromPendingToCancelled() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.CANCELLED, service.waitForStateChange(1000L));
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.cancel();
		assertTrue(finished.await(50, TimeUnit.MILLISECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForStateChange1_FromPendingToExecuting() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		actionMock.run();
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.EXECUTING, service.waitForStateChange(1000L));
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.run();
		assertTrue(finished.await(50, TimeUnit.MILLISECONDS));
		control.verify();
	}

	@Test
	public void testWaitForStateChange1_FromExecutingToExecuted() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1),
				a_started = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				a_started.countDown();
				assertTrue(started.await(1, TimeUnit.SECONDS));
				return null;
			}
		});
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					a_started.await(1, TimeUnit.SECONDS);
					synchronized ( service ) {
						assertEquals(ARSTaskState.EXECUTING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.EXECUTED, service.waitForStateChange(1000L));
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}

	@Test
	public void testWaitForStateChange1_FromExecutingToFailed() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1),
				a_started = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				a_started.countDown();
				assertTrue(started.await(1, TimeUnit.SECONDS));
				throw new Exception("Test error");
			}
		});
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					a_started.await(1, TimeUnit.SECONDS);
					synchronized ( service ) {
						assertEquals(ARSTaskState.EXECUTING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.FAILED, service.waitForStateChange(1000L));
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForStateChange1_FromExecutingToTimeout() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1),
				a_started = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				a_started.countDown();
				assertTrue(started.await(1, TimeUnit.SECONDS));
				throw new InterruptedException("Test error");
			}
		});
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					a_started.await(1, TimeUnit.SECONDS);
					synchronized ( service ) {
						assertEquals(ARSTaskState.EXECUTING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.TIMEOUT, service.waitForStateChange(1000L));
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForStateChange1_SkipInsignificantSignals() throws Throwable {
		CountDownLatch 
			started = new CountDownLatch(1), // main monitor thread started

			n_finished = new CountDownLatch(1), // noise thread finished
			finished = new CountDownLatch(1); // main monitor thread finished
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				assertTrue(n_finished.await(1, TimeUnit.SECONDS));
				return null;
			}
		});
		control.replay();
		Thread t_n = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					assertTrue(started.await(1, TimeUnit.SECONDS));
					for ( int i = 0; i < 15; i ++ ) {
						synchronized ( service ) {
							service.notifyAll();
						}
					}
					n_finished.countDown();
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t_n.start();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.EXECUTING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.EXECUTED, service.waitForStateChange(1000L));
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		service.run();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForCompletion1_ImmediatelyIfCancelled() throws Throwable {
		control.replay();
		service.cancel();
		long start = System.currentTimeMillis();

		ARSTaskState actual = service.waitForCompletion(1000L);
		
		long diff = System.currentTimeMillis() - start;
		assertEquals(ARSTaskState.CANCELLED, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
		control.verify();
	}

	@Test
	public void testWaitForCompletion1_ImmediatelyIfExecuted() throws Throwable {
		actionMock.run();
		control.replay();
		service.run();
		long start = System.currentTimeMillis();

		ARSTaskState actual = service.waitForCompletion(1000L);
		
		long diff = System.currentTimeMillis() - start;
		assertEquals(ARSTaskState.EXECUTED, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
		control.verify();
	}

	@Test
	public void testWaitForCompletion1_ImmediatelyIfFailed() throws Throwable {
		actionMock.run();
		expectLastCall().andThrow(new Exception("Test error"));
		control.replay();
		service.run();
		long start = System.currentTimeMillis();

		ARSTaskState actual = service.waitForCompletion(1000L);
		
		long diff = System.currentTimeMillis() - start;
		assertEquals(ARSTaskState.FAILED, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
		control.verify();
	}
	
	@Test
	public void testWaitForCompletion1_ImmediatelyIfTimeout() throws Throwable {
		actionMock.run();
		expectLastCall().andThrow(new InterruptedException("Test error"));
		control.replay();
		service.run();
		long start = System.currentTimeMillis();
		
		ARSTaskState actual = service.waitForCompletion(1000L);
		
		long diff = System.currentTimeMillis() - start;
		assertEquals(ARSTaskState.TIMEOUT, actual);
		assertThat(diff, lessThanOrEqualTo(50L));
		control.verify();
	}
	
	@Test
	public void testWaitForCompletion1_Timeout() throws Throwable {
		CountDownLatch finished = new CountDownLatch(1);
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					service.waitForCompletion(100L);
				} catch ( TimeoutException e ) {
					finished.countDown();
				} catch ( InterruptedException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(finished.await(150L, TimeUnit.MILLISECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForCompletion1_FromPendingToCancelled() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.CANCELLED, service.waitForCompletion(100L));
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.cancel();
		assertTrue(finished.await(150L, TimeUnit.MILLISECONDS));
		control.verify();
	}

	@Test
	public void testWaitForCompletion1_FromPendingToExecuted() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		actionMock.run();
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.EXECUTED, service.waitForCompletion(1000L));
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.run();
		assertTrue(finished.await(150L, TimeUnit.MILLISECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForCompletion1_FromPendingToFailed() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andThrow(new Exception("Test error"));
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.FAILED, service.waitForCompletion(1000L));
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.run();
		assertTrue(finished.await(150L, TimeUnit.MILLISECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForCompletion1_FromPendingToTimeout() throws Throwable {
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andThrow(new InterruptedException("Test error"));
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized ( service ) {
						assertEquals(ARSTaskState.PENDING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.TIMEOUT, service.waitForCompletion(1000L));
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		service.run();
		assertTrue(finished.await(150L, TimeUnit.MILLISECONDS));
		control.verify();
	}

	@Test
	public void testWaitForCompletion1_FromExecutingToExecuted() throws Throwable {
		CountDownLatch a_started = new CountDownLatch(1), started = new CountDownLatch(1),
				finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				a_started.countDown();
				assertTrue(started.await(1, TimeUnit.SECONDS));
				return null;
			}
		});
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					assertTrue(a_started.await(1, TimeUnit.SECONDS));
					synchronized ( service ) {
						assertEquals(ARSTaskState.EXECUTING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.EXECUTED, service.waitForCompletion(1000L));
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		service.run();
		assertTrue(finished.await(150L, TimeUnit.MILLISECONDS));
		control.verify();
	}

	@Test
	public void testWaitForCompletion1_FromExecutingToFailed() throws Throwable {
		CountDownLatch a_started = new CountDownLatch(1), started = new CountDownLatch(1),
				finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				a_started.countDown();
				assertTrue(started.await(1, TimeUnit.SECONDS));
				throw new Exception("Test error");
			}
		});
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					assertTrue(a_started.await(1, TimeUnit.SECONDS));
					synchronized ( service ) {
						assertEquals(ARSTaskState.EXECUTING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.FAILED, service.waitForCompletion(1000L));
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		service.run();
		assertTrue(finished.await(150L, TimeUnit.MILLISECONDS));
		control.verify();
	}
	
	@Test
	public void testWaitForCompletion1_FromExecutingToTimeout() throws Throwable {
		CountDownLatch a_started = new CountDownLatch(1), started = new CountDownLatch(1),
				finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				a_started.countDown();
				assertTrue(started.await(1, TimeUnit.SECONDS));
				throw new InterruptedException("Test error");
			}
		});
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					assertTrue(a_started.await(1, TimeUnit.SECONDS));
					synchronized ( service ) {
						assertEquals(ARSTaskState.EXECUTING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.TIMEOUT, service.waitForCompletion(1000L));
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		
		service.run();
		assertTrue(finished.await(150L, TimeUnit.MILLISECONDS));
		control.verify();
	}

	@Test
	public void testWaitForCompletion1_SkipInsignificantSignals() throws Throwable {
		CountDownLatch a_started = new CountDownLatch(1), started = new CountDownLatch(1),
				n_finished = new CountDownLatch(1), finished = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				a_started.countDown();
				assertTrue(n_finished.await(1, TimeUnit.SECONDS));
				return null;
			}
		});
		control.replay();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					assertTrue(a_started.await(1, TimeUnit.SECONDS));
					synchronized ( service ) {
						assertEquals(ARSTaskState.EXECUTING, service.getCurrentState());
						started.countDown();
						assertEquals(ARSTaskState.EXECUTED, service.waitForCompletion(1000L));
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t.start();
		Thread t_n = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					started.await(1, TimeUnit.SECONDS);
					for ( int i = 0; i < 15; i ++ ) {
						synchronized ( service ) {
							service.notifyAll();
						}
					}
					n_finished.countDown();
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		});
		t_n.start();
		
		service.run();
		assertTrue(finished.await(150L, TimeUnit.MILLISECONDS));
		control.verify();
	}
	
	@Test
	public void testRun_OK() throws Throwable {
		actionMock.run();
		control.replay();
		Thread t = new Thread(service);
		t.start();
		
		assertEquals(ARSTaskState.EXECUTED, service.waitForCompletion(1000L));
		
		control.verify();
		assertNull(service.getThrownException());
	}
	
	@Test
	public void testRun_Timeout() throws Throwable {
		CountDownLatch x = new CountDownLatch(1);
		actionMock.run();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				x.await(5L, TimeUnit.SECONDS);
				return null;
			}
		});
		control.replay();
		Thread t = new Thread(service);
		t.start();
		
		try {
			service.waitForCompletion(100L);
			fail("Expected: " + TimeoutException.class.getSimpleName());
		} catch ( TimeoutException e ) { }
		t.interrupt();
		assertEquals(ARSTaskState.TIMEOUT, service.waitForCompletion(100L));
		control.verify();
		assertEquals(InterruptedException.class, service.getThrownException().getClass());
	}
	
	@Test
	public void testRun_Failed() throws Throwable {
		Exception error = new Exception("Hello, world");
		actionMock.run();
		expectLastCall().andThrow(error);
		control.replay();
		Thread t = new Thread(service);
		t.start();
		
		assertEquals(ARSTaskState.FAILED, service.waitForCompletion(1000L));
		assertSame(error, service.getThrownException());

		control.verify();
	}

}
