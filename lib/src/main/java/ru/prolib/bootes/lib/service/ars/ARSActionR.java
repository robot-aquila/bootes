package ru.prolib.bootes.lib.service.ars;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class ARSActionR implements ARSAction {
	private final Runnable r;
	
	public ARSActionR(Runnable r) {
		this.r = r;
	}
	
	public Runnable getRunnable() {
		return r;
	}

	@Override
	public void run() throws Throwable {
		r.run();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ARSActionR.class ) {
			return false;
		}
		ARSActionR o = (ARSActionR) other;
		return new EqualsBuilder()
				.append(o.r, r)
				.build();
	}

}
