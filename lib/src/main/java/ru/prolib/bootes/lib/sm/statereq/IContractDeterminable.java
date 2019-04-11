package ru.prolib.bootes.lib.sm.statereq;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.bootes.lib.cr.ContractParams;

public interface IContractDeterminable extends IStateObservable {
	ContractParams determineContractParams(Instant time);
	ContractParams getCurrentContractParamsOrNull();
	ContractParams getCurrentContractParams();
	boolean isCurrentContractParamsDefined();
	void setCurrentContractParams(ContractParams params);
	void setCurrentSecurity(Security security);
	Security getCurrentSecurity();
}
