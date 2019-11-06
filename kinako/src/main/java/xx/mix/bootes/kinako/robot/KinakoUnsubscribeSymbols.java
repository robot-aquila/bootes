package xx.mix.bootes.kinako.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;

public class KinakoUnsubscribeSymbols extends SMStateHandlerEx {
	private static final Logger logger;
	public static final String E_OK = "OK";
	
	static {
		logger = LoggerFactory.getLogger(KinakoUnsubscribeSymbols.class);
	}
	
	private final AppServiceLocator serviceLocator;
	private final KinakoRobotData data;
	
	public KinakoUnsubscribeSymbols(
			AppServiceLocator service_locator,
			KinakoRobotData robot_data
		)
	{
		this.serviceLocator = service_locator;
		this.data = robot_data;
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Terminal terminal = serviceLocator.getTerminal();
		for ( Symbol symbol : data.getSubscribedSymbols() ) {
			terminal.unsubscribe(symbol, MDLevel.L1_BBO);
			logger.debug("Unsubscribe symbol: {}", symbol);
		}
		return getExit(E_OK);
	}

}
