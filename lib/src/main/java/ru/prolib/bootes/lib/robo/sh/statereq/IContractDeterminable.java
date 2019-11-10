package ru.prolib.bootes.lib.robo.sh.statereq;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.bootes.lib.cr.ContractParams;
import ru.prolib.bootes.lib.cr.ContractResolver;

public interface IContractDeterminable extends IStateObservable {
	ContractResolver getContractResolver();
	ContractParams getContractParamsOrNull();
	ContractParams getContractParams();
	Security getSecurity();
	
	/**
	 * Get contract's subscription handler.
	 * <p>
	 * @return subscription handler
	 */
	SubscrHandler getContractSubscrHandler();
	
	void setContractParams(ContractParams params);
	void setSecurity(Security security);
	
	/**
	 * Set contract's subscription handler.
	 * <p>
	 * @param handler - subscription handler
	 */
	void setContractSubscrHandler(SubscrHandler handler);
}
