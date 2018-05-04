package ru.prolib.bootes.lib.service;

import java.util.ArrayList;
import java.util.List;

public class AppRuntimeServiceImpl implements AppRuntimeService {
	private final List<Runnable> startActions, shutdownActions;
	private boolean started, shutdown;
	
	AppRuntimeServiceImpl(List<Runnable> startActions, List<Runnable> shutdownActions) {
		this.startActions = startActions;
		this.shutdownActions = shutdownActions;
	}
	
	public synchronized boolean isStarted() {
		return started;
	}
	
	public synchronized boolean isShutdown() {
		return shutdown;
	}
	
	public AppRuntimeServiceImpl() {
		this(new ArrayList<>(), new ArrayList<>());
	}

	@Override
	public synchronized void addStartAction(Runnable task) {
		if ( started ) {
			throw new IllegalStateException("App already started");
		}
		startActions.add(task);
	}

	@Override
	public synchronized void addShutdownAction(Runnable task) {
		if ( shutdown ) {
			throw new IllegalStateException("App already shutdown");
		}
		shutdownActions.add(task);
	}

	@Override
	public void triggerStart() {
		List<Runnable> tasks = null;
		synchronized ( this ) {
			if ( started ) {
				throw new IllegalStateException("App already started");
			}
			tasks = new ArrayList<>(startActions);
			started = true;
		}
		for ( Runnable r : tasks ) {
			r.run();
		}
	}

	@Override
	public void triggerShutdown() {
		List<Runnable> tasks = null;
		synchronized ( this ) {
			if ( ! started ) {
				throw new IllegalStateException("App not started yet");
			}
			if ( shutdown ) {
				throw new IllegalStateException("App already shutdown");
			}
			tasks = new ArrayList<>(shutdownActions);
			shutdown = true;
		}
		for ( Runnable r : tasks ) {
			r.run();
		}
	}

}
