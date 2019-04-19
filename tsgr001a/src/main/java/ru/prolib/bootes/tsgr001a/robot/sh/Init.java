package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.LocalTime;
import java.time.ZoneId;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.aquila.core.utils.LocalTimeTable;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.cr.MOEXContractResolverRegistry;
import ru.prolib.bootes.lib.rm.RMContractStrategy;
import ru.prolib.bootes.lib.rm.RMContractStrategyParams;
import ru.prolib.bootes.lib.rm.RMPriceStats;
import ru.prolib.bootes.tsgr001a.robot.TSGR001APriceStats;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.TSGR001AStrategyObjectLocator;

public class Init extends CommonHandler {
	public static final String E_OK = "OK";
	
	public Init(AppServiceLocator serviceLocator, RobotState state) {
		super(serviceLocator, state);
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		final String cn = "RTS", an = "QFORTS-TEST";
		state.setContractName(cn);
		state.setContractResolver(new MOEXContractResolverRegistry().getResolver(cn));
		state.setAccountCode(an);
		
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
		TSGR001AStrategyObjectLocator ol = new TSGR001AStrategyObjectLocator(state);
		RMPriceStats ps = new TSGR001APriceStats(state);
		state.setContractStrategy(new RMContractStrategy(csp, ol, ps, ltt));
		
		state.getStateListener().robotStarted();
		return getExit(E_OK);
	}

}
