package ru.prolib.bootes.lib.config;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.config.ConfigException;

@Deprecated
public class BasicConfigBuilder {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(BasicConfigBuilder.class);
	}
	
	private boolean showHelp, headless, noOrders;
	private File dataDir, configFile, reportsDir;
	
	public BasicConfig build() throws ConfigException {
		return new BasicConfig(
				showHelp,
				headless,
				noOrders,
				dataDir,
				configFile,
				reportsDir
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
	
	public BasicConfigBuilder withNoOrders(boolean option) {
		logger.debug("withNoOrders={}", option);
		this.noOrders = option;
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
	
	public BasicConfigBuilder withReportsDirectory(File dir) {
		logger.debug("withReportsDirectory={}", dir);
		this.reportsDir = dir;
		return this;
	}

}
