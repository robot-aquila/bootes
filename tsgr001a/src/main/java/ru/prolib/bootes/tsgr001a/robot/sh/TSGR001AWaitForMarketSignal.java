package ru.prolib.bootes.tsgr001a.robot.sh;

import java.time.Instant;

import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMTrigger;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.lib.robo.s3.S3WaitForMarketSignal;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class TSGR001AWaitForMarketSignal extends S3WaitForMarketSignal {
	private final RobotState state;
	
	public TSGR001AWaitForMarketSignal(AppServiceLocator serviceLocator,
									   RobotState state)
	{
		super(serviceLocator, state);
		this.state = state;
	}
	
	/**
	 * Create trigger-initiator which will initiate signal check as reaction on
	 * some kind event. For example it may be recurrent call based on timer. Or
	 * it may be call based on event of data series length update. etc...
	 * <p>
	 * @param target_input - input to receive data
	 * @return trigger
	 */
	@Override
	protected SMTrigger createTriggerInitiator(SMInput target_input) {
		return newTriggerOnEvent(state.getSessionDataHandler()
				.getSeriesHandlerT0()
				.getSeries()
				.onLengthUpdate(), target_input);
	}
	
	/**
	 * Called each time when trigger-initiator signals for event.
	 * <p>
	 * @param curr_time - current time (according to app scheduler)
	 */
	@Override
	protected void onSignalDetectionTrigger(Instant curr_time) {
		RMContractStrategyPositionParams cspp = state.getContractStrategy().getPositionParams(curr_time);
		state.setPositionParams(cspp);
		state.getStateListener().riskManagementUpdate();
	}

}
