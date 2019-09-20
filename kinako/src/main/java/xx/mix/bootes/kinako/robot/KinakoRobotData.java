package xx.mix.bootes.kinako.robot;

import xx.mix.bootes.kinako.service.VVSignal;

public class KinakoRobotData {
	private VVSignal currentSignal;
	
	public VVSignal getCurrentSignal() {
		if ( currentSignal == null ) {
			throw new NullPointerException("Current signal was not defined");
		}
		return currentSignal;
	}
	
	public KinakoRobotData setCurrentSignal(VVSignal signal) {
		this.currentSignal = signal;
		return this;
	}

}
