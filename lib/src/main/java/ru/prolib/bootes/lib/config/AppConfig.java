package ru.prolib.bootes.lib.config;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Deprecated
public class AppConfig {
	private final BasicConfig basicConfig;
	private final SchedulerConfig schedulerConfig;
	private final OHLCHistoryConfig ohlcHistoryConfig;
	private final TerminalConfig terminalConfig;

	public AppConfig(BasicConfig basicConfig,
			SchedulerConfig schedulerConfig,
			OHLCHistoryConfig ohlcHistoryConfig,
			TerminalConfig terminalConfig)
	{
		this.basicConfig = basicConfig;
		this.schedulerConfig = schedulerConfig;
		this.ohlcHistoryConfig = ohlcHistoryConfig;
		this.terminalConfig = terminalConfig;
	}
	
	public BasicConfig getBasicConfig() {
		return basicConfig;
	}
	
	public SchedulerConfig getSchedulerConfig() {
		return schedulerConfig;
	}
	
	public OHLCHistoryConfig getOHLCHistoryConfig() {
		return ohlcHistoryConfig;
	}
	
	public TerminalConfig getTerminalConfig() {
		return terminalConfig;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("basic", basicConfig)
			.append("scheduler", schedulerConfig)
			.append("ohlcHistory", ohlcHistoryConfig)
			.append("terminal", terminalConfig)
			.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(127539, 39751)
			.append(basicConfig)
			.append(schedulerConfig)
			.append(ohlcHistoryConfig)
			.append(terminalConfig)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AppConfig.class ) {
			return false;
		}
		AppConfig o = (AppConfig) other;
		return new EqualsBuilder()
			.append(o.basicConfig, basicConfig)
			.append(o.schedulerConfig, schedulerConfig)
			.append(o.ohlcHistoryConfig, ohlcHistoryConfig)
			.append(o.terminalConfig, terminalConfig)
			.isEquals();
	}
	
}
