package ru.prolib.bootes.tsgr001a.robot.sh;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.mscan.sensors.TradeSignal;

public class SimOpenShortCtrl implements SimOpenPosition.Ctrl {

	@Override
	public void setExitParams(Speculation spec) {
		TradeSignal signal = spec.getTradeSignal();
		CDecimal price = spec.getEntryPoint().getPrice();
		spec.setTakeProfitAt(price.subtract(signal.getTakeProfitPts()));
		spec.setStopLossAt(price.add(signal.getStopLossPts()));
		spec.setBreakEvenAt(price.subtract(signal.getStopLossPts()));
	}

}
