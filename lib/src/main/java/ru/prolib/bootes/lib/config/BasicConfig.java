package ru.prolib.bootes.lib.config;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Note: This shouldn't contains any mandatory options.
 */
public class BasicConfig {
	private final boolean showHelp, headless, noOrders;
	private final File dataDir, configFile, reportsDir;
	
	public BasicConfig(boolean help,
			boolean headless,
			boolean noOrders,
			File dataDir,
			File configFile,
			File reportsDir)
	{
		this.showHelp = help;
		this.headless = headless;
		this.noOrders = noOrders;
		this.dataDir = dataDir;
		this.configFile = configFile;
		this.reportsDir = reportsDir;
	}
	
	public boolean isShowHelp() {
		return showHelp;
	}
	
	public boolean isHeadless() {
		return headless;
	}
	
	public boolean isNoOrders() {
		return noOrders;
	}
	
	public File getDataDirectory() {
		return dataDir;
	}
	
	public File getConfigFile() {
		return configFile;
	}
	
	public File getReportsDirectory() {
		return reportsDir;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != BasicConfig.class ) {
			return false;
		}
		BasicConfig o = (BasicConfig) other;
		return new EqualsBuilder()
			.append(o.showHelp, showHelp)
			.append(o.headless, headless)
			.append(o.noOrders, noOrders)
			.append(o.dataDir, dataDir)
			.append(o.configFile, configFile)
			.append(o.reportsDir, reportsDir)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("showHelp", showHelp)
			.append("headless", headless)
			.append("noOrders", noOrders)
			.append("dataDir", dataDir)
			.append("configFile", configFile)
			.append("reportsDir", reportsDir)
			.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(7117529, 995)
			.append(showHelp)
			.append(headless)
			.append(noOrders)
			.append(dataDir)
			.append(configFile)
			.append(reportsDir)
			.toHashCode();
	}
	
}
