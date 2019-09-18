package xx.mix.bootes.kinako.service;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

public class ImapMessageEvent extends EventImpl {
	private final long smtpTime, imapTime;
	private final ImapMessage message;
	
	public ImapMessageEvent(EventType type,
					   long smtp_time,
					   long imap_time,
					   ImapMessage message)
	{
		super(type);
		this.smtpTime = smtp_time;
		this.imapTime = imap_time;
		this.message = message;
	}
	
	public long getSmtpTime() {
		return smtpTime;
	}
	
	public long getImapTime() {
		return imapTime;
	}
	
	public ImapMessage getMessage() {
		return message;
	}

}
