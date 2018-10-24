package ru.prolib.bootes.lib.service.ars;

public interface ARSService {
	ARSTask getStartupTask();
	ARSTask getShutdownTask();
}
