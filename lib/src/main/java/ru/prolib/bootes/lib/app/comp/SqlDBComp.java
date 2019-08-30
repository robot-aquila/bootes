package ru.prolib.bootes.lib.app.comp;

import java.sql.Connection;
import java.sql.DriverManager;

import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.SqlDBConfig;
import ru.prolib.bootes.lib.config.SqlDBConfigSection;

public class SqlDBComp extends CommonComp {
	private static final String DEFAULT_COMP_ID = "SQLDB";
	private static final String CONFIG_SECTION_ID = "sqldb";
	private Connection dbh;

	public SqlDBComp(AppServiceLocator serviceLocator, String serviceID) {
		super(serviceLocator, serviceID);
	}
	
	public SqlDBComp(AppServiceLocator serviceLocator) {
		this(serviceLocator, DEFAULT_COMP_ID);
	}

	@Override
	public void init() throws Throwable {
		SqlDBConfig conf = serviceLocator.getConfig().getSection(CONFIG_SECTION_ID);
		String url = conf.getURL();
		if ( url.startsWith("jdbc:hsqldb:") ) {
			Class.forName("org.hsqldb.jdbcDriver");			
		}
		serviceLocator.setSqlDBConn(dbh = DriverManager.getConnection(url, conf.getUser(), conf.getPass()));
	}

	@Override
	public void startup() throws Throwable {
		
	}

	@Override
	public void shutdown() throws Throwable {
		if ( dbh != null ) {
			dbh.close();
		}
	}

	@Override
	public void registerConfig(AppConfigService2 config_service) {
		config_service.addSection(CONFIG_SECTION_ID, new SqlDBConfigSection());
	}

}
