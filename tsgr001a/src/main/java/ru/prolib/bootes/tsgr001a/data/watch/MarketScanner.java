package ru.prolib.bootes.tsgr001a.data.watch;


import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.tseries.STSeries;
import ru.prolib.bootes.tsgr001a.mscan.MSCANScanner;
import ru.prolib.bootes.tsgr001a.mscan.MSCANScannerImpl;
import ru.prolib.bootes.tsgr001a.mscan.sensors.SensorWAP3C;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;

public class MarketScanner implements EventListener {
	private final MSCANScanner scanner;
	private final MarketObserver observer;
	private final SensorWAP3C sensor;
	private STSeries source;
	
	public MarketScanner() {
		this.observer = new MarketObserver();
		this.sensor = new SensorWAP3C();
		this.scanner = new MSCANScannerImpl();
		this.scanner.addListener(observer);
		this.scanner.addSensor(sensor);
	}
	
	public void printStats() {
		observer.printStats();
	}

	public synchronized void watch(STSeries source) {
		if ( this.source != null ) {
			this.source.onLengthUpdate().removeListener(this);
		}
		this.source = source;
		source.onLengthUpdate().addListener(this);
		sensor.setConditionSeries(source.getSeries(SetupT0.SID_PVC_WAVG));
		sensor.setValueSeries(source.getSeries(SetupT0.SID_CLOSE_PRICE));
	}

	@Override
	public synchronized void onEvent(Event event) {
		if ( source != null && event.isType(source.onLengthUpdate()) ) {
			try {
				scanner.analyze(source.get());
			} catch ( ValueException e ) {
				throw new IllegalStateException("Unexpected exception: ", e);
			}
		}
	}

}
