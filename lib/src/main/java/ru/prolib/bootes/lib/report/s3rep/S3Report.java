package ru.prolib.bootes.lib.report.s3rep;

import java.util.ArrayList;
import java.util.List;

public class S3Report implements IS3Report {
	private final List<S3RRecord> records;
	
	public S3Report(List<S3RRecord> records) {
		this.records = records;
	}
	
	public S3Report() {
		this(new ArrayList<>());
	}

	@Override
	public synchronized S3RRecord create(S3RRecordCreate request) {
		long index = records.size();
		S3RRecord n = new S3RRecord(
				index,
				request.getType(),
				request.getEntryTime(),
				request.getEntryPrice(),
				request.getQty(),
				request.getTakeProfit(),
				request.getStopLoss(),
				request.getBreakEven()
			);
		records.add(n);
		return n;
	}

	@Override
	public synchronized S3RRecord update(S3RRecordUpdateLast request) {
		int index = records.size() - 1;
		S3RRecord o = records.get(index);
		S3RRecord n = new S3RRecord(
				o.getID(),
				o.getType(),
				o.getEntryTime(),
				o.getEntryPrice(),
				o.getQty(),
				o.getTakeProfit(),
				o.getStopLoss(),
				o.getBreakEven(),
				request.getExitTime(),
				request.getExitPrice(),
				request.getProfitAndLoss()
			);
		records.set(index, n);
		return n;
	}

	@Override
	public synchronized S3RRecord getRecord(int recordIndex) {
		return records.get(recordIndex);
	}

	@Override
	public synchronized int getRecordCount() {
		return records.size();
	}

}
