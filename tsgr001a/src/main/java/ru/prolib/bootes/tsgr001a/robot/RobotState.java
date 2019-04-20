package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.bootes.lib.rm.RMContractStrategyParams;
import ru.prolib.bootes.lib.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.lib.robo.s3.S3RobotState;

/**
 * Robot state.
 * <p>
 * This class is intended to be accessible outside. Thus, all methods must be synchronized.
 */
public class RobotState extends S3RobotState {
	private String contractName;
	private RMContractStrategyPositionParams positionParams;
	private RMContractStrategyParams strategyParams;
	
	@Override
	public TSGR001ADataHandler getSessionDataHandler() {
		return (TSGR001ADataHandler) super.getSessionDataHandler();
	}
	
	public synchronized void setContractStrategyParams(RMContractStrategyParams params) {
		this.strategyParams = params;
	}
	
	public synchronized void setContractName(String contractName) {
		this.contractName = contractName;
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
