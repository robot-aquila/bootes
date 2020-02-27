package ru.prolib.bootes.lib.app.comp;

import ru.prolib.aquila.core.utils.PriceScaleDBLazy;
import ru.prolib.aquila.core.utils.PriceScaleDBTB;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;

@Deprecated
public class PriceScaleDBComp extends CommonComp {
	private static final String DEFAULT_ID = "PRICE-SCALE-DB";
	private final PriceScaleDBLazy db;

	public PriceScaleDBComp(AppServiceLocator serviceLocator, String serviceID) {
		super(serviceLocator, serviceID);
		db = new PriceScaleDBLazy();
	}
	
	public PriceScaleDBComp(AppServiceLocator serviceLocator) {
		this(serviceLocator, DEFAULT_ID);
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

	@Override
	public void registerConfig(AppConfigService2 config_service) {
		
	}

}
