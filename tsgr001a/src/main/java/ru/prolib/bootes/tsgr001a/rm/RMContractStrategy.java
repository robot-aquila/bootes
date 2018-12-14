package ru.prolib.bootes.tsgr001a.rm;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Strategy risk management of separate contract.
 */
public class RMContractStrategy {
	private RMContractStrategyParams params;
	private Portfolio portfolio;
	private Security security;
	private Series<CDecimal> avgDailyPriceMove, avgLocalPriceMove;

	public void setStrategyParams(RMContractStrategyParams params) {
		this.params = params;
	}
	
	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}
	
	public void setAvgDailyPriceMove(Series<CDecimal> series) {
		this.avgDailyPriceMove = series;
	}
	
	public void setAvgLocalPriceMove(Series<CDecimal> series) {
		this.avgLocalPriceMove = series;
	}

	public RMContractStrategyParams getStrategyParams() {
		return params;
	}
	
	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	public Security getSecurity() {
		return security;
	}
	
	public Series<CDecimal> getAvgDailyPriceMove() {
		return avgDailyPriceMove;
	}
	
	public Series<CDecimal> getAvgLocalPriceMove() {
		return avgLocalPriceMove;
	}
	
	public CDecimal priceToMoney(CDecimal price) {
		CDecimal d_step_size = security.getTickSize();
		if ( d_step_size.compareTo(CDecimalBD.ZERO) == 0 ) {
			return CDecimalBD.ofRUB2("0");
		}
		CDecimal d_steps = price.divideExact(d_step_size, 0);
		return security.getTickValue().multiply(d_steps).withScale(2);
	}
	
	public CDecimal moneyToPrice(CDecimal money) {
		CDecimal d_step_cost = security.getTickValue().toAbstract();
		if ( d_step_cost.compareTo(CDecimalBD.ZERO) == 0 ) {
			return CDecimalBD.ZERO;
		}
		CDecimal d_steps = money.divideExact(d_step_cost, 0).toAbstract();
		return security.getTickSize().multiply(d_steps);
	}
	
	private CDecimal stepsToPrice(long steps) {
		return security.getTickSize().multiply(steps);
	}
	
	private RMContractStrategyPositionParams emptyPositionParams() {
		return new RMContractStrategyPositionParams(
				0,
				stepsToPrice(0),
				stepsToPrice(0),
				priceToMoney(CDecimalBD.of(0L)),
				priceToMoney(CDecimalBD.of(0L))
			);
	}
	
	public RMContractStrategyPositionParams getPositionParams() {
		// TODO: от баланса или эквити?
		try {
			CDecimal d_trade_loss_cap_per = params.getTradeLossCapPer();
			CDecimal d_trade_goal_cap_per = params.getTradeGoalCapPer();
			CDecimal d_price_step_size = security.getTickSize();
			CDecimal d_price_step_cost = security.getTickValue().toAbstract();
			CDecimal d_exp_local_price_move_per = params.getExpLocalPriceMovePer();
			if ( d_trade_goal_cap_per.compareTo(CDecimalBD.ZERO) == 0
			  || d_trade_loss_cap_per.compareTo(CDecimalBD.ZERO) == 0
			  || d_price_step_size.compareTo(CDecimalBD.ZERO) == 0
			  || d_price_step_cost.compareTo(CDecimalBD.ZERO) == 0
			  || d_exp_local_price_move_per.compareTo(CDecimalBD.ZERO) == 0 )
			{
				return emptyPositionParams();
			}
			
			CDecimal d_balance = portfolio.getBalance();
			if ( d_balance.toAbstract().compareTo(CDecimalBD.ZERO) == 0 ) {
				return emptyPositionParams();
			}
			CDecimal d_trade_goal_cap = d_balance.multiply(d_trade_goal_cap_per).withScale(2);
			CDecimal d_trade_loss_cap = d_balance.multiply(d_trade_loss_cap_per).withScale(2);
			CDecimal d_take_profit_pts = avgDailyPriceMove.get()
					.multiply(params.getExpDailyPriceMovePer())
					.divideExact(d_price_step_size, 0)
					.multiply(d_price_step_size);
			if ( d_take_profit_pts.compareTo(CDecimalBD.ZERO) == 0 ) {
				return emptyPositionParams();
			}
			CDecimal d_num_contracts = d_price_step_size.multiply(d_trade_goal_cap.toAbstract())
					.divide(d_price_step_cost)
					.divide(d_take_profit_pts)
					.withScale(0);
			if ( d_num_contracts.compareTo(CDecimalBD.ZERO) == 0 ) {
				return emptyPositionParams();
			}
			CDecimal d_stop_loss_pts = d_price_step_size.multiply(d_trade_loss_cap.toAbstract())
					.divide(d_num_contracts)
					.divide(d_price_step_cost)
					.subtract(d_price_step_size.multiply((long) params.getSlippageStp()))
					.divideExact(d_price_step_size, 0)
					.multiply(d_price_step_size);
			return new RMContractStrategyPositionParams(
					d_num_contracts.toBigDecimal().intValue(),
					d_take_profit_pts,
					d_stop_loss_pts,
					d_trade_goal_cap,
					d_trade_loss_cap
				);
		} catch ( ValueException e ) {
			throw new IllegalStateException(e);
		}
	}

}
