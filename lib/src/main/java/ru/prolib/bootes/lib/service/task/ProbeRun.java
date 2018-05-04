package ru.prolib.bootes.lib.service.task;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.probe.SchedulerImpl;

public class ProbeRun implements Runnable {
	protected final SchedulerImpl probe;
	
	public ProbeRun(SchedulerImpl probe) {
		this.probe = probe;
	}

	@Override
	public void run() {
		probe.setExecutionSpeed(0);
		probe.setModeRun();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ProbeRun.class ) {
			return false;
		}
		ProbeRun o = (ProbeRun) other;
		return new EqualsBuilder()
			.append(o.probe, probe)
			.isEquals();
	}

}
