package ru.prolib.bootes.lib.service.ars;

import java.util.concurrent.CompletableFuture;

public interface IAppRuntimeService {
	ARSTask addSrvStartupAction(ARSAction action, String taskID);
	ARSTask addAppStartupAction(ARSAction action, String taskID);
	ARSTask addSrvShutdownAction(ARSAction action, String taskID);
	ARSTask addAppShutdownAction(ARSAction action, String taskID);
	CompletableFuture<ARSResult> startup();
	CompletableFuture<ARSResult> shutdown();
}
