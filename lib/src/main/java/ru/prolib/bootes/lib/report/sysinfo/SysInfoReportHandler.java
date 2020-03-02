package ru.prolib.bootes.lib.report.sysinfo;

import java.time.Instant;

import ru.prolib.bootes.lib.robo.s3.S3RobotStateListenerStub;

public class SysInfoReportHandler extends S3RobotStateListenerStub {
	private Instant started, stopped;
	
	public synchronized SysInfoReport getReport() {
		return new SysInfoReport(
				started == null ? Instant.now() : started,
				stopped == null ? Instant.now() : stopped
			);
	}

	@Override
	public synchronized void robotStarted() {
		started = Instant.now();
	}
	
	@Override
	public synchronized void robotStopped() {
		stopped = Instant.now();
	}

}
