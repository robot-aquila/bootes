package xx.mix.bootes.kinako.robot;

import xx.mix.bootes.kinako.service.VVSignal;

public class KinakoRobotData {
	private VVSignal currentSignal;
	
	public synchronized VVSignal getCurrentSignal() {
		if ( currentSignal == null ) {
			throw new NullPointerException("Current signal was not defined");
		}
		return currentSignal;
	}
	
	public synchronized KinakoRobotData setCurrentSignal(VVSignal signal) {
		this.currentSignal = signal;
		return this;
	}

}
