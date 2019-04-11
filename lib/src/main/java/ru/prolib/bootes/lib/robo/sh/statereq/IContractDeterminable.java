package ru.prolib.bootes.lib.robo.sh.statereq;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.bootes.lib.cr.ContractParams;

public interface IContractDeterminable extends IStateObservable {
	ContractParams determineContractParams(Instant time);
	ContractParams getContractParamsOrNull();
	ContractParams getContractParams();
	boolean isContractParamsDefined();
	void setContractParams(ContractParams params);
	void setSecurity(Security security);
	Security getSecurity();
}
