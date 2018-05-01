package ru.prolib.bootes.lib.config;

import java.io.File;

public class BasicConfigBuilder {
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
		this.showHelp = showHelp;
		return this;
	}
	
	public BasicConfigBuilder withHeadless(boolean headless) {
		this.headless = headless;
		return this;
	}
	
	public BasicConfigBuilder withDataDirectory(File dataDir) {
		this.dataDir = dataDir;
		return this;
	}
	
	public BasicConfigBuilder withConfigFile(File configFile) {
		this.configFile = configFile;
		return this;
	}

}
