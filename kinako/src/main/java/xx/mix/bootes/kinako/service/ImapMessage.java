package xx.mix.bootes.kinako.service;

import java.time.Instant;

public class ImapMessage {
	private final Instant received;
	private final String sender, subject, body;
	
	public ImapMessage(Instant received, String sender, String subject, String body) {
		this.received = received;
		this.sender = sender;
		this.subject = subject;
		this.body = body;
	}
	
	public Instant getReceived() {
		return received;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public String getBody() {
		return body;
	}

}
