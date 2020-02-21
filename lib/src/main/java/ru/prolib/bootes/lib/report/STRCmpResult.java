package ru.prolib.bootes.lib.report;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Chunk;

public class STRCmpResult {
	private final boolean identical;
	private final List<STRBCmpResult> blockCmpResult;
	
	public STRCmpResult(boolean identical, List<STRBCmpResult> block_cmp_result) {
		this.identical = identical;
		this.blockCmpResult = block_cmp_result;
	}
	
	public boolean identical() {
		return identical;
	}
	
	public List<STRBCmpResult> getReportBlockCmpResult() {
		return blockCmpResult;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(665891, 705)
				.append(identical)
				.append(blockCmpResult)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if  ( other == null || other.getClass() != STRCmpResult.class ) {
			return false;
		}
		STRCmpResult o = (STRCmpResult) other;
		return new EqualsBuilder()
				.append(o.identical, identical)
				.append(o.blockCmpResult, blockCmpResult)
				.build();
	}
	
	static StringBuilder appendList(StringBuilder sb, List<String> lines) {
		for ( String line : lines ) {
			sb.append("\t\t[").append(line).append("]\n");
		}
		return sb;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
			.append("STRCmpResult identical=").append(identical).append(", blocks:\n");
		for ( STRBCmpResult block : blockCmpResult ) {
			STRBHeader header = block.getHeader();
			sb.append(header.getReportID()).append("[").append(header.getTitle()).append("] status ")
				.append(block.getType()).append("[").append(block.getDescription()).append("]\n");
			List<AbstractDelta<String>> deltas = block.getDeltas();
			if ( deltas.size() > 0 ) {
				for ( AbstractDelta<String> delta : deltas ) {
					Chunk<String> src = delta.getSource(), tgt = delta.getTarget();
					switch ( delta.getType() ) {
					case CHANGE:
						sb.append("\tChange at line ").append(src.getPosition()).append(":\n");
						appendList(sb, src.getLines());
						sb.append("\tchanged to:\n");
						appendList(sb, tgt.getLines());
						break;
					case INSERT:
						sb.append("\tInsert at line ").append(src.getPosition()).append(":\n");
						appendList(sb, tgt.getLines());
						break;
					case DELETE:
						sb.append("\tDelete at line ").append(src.getPosition()).append(":\n");
						appendList(sb, src.getLines());
						break;
					case EQUAL:
						break;
					default:
						throw new IllegalArgumentException("Unsuported delta type: " + delta.getType());
					}
				}
			}
		}
		return sb.toString();
	}
	
}
