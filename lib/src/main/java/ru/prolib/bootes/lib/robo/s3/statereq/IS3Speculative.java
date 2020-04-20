package ru.prolib.bootes.lib.robo.s3.statereq;

import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.bootes.lib.rm.IRMContractStrategy;
import ru.prolib.bootes.lib.robo.s3.S3Speculation;
import ru.prolib.bootes.lib.robo.sh.statereq.IAccountDeterminable;
import ru.prolib.bootes.lib.robo.sh.statereq.IContractDeterminable;

public interface IS3Speculative extends
	IS3StateObservable,
	IAccountDeterminable,
	IContractDeterminable
{
	boolean isSpeculationActive();
	S3Speculation getActiveSpeculation();
	void setActiveSpeculation(S3Speculation spec);
	IRMContractStrategy getContractStrategy();
	void setLastOrder(Order order);
	Order getLastOrder();
}
