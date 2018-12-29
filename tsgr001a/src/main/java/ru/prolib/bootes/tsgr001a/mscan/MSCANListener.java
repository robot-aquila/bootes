package ru.prolib.bootes.tsgr001a.mscan;

public interface MSCANListener {
	
	/**
	 * Process a skipped event.
	 * Skipped event is an event which is finished immediately after they created.
	 * <p>
	 * @param event - event instance
	 */
	void onEventSkipped(MSCANEvent event);
	
	void onEventStarted(MSCANEvent event);
	
	void onEventChanged(MSCANEvent event, MSCANLogEntry entry);
	
	void onEventClosed(MSCANEvent event);
}
