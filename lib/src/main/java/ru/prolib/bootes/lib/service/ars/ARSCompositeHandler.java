package ru.prolib.bootes.lib.service.ars;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ARSCompositeHandler implements ARSHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(ARSCompositeHandler.class);
	}
	
	private final String handlerID;
	private final List<ARSAction> startupActions, shutdownActions;

	public ARSCompositeHandler(String handlerID, List<ARSAction> startupActions, List<ARSAction> shutdownActions) {
		this.handlerID = handlerID;
		this.startupActions = startupActions;
		this.shutdownActions = shutdownActions;
	}
	
	public String getHanderID() {
		return handlerID;
	}
	
	public List<ARSAction> getStartupActions() {
		return startupActions;
	}
	
	public List<ARSAction> getShutdownActions() {
		return shutdownActions;
	}

	@Override
	public void startup() throws Throwable {
		int i = 1, count = startupActions.size();
		try {
			for ( ARSAction action : startupActions ) {
				action.run();
				i ++;
			}
		} catch ( Throwable t ) {
			Object args[] = { handlerID, i, count };
			logger.error("startup {} failed ({}/{})", args);
			throw t;
		}
		if ( logger.isDebugEnabled() ) {
			Object args[] = { handlerID, i - 1, count };
			logger.debug("startup {} ok ({}/{})", args);
		}
	}

	@Override
	public void shutdown() throws Throwable {
		int i = 1, count = shutdownActions.size();
		try {
			for ( ARSAction action : shutdownActions ) {
				action.run();
				i ++;
			}
		} catch ( Throwable t ) {
			Object args[] = { handlerID, i, count };
			logger.error("shutdown {} failed ({}/{})", args);
			throw t;
		}
		if ( logger.isDebugEnabled() ) {
			Object args[] = { handlerID, i - 1, count };
			logger.debug("shutdown {} ok ({}/{})", args);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ARSCompositeHandler.class ) {
			return false;
		}
		ARSCompositeHandler o = (ARSCompositeHandler) other;
		return new EqualsBuilder()
				.append(o.handlerID, handlerID)
				.append(o.startupActions, startupActions)
				.append(o.shutdownActions, shutdownActions)
				.build();
	}

}
