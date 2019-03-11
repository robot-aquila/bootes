package ru.prolib.bootes.tsgr001a.robot.filter;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.impl.AbstractFilter;

@Deprecated // Use strategy timetable instead
public class SignalTimetable extends AbstractFilter<S3TradeSignal> {
	private final ZoneId zoneID;

	public SignalTimetable(ZoneId zoneID) {
		super("SigTT");
		this.zoneID = zoneID;
	}

	@Override
	public boolean approve(S3TradeSignal signal) {
		ZonedDateTime zoned_date_time = ZonedDateTime.ofInstant(signal.getTime(), zoneID);
		LocalTime local_time = zoned_date_time.toLocalTime();
		LocalTime start_time = LocalTime.of(10, 30);
		return local_time.compareTo(start_time) >= 0;
	}
	
}
