package ru.prolib.bootes.tsgr001a.robot;

import java.time.Instant;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.data.SecurityChartSetupTX;
import ru.prolib.bootes.lib.robo.sh.statereq.IContractDeterminable;

public abstract class SetupTX extends SecurityChartSetupTX {
	private final IContractDeterminable state;
	
	public SetupTX(AppServiceLocator serviceLocator, IContractDeterminable state) {
		super(serviceLocator, state.getContractParams().getSymbol());
		this.state = state;
	}

	@Override
	public void loadInitialData(EditableTSeries<Candle> ohlc) {
	int length = getLengthOfOhlcDataToInitialLoad();
		if ( length > 0 ) {
			Instant curr_end = state.getContractParams().getDataTrackingPeriod().getEnd();
			Instant next_start = state.getContractResolver().determineContract(curr_end)
					.getDataTrackingPeriod().getStart();
			warmUp(new TFSymbol(symbol, ohlc.getTimeFrame()), length, next_start);
		}
		super.loadInitialData(ohlc);
	}
	
	private void warmUp(TFSymbol key, int count, Instant end_time) {
		serviceLocator.getOHLCHistoryStorage().warmingUpReader(key, count, end_time);
	}

}
