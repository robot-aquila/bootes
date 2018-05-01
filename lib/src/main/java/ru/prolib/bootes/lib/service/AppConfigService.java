package ru.prolib.bootes.lib.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.config.AppConfigBuilder;
import ru.prolib.bootes.lib.config.AppConfigLoader;
import ru.prolib.bootes.lib.config.BasicConfig;
import ru.prolib.bootes.lib.config.ConfigException;
import ru.prolib.bootes.lib.config.OptionProvider;
import ru.prolib.bootes.lib.config.OptionProviderKvs;
import ru.prolib.bootes.lib.config.kvstore.KVStoreCli;
import ru.prolib.bootes.lib.config.kvstore.KVStoreIni;

public class AppConfigService {
	
	public static class Factory {
		
		public AppConfigBuilder createAppConfigBuilder() {
			return new AppConfigBuilder();
		}
		
		public AppConfigLoader createAppConfigLoader() {
			return new AppConfigLoader();
		}
		
		public Options createOptions() {
			return new Options();
		}
		
		public HelpFormatter createHelpFormatter() {
			return new HelpFormatter();
		}
		
		public OptionProvider createOptionProviderCli(Options options, String[] args) throws ConfigException {
			try {
				return new OptionProviderKvs(new KVStoreCli(new DefaultParser().parse(options, args)));
			} catch ( ParseException e ) {
				throw new ConfigException(e.getMessage(), e);
			}
		}
		
		public OptionProvider createOptionProviderIni(File file, String sectionID) throws ConfigException {
			Section sec = null;
			try {
				Ini ini = new Ini(file);
				sec = ini.get(sectionID);
			} catch ( IOException e ) {
				throw new ConfigException("Error loading config file: " + e.getMessage(), e);
			}
			if ( sec == null ) {
				throw new ConfigException("Section " + sectionID + " is not found in config file: " + file);
			}
			return new OptionProviderKvs(new KVStoreIni(sec));
		}
		
		public PrintWriter createPrintWriter(OutputStream stream) {
			return new PrintWriter(stream);
		}
		
	}
	
	private final Factory factory;
	
	AppConfigService(Factory factory) {
		this.factory = factory;
	}
	
	public AppConfigService() {
		this(new Factory());
	}
	
	Factory getFactory() {
		return factory;
	}
	
	public AppConfig loadConfig(String[] args, String sectionID) throws ConfigException {
		AppConfigBuilder builder = factory.createAppConfigBuilder();
		AppConfigLoader loader = factory.createAppConfigLoader();
		Options options = getConfiguredOptions();
		OptionProvider opINI = null, opCLI = factory.createOptionProviderCli(options, args);
		loader.load(builder, opCLI);
		BasicConfig bc = builder.getBasicConfigBuilder().build();
		if ( bc.getConfigFile() != null ) {
			opINI = factory.createOptionProviderIni(bc.getConfigFile(), sectionID);
			loader.load(builder, opINI);
			loader.load(builder, opCLI);
		}
		return builder.build();
	}
	
	public AppConfig loadConfig(String[] args) throws ConfigException {
		return loadConfig(args, "aquila");
	}
	
	public void showHelp(PrintWriter writer, int width, String cmdLineSyntax, String header, String footer) {
		int leftPad = 0, descPad = 1;
		factory.createHelpFormatter()
			.printHelp(writer, width, cmdLineSyntax, header, getConfiguredOptions(), leftPad, descPad, footer);
	}
	
	public void showHelp(int width, String cmdLineSyntax, String header, String footer) {
		PrintWriter writer = factory.createPrintWriter(System.out);
		showHelp(writer, width, cmdLineSyntax, header, footer);
		writer.close();
	}
	
	protected Options getConfiguredOptions() {
		Options options = factory.createOptions();
		factory.createAppConfigLoader().configureOptions(options);
		return options;
	}

}
