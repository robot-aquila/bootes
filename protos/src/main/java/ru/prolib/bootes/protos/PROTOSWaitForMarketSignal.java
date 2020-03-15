package ru.prolib.bootes.protos;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMExitAction;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMTrigger;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.lib.robo.s3.S3Speculation;
import ru.prolib.bootes.lib.robo.s3.S3WaitForMarketSignal;

public class PROTOSWaitForMarketSignal extends S3WaitForMarketSignal implements SMExitAction {
	static final Logger logger = LoggerFactory.getLogger(PROTOSWaitForMarketSignal.class);
	private final PROTOSRobotState state;

	public PROTOSWaitForMarketSignal(AppServiceLocator serviceLocator,
									 PROTOSRobotState state)
	{
		super(serviceLocator, state);
		this.setExitAction(this);
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
		RMContractStrategyPositionParams cspp = state.getContractStrategy().getPositionParams(curr_time);	
		state.setPositionParams(cspp);
		state.getStateListener().riskManagementUpdate();
	}

	@Override
	public void exit() {
		logger.debug("Exiting at {}", serviceLocator.getScheduler().getCurrentTime());
	}
	
	@Override
	public SMExit input(Object data) {
		SMExit exit = super.input(data);
		if ( exit == getExit(E_BUY) || exit == getExit(E_SELL) ) {
			dumpPreSignalStateInfo();
		}
		return exit;
	}
	
	protected void dumpPreSignalStateInfo() {
		Portfolio port = state.getPortfolio();
		RMContractStrategyPositionParams cspp = state.getPositionParams();
		S3Speculation spec = state.getActiveSpeculation();
		StringBuilder sb = new StringBuilder();
		String ln = System.lineSeparator();
		sb.append("State at ").append(cspp.getTime()).append(ln)
			.append("Portfolio info ---------------------------").append(ln)
			.append("     Equity: ").append(port.getEquity()).append(ln)
			.append("        P&L: ").append(port.getProfitAndLoss()).append(ln)
			.append("Used Margin: ").append(port.getUsedMargin()).append(ln)
			.append("Free Margin: ").append(port.getFreeMargin()).append(ln)
			.append("Position params --------------------------").append(ln)
			.append("              Time: ").append(cspp.getTime()).append(ln)
			.append("          Base cap: ").append(cspp.getBaseCap()).append(ln)
			.append("     Num Contracts: ").append(cspp.getNumberOfContracts()).append(ln)
			.append("  Take Profit Pts.: ").append(cspp.getTakeProfitPts()).append(ln)
			.append("    Stop-Loss Pts.: ").append(cspp.getStopLossPts()).append(ln)
			.append("     Slippage Pts.: ").append(cspp.getSlippagePts()).append(ln)
			.append("    Trage Goal Cap: ").append(cspp.getTradeGoalCap()).append(ln)
			.append("    Trade Loss Cap: ").append(cspp.getTradeLossCap()).append(ln)
			.append("  Avg.Daily Pr.Mv.: ").append(cspp.getAvgDailyPriceMove()).append(ln)
			.append("  Avg.Local Pr.Mv.: ").append(cspp.getAvgLocalPriceMove()).append(ln)
			.append("Signal info ------------------------------").append(ln)
			.append("       Type: ").append(spec.getSignalType()).append(ln)
			// spec's TP,SL & BE not yet specified
			// will be set at tracking phase
			//.append("Take Profit: ").append(spec.getTakeProfit()).append(ln)
			//.append("  Stop-Loss: ").append(spec.getStopLoss()).append(ln)
			//.append(" Break-Even: ").append(spec.getBreakEven()).append(ln)
			;
		logger.debug(sb.toString());
	}

}
