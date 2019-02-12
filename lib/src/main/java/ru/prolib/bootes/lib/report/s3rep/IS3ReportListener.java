package ru.prolib.bootes.lib.report.s3rep;

public interface IS3ReportListener {
	void recordCreated(S3RRecord record);
	void recordUpdated(S3RRecord record);
}
