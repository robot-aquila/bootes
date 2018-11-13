package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.BusinessEntities.Security;

/**
 * Robot state.
 * <p>
 * This class is intended to be accessible outside. Thus, all methods must be synchronized.
 */
public class RobotState {
	private final RobotStateListener stateListener;
	private String contractName;
	private ContractResolver contractResolver;
	private ContractParams contractParams;
	private Security security;
	private SuperSeriesHandler sht0, sht1, sht2;
	
	public RobotState(RobotStateListener stateListener) {
		this.stateListener = stateListener;
	}
	
	public RobotStateListener getStateListener() {
		return stateListener;
	}
	
	public synchronized void setContractName(String contractName) {
		this.contractName = contractName;
	}
	
	public synchronized void setContractResolver(ContractResolver resolver) {
		this.contractResolver = resolver;
	}
	
	public synchronized void setContractParams(ContractParams contractParams) {
		this.contractParams = contractParams;
	}
	
	public synchronized void setSecurity(Security security) {
		this.security = security;
	}
	
	public synchronized String getContractName() {
		return contractName;
	}
	
	public synchronized ContractResolver getContractResolver() {
		return contractResolver;
	}
	
	public synchronized ContractParams getContractParams() {
		return contractParams;
	}
	
	public synchronized Security getSecurity() {
		return security;
	}
	
	public synchronized void setSeriesHandlerT0(SuperSeriesHandler handler) {
		this.sht0 = handler;
	}
	
	public synchronized void setSeriesHandlerT1(SuperSeriesHandler handler) {
		this.sht1 = handler;
	}
	
	public synchronized void setSeriesHandlerT2(SuperSeriesHandler handler) {
		this.sht2 = handler;
	}
	
	public synchronized SuperSeriesHandler getSeriesHandlerT0() {
		return sht0;
	}
	
	public synchronized SuperSeriesHandler getSeriesHandlerT1() {
		return sht1;
	}
	
	public synchronized SuperSeriesHandler getSeriesHandlerT2() {
		return sht2;
	}
	
}
