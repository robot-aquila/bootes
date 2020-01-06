package ru.prolib.bootes.lib.report.sysinfo;

import java.time.Instant;

import ru.prolib.bootes.lib.robo.s3.S3RobotStateListener;

public class SysInfoReportHandler implements S3RobotStateListener {
	private Instant started, stopped;
	
	public synchronized SysInfoReport getReport() {
		return new SysInfoReport(started, stopped);
	}

	@Override
	public synchronized void robotStarted() {
		started = Instant.now();
	}
	
	@Override
	public synchronized void robotStopped() {
		stopped = Instant.now();
	}

	@Override
	public void accountSelected() {
		
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
	public void sessionDataCleanup() {
		
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

}
