package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.LocalTime;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.aquila.core.utils.LocalTimeTable;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.cr.MOEXContractResolverRegistry;
import ru.prolib.bootes.lib.data.ts.S3CESDSignalTrigger;
import ru.prolib.bootes.lib.rm.RMContractStrategy;
import ru.prolib.bootes.lib.rm.RMContractStrategyParams;
import ru.prolib.bootes.lib.rm.RMPriceStats;
import ru.prolib.bootes.lib.robo.s3.S3RMCSObjectLocator;
import ru.prolib.bootes.tsgr001a.robot.TSGR001APriceStats;
import ru.prolib.bootes.tsgr001a.robot.TSGR001ASigTriggerObjectLocator;
import ru.prolib.bootes.tsgr001a.robot.TSGR001AReports;
import ru.prolib.bootes.tsgr001a.config.TSGR001AInstConfig;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;
import ru.prolib.bootes.tsgr001a.robot.TSGR001ADataHandler;
import ru.prolib.bootes.tsgr001a.robot.filter.S3TSFilterFactory;
import ru.prolib.bootes.tsgr001a.robot.filter.S3TSFilterSetFactory;

public class TSGR001AInit extends CommonHandler {
	public static final String E_OK = "OK";
	
	private final TSGR001AReports reports;
	private final TSGR001AInstConfig config;
	
	public TSGR001AInit(AppServiceLocator serviceLocator,
						TSGR001AReports reports,
						RobotState state,
						TSGR001AInstConfig config)
	{
		super(serviceLocator, state);
		this.reports = reports;
		this.config = config;
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		final String cn = "RTS"/*, an = "QFORTS-TEST"*/;
		state.setContractName(cn);
		state.setContractResolver(new MOEXContractResolverRegistry().getResolver(cn));
		//state.setAccount(new Account(an));
		state.setAccount(config.getAccount());
		
		
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
		LocalTimeTable ltt = new LocalTimeTable(serviceLocator.getZoneID())
				.addPeriod(LocalTime.of(10, 30), LocalTime.of(13, 50))
				.addPeriod(LocalTime.of(14, 10), LocalTime.of(18, 30));
		S3RMCSObjectLocator ol = new S3RMCSObjectLocator(state);
		RMPriceStats ps = new TSGR001APriceStats(state);
		state.setContractStrategy(new RMContractStrategy(csp, ol, ps, ltt));
		state.setContractStrategyParams(csp);
		
		state.setSignalTrigger(new S3CESDSignalTrigger(new TSGR001ASigTriggerObjectLocator(state, SetupT0.SID_CLOSE_PRICE)));
		state.setSignalFilter(new S3TSFilterSetFactory(new S3TSFilterFactory(reports, state))
				//.produce("CoolDown30, SLgtATR, MADevLim, ByTrendT1, FCSD"));
				.produce(config.getFilterDefs()));
		reports.setHeader(config.getReportHeader());
		
		state.getStateListener().robotStarted();
		return getExit(E_OK);
	}

}
