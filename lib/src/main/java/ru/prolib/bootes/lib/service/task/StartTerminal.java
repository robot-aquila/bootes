package ru.prolib.bootes.lib.service.task;

import ru.prolib.aquila.core.BusinessEntities.Terminal;

public class StartTerminal implements Runnable {
	private final Terminal terminal;
	
	public StartTerminal(Terminal terminal) {
		this.terminal = terminal;
	}

	@Override
	public void run() {
		terminal.start();
	}

}
