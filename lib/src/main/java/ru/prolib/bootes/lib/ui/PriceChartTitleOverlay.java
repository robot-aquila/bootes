package ru.prolib.bootes.lib.ui;

import java.time.ZoneId;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.utils.experimental.chart.SelectedCategoryTracker;
import ru.prolib.aquila.utils.experimental.chart.TextOverlay;

public class PriceChartTitleOverlay implements TextOverlay {
	protected final String prefix;
	protected final ZoneId zoneID;
	protected final TSeries<Candle> candles;
	protected final SelectedCategoryTracker tracker;
	
	public PriceChartTitleOverlay(String prefix,
			ZoneId zoneID,
			TSeries<Candle> candles,
			SelectedCategoryTracker tracker)
	{
		this.prefix = prefix;
		this.zoneID = zoneID;
		this.candles = candles;
		this.tracker = tracker;
	}

	@Override
	public boolean isVisible() {
		return tracker.isSelected();
	}

	@Override
	public String getText() {
		if ( ! tracker.isSelected() ) {
			return "";
		}
		try {
			int index = tracker.getAbsoluteIndex();
			return getText(index, candles.get(index));
		} catch ( Exception e ) {
			throw new IllegalStateException(e);
		}
	}
	
	protected String getText(int categoryIndex, Candle candle) throws Exception {
		if ( candle == null ) {
			return "";
		}
		return new StringBuilder()
				.append(prefix)
				.append(" ")
				.append(candle.getStartTime().atZone(zoneID).toLocalDateTime())
				.append(" O:")
				.append(candle.getOpen())
				.append(" H:")
				.append(candle.getHigh())
				.append(" L:")
				.append(candle.getLow())
				.append(" C:")
				.append(candle.getClose())
				.append(" VOL:")
				.append(candle.getVolume())
				.toString();
	}

}
