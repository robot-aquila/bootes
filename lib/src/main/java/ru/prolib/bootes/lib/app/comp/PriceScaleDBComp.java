package ru.prolib.bootes.lib.app.comp;

import ru.prolib.aquila.core.utils.PriceScaleDBLazy;
import ru.prolib.aquila.core.utils.PriceScaleDBTB;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;

public class PriceScaleDBComp extends CommonComp {
	private static final String DEFAULT_ID = "PRICE-SCALE-DB";
	private final PriceScaleDBLazy db;

	public PriceScaleDBComp(AppConfig appConfig, AppServiceLocator serviceLocator, String serviceID) {
		super(appConfig, serviceLocator, serviceID);
		db = new PriceScaleDBLazy();
	}
	
	public PriceScaleDBComp(AppConfig appConfig, AppServiceLocator serviceLocator) {
		this(appConfig, serviceLocator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		serviceLocator.setPriceScaleDB(db);
	}

	@Override
	public void startup() throws Throwable {
		db.setParentDB(new PriceScaleDBTB(serviceLocator.getTerminal()));
	}

	@Override
	public void shutdown() throws Throwable {
		
	}

}
