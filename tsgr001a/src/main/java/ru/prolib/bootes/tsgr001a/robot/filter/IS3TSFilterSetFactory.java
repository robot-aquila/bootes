package ru.prolib.bootes.tsgr001a.robot.filter;

import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.IFilterSet;

public interface IS3TSFilterSetFactory {
	
	IFilterSet<S3TradeSignal> produce(String args);

}
