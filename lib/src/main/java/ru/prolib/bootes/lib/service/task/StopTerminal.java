package ru.prolib.bootes.lib.service.task;

import ru.prolib.aquila.core.BusinessEntities.Terminal;

public class StopTerminal implements Runnable {
	private final Terminal terminal;
	
	public StopTerminal(Terminal terminal) {
		this.terminal = terminal;
	}

	@Override
	public void run() {
		terminal.stop();
	}

}
