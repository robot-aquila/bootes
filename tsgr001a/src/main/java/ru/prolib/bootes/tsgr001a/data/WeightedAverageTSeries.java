package ru.prolib.bootes.tsgr001a.data;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.ZTFrame;

public class WeightedAverageTSeries implements TSeries<CDecimal> {
	private final TSeries<PVCluster> source;
	private final String seriesID;
	
	public WeightedAverageTSeries(TSeries<PVCluster> source, String seriesID) {
		this.source = source;
		this.seriesID = seriesID;
	}
	
	public TSeries<PVCluster> getSource() {
		return source;
	}

	@Override
	public CDecimal get(Instant key) {
		PVCluster c = source.get(key);
		return c == null ? null : c.getWeightedAverage();
	}

	@Override
	public int toIndex(Instant key) {
		return source.toIndex(key);
	}

	@Override
	public Instant toKey(int index) throws ValueException {
		return source.toKey(index);
	}

	@Override
	public String getId() {
		return seriesID;
	}

	@Override
	public CDecimal get() throws ValueException {
		PVCluster c = source.get();
		return c == null ? null : c.getWeightedAverage();
	}

	@Override
	public CDecimal get(int index) throws ValueException {
		PVCluster c = source.get(index);
		return c == null ? null : c.getWeightedAverage();
	}

	@Override
	public int getLength() {
		return source.getLength();
	}

	@Override
	public LID getLID() {
		return source.getLID();
	}

	@Override
	public void lock() {
		source.lock();
	}

	@Override
	public void unlock() {
		source.unlock();
	}

	@Override
	public ZTFrame getTimeFrame() {
		return source.getTimeFrame();
	}

}
