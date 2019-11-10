package xx.mix.bootes.kinako.robot;

import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;

public class KinakoUnsubscribeSymbols extends SMStateHandlerEx {
	public static final String E_OK = "OK";
	
	private final KinakoRobotData data;
	
	public KinakoUnsubscribeSymbols(
			AppServiceLocator service_locator,
			KinakoRobotData robot_data
		)
	{
		this.data = robot_data;
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		for ( SubscrHandler handler : data.getSymbolSubscrHandlers() ) {
			handler.close();
		}
		return getExit(E_OK);
	}

}
