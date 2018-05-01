package ru.prolib.bootes.lib.config;

import java.io.File;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;

public class TerminalConfigBuilder {
	private Account qfTestAccount = new Account("TEST-ACCOUNT");
	private CDecimal qfTestBalance = CDecimalBD.ofRUB2("1000000");
	private File qfSymbolDir, qfL1Dir;

	public TerminalConfig build(BasicConfig basicConfig) throws ConfigException {
		if ( qfTestAccount == null ) {
			throw new ConfigException("Test account was not specified");
		}
		if ( qfTestBalance == null ) {
			throw new ConfigException("Test balance was not specified");
		}
		if ( qfTestBalance.getScale() != 2 ) {
			throw new ConfigException("Expected scale of test balance is 2 but: " + qfTestBalance.getScale());
		}
		if ( ! CDecimalBD.RUB.equals(qfTestBalance.getUnit()) ) {
			throw new ConfigException("Expected currency of test balance is RUB but: " + qfTestBalance.getUnit());
		}
		if ( qfSymbolDir == null ) {
			throw new ConfigException("Directory of symbol data was not specified");
		}
		if ( qfL1Dir == null ) {
			throw new ConfigException("Directory of L1 data was not specified");
		}
		return new TerminalConfig(
				qfTestAccount,
				qfTestBalance,
				qfSymbolDir,
				qfL1Dir
			);
	}
	
	public TerminalConfigBuilder withQFortsTestAccount(Account account) {
		this.qfTestAccount = account;
		return this;
	}
	
	public TerminalConfigBuilder withQFortsTestBalance(CDecimal balance) {
		this.qfTestBalance = balance;
		return this;
	}
	
	public TerminalConfigBuilder withQFortsSymbolDirectory(File dir) {
		this.qfSymbolDir = dir;
		return this;
	}
	
	public TerminalConfigBuilder withQFortsL1Directory(File dir) {
		this.qfL1Dir = dir;
		return this;
	}
	
}
