package ru.prolib.bootes.tsgr001a.data.watch;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.bootes.tsgr001a.mscan.MSCANEvent;
import ru.prolib.bootes.tsgr001a.mscan.MSCANListener;
import ru.prolib.bootes.tsgr001a.mscan.MSCANLogEntry;

public class MarketObserver implements MSCANListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(MarketObserver.class);
	}
	
	private int buyCount, sellCount;
	
	public synchronized void printStats() {
		int totalCount = buyCount + sellCount;
		logger.debug("STAT:    total events: {}", totalCount);
		logger.debug("STAT:  signals to buy: {}", buyCount);
		logger.debug("STAT: signals to sell: {}", sellCount);
	}

	@Override
	public synchronized void onEventSkipped(MSCANEvent event) {
		switch ( event.getStartTypeID() ) {
		case "BUY":
			buyCount ++;
			break;
		case "SELL":
			sellCount ++;
			break;
		}
		logger.debug("SKIP: {}", event.getStart());
	}

	@Override
	public void onEventStarted(MSCANEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEventChanged(MSCANEvent event, MSCANLogEntry entry) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEventClosed(MSCANEvent event) {
		// TODO Auto-generated method stub

	}

}
