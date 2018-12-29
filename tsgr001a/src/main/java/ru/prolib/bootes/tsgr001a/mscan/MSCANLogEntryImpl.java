package ru.prolib.bootes.tsgr001a.mscan;

import java.time.Instant;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class MSCANLogEntryImpl implements MSCANLogEntry {
	private final boolean trigger;
	private final String typeID;
	private final Instant time;
	private final CDecimal value;
	private final String text;
	
	public MSCANLogEntryImpl(boolean trigger,
			String typeID,
			Instant time,
			CDecimal value,
			String text)
	{
		this.trigger = trigger;
		this.typeID = typeID;
		this.time = time;
		this.value = value;
		this.text = text;
	}

	@Override
	public boolean isTrigger() {
		return trigger;
	}

	@Override
	public String getTypeID() {
		return typeID;
	}

	@Override
	public Instant getTime() {
		return time;
	}

	@Override
	public CDecimal getValue() {
		return value;
	}

	@Override
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(9005441, 725)
				.append(trigger)
				.append(typeID)
				.append(time)
				.append(value)
				.append(text)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != MSCANLogEntryImpl.class ) {
			return false;
		}
		MSCANLogEntryImpl o = (MSCANLogEntryImpl) other;
		return new EqualsBuilder()
				.append(o.trigger, trigger)
				.append(o.typeID, typeID)
				.append(o.time, time)
				.append(o.value, value)
				.append(o.text, text)
				.build();
	}

}
