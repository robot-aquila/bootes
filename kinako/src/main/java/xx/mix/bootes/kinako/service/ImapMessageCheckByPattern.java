package xx.mix.bootes.kinako.service;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImapMessageCheckByPattern implements ImapMessageChecker {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(ImapMessageCheckByPattern.class);
	}
	
	static Pattern patternize(String pattern) {
		if ( pattern == null || pattern.length() == 0 ) {
			return null;
		}
		"X".matches(pattern);
		return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
	}
	
	private final Pattern senderPattern, subjectPattern, bodyPattern;
	
	public ImapMessageCheckByPattern(String sender_pattern, String subject_pattern, String body_pattern) {
		this.senderPattern = patternize(sender_pattern);
		this.subjectPattern = patternize(subject_pattern);
		this.bodyPattern = patternize(body_pattern);
	}

	@Override
	public boolean approve(ImapMessage message) {
		if ( senderPattern != null && ! senderPattern.matcher(message.getSender()).find() ) {
			logger.debug("Sender mismatch: {}", message.getSender());
			return false;
		}
		if ( subjectPattern != null &&  ! subjectPattern.matcher(message.getSubject()).find() ) {
			logger.debug("Subject mismatch: {}", message.getSubject());
			return false;
		}
		if ( bodyPattern != null && ! bodyPattern.matcher(message.getBody()).find() ) {
			logger.debug("Body mismatch: {}", message.getBody());
			return false;
		}
		return true;
	}

}
