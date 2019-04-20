package ru.prolib.bootes.lib.robo.s3.statereq;

import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.SignalTrigger;
import ru.prolib.bootes.lib.data.ts.filter.IFilterSet;
import ru.prolib.bootes.lib.rm.IRMContractStrategy;

public interface IS3SignalDeterminable {
	SignalTrigger getSignalTrigger();
	IFilterSet<S3TradeSignal> getSignalFilter();
	IRMContractStrategy getContractStrategy();
}
