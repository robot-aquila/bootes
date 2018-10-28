package ru.prolib.bootes.lib.app;

public interface AppRuntimeService {
	void addService(AppComponent component);
	void addApplication(AppComponent component);
	void init() throws Throwable;
	void startup();
	void shutdown();
	void waitForShutdown() throws InterruptedException;
	void waitForStartup() throws InterruptedException;
	boolean waitForShutdown(long timeout) throws InterruptedException;
	boolean waitForStartup(long timeout) throws InterruptedException;
}
