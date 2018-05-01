package ru.prolib.bootes.lib.config;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class OHLCHistoryConfig {
	private final File dataDir, cacheDir;
	
	public OHLCHistoryConfig(File dataDir, File cacheDir) {
		this.dataDir = dataDir;
		this.cacheDir = cacheDir;
	}
	
	public File getDataDirectory() {
		return dataDir;
	}
	
	public File getCacheDirectory() {
		return cacheDir;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("dataDir", dataDir)
			.append("cacheDir", cacheDir)
			.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(71245, 11519)
			.append(dataDir)
			.append(cacheDir)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OHLCHistoryConfig.class ) {
			return false;
		}
		OHLCHistoryConfig o = (OHLCHistoryConfig) other;
		return new EqualsBuilder()
			.append(o.dataDir, dataDir)
			.append(o.cacheDir, cacheDir)
			.isEquals();
	}

}
