package ru.prolib.bootes.lib.config;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.config.OptionProvider;

public class AppConfig2 {
	public static final String BASIC_SECTION_ID = "basic";
	private final Map<String, Object> sections;
	private final OptionProvider options;
	
	public AppConfig2(Map<String, Object> sections, OptionProvider option_provider) {
		this.sections = sections;
		this.options = option_provider;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getSection(String section_id) {
		if ( ! sections.containsKey(section_id) ) {
			throw new IllegalStateException("Section not exists: " + section_id);
		}
		return (T) sections.get(section_id);
	}
	
	public OptionProvider getOptions() {
		return options;
	}
	
	public BasicConfig2 getBasicConfig() {
		return getSection(BASIC_SECTION_ID);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AppConfig2.class ) {
			return false;
		}
		AppConfig2 o = (AppConfig2) other;
		return new EqualsBuilder()
				.append(o.sections, sections)
				.append(o.options, options)
				.build();
	}

}
