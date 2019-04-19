package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.bootes.lib.rm.RMContractStrategy.ObjectLocator;

public class TSGR001AStrategyObjectLocator implements ObjectLocator {
	private final RobotState state;
	
	public TSGR001AStrategyObjectLocator(RobotState state) {
		this.state = state;
	}

	@Override
	public Security getSecurity() {
		return state.getSecurity();
	}

	@Override
	public Portfolio getPortfolio() {
		return state.getPortfolio();
	}

}
