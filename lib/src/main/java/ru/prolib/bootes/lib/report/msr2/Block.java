package ru.prolib.bootes.lib.report.msr2;

import java.time.Instant;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class Block implements IBlock, Comparable<IBlock> {
	protected final String typeID;
	protected final CDecimal price;
	protected final Instant time;
	
	public Block(String typeID, CDecimal price, Instant time) {
		this.typeID = typeID;
		this.price = price;
		this.time = time;
	}

	@Override
	public int compareTo(IBlock o) {
		return new CompareToBuilder()
				.append(time, o.getTime())
				.build();
	}

	@Override
	public String getTypeID() {
		return typeID;
	}

	@Override
	public CDecimal getPrice() {
		return price;
	}

	@Override
	public Instant getTime() {
		return time;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		if ( getClass() != Block.class ) {
			throw new IllegalStateException("This method must be overriden");
		}
		return new HashCodeBuilder(86900129, 667127)
				.append(typeID)
				.append(price)
				.append(time)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Block.class ) {
			return false;
		}
		Block o = (Block) other;
		return new EqualsBuilder()
				.append(o.typeID, typeID)
				.append(o.price, price)
				.append(o.time, time)
				.build();

	}

}
