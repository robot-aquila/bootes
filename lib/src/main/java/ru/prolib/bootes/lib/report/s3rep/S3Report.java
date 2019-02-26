package ru.prolib.bootes.lib.report.s3rep;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ru.prolib.bootes.lib.data.ts.filter.IFilter;

public class S3Report implements IS3Report {
	private final IFilter<S3RRecord> filter;
	private final List<S3RRecord> records;
	private final Set<IS3ReportListener> listeners;
	private Boolean lastRecordDeclined = null;
	
	public S3Report(IFilter<S3RRecord> filter, List<S3RRecord> records, Set<IS3ReportListener> listeners) {
		this.filter = filter;
		this.records = records;
		this.listeners = listeners;
	}
	
	public S3Report(List<S3RRecord> records, Set<IS3ReportListener> listeners) {
		this(null, records, listeners);
	}
	
	public S3Report(IFilter<S3RRecord> filter) {
		this(filter, new ArrayList<>(), new LinkedHashSet<>());
	}
	
	public S3Report() {
		this(null);
	}
	
	public IFilter<S3RRecord> getFilter() {
		return filter;
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
		boolean add_record = filter == null ? true : filter.approve(n);
		if ( add_record ) {
			records.add(n);
			lastRecordDeclined = false;
			for ( IS3ReportListener listener : listeners ) {
				listener.recordCreated(n);
			}
			return n;
		} else {
			lastRecordDeclined = true;
			return null;
		}
	}

	@Override
	public synchronized S3RRecord update(S3RRecordUpdateLast request) {
		if ( lastRecordDeclined == null ) {
			throw new IllegalStateException();
		} else if ( lastRecordDeclined ) {
			return null;
		}
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
		boolean add_record = filter == null ? true : filter.approve(n);
		if ( add_record ) {
			records.set(index, n);
			for ( IS3ReportListener listener : listeners ) {
				listener.recordUpdated(n);
			}
			return n;
		} else {
			records.remove(index);
			for ( IS3ReportListener listener : listeners ) {
				listener.recordDeleted(n);
			}
			return null;
		}
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
