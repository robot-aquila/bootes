package ru.prolib.bootes.protos.sos;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.OrderDefinition;
import ru.prolib.aquila.core.BusinessEntities.OrderDefinitionProvider;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;

public class SOSWaitForSignal extends SMStateHandlerEx implements SMInputAction {
	static final Logger logger = LoggerFactory.getLogger(SOSWaitForSignal.class);
	
	public static final String E_BUY  = "BUY";
	public static final String E_SELL = "SELL";
	public static final String E_END  = "END";
	
	protected final AppServiceLocator serviceLocator;
	protected final OrderDefinitionProvider provider;
	protected final SMInput in;
	private OrderDefinition orderDef;
	
	public SOSWaitForSignal(AppServiceLocator serviceLocator, OrderDefinitionProvider provider) {
		this.serviceLocator = serviceLocator;
		this.provider = provider;
		registerExit(E_BUY);
		registerExit(E_SELL);
		registerExit(E_END);
		in = registerInput(this);
		setResultDataType(OrderDefinition.class);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		try {
			setResultData(orderDef = provider.getNextDefinition());
		} catch ( IOException e ) {
			logger.error("Unexpected error: ", e);
			return getExit(E_ERROR);
		}
		if ( orderDef == null ) {
			return getExit(E_END);
		}
		triggers.add(newTriggerOnTimer(serviceLocator.getScheduler(), orderDef.getPlacementTime(), in));
		return null;
	}

	@Override
	public SMExit input(Object data) {
		return getExit(orderDef.getAction().isBuy() ? E_BUY : E_SELL);
	}

}
