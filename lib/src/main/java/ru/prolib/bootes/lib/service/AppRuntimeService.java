package ru.prolib.bootes.lib.service;

/**
 * <b>Definitions:</b>
 * <ul>
 * <li>Application is a program module to solve specific task. It uses services to do work.</li>
 * <li>Service is a provider of specific functionality logically combined in separate unit.</li>
 * <li>System is a combination of applications and services they use.</li>
 * <li>Exit signal is an observable object which signals that the system is completely shutdown and program should finish.</li>
 * </ul>
 * <b>Requirements:</b>
 * <ul>
 * <li>Exit signal should be available during time of system existence in any of its state.</li>
 * <li>Services and applications must have its own logic of startup and shutdown procedures,
 *     should register them at its own discretion.</li>
 * <li>Service startup procedure should be registered prior to system start.</li>
 * <li>Service shutdown procedure should be registered prior to system shutdown.</li>
 * <li>Application startup procedure should be registered prior to phase of applications start.</li>
 * <li>Application shutdown procedure should be registered prior to system shutdown.</li>
 * <li>Any procedure: startup or shutdown, service or application can be cancelled prior to its own execution.</li>
 * <li>System startup should be initiated by a single call.</li>
 * <li>Phase of services startup should execute prior to phase of applications startup.</li>
 * <li>Fail of a single service startup will fail whole system. Applications shouldn't start in this case.</li>
 * <li>Service startup can take time and its result can be obtained asynchronously relating to system startup procedure.</li>
 * <li>Services can register its own specific application tasks as application startup and shutdown procedures.</li>
 * <li>Services must finish startup in specified period. At least one timeout will fail whole system.</li>
 * <li>The system marked as started when all services started.</li>
 * <li>Startup of applications should start after all services successfully started.</li>
 * <li>Application startup fail shouldn't affect whole system. Other applications should continue.</li>
 * <li>To prevent continue running the system in case of failure, application should explicitly initiate system shutdown.</li> 
 * <li>System shutdown should be initiated by a single call.</li>
 * <li>Phase of applications shutdown should start prior to services shutdown.</li>
 * <li>Application shutdown can take time and its result can be obtained asynchronously relating to system shutdown.</li>
 * <li>Any application shutdown result (success, fail, exception, timeout) shouldn't affect the system shutdown.
 *     It just points to a time when application is finished.</li>
 * <li>Phase of services shutdown should start after phase of applications shutdown.</li>
 * <li>Service shutdown can take time and its result can be obtained asynchronously relating to system shutdown.</li>
 * <li>Any service shutdown result (success, fail, exception, timeout) shouldn't affect the system shutdown.
 *     It just points to a time when service is finished.</li>
 * <li>Shutting down all services should trigger the exit signal.</li>
 * </ul>
 */
public interface AppRuntimeService {
	
	void addStartAction(Runnable task);
	void addShutdownAction(Runnable task);
	void triggerStart();
	void triggerShutdown();

}
