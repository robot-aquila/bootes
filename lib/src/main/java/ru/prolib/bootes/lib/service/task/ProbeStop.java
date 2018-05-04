package ru.prolib.bootes.lib.service.task;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.probe.SchedulerImpl;

public class ProbeStop implements Runnable {
	protected final SchedulerImpl probe;
	
	public ProbeStop(SchedulerImpl probe) {
		this.probe = probe;
	}

	@Override
	public void run() {
		probe.setModeWait();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ProbeStop.class ) {
			return false;
		}
		ProbeStop o = (ProbeStop) other;
		return new EqualsBuilder()
			.append(o.probe, probe)
			.isEquals();
	}

}
