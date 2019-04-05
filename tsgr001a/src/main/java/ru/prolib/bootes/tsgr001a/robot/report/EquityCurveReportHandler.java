package ru.prolib.bootes.tsgr001a.robot.report;

import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioEvent;
import ru.prolib.aquila.core.BusinessEntities.SPRunnable;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.data.OHLCScalableSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.RobotStateListener;

public class EquityCurveReportHandler implements RobotStateListener, EventListener, SPRunnable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(EquityCurveReportHandler.class);
	}

	private final RobotState state;
	private final OHLCScalableSeries report;
	private final boolean dumpAtShutdown;
	private final Lock lock;
	private Portfolio portfolio;
	private TaskHandler taskHandler;
	
	public EquityCurveReportHandler(RobotState state,
									OHLCScalableSeries report,
									boolean dumpAtShutdown) {
		this.state = state;
		this.report = report;
		this.dumpAtShutdown = dumpAtShutdown;
		this.lock = new ReentrantLock();
	}
	
	public EquityCurveReportHandler(RobotState state,
									OHLCScalableSeries report)
	{
		this(state, report, false);
	}
	
	@Override
	public void robotStarted() {
		
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
	public void contractSelected() {
		
	}

	@Override
	public void sessionDataAvailable() {
		
	}

	@Override
	public void riskManagementUpdate() {
		
	}

	@Override
	public void speculationOpened() {
		
	}

	@Override
	public void speculationUpdate() {
		
	}

	@Override
	public void speculationClosed() {
		
	}

	@Override
	public void sessionDataCleanup() {
		
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
		PortfolioEvent e = (PortfolioEvent) event;
		report.append(e.getPortfolio().getEquity(), e.getTime());
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
