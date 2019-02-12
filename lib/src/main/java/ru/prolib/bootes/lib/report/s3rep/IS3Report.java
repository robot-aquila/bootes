package ru.prolib.bootes.lib.report.s3rep;

public interface IS3Report {
	S3RRecord create(S3RRecordCreate request);
	S3RRecord update(S3RRecordUpdateLast request);
	S3RRecord getRecord(int recordIndex);
	int getRecordCount();
}
