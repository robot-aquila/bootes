package ru.prolib.bootes.lib.ui;

import java.time.Instant;
import java.time.ZoneId;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.chart.SelectedCategoryTracker;
import ru.prolib.aquila.utils.experimental.chart.TextOverlay;

public class IndicatorChartTitleOverlay implements TextOverlay {
	private final String prefix;
	private final ZoneId zoneID;
	private final TSeries<CDecimal> values;
	private final SelectedCategoryTracker tracker;
	
	public IndicatorChartTitleOverlay(String prefix,
			ZoneId zoneID,
			TSeries<CDecimal> values,
			SelectedCategoryTracker tracker)
	{
		this.prefix = prefix;
		this.zoneID = zoneID;
		this.values = values;
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
		int index = tracker.getAbsoluteIndex();
		try {
			CDecimal value = values.get(index);
			Instant time = values.toKey(index);
			return new StringBuilder()
					.append(prefix)
					.append(" ")
					.append(time.atZone(zoneID).toLocalDateTime())
					.append(" ")
					.append(value)
					.toString();
		} catch ( ValueException e ) {
			throw new IllegalStateException(e);
		}
	}
}
