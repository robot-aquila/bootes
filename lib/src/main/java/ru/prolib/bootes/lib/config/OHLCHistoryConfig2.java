package ru.prolib.bootes.lib.config;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.config.OptionProvider;

public class OHLCHistoryConfig2 {
	private final OptionProvider options;
	
	public OHLCHistoryConfig2(OptionProvider options) {
		this.options = options;
	}
	
	public File getDataDirectory() throws Exception {
		return options.getFile(OHLCHistoryConfig2Section.LOPT_DATA_DIR);
	}
	
	public File getCacheDirectory() throws Exception {
		return options.getFile(OHLCHistoryConfig2Section.LOPT_CACHE_DIR);
	}

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OHLCHistoryConfig2.class ) {
			return false;
		}
		OHLCHistoryConfig2 o = (OHLCHistoryConfig2) other;
		return new EqualsBuilder()
				.append(o.options, options)
				.build();
	}
	
}
