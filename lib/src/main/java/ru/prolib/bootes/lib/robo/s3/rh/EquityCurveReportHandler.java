package ru.prolib.bootes.lib.robo.s3.rh;

import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.PortfolioUpdateEvent;
import ru.prolib.aquila.core.BusinessEntities.SPRunnable;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.data.OHLCScalableSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListenerStub;
import ru.prolib.bootes.lib.robo.sh.statereq.IAccountDeterminable;

public class EquityCurveReportHandler extends S3RobotStateListenerStub implements EventListener, SPRunnable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(EquityCurveReportHandler.class);
	}

	private final IAccountDeterminable state;
	private final OHLCScalableSeries report;
	private final boolean dumpAtShutdown;
	private final Lock lock;
	private Portfolio portfolio;
	private TaskHandler taskHandler;
	
	public EquityCurveReportHandler(IAccountDeterminable state,
									OHLCScalableSeries report,
									boolean dumpAtShutdown) {
		this.state = state;
		this.report = report;
		this.dumpAtShutdown = dumpAtShutdown;
		this.lock = new ReentrantLock();
	}
	
	public EquityCurveReportHandler(IAccountDeterminable state,
									OHLCScalableSeries report)
	{
		this(state, report, false);
	}

	@Override
	public void accountSelected() {
		Portfolio p;
		synchronized ( state ) {
			p = state.getPortfolio();
		}
		lock.lock();
		try {
			if ( portfolio != null ) {
				throw new IllegalStateException();
			}
			(portfolio = p).onUpdate().addListener(this);
			taskHandler = portfolio.getTerminal().schedule(this);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void robotStopped() {
		lock.lock();
		try {
			if ( portfolio != null ) {
				portfolio.onUpdate().removeListener(this);
				portfolio = null;
			}
			if ( taskHandler != null ) {
				taskHandler.cancel();
				taskHandler = null;
			}
		} finally {
			lock.unlock();
		}

		if ( dumpAtShutdown ) {
			logger.debug("Stopping...");
			String ls = System.lineSeparator();
			StringBuilder sb = new StringBuilder()
				.append("------- Equity curve -------------").append(ls);
			int length = report.getLength();
			for ( int i = 0; i < length; i ++ ) {
				try {
					sb.append(report.get(i).toString()).append(ls);
				} catch ( ValueException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
			logger.debug(sb.toString());
		}
	}

	@Override
	public void onEvent(Event event) {
		PortfolioUpdateEvent e = (PortfolioUpdateEvent) event;
		if ( e.hasChanged(PortfolioField.EQUITY) ) {
			CDecimal equity = (CDecimal) e.getNewValues().get(PortfolioField.EQUITY);
			if ( equity != null ) {
				report.append(equity, e.getTime());
			}
		}
	}

	@Override
	public void run() {
		lock.lock();
		try {
			if ( portfolio != null ) {
				report.append(portfolio.getEquity(), portfolio.getTerminal().getCurrentTime());
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Instant getNextExecutionTime(Instant current_time) {	
		long period = 10000L;
		return Instant.ofEpochMilli((current_time.toEpochMilli() / period + 1) * period);
	}

	@Override
	public boolean isLongTermTask() {
		return false;
	}

}
