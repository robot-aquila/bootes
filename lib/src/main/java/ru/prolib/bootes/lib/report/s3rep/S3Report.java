package ru.prolib.bootes.lib.report.s3rep;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class S3Report implements IS3Report {
	private final List<S3RRecord> records;
	private final Set<IS3ReportListener> listeners;
	
	public S3Report(List<S3RRecord> records, Set<IS3ReportListener> listeners) {
		this.records = records;
		this.listeners = listeners;
	}
	
	public S3Report() {
		this(new ArrayList<>(), new LinkedHashSet<>());
	}

	@Override
	public synchronized S3RRecord create(S3RRecordCreate request) {
		int index = records.size();
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
		for ( IS3ReportListener listener : listeners ) {
			listener.recordCreated(n);
		}
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
		for ( IS3ReportListener listener : listeners ) {
			listener.recordUpdated(n);
		}
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

	@Override
	public synchronized void addListener(IS3ReportListener listener) {
		listeners.add(listener);
	}

	@Override
	public synchronized void removeListener(IS3ReportListener listener) {
		listeners.remove(listener);
	}

}
