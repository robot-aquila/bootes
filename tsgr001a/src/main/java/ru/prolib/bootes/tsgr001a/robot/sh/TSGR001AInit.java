package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.LocalTime;
import java.time.ZoneId;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.aquila.core.utils.LocalTimeTable;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.cr.MOEXContractResolverRegistry;
import ru.prolib.bootes.lib.data.ts.S3CESDSignalTrigger;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.FilterSet;
import ru.prolib.bootes.lib.rm.RMContractStrategy;
import ru.prolib.bootes.lib.rm.RMContractStrategyParams;
import ru.prolib.bootes.lib.rm.RMPriceStats;
import ru.prolib.bootes.lib.robo.s3.S3RMCSObjectLocator;
import ru.prolib.bootes.tsgr001a.robot.TSGR001APriceStats;
import ru.prolib.bootes.tsgr001a.robot.TSGR001ASigTriggerObjectLocator;
import ru.prolib.bootes.tsgr001a.robot.TSGR001AReports;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.TSGR001ADataHandler;
import ru.prolib.bootes.tsgr001a.robot.filter.S3TSFilterFactory;

public class TSGR001AInit extends CommonHandler {
	public static final String E_OK = "OK";
	
	private final TSGR001AReports reports;
	
	public TSGR001AInit(AppServiceLocator serviceLocator,
						TSGR001AReports reports,
						RobotState state)
	{
		super(serviceLocator, state);
		this.reports = reports;
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		final String cn = "RTS", an = "QFORTS-TEST";
		state.setContractName(cn);
		state.setContractResolver(new MOEXContractResolverRegistry().getResolver(cn));
		state.setAccount(new Account(an));
		
		TSGR001ADataHandler data_handler = new TSGR001ADataHandler(serviceLocator, state);
		state.setSessionDataHandler(data_handler);
		
		RMContractStrategyParams csp = new RMContractStrategyParams(
				of("0.075"),
				of("0.012"),
				of("0.600"),
				of("1.050"),
				3,
				of(1L)
			);
		LocalTimeTable ltt = new LocalTimeTable(ZoneId.of("Europe/Moscow"))
				.addPeriod(LocalTime.of(10, 30), LocalTime.of(13, 50))
				.addPeriod(LocalTime.of(14, 10), LocalTime.of(18, 30));
		S3RMCSObjectLocator ol = new S3RMCSObjectLocator(state);
		RMPriceStats ps = new TSGR001APriceStats(state);
		state.setContractStrategy(new RMContractStrategy(csp, ol, ps, ltt));
		state.setContractStrategyParams(csp);
		
		state.setSignalTrigger(new S3CESDSignalTrigger(new TSGR001ASigTriggerObjectLocator(state)));
		S3TSFilterFactory filter_factory = new S3TSFilterFactory(reports, state);
		FilterSet<S3TradeSignal> filters = new FilterSet<>();
		filters.addFilter(filter_factory.produce("CoolDown30"));
		filters.addFilter(filter_factory.produce("SLgtATR"));
		filters.addFilter(filter_factory.produce("MADevLim"));
		filters.addFilter(filter_factory.produce("ByTrendT1"));
		filters.addFilter(filter_factory.produce("FCSD"));
		state.setSignalFilter(filters);
		
		state.getStateListener().robotStarted();
		return getExit(E_OK);
	}

}
