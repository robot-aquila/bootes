package ru.prolib.bootes.tsgr001a.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;

/**
 * Price-value cluster.
 * <p>
 * This class accumulates information of trades according its price.
 */
public class PVCluster {
	private final Map<CDecimal, CDecimal> pvMap;
	private CDecimal totalVolume;
	
	public PVCluster(Map<CDecimal, CDecimal> pvMap) {
		this.pvMap = pvMap;
		this.totalVolume = CDecimalBD.ZERO;
	}
	
	public PVCluster() {
		this(new HashMap<>());
	}
	
	public synchronized void addTrade(CDecimal price, CDecimal volume) {
		CDecimal v = pvMap.get(price);
		if ( v == null ) {
			v = volume;
		} else {
			v = v.add(volume);
		}
		pvMap.put(price, v);
		totalVolume = totalVolume.add(volume);
	}
	
	public synchronized CDecimal getTotalVolume() {
		return totalVolume;
	}
	
	public synchronized CDecimal getWeightedAverage() {
		Iterator<Entry<CDecimal, CDecimal>> it = pvMap.entrySet().iterator();
		CDecimal result = null;
		while ( it.hasNext() ) {
			Entry<CDecimal, CDecimal> item = it.next();
			CDecimal weight = item.getValue().divideExact(totalVolume, 8);
			CDecimal weighted = item.getKey().multiply(weight);
			if ( result == null ) {
				result = weighted;
			} else {
				result = result.add(weighted);
			}
		}
		return result;
	}

}
