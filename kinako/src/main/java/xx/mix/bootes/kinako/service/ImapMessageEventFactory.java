package xx.mix.bootes.kinako.service;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventType;

public class ImapMessageEventFactory implements EventFactory {
	private final long smtp_time, imap_time;
	private final ImapMessage message;
	
	public ImapMessageEventFactory(long smtp_time, long imap_time, ImapMessage message) {
		this.smtp_time = smtp_time;
		this.imap_time = imap_time;
		this.message = message;
	}

	@Override
	public Event produceEvent(EventType type) {
		return new ImapMessageEvent(type, smtp_time, imap_time, message);
	}

}
