package ru.prolib.bootes.lib.service.ars;

import java.util.concurrent.CompletableFuture;

public interface ARS {
	ARSService addService(ARSServiceAction action, String serviceID);
	ARSTask addAppStartupAction(ARSAction action, String taskID);
	ARSTask addAppShutdownAction(ARSAction action, String taskID);
	CompletableFuture<ARSResult> startup();
	CompletableFuture<ARSResult> shutdown();
}
