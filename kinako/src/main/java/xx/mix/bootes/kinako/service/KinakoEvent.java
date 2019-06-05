package xx.mix.bootes.kinako.service;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

public class KinakoEvent extends EventImpl {
	private final long smtpTime, imapTime;
	private final String eventText;
	
	public KinakoEvent(EventType type,
					   long smtp_time,
					   long imap_time,
					   String event_text)
	{
		super(type);
		this.smtpTime = smtp_time;
		this.imapTime = imap_time;
		this.eventText = event_text;
	}
	
	public long getSmtpTime() {
		return smtpTime;
	}
	
	public long getImapTime() {
		return imapTime;
	}
	
	public String getEventText() {
		return eventText;
	}

}
