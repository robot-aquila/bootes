package ru.prolib.bootes.lib.service.task;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.bootes.lib.service.AppRuntimeService;

public class AppShutdown implements Runnable {
	protected final AppRuntimeService rts;

	public AppShutdown(AppRuntimeService rts) {
		this.rts = rts;
	}

	@Override
	public void run() {
		rts.triggerShutdown();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AppShutdown.class ) {
			return false;
		}
		AppShutdown o = (AppShutdown) other;
		return new EqualsBuilder()
			.append(o.rts, rts)
			.isEquals();
	}

}
