package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.tseries.STSeriesHandler;

/**
 * Robot state.
 * <p>
 * This class is intended to be accessible outside. Thus, all methods must be synchronized.
 */
public class RobotState {
	private final RobotStateListener stateListener;
	private String contractName, accountCode;
	private ContractResolver contractResolver;
	private ContractParams contractParams;
	private Portfolio portfolio;
	private Security security;
	private STSeriesHandler sht0, sht1, sht2;
	
	public RobotState(RobotStateListener stateListener) {
		this.stateListener = stateListener;
	}
	
	public RobotStateListener getStateListener() {
		return stateListener;
	}
	
	public synchronized void setContractName(String contractName) {
		this.contractName = contractName;
	}
	
	public synchronized void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	
	public synchronized void setContractResolver(ContractResolver resolver) {
		this.contractResolver = resolver;
	}
	
	public synchronized void setContractParams(ContractParams contractParams) {
		this.contractParams = contractParams;
	}
	
	public synchronized void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
	public synchronized void setSecurity(Security security) {
		this.security = security;
	}
	
	public synchronized void setSeriesHandlerT0(STSeriesHandler handler) {
		this.sht0 = handler;
	}
	
	public synchronized void setSeriesHandlerT1(STSeriesHandler handler) {
		this.sht1 = handler;
	}
	
	public synchronized void setSeriesHandlerT2(STSeriesHandler handler) {
		this.sht2 = handler;
	}
	
	public synchronized String getContractName() {
		if ( contractName == null ) {
			throw new NullPointerException();
		}
		return contractName;
	}
	
	public synchronized String getAccountCode() {
		if ( accountCode == null ) {
			throw new NullPointerException();
		}
		return accountCode;
	}
	
	public synchronized ContractResolver getContractResolver() {
		if ( contractResolver == null ) {
			throw new NullPointerException(); 
		}
		return contractResolver;
	}
	
	public synchronized ContractParams getContractParams() {
		if ( contractParams == null ) {
			throw new NullPointerException();
		}
		return contractParams;
	}
	
	public synchronized Portfolio getPortfolio() {
		if ( portfolio == null ) {
			throw new NullPointerException();
		}
		return portfolio;
	}
	
	public synchronized Security getSecurity() {
		if ( security == null ) {
			throw new NullPointerException();
		}
		return security;
	}
	
	public synchronized STSeriesHandler getSeriesHandlerT0() {
		if ( sht0 == null ) {
			throw new NullPointerException();
		}
		return sht0;
	}
	
	public synchronized STSeriesHandler getSeriesHandlerT1() {
		if ( sht1 == null ) {
			throw new NullPointerException();
		}
		return sht1;
	}
	
	public synchronized STSeriesHandler getSeriesHandlerT2() {
		if ( sht2 == null ) {
			throw new NullPointerException();
		}
		return sht2;
	}
	
}
