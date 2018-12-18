package ru.prolib.bootes.tsgr001a.robot.ui;

import java.time.ZoneId;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.utils.experimental.chart.SelectedCategoryTracker;
import ru.prolib.bootes.lib.ui.PriceChartTitleOverlay;

public class PriceChartTitleOverlayWithEMA extends PriceChartTitleOverlay {
	protected final TSeries<CDecimal> ema;

	public PriceChartTitleOverlayWithEMA(String prefix,
			ZoneId zoneID,
			TSeries<Candle> candles,
			TSeries<CDecimal> ema,
			SelectedCategoryTracker tracker)
	{
		super(prefix, zoneID, candles, tracker);
		this.ema = ema;
	}

	@Override
	protected String getText(int categoryIndex, Candle candle) throws Exception {
		StringBuilder sb = new StringBuilder()
				.append(super.getText(categoryIndex, candle));
		CDecimal value = ema.get(categoryIndex);
		if ( value != null ) {
			sb.append(" EMA:").append(value);
		}
		return sb.toString();
	}
}
