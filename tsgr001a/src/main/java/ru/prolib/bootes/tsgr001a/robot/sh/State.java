package ru.prolib.bootes.tsgr001a.robot.sh;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.bootes.tsgr001a.robot.ContractParams;
import ru.prolib.bootes.tsgr001a.robot.ContractResolver;

/**
 * Robot state.
 * <p>
 * This class is intended to be accessible outside. Thus, all methods must be synchronized.
 */
public class State {
	private String contractName;
	private ContractResolver contractResolver;
	private ContractParams contractParams;
	private Security security;
	
	public State() {
		
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
	
}
