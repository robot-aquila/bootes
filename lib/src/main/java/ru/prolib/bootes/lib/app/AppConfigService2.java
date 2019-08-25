package ru.prolib.bootes.lib.app;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVStoreCli;
import ru.prolib.aquila.core.config.KVStoreHash;
import ru.prolib.aquila.core.config.KVStoreIni;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.aquila.core.config.OptionProviderKvs;
import ru.prolib.aquila.core.config.OptionProviderML;
import ru.prolib.bootes.lib.config.AppConfig2;
import ru.prolib.bootes.lib.config.BasicConfig2Section;
import ru.prolib.bootes.lib.config.ConfigSection;

public class AppConfigService2 {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(AppConfigService2.class);
	}
	
	public static final String DEFAULT_INI_SECTION_ID = "aquila";
	public static final String DEFAULT_CONFIG_FILE_OPTION_NAME = "config-file";
	
	private final String config_file_option_name;
	private final LinkedHashMap<String, ConfigSection> sections;
	
	public AppConfigService2(LinkedHashMap<String, ConfigSection> sections,
							 String config_file_option_name)
	{
		this.sections = sections;
		this.config_file_option_name = config_file_option_name;
	}
	
	public AppConfigService2() {
		this(new LinkedHashMap<>(), DEFAULT_CONFIG_FILE_OPTION_NAME);
		sections.put(AppConfig2.BASIC_SECTION_ID, new BasicConfig2Section());
	}
	
	private OptionProvider fromCMDL(Options options, String[] args) throws ConfigException {
		try {
			return new OptionProviderKvs(new KVStoreCli(new DefaultParser().parse(options, args)));
		} catch ( ParseException e ) {
			throw new ConfigException("Error parsing comand line: ", e);
		}
	}
	
	private OptionProvider fromINI(File file, String ini_section_id) throws ConfigException {
		Section sec = null;
		try {
			Ini ini = new Ini(file);
			sec = ini.get(ini_section_id);
		} catch ( IOException e ) {
			throw new ConfigException("Error loading config file: " + e.getMessage(), e);
		}
		if ( sec == null ) {
			throw new ConfigException("Section " + ini_section_id + " is not found in config file: " + file);
		}
		return new OptionProviderKvs(new KVStoreIni(sec));
	}
	
	public AppConfig2 loadConfig(String[] args, String ini_section_id) throws ConfigException {
		Options options = new Options();
		for ( ConfigSection section : sections.values() ) {
			section.configureOptions(options);
		}
		OptionProviderML op_top = new OptionProviderML();
		op_top.addLayer(fromCMDL(options, args));
		File config_file = op_top.getFile(config_file_option_name);
		if ( config_file != null ) {
			op_top.addLayer(fromINI(config_file, ini_section_id));
		}
		KVStoreHash defaults = new KVStoreHash();
		op_top.addLayer(new OptionProviderKvs(defaults));
		for ( ConfigSection section : sections.values() ) {
			section.configureDefaults(defaults, op_top);
		}
		LinkedHashMap<String, Object> sections_data = new LinkedHashMap<>();
		Iterator<Map.Entry<String, ConfigSection>> it = sections.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<String, ConfigSection> pair = it.next();
			sections_data.put(pair.getKey(), pair.getValue().configure(op_top));
		}
		return new AppConfig2(sections_data, op_top);
	}
	
	public AppConfig2 loadConfig(String[] args) throws ConfigException {
		return loadConfig(args, DEFAULT_INI_SECTION_ID);
	}
	
	public AppConfigService2 addSection(String section_id, ConfigSection section) {
		sections.put(section_id, section);
		logger.debug("Section registered: {}", section_id);
		return this;
	}
	
	public void showHelp(PrintWriter writer, int width, String cmdLineSyntax, String header, String footer) {
		int leftPad = 0, descPad = 1;
		Options options = new Options();
		for ( ConfigSection section : sections.values() ) {
			section.configureOptions(options);
		}
		new HelpFormatter()
			.printHelp(writer, width, cmdLineSyntax, header, options, leftPad, descPad, footer);
	}
	
	public void showHelp(int width, String cmdLineSyntax, String header, String footer) {
		PrintWriter writer = new PrintWriter(System.out);
		showHelp(writer, width, cmdLineSyntax, header, footer);
		writer.close();
	}

}
