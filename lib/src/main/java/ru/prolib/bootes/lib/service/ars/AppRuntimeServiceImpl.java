package ru.prolib.bootes.lib.service.ars;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppRuntimeService;

public class AppRuntimeServiceImpl implements AppRuntimeService, Runnable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(AppRuntimeServiceImpl.class);
	}
	
	private final CountDownLatch startup, shutdown, finished, started;
	private final Thread worker;
	private final List<AppComponent> services, applications;
	private boolean init, cancel;
	
	public AppRuntimeServiceImpl(String workerThreadID) {
		this.startup = new CountDownLatch(1);
		this.shutdown = new CountDownLatch(1);
		this.finished = new CountDownLatch(1);
		this.started = new CountDownLatch(1);
		services = new ArrayList<>();
		applications = new ArrayList<>();
		worker = new Thread(this, workerThreadID);
		worker.setDaemon(true);
		worker.start();
	}
	
	public Thread getWorkerThread() {
		return worker;
	}

	@Override
	public synchronized void addService(AppComponent handler) {
		if ( init ) {
			throw new IllegalStateException("Already initialized");
		}
		services.add(handler);
	}

	@Override
	public synchronized void addApplication(AppComponent handler) {
		if ( init ) {
			throw new IllegalStateException("Already initialized");
		}
		applications.add(handler);
	}
	
	@Override
	public synchronized void init() throws Throwable {
		if ( init ) {
			throw new IllegalStateException("Already initialized");
		}
		for ( AppComponent comp : services ) {
			comp.init();
		}
		for ( AppComponent comp : applications ) {
			comp.init();
		}
		init = true;
	}

	@Override
	public synchronized void startup() {
		if ( ! init ) {
			throw new IllegalStateException("Not initialized");
		}
		startup.countDown();
	}

	@Override
	public synchronized void shutdown() {
		shutdown.countDown(); // must be first
		if ( startup.getCount() > 0 ) {
			cancel = true;
			startup.countDown();
		}
	}

	@Override
	public void waitForShutdown() throws InterruptedException {
		finished.await();
	}
	
	@Override
	public void waitForStartup() throws InterruptedException {
		started.await();
	}
	
	@Override
	public boolean waitForShutdown(long timeout) throws InterruptedException {
		return finished.await(timeout, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public boolean waitForStartup(long timeout) throws InterruptedException {
		return started.await(timeout, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		LinkedList<AppComponent> svcsStarted = new LinkedList<>(), appsStarted = new LinkedList<>();
		List<AppComponent> svcs, apps;
		try {
			startup.await();
			synchronized ( this ) {
				if ( cancel ) {
					started.countDown();
					return;
				}
				svcs = new ArrayList<>(services);
				apps = new ArrayList<>(applications);
			}
			boolean hasErrors = false;
			for ( AppComponent comp : svcs ) {
				try {
					comp.startup();
					svcsStarted.addFirst(comp);
				} catch ( Throwable e ) {
					logger.error("Service startup failed: ", e);
					hasErrors = true;
					break;
				}
			}
			if ( ! hasErrors ) {
				for ( AppComponent comp : apps ) {
					try {
						comp.startup();
						appsStarted.addFirst(comp);
					} catch ( Throwable e ) {
						logger.error("Application startup failed: ", e);
					}
				}
			}
			started.countDown();
			if ( ! hasErrors ) {
				try {
					shutdown.await();
				} catch ( InterruptedException e ) {
					logger.error("Unexpected interruption: ", e);
				}
			}

			for ( AppComponent comp : appsStarted ) {
				try {
					comp.shutdown();
				} catch ( Throwable e ) {
					logger.error("Application shutdown failed: ", e);
				}
			}
			for ( AppComponent comp : svcsStarted ) {
				try {
					comp.shutdown();
				} catch ( Throwable e ) {
					logger.error("Service shutdown failed: ", e);
				}
			}
		} catch ( InterruptedException e ) {
			logger.error("Thread interrupted: ", e);
		} finally {
			finished.countDown();
			started.countDown();
		}
	}

}
