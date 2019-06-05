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
					.setText(new StringBuilder()
						.append("Notification test. ")
						.append("Send test message to a secret email ")
						.append("with subject starting with: ")
						.append("Cicero says YOUR TEXT HERE ")
						.append("and you'll get message in a secret chat.")
						.toString());
			try {
				execute(msg);
			} catch ( TelegramApiException e ) {
				logger.error("Unexpected exception: ", e);
			}
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
	
	public void sendNotification(KinakoEvent event) {
		long curr = System.currentTimeMillis();
		SendMessage msg = new SendMessage()
				.setChatId(botChatId)
				.setText(event.getEventText());
		try {
			execute(msg);
		} catch ( TelegramApiException e ) {
			logger.error("Unexpected exception: ", e);
		}
		long tg_time = System.currentTimeMillis() - curr;
		msg = new SendMessage()
				.setChatId(botChatId)
				.setText(new StringBuilder()
					.append("Timing ")
					.append("SMTP:").append(event.getSmtpTime()).append(" ")
					.append("IMAP:").append(event.getImapTime()).append(" ")
					.append("TG:").append(tg_time)
					.toString());
		try {
			execute(msg);
		} catch ( TelegramApiException e ) {
			logger.error("Unexpected exception: ", e);
		}
	}

}
