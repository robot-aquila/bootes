package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.tseries.STSeriesHandler;
import ru.prolib.bootes.lib.cr.ContractParams;
import ru.prolib.bootes.lib.cr.ContractResolver;
import ru.prolib.bootes.lib.rm.RMContractStrategy;
import ru.prolib.bootes.lib.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListener;
import ru.prolib.bootes.lib.robo.s3.S3Speculation;
import ru.prolib.bootes.lib.robo.s3.statereq.IS3Speculative;

/**
 * Robot state.
 * <p>
 * This class is intended to be accessible outside. Thus, all methods must be synchronized.
 */
public class RobotState implements IS3Speculative {
	private final S3RobotStateListener stateListener;
	private String contractName, accountCode;
	private ContractResolver contractResolver;
	private ContractParams contractParams;
	private RMContractStrategy contractStrategy;
	private RMContractStrategyPositionParams positionParams;
	private Portfolio portfolio;
	private Security security;
	private STSeriesHandler sht0, sht1, sht2;
	private S3Speculation speculation;
	
	public RobotState(S3RobotStateListener stateListener) {
		this.stateListener = stateListener;
	}
	
	public S3RobotStateListener getStateListener() {
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
	
	@Override
	public synchronized void setContractParams(ContractParams contractParams) {
		this.contractParams = contractParams;
	}
	
	public synchronized void setContractStrategy(RMContractStrategy contractStrategy) {
		this.contractStrategy = contractStrategy;
	}
	
	public synchronized void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
	@Override
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
	
	public synchronized void setPositionParams(RMContractStrategyPositionParams params) {
		this.positionParams = params;
	}
	
	@Override
	public synchronized void setActiveSpeculation(S3Speculation speculation) {
		this.speculation = speculation;
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
	
	@Override
	public Account getAccount() {
		return new Account(getAccountCode());
	}
	
	@Override
	public synchronized ContractResolver getContractResolver() {
		if ( contractResolver == null ) {
			throw new NullPointerException(); 
		}
		return contractResolver;
	}
	
	@Override
	public synchronized ContractParams getContractParams() {
		if ( contractParams == null ) {
			throw new NullPointerException();
		}
		return contractParams;
	}
	
	@Override
	public synchronized ContractParams getContractParamsOrNull() {
		return contractParams;
	}
	
	public synchronized RMContractStrategy getContractStrategy() {
		if ( contractStrategy == null ) {
			throw new NullPointerException();
		}
		return contractStrategy;
	}
	
	public synchronized Portfolio getPortfolio() {
		if ( portfolio == null ) {
			throw new NullPointerException();
		}
		return portfolio;
	}
	
	@Override
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
	
	public synchronized boolean isSeriesHandlerT0Defined() {
		return sht0 != null;
	}
	
	public synchronized STSeriesHandler getSeriesHandlerT1() {
		if ( sht1 == null ) {
			throw new NullPointerException();
		}
		return sht1;
	}
	
	public synchronized boolean isSeriesHandlerT1Defined() {
		return sht1 != null;
	}
	
	public synchronized STSeriesHandler getSeriesHandlerT2() {
		if ( sht2 == null ) {
			throw new NullPointerException();
		}
		return sht2;
	}
	
	public synchronized boolean isSeriesHandlerT2Defined() {
		return sht2 != null;
	}
	
	public synchronized RMContractStrategyPositionParams getPositionParams() {
		if ( positionParams == null ) {
			throw new NullPointerException();
		}
		return positionParams;
	}
	
	public synchronized boolean isPositionParamsDefined() {
		return positionParams != null;
	}
	
	@Override
	public synchronized S3Speculation getActiveSpeculation() {
		if ( speculation == null ) {
			throw new NullPointerException();
		}
		return speculation;
	}
	
}
