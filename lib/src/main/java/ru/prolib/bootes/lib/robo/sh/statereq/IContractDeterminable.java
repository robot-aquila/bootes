package ru.prolib.bootes.lib.robo.sh.statereq;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.bootes.lib.cr.ContractParams;
import ru.prolib.bootes.lib.cr.ContractResolver;

public interface IContractDeterminable extends IStateObservable {
	ContractResolver getContractResolver();
	ContractParams getContractParamsOrNull();
	ContractParams getContractParams();
	Security getSecurity();
	void setContractParams(ContractParams params);
	void setSecurity(Security security);
}
