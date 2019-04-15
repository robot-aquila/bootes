package ru.prolib.bootes.lib.robo.sh.statereq;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;

public interface IAccountDeterminable extends IStateObservable {
	Account getAccount();
	Portfolio getPortfolio();
	void setPortfolio(Portfolio portfolio);
}
