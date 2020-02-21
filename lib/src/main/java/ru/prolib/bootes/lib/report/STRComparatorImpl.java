package ru.prolib.bootes.lib.report;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.difflib.patch.AbstractDelta;

public class STRComparatorImpl implements STRComparator {
	private static final Logger logger;
	private static final Pattern HEADER_PATTERN = Pattern.compile("^# ReportID=(.*?)\\s+Title=(.*?)\\s*$");
	
	static {
		logger = LoggerFactory.getLogger(STRComparatorImpl.class);
	}
	
	private final Map<String, STRBComparator> comparators;
	
	public STRComparatorImpl(Map<String, STRBComparator> comparators) {
		this.comparators = comparators;
	}
	
	/**
	 * Try find header of the next report.
	 * <p>
	 * @param reader - reader instance
	 * @return header or null if no more data
	 * @throws IOException an error occurred
	 * @throws MalformedHeaderException found line does not match allowed patterns
	 */
	static STRBHeader findHeader(TextLineReader reader)
			throws IOException, MalformedHeaderException
	{
		TextLine text_line;
		while ( (text_line = reader.readLine()) != null ) {
			String line = text_line.getLineText().trim();
			if ( line.length() > 0 ) {
				Matcher matcher = HEADER_PATTERN.matcher(line);
				if ( ! matcher.find() ) {
					throw new MalformedHeaderException("Malformed header at line "
							+ text_line.getLineNo() + ": " + line, text_line.getLineNo());
				}
				return new STRBHeader(matcher.group(1), matcher.group(2));
			}
		}
		return null;
	}
	
	/**
	 * Create block reader.
	 * <p>
	 * @param reader - source reader instance
	 * @return a reader which is limited to end of current block
	 */
	static TextLineReaderConditionalStop blockReader(TextLineReader reader) {
		return new TextLineReaderConditionalStop(reader, ConditionStopAtEmptyLine.getInstance());
	}
	
	/**
	 * Read all report blocks.
	 * <p>
	 * @param reader - reader instance
	 * @return list of report block data including block reader and lines
	 * @throws IOException an error occurred
	 * @throws MalformedHeaderException found line does not match allowed patterns
	 */
	static List<STRBHandler<List<TextLine>>> readBlocks(TextLineReader reader)
			throws IOException, MalformedHeaderException
	{
		List<STRBHandler<List<TextLine>>> blocks = new ArrayList<>();
		STRBHeader header;
		while ( (header = findHeader(reader)) != null ) {
			TextLineReader rw = blockReader(reader); 
			List<TextLine> lines = new ArrayList<>();
			TextLine line;
			while ( (line = rw.readLine()) != null ) {
				lines.add(line);
			}
			blocks.add(new STRBHandler<>(header, lines));
		}
		return blocks;
	}
	
	static List<STRBHandler<List<TextLine>>> readReport(TextLineReader reader, String subject_message,
			boolean allow_no_blocks) throws IOException, MalformedHeaderException, NoBlocksFoundException
	{
		List<STRBHandler<List<TextLine>>> blocks;
		try {
			blocks = readBlocks(reader);
		} catch ( MalformedHeaderException e ) {
			throw new MalformedHeaderException("Error parsing "
					+ subject_message + ": " + e.getMessage(), e.getErrorOffset());
		}
		if ( blocks.size() == 0 && allow_no_blocks == false ) {
			throw new NoBlocksFoundException(StringUtils.capitalize(subject_message)
					+ " has no blocks", reader.getNextLineNo());
		}
		return blocks;
	}

	@Override
	public STRCmpResult diff(TextLineReader expected, TextLineReader actual) throws IOException, ParseException {
		List<STRBHandler<List<TextLine>>> exp_blocks = readReport(expected, "expected report", false), act_blocks;
		boolean identical = true, act_malformed_header = false;
		String act_malformed_header_desc = null;
		try {
			act_blocks = readReport(actual, "actual report", true);
		} catch ( MalformedHeaderException e ) {
			act_malformed_header = true;
			act_malformed_header_desc = e.getMessage();
			act_blocks = Arrays.asList();
		}
		List<STRBCmpResult> block_cmp_result = new ArrayList<>();
		STRBComparator cmp;
		for ( int i = 0; i < exp_blocks.size(); i ++ ) {
			STRBHandler<List<TextLine>> _exp = exp_blocks.get(i);
			STRBHeader _exp_head = _exp.getHeader();
			if ( act_malformed_header ) {
				identical = false;
				block_cmp_result.add(new STRBCmpResult(_exp_head,
						STRBCmpResultType.HEADER_MALFORMED,
						act_malformed_header_desc));
				continue;
			}
			if ( i >= act_blocks.size() ) {
				identical = false;
				block_cmp_result.add(new STRBCmpResult(_exp_head,
						STRBCmpResultType.NOT_EXISTS,
						"Report block not found"));
				continue;
			}
			STRBHandler<List<TextLine>> _act = act_blocks.get(i);
			if ( _exp_head.equals(_act.getHeader()) == false ) {
				identical = false;
				block_cmp_result.add(new STRBCmpResult(_exp_head,
						STRBCmpResultType.HEADER_MISMATCH,
						"Unexpected header: " + _act.getHeader()));
				continue;
			}
			if ( (cmp = comparators.get(_exp_head.getReportID())) == null ) {
				throw new IllegalStateException("Comparator not found: " + _exp_head.getReportID());
			}
			List<AbstractDelta<String>> deltas;
			try {
				deltas = cmp.diff(_exp.getHandler(), _act.getHandler());
			} catch ( Exception e ) {
				identical = false;
				block_cmp_result.add(new STRBCmpResult(_exp_head,
						STRBCmpResultType.REPORT_COMPARE_FAILED,
						e.getMessage()));
				logger.error("Unhandled exception", e);
				continue;
			}
			if ( deltas.size() > 0 ) {
				identical = false;
				block_cmp_result.add(new STRBCmpResult(_exp_head,
						STRBCmpResultType.REPORT_MISMATCH,
						"Found differences", deltas));
				continue;
			}
			block_cmp_result.add(new STRBCmpResult(_exp_head, STRBCmpResultType.IDENTICAL, "Identical"));
		}
		for ( int i = exp_blocks.size(); i < act_blocks.size(); i ++ ) {
			STRBHandler<List<TextLine>> _act = act_blocks.get(i);
			identical = false;
			block_cmp_result.add(new STRBCmpResult(_act.getHeader(),
					STRBCmpResultType.UNEXPECTED,
					"Report block is not expected"));
		}
		return new STRCmpResult(identical, block_cmp_result);
	}

}
