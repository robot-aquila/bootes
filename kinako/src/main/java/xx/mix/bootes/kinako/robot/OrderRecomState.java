package xx.mix.bootes.kinako.robot;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import xx.mix.bootes.kinako.service.VVOrderType;

public class OrderRecomState {
	private final VVOrderType type;
	private final String symbol;
	private final CDecimal volume;	
	private final Symbol localSymbol;
	private OrderRecomStatus status = OrderRecomStatus.NEW;
	private CDecimal executedVolume = of(0L);
	private String comment = "";
	
	public OrderRecomState(
			VVOrderType type,
			String symbol,
			CDecimal volume,
			Symbol localSymbol
		)
	{
		this.type = type;
		this.symbol = symbol;
		this.volume = volume;
		this.localSymbol = localSymbol;
	}
	
	public VVOrderType getType() {
		return type;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public CDecimal getVolume() {
		return volume;
	}
	
	public Symbol getLocalSymbol() {
		return localSymbol;
	}
	
	public synchronized OrderRecomStatus getStatus() {
		return status;
	}
	
	public synchronized CDecimal getExecutedVolume() {
		return executedVolume;
	}
	
	public synchronized String getComment() {
		return comment;
	}
	
	public synchronized OrderRecomState setStatus(OrderRecomStatus status) {
		this.status = status;
		return this;
	}
	
	public synchronized OrderRecomState setExecutedVolume(CDecimal volume) {
		this.executedVolume = volume;
		return this;
	}
	
	public synchronized OrderRecomState setComment(String comment) {
		this.comment = comment;
		return this;
	}

}
