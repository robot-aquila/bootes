package ru.prolib.bootes.lib.config;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.OptionProvider;

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
