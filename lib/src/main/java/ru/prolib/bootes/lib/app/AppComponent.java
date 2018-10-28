package ru.prolib.bootes.lib.app;

public interface AppComponent {
	void init() throws Throwable;
	void startup() throws Throwable;
	void shutdown() throws Throwable;
}
