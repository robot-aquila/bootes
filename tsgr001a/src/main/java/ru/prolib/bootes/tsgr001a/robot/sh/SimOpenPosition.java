package ru.prolib.bootes.tsgr001a.robot.sh;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListener;
import ru.prolib.bootes.lib.robo.s3.S3Speculation;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class SimOpenPosition extends CommonHandler {
	public static final String E_OPENED = "OPENED";
	
	public SimOpenPosition(AppServiceLocator serviceLocator,
			RobotState state)
	{
		super(serviceLocator, state);
		registerExit(E_OPENED);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		S3Speculation spec = state.getActiveSpeculation();
		Security security = state.getSecurity();
		S3RobotStateListener listener = state.getStateListener();
		synchronized ( spec ) {
			S3TradeSignal signal = spec.getTradeSignal();
			CDecimal price = signal.getExpectedPrice();
			Tick entry = Tick.of(TickType.TRADE,
					signal.getTime(),
					price,
					signal.getExpectedQty(),
					security.priceToValueWR(price, signal.getExpectedQty())
				);
			spec.setFlags(S3Speculation.SF_NEW);
			spec.setEntryPoint(entry);
		}
		listener.speculationOpened();
		return getExit(E_OPENED);
	}

}
