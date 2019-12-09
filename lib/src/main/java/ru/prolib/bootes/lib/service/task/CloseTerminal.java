package ru.prolib.bootes.lib.service.task;

import ru.prolib.aquila.core.BusinessEntities.Terminal;

public class CloseTerminal implements Runnable {
	private final Terminal terminal;
	
	public CloseTerminal(Terminal terminal) {
		this.terminal = terminal;
	}

	@Override
	public void run() {
		terminal.close();
	}

}
