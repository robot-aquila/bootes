package ru.prolib.bootes.tsgr001a.data;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.tseries.filler.FillBySecurityEvent;

public class PVClusterSeriesByLastTrade extends FillBySecurityEvent<PVCluster> {
	protected final PVClusterAggregator aggregator;

	public PVClusterSeriesByLastTrade(EditableTSeries<PVCluster> series,
			Terminal terminal,
			Symbol symbol,
			PVClusterAggregator aggregator)
	{
		super(series, terminal, symbol);
		this.aggregator = aggregator;
	}
	
	public PVClusterSeriesByLastTrade(EditableTSeries<PVCluster> series,
			Terminal terminal,
			Symbol symbol)
	{
		this(series, terminal, symbol, PVClusterAggregator.getInstance());
	}
	
	public PVClusterAggregator getAggregator() {
		return aggregator;
	}

	@Override
	protected void processEvent(Event event) {
		if ( event instanceof SecurityTickEvent ) {
			SecurityTickEvent e = (SecurityTickEvent) event;
			series.lock();
			try {
				aggregator.aggregate(series, e.getTick());
			} finally {
				series.unlock();
			}
		}
	}

	@Override
	protected void stopListening(Security security) {
		security.onLastTrade().removeListener(this);
	}

	@Override
	protected void startListening(Security security) {
		security.onLastTrade().addListener(this);
	}

}
