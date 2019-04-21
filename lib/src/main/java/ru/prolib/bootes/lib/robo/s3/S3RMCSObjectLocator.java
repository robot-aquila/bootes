package ru.prolib.bootes.lib.robo.s3;

import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.bootes.lib.rm.RMContractStrategy.ObjectLocator;

public class S3RMCSObjectLocator implements ObjectLocator {
	private final S3RobotState state;
	
	public S3RMCSObjectLocator(S3RobotState state) {
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
