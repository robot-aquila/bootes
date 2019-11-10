package ru.prolib.bootes.lib.robo.s3;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.bootes.lib.cr.ContractParams;
import ru.prolib.bootes.lib.cr.ContractResolver;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.SignalTrigger;
import ru.prolib.bootes.lib.data.ts.filter.IFilterSet;
import ru.prolib.bootes.lib.rm.IRMContractStrategy;
import ru.prolib.bootes.lib.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.lib.robo.ISessionDataHandler;
import ru.prolib.bootes.lib.robo.s3.statereq.IS3SignalDeterminable;
import ru.prolib.bootes.lib.robo.s3.statereq.IS3Speculative;
import ru.prolib.bootes.lib.robo.sh.statereq.ISessionDataTrackable;

/**
 * Common S3 robot state.
 */
public class S3RobotState implements
	IS3Speculative,
	ISessionDataTrackable,
	IS3SignalDeterminable
{
	private final S3RobotStateListenerComp stateListener;
	private Account account;
	private Portfolio portfolio;
	private ContractResolver contractResolver;
	private ContractParams contractParams;
	private Security security;
	private S3Speculation activeSpeculation;
	private String robotTitle;
	private ISessionDataHandler sessionDataHandler;
	private SignalTrigger signalTrigger;
	private IFilterSet<S3TradeSignal> signalFilter;
	private IRMContractStrategy contractStrategy;
	private RMContractStrategyPositionParams positionParams;
	private SubscrHandler symbolSubsHandler;
	
	public S3RobotState(S3RobotStateListenerComp listener) {
		this.stateListener = listener;
	}
	
	public S3RobotState() {
		this(new S3RobotStateListenerComp());
	}

	@Override
	public S3RobotStateListenerComp getStateListener() {
		return stateListener;
	}

	@Override
	public synchronized Account getAccount() {
		if ( account == null ) {
			throw new NullPointerException();
		}
		return account;
	}
	
	public synchronized void setAccount(Account account) {
		this.account = account;
	}

	@Override
	public synchronized Portfolio getPortfolio() {
		if ( portfolio == null ) {
			throw new NullPointerException();
		}
		return portfolio;
	}

	@Override
	public synchronized void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	@Override
	public synchronized ContractResolver getContractResolver() {
		if ( contractResolver == null ) {
			throw new NullPointerException();
		}
		return contractResolver;
	}
	
	public synchronized void setContractResolver(ContractResolver resolver) {
		this.contractResolver = resolver;
	}

	@Override
	public synchronized ContractParams getContractParamsOrNull() {
		return contractParams;
	}

	@Override
	public synchronized ContractParams getContractParams() {
		if ( contractParams == null ) {
			throw new NullPointerException();
		}
		return contractParams;
	}
	
	@Override
	public synchronized void setContractParams(ContractParams params) {
		this.contractParams = params;
	}

	@Override
	public synchronized Security getSecurity() {
		if ( security == null ) {
			throw new NullPointerException();
		}
		return security;
	}

	@Override
	public synchronized void setSecurity(Security security) {
		this.security = security;
	}

	@Override
	public synchronized S3Speculation getActiveSpeculation() {
		if ( activeSpeculation == null ) {
			throw new NullPointerException();
		}
		return activeSpeculation;
	}

	@Override
	public synchronized void setActiveSpeculation(S3Speculation spec) {
		this.activeSpeculation = spec;
	}
	
	public synchronized String getRobotTitle() {
		if ( robotTitle == null ) {
			throw new NullPointerException();
		}
		return robotTitle;
	}
	
	public synchronized void setRobotTitle(String title) {
		this.robotTitle = title;
	}

	@Override
	public synchronized ISessionDataHandler getSessionDataHandler() {
		if ( sessionDataHandler == null ) {
			throw new NullPointerException();
		}
		return sessionDataHandler;
	}

	public synchronized void setSessionDataHandler(ISessionDataHandler handler) {
		this.sessionDataHandler = handler;
	}

	@Override
	public synchronized SignalTrigger getSignalTrigger() {
		if ( signalTrigger == null ) {
			throw new NullPointerException();
		}
		return signalTrigger;
	}
	
	public synchronized void setSignalTrigger(SignalTrigger trigger) {
		this.signalTrigger = trigger;
	}

	@Override
	public synchronized IFilterSet<S3TradeSignal> getSignalFilter() {
		if ( signalFilter == null ) {
			throw new NullPointerException();
		}
		return signalFilter;
	}
	
	public synchronized void setSignalFilter(IFilterSet<S3TradeSignal> filter) {
		this.signalFilter = filter;
	}

	@Override
	public synchronized IRMContractStrategy getContractStrategy() {
		if ( contractStrategy == null ) {
			throw new NullPointerException();
		}
		return contractStrategy;
	}
	
	public synchronized void setContractStrategy(IRMContractStrategy strategy) {
		this.contractStrategy = strategy;
	}
	
	@Override
	public synchronized RMContractStrategyPositionParams getPositionParams() {
		if ( positionParams == null ) {
			throw new NullPointerException();
		}
		return positionParams;
	}
	
	@Override
	public synchronized RMContractStrategyPositionParams getPositionParamsOrNull() {
		return positionParams;
	}
	
	@Override
	public synchronized void setPositionParams(RMContractStrategyPositionParams params) {
		this.positionParams = params;
	}

	@Override
	public synchronized SubscrHandler getContractSubscrHandler() {
		return symbolSubsHandler;
	}

	@Override
	public synchronized void setContractSubscrHandler(SubscrHandler handler) {
		this.symbolSubsHandler = handler;
	}

}
