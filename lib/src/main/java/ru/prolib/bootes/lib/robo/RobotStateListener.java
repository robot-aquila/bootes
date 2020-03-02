package ru.prolib.bootes.lib.robo;

import ru.prolib.aquila.core.BusinessEntities.Order;

public interface RobotStateListener {

	void robotStarted();

	void accountSelected();

	void contractSelected();

	void sessionDataAvailable();

	void riskManagementUpdate();

	void sessionDataCleanup();

	void robotStopped();
	
	void orderFinished(Order order);

}