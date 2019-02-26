package ru.prolib.bootes.lib.report.s3rep;

public interface IS3Report {
	/**
	 * Create new record.
	 * <p>
	 * @param request - request parameters
	 * @return record instance or null if record was not created
	 */
	S3RRecord create(S3RRecordCreate request);
	
	/**
	 * Update last created record.
	 * <p>
	 * @param request - request parameters
	 * @return record instance or null if record was deleted
	 */
	S3RRecord update(S3RRecordUpdateLast request);
	
	/**
	 * Get record by index.
	 * <p>
	 * @param recordIndex - record index
	 * @return record instance
	 */
	S3RRecord getRecord(int recordIndex);
	
	/**
	 * Get number of records in report.
	 * <p>
	 * @return number of records
	 */
	int getRecordCount();
	
	/**
	 * Add report listener.
	 * <p>
	 * @param listener - listener instance
	 */
	void addListener(IS3ReportListener listener);
	
	/**
	 * Remove report listener.
	 * <p>
	 * @param listener - listener instance
	 */
	void removeListener(IS3ReportListener listener);
	
}
