package ru.prolib.bootes.lib.report.blockrep;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class BlockReport implements IBlockReport {
	private final Map<String, IBlock> blocks;
	
	BlockReport(Map<String, IBlock> blocks) {
		this.blocks = blocks;
	}
	
	public BlockReport(IBlock startBlock) {
		this(new HashMap<>());
		setBlock(startBlock);
	}

	@Override
	public synchronized IBlock getBlock(String typeID) {
		IBlock result = blocks.get(typeID);
		if ( result == null ) {
			throw new IllegalArgumentException("Block not exists: " + typeID);
		}
		return result;
	}

	@Override
	public synchronized List<IBlock> getBlocks() {
		return new ArrayList<>(blocks.values());
	}

	@Override
	public synchronized void setBlock(IBlock block) {
		blocks.put(block.getTypeID(), block);
	}

	@Override
	public synchronized IBlock getEarlyBlock() {
		if ( blocks.size() == 0 ) {
			throw new IllegalStateException("The report has no blocks");
		}
		IBlock result = null;
		Instant t = null;
		for ( IBlock block : blocks.values() ) {
			if ( result == null ) {
				result = block;
			} else if ( (t = block.getTime()) != null ) {
				if ( result.getTime() == null || result.getTime().compareTo(t) > 0 ) {
					result = block;
				}
			}
		}
		return result;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != BlockReport.class ) {
			return false;
		}
		BlockReport o = (BlockReport) other;
		List<IBlock> this_blocks, other_blocks;
		synchronized ( this ) {
			this_blocks = new ArrayList<>(blocks.values());
		}
		synchronized ( o ) {
			other_blocks = new ArrayList<>(o.blocks.values());
		}
		return new EqualsBuilder()
				.append(this_blocks, other_blocks)
				.build();
	}
	
	@Override
	public synchronized String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public synchronized int hashCode() {
		return new HashCodeBuilder(1766257, 924053)
				.append(blocks)
				.build();
	}

}
