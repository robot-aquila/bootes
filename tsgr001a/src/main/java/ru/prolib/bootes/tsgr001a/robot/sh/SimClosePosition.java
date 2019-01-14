package ru.prolib.bootes.tsgr001a.robot.sh;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.RobotStateListener;

public class SimClosePosition extends CommonHandler {
	public static final String E_CLOSED = "CLOSED";
	
	public interface Ctrl {
		CDecimal getSpeculationPL(Speculation spec);
	}

	private final Ctrl ctrl;
	
	public SimClosePosition(AppServiceLocator serviceLocator,
			RobotState state,
			Ctrl ctrl)
	{
		super(serviceLocator, state);
		this.ctrl = ctrl;
		registerExit(E_CLOSED);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Speculation spec = null;
		Security security = null;
		RobotStateListener listener = null;
		synchronized ( state ) {
			spec = state.getActiveSpeculation();
			security = state.getSecurity();
			listener = state.getStateListener();
		}
		CDecimal last_price = security.getLastTrade().getPrice();
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
			int flags = spec.getFlags() | Speculation.SF_STATUS_CLOSED;
			CDecimal result = ctrl.getSpeculationPL(spec);
			if ( result.compareTo(CDecimalBD.ZERO) > 0 ) {
				flags |= Speculation.SF_RESULT_PROFIT;
			}
			spec.setFlags(flags);
		}
		listener.speculationClosed();
		return getExit(E_CLOSED);
	}

}
