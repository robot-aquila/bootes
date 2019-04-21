package ru.prolib.bootes.lib.robo.s3.statereq;

import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.SignalTrigger;
import ru.prolib.bootes.lib.data.ts.filter.IFilterSet;

public interface IS3SignalDeterminable extends IS3Speculative, IS3PositionDeterminable {
	SignalTrigger getSignalTrigger();
	IFilterSet<S3TradeSignal> getSignalFilter();
}
