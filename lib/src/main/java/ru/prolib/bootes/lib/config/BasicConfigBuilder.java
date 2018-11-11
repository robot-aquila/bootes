package ru.prolib.bootes.lib.config;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicConfigBuilder {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(BasicConfigBuilder.class);
	}
	
	private boolean showHelp, headless;
	private File dataDir, configFile;
	
	public BasicConfig build() throws ConfigException {
		return new BasicConfig(
				showHelp,
				headless,
				dataDir,
				configFile
			);
	}
	
	public BasicConfigBuilder withShowHelp(boolean showHelp) {
		logger.debug("withShowHelp={}", showHelp);
		this.showHelp = showHelp;
		return this;
	}
	
	public BasicConfigBuilder withHeadless(boolean headless) {
		logger.debug("withHeadless={}", headless);
		this.headless = headless;
		return this;
	}
	
	public BasicConfigBuilder withDataDirectory(File dataDir) {
		logger.debug("withDataDirectory={}", dataDir);
		this.dataDir = dataDir;
		return this;
	}
	
	public BasicConfigBuilder withConfigFile(File configFile) {
		logger.debug("withConfigFile={}", configFile);
		this.configFile = configFile;
		return this;
	}

}
