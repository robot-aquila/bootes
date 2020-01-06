package ru.prolib.bootes.lib.report.sysinfo;

import java.time.Instant;

public class SysInfoReport {
	private final Instant jobStarted, jobFinished;
	
	public SysInfoReport(Instant jobStarted, Instant jobFinished) {
		this.jobStarted = jobStarted;
		this.jobFinished = jobFinished;
	}
	
	public Instant getJobStarted() {
		return jobStarted;
	}
	
	public Instant getJobFinished() {
		return jobFinished;
	}

}
