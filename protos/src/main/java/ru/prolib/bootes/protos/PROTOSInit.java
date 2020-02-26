package ru.prolib.bootes.protos;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.LocalTime;
import java.time.ZoneId;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.aquila.core.utils.LocalTimeTable;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.cr.MOEXContractResolverRegistry;
import ru.prolib.bootes.lib.data.ts.CMASignalTrigger;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.FilterSet;
import ru.prolib.bootes.lib.rm.RMContractStrategy;
import ru.prolib.bootes.lib.rm.RMContractStrategyParams;
import ru.prolib.bootes.lib.rm.RMPriceStats;
import ru.prolib.bootes.lib.robo.s3.S3RMCSObjectLocator;
import ru.prolib.bootes.protos.config.ProtosConfig;

public class PROTOSInit extends SMStateHandlerEx {
	public static final String E_OK = "OK";
	
	protected final AppServiceLocator serviceLocator;
	protected final PROTOSRobotState state;
	
	public PROTOSInit(AppServiceLocator serviceLocator, PROTOSRobotState state) {
		this.serviceLocator = serviceLocator;
		this.state = state;
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		final String contract_name = "RTS", account_name = state.getRobotID() + "-TEST";
		state.setRobotTitle(state.getRobotID() + "-" + contract_name);
		state.setContractResolver(new MOEXContractResolverRegistry().getResolver(contract_name));
		state.setAccount(new Account(account_name));
		ProtosConfig conf = serviceLocator.getConfig().getSection(PROTOSRobotComp.CONFIG_SECTION_ID);
		PROTOSDataHandler data_handler = new PROTOSDataHandler(serviceLocator, state, conf.isUseOhlcProvider());
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
		RMPriceStats ps = new PROTOSPriceStats(state);
		state.setContractStrategy(new RMContractStrategy(csp, ol, ps, ltt));
		state.setSignalTrigger(new CMASignalTrigger(new PROTOSSigTrigObjectLocator(state)));
		state.setSignalFilter(new FilterSet<S3TradeSignal>());
		state.setSessionDataHandler(data_handler);
		
		state.getStateListener().robotStarted();
		return getExit(E_OK);
	}

}
