package ru.prolib.bootes.protos;

import java.time.Instant;

import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMTrigger;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.lib.robo.s3.S3WaitForMarketSignal;

public class PROTOSWaitForMarketSignal extends S3WaitForMarketSignal {
	private final PROTOSRobotState state;

	public PROTOSWaitForMarketSignal(AppServiceLocator serviceLocator,
									 PROTOSRobotState state)
	{
		super(serviceLocator, state);
		this.state = state;
	}

	@Override
	protected SMTrigger createTriggerInitiator(SMInput target_input) {
		return newTriggerOnEvent(state.getSessionDataHandler()
				.getSeriesHandlerT0()
				.getSeries()
				.onLengthUpdate(), target_input);
	}

	@Override
	protected void onSignalDetectionTrigger(Instant curr_time) {
		RMContractStrategyPositionParams cspp = state.getContractStrategy()
				.getPositionParams(curr_time);
		state.setPositionParams(cspp);
		state.getStateListener().riskManagementUpdate();
	}

}
