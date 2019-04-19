package ru.prolib.bootes.lib.rm;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.math.RoundingMode;
import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.utils.LocalTimeTable;

/**
 * Strategy risk management of separate contract.
 */
public class RMContractStrategy {
	
	public interface ObjectLocator {
		Security getSecurity();
		Portfolio getPortfolio();
	}
	
	public static class ObjectLocatorStub implements ObjectLocator {
		private Security security;
		private Portfolio portfolio;

		@Override
		public Security getSecurity() {
			return security;
		}

		@Override
		public Portfolio getPortfolio() {
			return portfolio;
		}
		
		public void setSecurity(Security security) {
			this.security = security;
		}
		
		public void setPortfolio(Portfolio portfolio) {
			this.portfolio = portfolio;
		}
		
	}
	
	private final RMContractStrategyParams params;
	private final RMPriceStats priceStats;
	private final LocalTimeTable timetable;
	private final ObjectLocator locator;
	
	public RMContractStrategy(RMContractStrategyParams params,
							  ObjectLocator locator,
							  RMPriceStats priceStats,
							  LocalTimeTable timetable)
	{
		this.params = params;
		this.locator = locator;
		this.priceStats = priceStats;
		this.timetable = timetable;
	}

	public RMContractStrategyParams getStrategyParams() {
		return params;
	}
	
	public LocalTimeTable getTradingTimetable() {
		if ( timetable == null ) {
			throw new NullPointerException("Timetable not defined");
		}
		return timetable;
	}
	
	public RMPriceStats getPriceStats() {
		return priceStats;
	}
	
	public ObjectLocator getObjectLocator() {
		return locator;
	}
	
	public CDecimal priceToMoney(CDecimal price) {
		Security security = locator.getSecurity();
		CDecimal d_step_size = security.getTickSize();
		if ( d_step_size.compareTo(ZERO) == 0 ) {
			return CDecimalBD.ofRUB2("0");
		}
		CDecimal d_steps = price.divideExact(d_step_size, 0);
		return security.getTickValue().multiply(d_steps).withScale(2);
	}
	
	public CDecimal moneyToPrice(CDecimal money) {
		Security security = locator.getSecurity();
		CDecimal d_step_cost = security.getTickValue().toAbstract();
		if ( d_step_cost.compareTo(ZERO) == 0 ) {
			return ZERO;
		}
		CDecimal d_steps = money.divideExact(d_step_cost, 0).toAbstract();
		return security.getTickSize().multiply(d_steps);
	}
	
	private CDecimal stepsToPrice(long steps) {
		Security security = locator.getSecurity();
		return security.getTickSize().multiply(steps);
	}
	
	private RMContractStrategyPositionParams emptyPositionParams() {
		return new RMContractStrategyPositionParams(
				0,
				stepsToPrice(0),
				stepsToPrice(0),
				stepsToPrice(0),
				priceToMoney(of(0L)),
				priceToMoney(of(0L)),
				stepsToPrice(0),
				stepsToPrice(0),
				priceToMoney(of(0L))
			);
	}
	
	/**
	 * Get position parameters for time.
	 * <p>
	 * @param time - time to determine parameters for 
	 * @return position parameters
	 */
	public RMContractStrategyPositionParams getPositionParams(Instant time) {
		Security security = locator.getSecurity();
		Portfolio portfolio = locator.getPortfolio();
		CDecimal d_trade_loss_cap_per = params.getTradeLossCapPer();
		CDecimal d_trade_goal_cap_per = params.getTradeGoalCapPer();
		CDecimal d_price_step_size = security.getTickSize();
		CDecimal d_price_step_cost = security.getTickValue().toAbstract();
		CDecimal d_exp_local_price_move_per = params.getExpLocalPriceMovePer();
		CDecimal d_avg_daily_price_move = priceStats.getDailyPriceMove(time);
		CDecimal d_avg_local_price_move = priceStats.getLocalPriceMove(time);
		CDecimal d_strategy_cap_share_per = params.getStrategyCapSharePer(); // TODO: check null
		CDecimal d_init_margin = security.getInitialMargin(); // TODO: check null
		if ( d_trade_goal_cap_per.compareTo(ZERO) == 0
		  || d_trade_loss_cap_per.compareTo(ZERO) == 0
		  || d_price_step_size.compareTo(ZERO) == 0
		  || d_price_step_cost.compareTo(ZERO) == 0
		  || d_exp_local_price_move_per.compareTo(ZERO) == 0 )
		{
			return emptyPositionParams();
		}

		// TODO: От баланса, эквити или свободной маржи?
		// Скорее всего от эквити, но не более свободной маржи.
		CDecimal d_basis_value = portfolio.getFreeMargin().multiply(d_strategy_cap_share_per);
		if ( d_basis_value.toAbstract().compareTo(ZERO) == 0 ) {
			return emptyPositionParams();
		}
		CDecimal d_trade_goal_cap = d_basis_value.multiply(d_trade_goal_cap_per).withScale(2);
		CDecimal d_trade_loss_cap = d_basis_value.multiply(d_trade_loss_cap_per).withScale(2);
		CDecimal d_take_profit_pts = d_avg_daily_price_move
				.multiply(params.getExpDailyPriceMovePer())
				.divideExact(d_price_step_size, 0)
				.multiply(d_price_step_size);
		if ( d_take_profit_pts.compareTo(ZERO) == 0 ) {
			return emptyPositionParams();
		}
		CDecimal d_num_contracts = d_price_step_size.multiply(d_trade_goal_cap.toAbstract())
				.divide(d_price_step_cost)
				.divide(d_take_profit_pts)
				.withScale(0, RoundingMode.DOWN);
		CDecimal d_num_contracts_max = d_basis_value.divide(d_init_margin).withScale(0, RoundingMode.DOWN);
		d_num_contracts = d_num_contracts.min(d_num_contracts_max);
		if ( d_num_contracts.compareTo(ZERO) == 0 ) {
			return emptyPositionParams();
		}
		
		CDecimal d_stop_loss_pts = d_price_step_size.multiply(d_trade_loss_cap.toAbstract())
				.divide(d_num_contracts)
				.divide(d_price_step_cost)
				.subtract(d_price_step_size.multiply((long) params.getSlippageStp()))
				.divideExact(d_price_step_size, 0)
				.multiply(d_price_step_size);
		CDecimal d_slippage_pts = d_price_step_size.multiply((long) params.getSlippageStp());
		return new RMContractStrategyPositionParams(
				d_num_contracts.toBigDecimal().intValue(),
				d_take_profit_pts,
				d_stop_loss_pts,
				d_slippage_pts,
				d_trade_goal_cap,
				d_trade_loss_cap,
				d_avg_daily_price_move,
				d_avg_local_price_move,
				d_basis_value
			);
	}

}
