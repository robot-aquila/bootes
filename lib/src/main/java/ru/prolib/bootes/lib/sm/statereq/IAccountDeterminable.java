package ru.prolib.bootes.lib.sm.statereq;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;

public interface IAccountDeterminable extends IStateObservable {
	Account getAccount();
	void setPortfolio(Portfolio portfolio);
	Portfolio getPortfolio();
}
