package ru.prolib.bootes.lib.robo.s3.statereq;

import ru.prolib.bootes.lib.rm.RMContractStrategyPositionParams;

public interface IS3PositionDeterminable {
	RMContractStrategyPositionParams getPositionParams();
	RMContractStrategyPositionParams getPositionParamsOrNull();
	void setPositionParams(RMContractStrategyPositionParams params);
}
