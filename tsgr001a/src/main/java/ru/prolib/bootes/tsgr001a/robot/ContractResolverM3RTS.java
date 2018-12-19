package ru.prolib.bootes.tsgr001a.robot;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class ContractResolverM3RTS implements ContractResolver {
	private static final LocalDate MIN_DATE = LocalDate.of(2005, 1, 1);
	private final ZoneId zoneID;
	private final String symbolName;
	private final int firstMonth, dayOfMonth;
	
	public ContractResolverM3RTS(ZoneId zoneID, String symbolName, int firstMonth, int dayOfMonth) {
		if ( firstMonth < 1 || firstMonth > 3 ) {
			throw new IllegalArgumentException("Start month should be in range between 1 and 3");
		}
		if ( dayOfMonth < 1 || dayOfMonth > 28 ) {
			throw new IllegalArgumentException("Day of month should be in range between 1 and 28");
		}
		this.zoneID = zoneID;
		this.symbolName = symbolName;
		this.firstMonth = firstMonth;
		this.dayOfMonth = dayOfMonth;
	}
	
	public ZoneId getZoneID() {
		return zoneID;
	}
	
	public String getSymbolName() {
		return symbolName;
	}
	
	public int getFirstMonth() {
		return firstMonth;
	}
	
	public int getDayOfMonth() {
		return dayOfMonth;
	}

	@Override
	public ContractParams determineContract(Instant time) {
		ZonedDateTime zdt = time.atZone(zoneID);
		LocalDate ld = zdt.toLocalDate();
		if ( ld.getYear() < 2005 ) {
			ld = MIN_DATE;
		} else {
			LocalTime lt = zdt.toLocalTime();
			if ( lt.compareTo(LocalTime.of(18, 30)) >= 0 ) {
				ld = ld.plusDays(1);
			}
		}
		LocalDate end = LocalDate.of(ld.getYear(), firstMonth, dayOfMonth);
		LocalDate start = null;
		for ( int i = 0; i <= 4; i ++ ) {
			if ( end.compareTo(ld) > 0 ) {
				start = end.minusMonths(3);
				if ( start.compareTo(MIN_DATE) < 0 ) {
					start = MIN_DATE;
				}
				break;
			}
			end = end.plusMonths(3);
		}
		if ( start == null ) {
			throw new IllegalStateException();
		}
		
		String s = String.format("%s-%d.%02d", symbolName,
				end.getMonth().getValue(),
				end.getYear() % 100);
		Interval dataTrackingPeriod = Interval.of(
				ld.atTime( 9, 55).atZone(zoneID).toInstant(),
				ld.atTime(23, 55).atZone(zoneID).toInstant()
			);
		Interval tradeAllowedPeriod = Interval.of(
				ld.atTime(10, 0).atZone(zoneID).toInstant(),
				ld.atTime(18, 30).atZone(zoneID).toInstant()
			);
		return new ContractParams(
				new Symbol(s),
				dataTrackingPeriod,
				tradeAllowedPeriod
			);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ContractResolverM3RTS.class ) {
			return false;
		}
		ContractResolverM3RTS o = (ContractResolverM3RTS) other;
		return new EqualsBuilder()
				.append(o.zoneID, zoneID)
				.append(o.symbolName, symbolName)
				.append(o.firstMonth, firstMonth)
				.append(o.dayOfMonth, dayOfMonth)
				.build();
	}

}
