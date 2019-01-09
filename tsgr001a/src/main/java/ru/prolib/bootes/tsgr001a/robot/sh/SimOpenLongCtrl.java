package ru.prolib.bootes.tsgr001a.robot.sh;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.mscan.sensors.TradeSignal;

public class SimOpenLongCtrl implements SimOpenPosition.Ctrl {

	@Override
	public void setExitParams(Speculation spec) {
		TradeSignal signal = spec.getTradeSignal();
		CDecimal price = spec.getEntryPoint().getPrice();
		spec.setTakeProfitAt(price.add(signal.getTakeProfitPts()));
		spec.setStopLossAt(price.subtract(signal.getStopLossPts()));
		spec.setBreakEvenAt(price.add(signal.getStopLossPts()));
	}

}
