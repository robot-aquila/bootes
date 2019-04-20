package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.data.tseries.STSeriesHandler;
import ru.prolib.bootes.lib.rm.RMContractStrategyParams;
import ru.prolib.bootes.lib.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.lib.robo.s3.S3RobotState;

/**
 * Robot state.
 * <p>
 * This class is intended to be accessible outside. Thus, all methods must be synchronized.
 */
public class RobotState extends S3RobotState /*implements IS3Speculative*/ {
	private String contractName;//, accountCode;
	private RMContractStrategyPositionParams positionParams;
	private STSeriesHandler sht0, sht1, sht2;
	private RMContractStrategyParams strategyParams;
	
	public synchronized void setContractStrategyParams(RMContractStrategyParams params) {
		this.strategyParams = params;
	}
	
	public synchronized void setContractName(String contractName) {
		this.contractName = contractName;
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
	
	public synchronized String getContractName() {
		if ( contractName == null ) {
			throw new NullPointerException();
		}
		return contractName;
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
	
	public synchronized RMContractStrategyParams getContractStrategyParams() {
		if ( strategyParams == null ) {
			throw new NullPointerException();
		}
		return strategyParams;
	}
	
}
