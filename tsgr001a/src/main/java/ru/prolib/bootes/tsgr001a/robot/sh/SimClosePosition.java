package ru.prolib.bootes.tsgr001a.robot.sh;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.s3.S3RobotStateListener;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class SimClosePosition extends CommonHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SimClosePosition.class);
	}
	
	public static final String E_CLOSED = "CLOSED";
	
	public SimClosePosition(AppServiceLocator serviceLocator,
			RobotState state)
	{
		super(serviceLocator, state);
		registerExit(E_CLOSED);
	}
	
	private CDecimal getSpeculationPL(Speculation spec) {
		switch ( spec.getTradeSignal().getType() ) {
		case BUY:
			return spec.getExitPoint().getValue()
				.subtract(spec.getEntryPoint().getValue());
		case SELL:
			return spec.getEntryPoint().getValue()
				.subtract(spec.getExitPoint().getValue());
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Speculation spec = null;
		Security security = null;
		S3RobotStateListener listener = null;
		synchronized ( state ) {
			spec = state.getActiveSpeculation();
			security = state.getSecurity();
			listener = state.getStateListener();
		}
		CDecimal last_price = security.getLastTrade().getPrice(), result;
		Instant curr_time = serviceLocator.getTerminal().getCurrentTime();
		synchronized ( spec ) {
			CDecimal qty = spec.getEntryPoint().getSize();
			Tick entry = Tick.of(TickType.TRADE,
					curr_time,
					last_price,
					qty,
					security.priceToValueWR(last_price, qty)
				);
			spec.setExitPoint(entry);
			spec.setResult(result = getSpeculationPL(spec));
			int flags = spec.getFlags() | Speculation.SF_STATUS_CLOSED;
			if ( result.compareTo(CDecimalBD.ZERO) > 0 ) {
				flags |= Speculation.SF_RESULT_PROFIT;
				logger.debug("{} Profit: {}", curr_time, result);
			} else {
				logger.debug("{}   Loss: {}", curr_time, result);
			}
			spec.setFlags(flags);
		}
		listener.speculationClosed();
		return getExit(E_CLOSED);
	}

}
