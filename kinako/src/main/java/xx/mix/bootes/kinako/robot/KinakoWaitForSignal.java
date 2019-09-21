package xx.mix.bootes.kinako.robot;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import xx.mix.bootes.kinako.service.ImapMessage;
import xx.mix.bootes.kinako.service.ImapMessageEvent;
import xx.mix.bootes.kinako.service.VVOrderRecom;
import xx.mix.bootes.kinako.service.VVSignal;
import xx.mix.bootes.kinako.service.VVSignalParseException;

public class KinakoWaitForSignal extends SMStateHandlerEx implements SMInputAction {
	private static final Logger logger;
	private static final String LN = System.lineSeparator();
	public static final String E_SIGNAL_DETECTED = "SIGNAL_DETECTED";
	public static final String E_SIGNAL_REJECTED = "SIGNAL_REJECTED";
	
	static {
		logger = LoggerFactory.getLogger(KinakoWaitForSignal.class);
	}
	
	protected final KinakoRobotServiceLocator kinakoServiceLocator;
	protected final KinakoRobotData robotData;
	protected final SMInput in;
	
	public KinakoWaitForSignal(
			KinakoRobotServiceLocator kinako_service_locator,
			KinakoRobotData robot_data
		)
	{
		this.kinakoServiceLocator = kinako_service_locator;
		this.robotData = robot_data;
		registerExit(E_SIGNAL_DETECTED);
		registerExit(E_SIGNAL_REJECTED);
		in = registerInput(this);
	}
	
	private VVSignal parse(String text, Instant time) throws VVSignalParseException {
		return kinakoServiceLocator.getSignalParser().parse(text, time);
	}
	
	private void sendToChat(String message_text) {
		kinakoServiceLocator.getBotService().sendNotification(message_text);
	}
	
	private StringBuilder appendSourceMessageInfo(StringBuilder sb, ImapMessageEvent event) {
		ImapMessage imap_msg = event.getMessage();
		return sb.append("Source message information: ").append(LN)
			.append("Time: ").append(imap_msg.getReceived()).append(LN)
			.append("From: ").append(imap_msg.getSender()).append(LN)
			.append("Subj: ").append(imap_msg.getSubject()).append(LN)
			.append("Body: ").append(imap_msg.getBody())
			.append("Timing SMTP:").append(event.getSmtpTime()).append(" ")
				   .append("IMAP:").append(event.getImapTime()).append(LN);
	}
	
	private StringBuilder appendSignalInfo(StringBuilder sb, VVSignal signal) {
		for ( VVOrderRecom recom : signal.getRecommendations() ) {
			sb.append(recom).append(LN);
		}
		return sb;
	}
	
	private void sendToChat_ParsingOK(ImapMessageEvent event, VVSignal signal) {
		StringBuilder sb = new StringBuilder()
				.append("Signal acquired, parsed and scheduled for execution:").append(LN);
		appendSignalInfo(sb, signal).append(LN);
		String msg = appendSourceMessageInfo(sb, event).toString();
		sendToChat(msg);
		logger.debug(msg);
	}
	
	private void sendToChat_ParsingError(ImapMessageEvent event, Throwable exception) {
		StringBuilder sb = new StringBuilder()
				.append("Error parsing signal: ").append(exception.getMessage()).append(LN);
		String msg = appendSourceMessageInfo(sb, event).toString();
		sendToChat(msg);
		logger.debug(msg);
	}

	@Override
	public SMExit input(Object data) {
		logger.debug("Incoming data: {}", data);
		ImapMessageEvent event = (ImapMessageEvent) data;
		ImapMessage imap_msg = event.getMessage();
		try {
			VVSignal signal = parse(imap_msg.getBody(), imap_msg.getReceived());
			robotData.setCurrentSignal(signal);
			sendToChat_ParsingOK(event, signal);
			return getExit(E_SIGNAL_DETECTED);
		} catch ( VVSignalParseException e ) {
			sendToChat_ParsingError(event, e);
			return getExit(E_SIGNAL_REJECTED);
		}
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		triggers.add(newTriggerOnEvent(kinakoServiceLocator.getMessageService().onMessage(), in));
		return null;
	}

}
