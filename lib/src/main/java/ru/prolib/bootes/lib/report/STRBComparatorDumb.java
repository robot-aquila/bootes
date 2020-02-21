package ru.prolib.bootes.lib.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.ChangeDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.InsertDelta;
import com.github.difflib.patch.Patch;

/**
 * A dumb comparator can be used to compare reports line by line using java-diff-utils. 
 */
public class STRBComparatorDumb implements STRBComparator {
	
	public static List<String> readLines(List<TextLine> text_lines) throws IOException {
		List<String> result = new ArrayList<>();
		for ( TextLine text_line : text_lines ) {
			result.add(text_line.getLineText());
		}
		return result;
	}
	
	public static <T> Chunk<T> fixChunkPos(Chunk<T> chunk, int offset) {
		return new Chunk<T>(chunk.getPosition() + offset, chunk.getLines());
	}
	
	public static List<AbstractDelta<String>> fixDeltaPos(List<AbstractDelta<String>> deltas,
			int source_offset, int target_offset)
	{
		List<AbstractDelta<String>> deltas_fixed = new ArrayList<>();
		for ( AbstractDelta<String> delta : deltas  ) {
			Chunk<String> src_chunk = fixChunkPos(delta.getSource(), source_offset),
					tgt_chunk = fixChunkPos(delta.getTarget(), target_offset);
			switch ( delta.getType() ) {
			case CHANGE:
				delta = new ChangeDelta<>(src_chunk, tgt_chunk);
				break;
			case INSERT:
				delta = new InsertDelta<>(src_chunk, tgt_chunk);
				break;
			case DELETE:
				delta = new DeleteDelta<>(src_chunk, tgt_chunk);
				break;
			default:
				throw new IllegalArgumentException("Unsupported delta type: " + delta.getType());
			}
			deltas_fixed.add(delta);
		}
		return deltas_fixed;
	}
	
	private final String reportID;
	
	public STRBComparatorDumb(String report_id) {
		this.reportID = report_id;
	}
	
	/**
	 * Fix lines of expected report before comparison.
	 * <p>
	 * @param lines - expected report lines
	 * @return fixed lines
	 */
	protected List<String> fixExpectedLines(List<String> lines) {
		return lines;
	}
	
	/**
	 * Fix lines of actual report before comparison.
	 * <p>
	 * @param lines - actual report lines
	 * @return fixed lines
	 */
	protected List<String> fixActualLines(List<String> lines) {
		return lines;
	}
	
	/**
	 * Fix deltas before the result will be returned to caller.
	 * <p>
	 * @param deltas - list of deltas (possible empty)
	 * @return fixed deltas
	 */
	protected List<AbstractDelta<String>> fixResultDeltas(List<AbstractDelta<String>> deltas) {
		return deltas;
	}

	@Override
	public List<AbstractDelta<String>> diff(List<TextLine> expected, List<TextLine> actual) throws IOException {
		int expected_pos = expected.size() > 0 ? expected.get(0).getLineNo() : -1,
			actual_pos = actual.size() > 0 ? actual.get(0).getLineNo() : -1;
		Patch<String> patch;
		try {
			patch = DiffUtils.diff(fixExpectedLines(readLines(expected)), fixActualLines(readLines(actual)));
		} catch ( DiffException e ) {
			throw new IOException("Diff failed", e);
		}
		return fixResultDeltas(fixDeltaPos(patch.getDeltas(), expected_pos, actual_pos));
	}

	@Override
	public String getReportID() {
		return reportID;
	}

}
