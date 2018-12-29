package ru.prolib.bootes.tsgr001a.mscan;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class MSCANEventImpl implements MSCANEvent {
	private final MSCANLogEntry start;
	private final List<MSCANLogEntry> entries;
	private MSCANLogEntry close;
	
	public MSCANEventImpl(MSCANLogEntry startEntry) {
		this.entries = new ArrayList<>();
		this.entries.add(startEntry);
		this.start = startEntry;
		if ( startEntry.isTrigger() ) {
			close = startEntry;
		}
	}
	
	/**
	 * Set a closing entry.
	 * For testing purposes only.
	 * <p>
	 * @param entry - entry to set
	 */
	synchronized void setClose(MSCANLogEntry entry) {
		close = entry;
	}
	
	private void mustBeClosed() {
		if ( ! isFinished() ) {
			throw new IllegalStateException("Event is not closed");
		}
	}
	
	public synchronized void addLogEntry(MSCANLogEntry entry) {
		if ( close != null ) {
			throw new IllegalStateException("Already closed");
		}
		entries.add(entry);
		if ( entry.isTrigger() ) {
			close = entry;
		}
	}

	@Override
	public MSCANLogEntry getStart() {
		return start;
	}

	@Override
	public String getStartTypeID() {
		return start.getTypeID();
	}

	@Override
	public Instant getStartTime() {
		return start.getTime();
	}

	@Override
	public CDecimal getStartValue() {
		return start.getValue();
	}

	@Override
	public synchronized List<MSCANLogEntry> getEntries() {
		return new ArrayList<>(entries);
	}

	@Override
	public synchronized boolean isFinished() {
		return close != null;
	}

	@Override
	public synchronized MSCANLogEntry getClose() {
		mustBeClosed();
		return close;
	}

	@Override
	public synchronized String getCloseTypeID() {
		mustBeClosed();
		return close.getTypeID();
	}

	@Override
	public synchronized Instant getCloseTime() {
		mustBeClosed();
		return close.getTime();
	}

	@Override
	public synchronized CDecimal getCloseValue() {
		mustBeClosed();
		return close.getValue();
	}
	
	@Override
	public synchronized int hashCode() {
		return new HashCodeBuilder(244215, 907)
				.append(entries)
				.append(start)
				.append(close)
				.build();
	}
	
	@Override
	public synchronized String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != MSCANEventImpl.class ) {
			return false;
		}
		MSCANEventImpl o = (MSCANEventImpl) other;
		return new EqualsBuilder()
				.append(o.start, start)
				.append(o.entries, entries)
				.append(o.close, close)
				.build();
	}

}
