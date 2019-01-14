package ru.prolib.bootes.tsgr001a.robot.sh;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;

public class SimTrackLongCtrl implements SimTrackPosition.Ctrl {
	
	@Override
	public boolean isTakeProfit(CDecimal lastPrice, Speculation spec) {
		 return lastPrice.compareTo(spec.getTakeProfitAt()) >= 0;
	}

	@Override
	public boolean isStopLoss(CDecimal lastPrice, Speculation spec) {
		return lastPrice.compareTo(spec.getStopLossAt()) <= 0; 
	}

	@Override
	public boolean isBreakEven(CDecimal lastPrice, Speculation spec) {
		return lastPrice.compareTo(spec.getBreakEvenAt()) >= 0;
	}

}
