package ru.prolib.bootes.lib.config;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.aquila.qforts.impl.QFOrderExecutionTriggerMode;
import ru.prolib.aquila.qforts.impl.QForts;

public class QFTerminalConfig {
	private final OptionProvider options;

	public QFTerminalConfig(OptionProvider options) {
		this.options = options;
	}
	
	public Account getTestAccount() throws ConfigException {
		return new Account(options.getStringNotNull(QFTerminalConfigSection.LOPT_TEST_ACCOUNT, null));
	}
	
	public CDecimal getTestBalance() throws ConfigException {
		return CDecimalBD.ofRUB2(options.getStringNotNull(QFTerminalConfigSection.LOPT_TEST_BALANCE, null));
	}
	
	public File getDataDirectory() throws ConfigException {
		return options.getFileNotNull(QFTerminalConfigSection.LOPT_DATA_DIR);
	}
	
	public int getLiquidityMode() throws ConfigException {
		int mode = options.getIntegerPositive(QFTerminalConfigSection.LOPT_LIQUIDITY_MODE, QForts.LIQUIDITY_LIMITED);
		if ( mode != QForts.LIQUIDITY_LIMITED
		  && mode != QForts.LIQUIDITY_APPLY_TO_ORDER
		  && mode != QForts.LIQUIDITY_UNLIMITED )
		{
			throw new ConfigException(new StringBuilder()
					.append(QFTerminalConfigSection.LOPT_LIQUIDITY_MODE)
					.append(" expected to be 0, 1 or 2 but: ")
					.append(mode)
					.toString());
		}
		return mode;
	}
	
	public boolean isLegacySymbolDataService() throws ConfigException {
		return options.getBoolean(QFTerminalConfigSection.LOPT_LEGACY_SDS);
	}
	
	public QFOrderExecutionTriggerMode getOrderExecTriggerMode() throws ConfigException {
		int mode = options.getIntegerPositive(QFTerminalConfigSection.LOPT_ORDER_EXEC_TRIGGER_MODE, 0);
		switch ( mode ) {
		case 0:
			return QFOrderExecutionTriggerMode.USE_LAST_TRADE_EVENT_OF_SECURITY;
		case 1:
			return QFOrderExecutionTriggerMode.USE_L1UPDATES_WHEN_ORDER_APPEARS;
		default:
			throw new ConfigException(new StringBuilder()
					.append(QFTerminalConfigSection.LOPT_ORDER_EXEC_TRIGGER_MODE)
					.append(" expected to be 0 or 1 but: ")
					.append(mode)
					.toString());
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != QFTerminalConfig.class ) {
			return false;
		}
		QFTerminalConfig o = (QFTerminalConfig) other;
		return new EqualsBuilder()
				.append(o.options, options)
				.build();
	}

}
