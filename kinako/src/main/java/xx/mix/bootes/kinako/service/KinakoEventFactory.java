package xx.mix.bootes.kinako.service;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventType;

public class KinakoEventFactory implements EventFactory {
	private final long smtp_time, imap_time;
	private final String text;
	
	public KinakoEventFactory(long smtp_time, long imap_time, String text) {
		this.smtp_time = smtp_time;
		this.imap_time = imap_time;
		this.text = text;
	}

	@Override
	public Event produceEvent(EventType type) {
		return new KinakoEvent(type, smtp_time, imap_time, text);
	}

}
