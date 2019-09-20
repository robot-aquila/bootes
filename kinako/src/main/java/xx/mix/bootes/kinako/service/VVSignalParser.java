package xx.mix.bootes.kinako.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;

public class VVSignalParser {
	private static final Pattern pattern;
	private static final Map<String, VVOrderType> type_map;
	
	static {
		// example string is:
		//- sell long 58 shares of MKTX
		pattern = Pattern.compile("^- (sell long|sell short|buy long|cover short) (\\d+) shares of (\\w+)$");
		type_map = new HashMap<>();
		type_map.put("sell long",   VVOrderType.SELL_LONG);
		type_map.put("sell short",  VVOrderType.SELL_SHORT);
		type_map.put("buy long",    VVOrderType.BUY_LONG);
		type_map.put("cover short", VVOrderType.COVER_SHORT);
	}
	
	public VVSignal parse(String text, Instant time) throws VVSignalParseException {
		List<VVOrderRecom> signal_list = new ArrayList<>();
		String[] lines = text.split("\\r?\\n|\\r");
		for ( int i = 0; i < lines.length; i ++ ) {
			String line = lines[i];
			Matcher matcher = pattern.matcher(line);
			if ( ! matcher.matches() ) {
				continue;
			}
			MatchResult mres = matcher.toMatchResult();
			String str_type = mres.group(1), str_num = mres.group(2), symbol = mres.group(3);
			signal_list.add(new VVOrderRecom(type_map.get(str_type), CDecimalBD.of(str_num), symbol));
		}
		if ( signal_list.size() == 0 ) {
			throw new VVSignalParseException("No signal found", text);
		}
		return new VVSignal(time, signal_list);
	}

}
