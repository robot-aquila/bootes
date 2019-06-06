package ru.prolib.bootes.tsgr001a.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.config.ConfigException;

public class TSGR001AConfigBuilder {
	private static final String KEY_ENABLED = "enabled";
	private static final String KEY_ACCOUNT = "account";
	private static final String KEY_FILTERS = "filters";
	private static final String KEY_REPORT_HEADER = "report_header";
	
	private File instancesConfig;
	
	public TSGR001AConfig build() throws ConfigException {
		try {
			Wini ini = new Wini(instancesConfig);
			List<TSGR001AInstConfig> list = new ArrayList<>();
			for ( String s_title : ini.keySet() ) {
				Section section = ini.get(s_title);
				switch ( section.get(KEY_ENABLED, "1").toLowerCase() ) {
				case "1":
				case "y":
				case "yes":
				case "true":
					break;
				case "0":
				case "n":
				case "no":
				case "false":
				default:
					continue;
				}
				
				String s_account = section.get(KEY_ACCOUNT);
				if ( s_account == null ) {
					throw new ConfigException(new StringBuilder()
							.append("Account code not defined in section ")
							.append(s_title)
							.toString());
				}
				Account account = new Account(s_account);
				
				String s_filters = section.get(KEY_FILTERS, "");
				String s_report_header = section.get(KEY_REPORT_HEADER);
				list.add(new TSGR001AInstConfig(
						account,
						s_title,
						s_filters,
						s_report_header
					));
			}
			return new TSGR001AConfig(list);
		} catch ( IOException e ) {
			throw new ConfigException(e);
		}
	}
	
	public TSGR001AConfigBuilder withInstancesConfig(File instances_config) {
		this.instancesConfig = instances_config;
		return this;
	}
	
}
