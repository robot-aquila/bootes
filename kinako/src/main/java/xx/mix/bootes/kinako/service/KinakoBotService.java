package xx.mix.bootes.kinako.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class KinakoBotService extends TelegramLongPollingBot {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(KinakoBotService.class);
	}
	
	private final String botUsername, botToken, botChatId;
	
	public KinakoBotService(String botUsername,
							String botToken,
							String botChatId)
	{
		this.botUsername = botUsername;
		this.botToken = botToken;
		this.botChatId = botChatId;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if ( update.hasMessage() && update.getMessage().hasText() ) {
			SendMessage msg = new SendMessage()
					.setChatId(update.getMessage().getChatId())
					.setText("Notification test. ");
			send(msg);
		}
	}

	@Override
	public String getBotUsername() {
		return botUsername;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}
	
	/**
	 * Send message to bot chat.
	 * <p>
	 * @param msg - message
	 * @return time used to send messages in milliseconds
	 */
	private long send(SendMessage msg) {
		long curr = System.currentTimeMillis();
		try {
			execute(msg);
		} catch ( TelegramApiException e ) {
			logger.error("Unexpected exception: ", e);
		}
		long tg_time = System.currentTimeMillis() - curr;
		return tg_time;
	}
	
	/**
	 * Send text message to bot chat.
	 * <p>
	 * @param text - message text
	 * @return time used to send message in milliseconds
	 */
	private long send(String text) {
		return send(new SendMessage().setChatId(botChatId).setText(text));
	}
	
	private String formatMessage(ImapMessage imap_msg) {
		return new StringBuilder()
				.append("Time: ").append(imap_msg.getReceived()).append(System.lineSeparator())
				.append("From: ").append(imap_msg.getSender()).append(System.lineSeparator())
				.append("Subj: ").append(imap_msg.getSubject()).append(System.lineSeparator())
				.append("Body: ").append(imap_msg.getBody())
				.toString();
	}
	
	private String formatTimingMessage(ImapMessageEvent event, long tg_time) {
		return new StringBuilder()
				.append("Timing ")
				.append("SMTP:").append(event.getSmtpTime()).append(" ")
				.append("IMAP:").append(event.getImapTime()).append(" ")
				.append("TG:").append(tg_time)
				.toString();
	}
	
	private String formatTimingMessage(long tg_time) {
		return new StringBuilder().append("Timing TG:").append(tg_time).toString();
	}
	
	public void sendNotification(ImapMessageEvent event) {
		long tg_time = send(formatMessage(event.getMessage()));
		send(formatTimingMessage(event, tg_time));
	}
	
	public void sendNotification(String message_text) {
		long tg_time = send(message_text);
		send(formatTimingMessage(tg_time));
	}

}
