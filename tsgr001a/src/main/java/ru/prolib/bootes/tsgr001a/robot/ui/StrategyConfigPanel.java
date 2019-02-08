package ru.prolib.bootes.tsgr001a.robot.ui;

import java.time.ZoneId;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.bootes.tsgr001a.rm.RMContractStrategy;
import ru.prolib.bootes.tsgr001a.rm.RMContractStrategyParams;
import ru.prolib.bootes.tsgr001a.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.tsgr001a.robot.ContractParams;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class StrategyConfigPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final IMessages messages;
	private final RobotState state;
	private final ZoneId zoneID;
	private JLabel jlAccount = new JLabel();
	private JLabel jlContractName = new JLabel();
	private JLabel jlTradeGoalCap = new JLabel();
	private JLabel jlTradeLossCap = new JLabel();
	private JLabel jlExpDailyPriceMove = new JLabel();
	private JLabel jlExpLocalPriceMove = new JLabel();
	private JLabel jlSlippage = new JLabel();
	private JLabel jlContractSymbol = new JLabel();
	private JLabel jlTradingPeriod = new JLabel();	
	private JLabel jlAvgDailyPriceMove = new JLabel();
	private JLabel jlAvgLocalPriceMove = new JLabel();
	private JLabel jlNumberOfContracts = new JLabel();
	private JLabel jlTakeProfit = new JLabel();
	private JLabel jlStopLoss = new JLabel();

	public StrategyConfigPanel(IMessages messages,
			RobotState state,
			ZoneId zoneID)
	{
		super(new MigLayout());
		this.messages = messages;
		this.state = state;
		this.zoneID = zoneID;
		addFormRow(StrategyConfigMsg.ACCOUNT, jlAccount);
		addFormRow(StrategyConfigMsg.CONTRACT_NAME, jlContractName);
		addFormRow(StrategyConfigMsg.CONTRACT_SYMBOL, jlContractSymbol);
		addFormRow(StrategyConfigMsg.TRADING_PERIOD, jlTradingPeriod);
		addFormRow(StrategyConfigMsg.TRADE_GOAL_CAP, jlTradeGoalCap);
		addFormRow(StrategyConfigMsg.TRADE_LOSS_CAP, jlTradeLossCap);
		addFormRow(StrategyConfigMsg.AVG_DAILY_PRICE_MOVE, jlAvgDailyPriceMove);
		addFormRow(StrategyConfigMsg.EXP_DAILY_PRICE_MOVE, jlExpDailyPriceMove);
		addFormRow(StrategyConfigMsg.AVG_LOCAL_PRICE_MOVE, jlAvgLocalPriceMove);
		addFormRow(StrategyConfigMsg.EXP_LOCAL_PRICE_MOVE, jlExpLocalPriceMove);
		addFormRow(StrategyConfigMsg.NUMBER_OF_CONTRACTS, jlNumberOfContracts);
		addFormRow(StrategyConfigMsg.TAKE_PROFIT, jlTakeProfit);
		addFormRow(StrategyConfigMsg.STOP_LOSS, jlStopLoss);
		addFormRow(StrategyConfigMsg.SLIPPAGE, jlSlippage);
		updateView();
	}
	
	private void addFormRow(MsgID labelMsgID, JComponent component) {
		JLabel label = new JLabel(messages.get(labelMsgID));
		label.setLabelFor(component);
		add(label, "align right");
		add(component, "wrap");
	}
	
	private String NA() {
		return messages.get(StrategyConfigMsg.NOT_AVAILABLE_SHORT);
	}
	
	private String percents(CDecimal percents) {
		return messages.format(StrategyConfigMsg.VAL_PERCENTS, percents.multiply(100L));
	}
	
	private String percentsAndMoney(CDecimal percents, CDecimal money) {
		return messages.format(StrategyConfigMsg.VAL_PERCENTS_MONEY, percents.multiply(100L), money);
	}
	
	//private String percentsAndPoints(CDecimal percents, CDecimal points) {
	//	return messages.format(StrategyConfigMsg.VAL_PERCENTS_POINTS, percents.multiply(100L), points);
	//}
	
	private String points(CDecimal points) {
		return messages.format(StrategyConfigMsg.VAL_POINTS, points);
	}
	
	public void updateView() {
		RMContractStrategy cs = state.getContractStrategy();
		RMContractStrategyParams csp = cs.getStrategyParams();
		
		jlAccount.setText(state.getAccountCode());
		jlContractName.setText(state.getContractName());
		
		jlExpDailyPriceMove.setText(percents(csp.getExpDailyPriceMovePer()));
		jlExpLocalPriceMove.setText(percents(csp.getExpLocalPriceMovePer()));
		
		if ( state.isContractParamsDefined() ) {
			ContractParams cp = state.getContractParams();
			jlContractSymbol.setText(cp.getSymbol().toString());
			jlTradingPeriod.setText(cp.getTradeAllowedPeriod()
					.getStart()
					.atZone(zoneID)
					.toLocalDate()
					.toString()
				);
		} else {
			jlContractSymbol.setText(NA());
			jlTradingPeriod.setText(NA());
		}
		
		if ( state.isSeriesHandlerT0Defined() ) {
			long slippage = csp.getSlippageStp();
			jlSlippage.setText(messages.format(StrategyConfigMsg.VAL_STEPS_POINTS, slippage,
					state.getSecurity().getTickSize().multiply(slippage)));
		} else {
			jlSlippage.setText(messages.format(StrategyConfigMsg.VAL_STEPS, csp.getSlippageStp()));
		}
		
		if ( state.isPositionParamsDefined() ) {
			RMContractStrategyPositionParams cspp = state.getPositionParams();
			jlTradeGoalCap.setText(percentsAndMoney(csp.getTradeGoalCapPer(), cspp.getTradeGoalCap()));
			jlTradeLossCap.setText(percentsAndMoney(csp.getTradeLossCapPer(), cspp.getTradeLossCap()));
			
			jlNumberOfContracts.setText(Integer.toString(cspp.getNumberOfContracts()));
			jlTakeProfit.setText(points(cspp.getTakeProfitPts()));
			jlStopLoss.setText(points(cspp.getStopLossPts()));
			
			jlAvgDailyPriceMove.setText(points(cspp.getAvgDailyPriceMove()));
			jlAvgLocalPriceMove.setText(points(cspp.getAvgLocalPriceMove()));

		} else {
			jlTradeGoalCap.setText(percents(csp.getTradeGoalCapPer()));
			jlTradeLossCap.setText(percents(csp.getTradeLossCapPer()));
			
			jlNumberOfContracts.setText(NA());
			jlTakeProfit.setText(NA());
			jlStopLoss.setText(NA());
			
			jlAvgDailyPriceMove.setText(NA());
			jlAvgLocalPriceMove.setText(NA());
		}
	}

}
