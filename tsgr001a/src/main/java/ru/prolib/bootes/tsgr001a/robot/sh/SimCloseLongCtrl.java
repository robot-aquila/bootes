package ru.prolib.bootes.tsgr001a.robot.sh;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;

public class SimCloseLongCtrl implements SimClosePosition.Ctrl {

	@Override
	public CDecimal getSpeculationPL(Speculation spec) {
		return spec.getExitPoint().getValue()
				.subtract(spec.getEntryPoint().getValue());
	}

}
