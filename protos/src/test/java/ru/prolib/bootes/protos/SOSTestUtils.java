package ru.prolib.bootes.protos;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.qforts.impl.QFPortfolioField;
import ru.prolib.aquila.qforts.impl.QFPositionField;
import ru.prolib.bootes.protos.sos.SOSExtension;
import ru.prolib.bootes.protos.sos.SOSExtensionChain;

public class SOSTestUtils {
	static final ZoneId ZONE_ID = ZoneId.of("Europe/Moscow");

	public static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	public static Instant ZT(String time_string_msk) {
		return LocalDateTime.parse(time_string_msk).atZone(ZONE_ID).toInstant();
	}
	
	public static String ZT(Instant time) {
		return ZonedDateTime.ofInstant(time, ZONE_ID).toLocalDateTime().toString();
	}
	
	public static String[] args(String... args) {
		List<String> arg_list = new ArrayList<>(Arrays.asList(args));
		return arg_list.toArray(new String[0]);
	}
	
	/**
	 * Schedule extension execution at specific time.
	 * <p>
	 * @param time - time to execute at
	 * @param ext - extension to execute
	 * @return instance to register as extension
	 */
	public static SOSExtension at(Instant time, SOSExtension ext) {
		return (c) -> { c.getServiceLocator().getScheduler().schedule(() -> { ext.apply(c); }, time); };
	}
	
	public static SOSExtension at(String time_string_msk, SOSExtension ext) {
		return at(ZT(time_string_msk), ext);
	}
	
	public static SOSExtensionChain testPortfolioProperty(Object expected,
			int property_token,
			String property_id,
			Counter score)
	{
		return new SOSExtensionChain((c) -> {
			assertEquals(new StringBuilder()
				.append("Unexpected portfolio ")
				.append(property_id)
				.append(" at: ")
				.append(ZT(c.getServiceLocator().getScheduler().getCurrentTime()))
				.toString(),
				expected,
				c.getRobot().getState().getPortfolio().getObject(property_token));
			score.increment();
		});
	}
	
	public static SOSExtensionChain testPositionProperty(Object expected,
			int property_token,
			String property_id,
			Counter score)
	{
		return new SOSExtensionChain((c) -> {
			assertEquals(new StringBuilder()
				.append("Unexpected position ")
				.append(property_id)
				.append(" at: ")
				.append(ZT(c.getServiceLocator().getScheduler().getCurrentTime()))
				.toString(),
				expected,
				c.getRobot().getState().getPortfolio()
					.getPosition(c.getRobot().getState().getSecurity().getSymbol())
					.getObject(property_token));
			score.increment();
		});
	}
	
	public static SOSExtensionChain testPositionObjectNotExists(Counter score) {
		return new SOSExtensionChain((c) -> {
			assertFalse(new StringBuilder()
				.append("Position expected to be not exists at: ")
				.append(ZT(c.getServiceLocator().getScheduler().getCurrentTime()))
				.toString(),
				c.getRobot().getState().getPortfolio()
					.isPositionExists(c.getRobot().getState().getSecurity().getSymbol()));
			score.increment();
		});
	}
	
	public static SOSExtensionChain testPortfolioBalance(CDecimal expected, Counter score) {
		return testPortfolioProperty(expected, PortfolioField.BALANCE, "balance", score);
	}
	
	public static SOSExtensionChain testPortfolioEquity(CDecimal expected_equity, Counter score) {
		return testPortfolioProperty(expected_equity, PortfolioField.EQUITY, "equity", score);
	}
	
	public static SOSExtensionChain testPortfolioFreeMargin(CDecimal expected, Counter score) {
		return testPortfolioProperty(expected, PortfolioField.FREE_MARGIN, "free_margin", score);
	}
	
	public static SOSExtensionChain testPortfolioUsedMargin(CDecimal expected, Counter score) {
		return testPortfolioProperty(expected, PortfolioField.USED_MARGIN, "used_margin", score);
	}
	
	public static SOSExtensionChain testPortfolioPnL(CDecimal expected_pl, Counter score) {
		return testPortfolioProperty(expected_pl, PortfolioField.PROFIT_AND_LOSS, "profit&loss", score);
	}
	
	public static SOSExtensionChain testPortfolioVarMargin(CDecimal expected, Counter score) {
		return testPortfolioProperty(expected, QFPortfolioField.QF_VAR_MARGIN, "var_margin", score);
	}
	
	public static SOSExtensionChain testPortfolioVarMarginInter(CDecimal expected, Counter score) {
		return testPortfolioProperty(expected, QFPortfolioField.QF_VAR_MARGIN_INTER, "var_margin_inter", score);
	}
	
	public static SOSExtensionChain testPortfolioVarMarginClose(CDecimal expected, Counter score) {
		return testPortfolioProperty(expected, QFPortfolioField.QF_VAR_MARGIN_CLOSE, "var_margin_close", score);
	}
	
	public static SOSExtensionChain testPositionCurrentVolume(CDecimal expected_volume, Counter score) {
		return testPositionProperty(expected_volume, PositionField.CURRENT_VOLUME, "current_volume", score);
	}
	
	public static SOSExtensionChain testPositionOpenPrice(CDecimal expected, Counter score) {
		return testPositionProperty(expected, PositionField.OPEN_PRICE, "open_price", score);
	}
	
	public static SOSExtensionChain testPositionCurrentPrice(CDecimal expected, Counter score) {
		return testPositionProperty(expected, PositionField.CURRENT_PRICE, "current_price", score);
	}
	
	public static SOSExtensionChain testPositionUsedMargin(CDecimal expected, Counter score) {
		return testPositionProperty(expected, PositionField.USED_MARGIN, "used_margin", score);
	}
	
	public static SOSExtensionChain testPositionPnL(CDecimal expected, Counter score) {
		return testPositionProperty(expected, PositionField.PROFIT_AND_LOSS, "profit&loss", score);
	}
	
	public static SOSExtensionChain testPositionVarMargin(CDecimal expected, Counter score) {
		return testPositionProperty(expected, QFPositionField.QF_VAR_MARGIN, "var_margin", score);
	}
	
	public static SOSExtensionChain testPositionVarMarginInter(CDecimal expected, Counter score) {
		return testPositionProperty(expected, QFPositionField.QF_VAR_MARGIN_INTER, "var_margin_inter", score);
	}
	
	public static SOSExtensionChain testPositionVarMarginClose(CDecimal expected, Counter score) {
		return testPositionProperty(expected, QFPositionField.QF_VAR_MARGIN_CLOSE, "var_margin_close", score);
	}
	
	public static SOSExtensionChain testPositionTickValue(CDecimal expected, Counter score) {
		return testPositionProperty(expected, QFPositionField.QF_TICK_VALUE, "tick_value", score);
	}
	
}
