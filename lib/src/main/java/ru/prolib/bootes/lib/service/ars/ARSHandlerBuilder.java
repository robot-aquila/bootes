package ru.prolib.bootes.lib.service.ars;

import java.util.ArrayList;
import java.util.List;

public class ARSHandlerBuilder {
	private String handlerID;
	private final List<ARSAction> startupActions, shutdownActions;
	
	public ARSHandlerBuilder(List<ARSAction> startupActions, List<ARSAction> shutdownActions) {
		this.startupActions = startupActions;
		this.shutdownActions = shutdownActions;
	}
	
	public ARSHandlerBuilder() {
		this(new ArrayList<>(), new ArrayList<>());
	}
	
	public ARSHandlerBuilder withID(String handlerID) {
		this.handlerID = handlerID;
		return this;
	}
	
	public ARSHandlerBuilder addStartupAction(ARSAction action) {
		startupActions.add(action);
		return this;
	}
	
	public ARSHandlerBuilder addStartupAction(Runnable action) {
		startupActions.add(new ARSActionR(action));
		return this;
	}
	
	public ARSHandlerBuilder addShutdownAction(ARSAction action) {
		shutdownActions.add(action);
		return this;
	}
	
	public ARSHandlerBuilder addShutdownAction(Runnable action) {
		shutdownActions.add(new ARSActionR(action));
		return this;
	}
	
	public ARSHandler build() {
		if ( handlerID == null ) {
			throw new IllegalStateException("Handler ID cannot be null");
		}
		return new ARSCompositeHandler(handlerID, startupActions, shutdownActions);
	}

}
