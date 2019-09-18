package xx.mix.bootes.kinako.service;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.config.OptionProvider;

public class ImapMessageService {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(ImapMessageService.class);
	}
	
	private final EventQueue queue;
	private final OptionProvider options;
	private final EventType onMessage;
	private final ImapMessageChecker messageChecker;
	private IMAPStore store;
	private IMAPFolder inbox;
	private Worker worker;
	
	public ImapMessageService(
			EventQueue queue,
			OptionProvider options,
			ImapMessageChecker message_checker)
	{
		this.queue = queue;
		this.options = options;
		this.messageChecker = message_checker;
		this.onMessage = new EventTypeImpl("MESSAGE");
	}
	
	public ImapMessageService(EventQueue queue, OptionProvider options) {
		this(queue, options, null);
	}
	
	public EventType onMessage() {
		return onMessage;
	}
	
	public void startup() throws Throwable {
		logger.debug("Service starting...");
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "imaps");
		props.put("mail.imaps.host", options.getStringNotNull("imap.host", null));
		props.put("mail.imaps.port", options.getStringNotNull("imap.port", null));
		props.put("mail.imaps.timeout", options.getString("imap.timeout", "10000"));
		final String login = options.getStringNotNull("imap.login", null);
		final String password = options.getStringNotNull("imap.password", null);
		final String folder_name = options.getString("imap.folder_name", "INBOX");
		final String worker_name = options.getString("imap.worker_name", "IMAP-SIGNAL_SRC");
		ImapMessageChecker msg_check = messageChecker;
		if ( msg_check == null ) {
			msg_check = new ImapMessageCheckByPattern(
					options.getString("imap.msg_sender_pattern"),
					options.getString("imap.msg_subject_pattern"),
					options.getString("imap.msg_body_pattern")
				);
		}
		
		Session session = Session.getInstance(props, null);
		store = (IMAPStore) session.getStore("imaps");
		store.connect(login, password);
		if ( ! store.hasCapability("IDLE") ) {
			throw new RuntimeException("IDLE not supported");
		}
		inbox = (IMAPFolder) store.getFolder(folder_name);
		inbox.addMessageCountListener(new MessageProcessor(msg_check, queue, onMessage));
		inbox.open(Folder.READ_ONLY);
		worker = new Worker(inbox, login, password);
		worker.setName(worker_name);
		worker.start();
		logger.debug("Service started");
	}
	
	public void close() throws Throwable {
		logger.debug("Service stopping...");
		if ( worker != null ) {
			worker.kill();
			worker = null;
		}
		close(inbox);
		close(store);
		logger.debug("Service stopped");
	}
	
	public static class Worker extends Thread {
		private final IMAPFolder folder;
		private final String login, password;
		private volatile boolean running = true;
		
		public Worker(IMAPFolder folder,
					  String login,
					  String password)
		{
			this.folder = folder;
			this.login = login;
			this.password = password;
		}
		
		public synchronized void kill() {
			if ( ! running ) {
				return;
			}
			running = false;
		}
		
		@Override
		public void run() {
			logger.debug("Worker started");
			while ( running ) {
				try {
					ensureOpen();
					folder.idle();
				} catch ( Exception e ) {
                	logger.error("Unexpected exception: ", e);
                    try {
						Thread.sleep(100);
					} catch ( InterruptedException e1 ) {
						logger.error("Unexpected exception: ", e1);
						break;
					}
				}
			}
			logger.debug("Worker ended");
        }
		
		public void ensureOpen() throws MessagingException {
			Store store = folder.getStore();
			if ( store != null && ! store.isConnected() ) {
				logger.debug("(Re)connecting...");
				store.connect(login, password);
			}

			if ( folder.exists() && ! folder.isOpen()
			  && ( folder.getType() & Folder.HOLDS_MESSAGES ) != 0 )
			{
				logger.debug("Opening folder " + folder.getFullName());
				folder.open(Folder.READ_ONLY);
				if ( ! folder.isOpen() )
			        throw new MessagingException("Unable to open folder " + folder.getFullName());
			}
	    }
		
	}
	
	public static class MessageProcessor implements MessageCountListener {
		private final ImapMessageChecker messageChecker;
		private final EventQueue queue;
		private final EventType onMessage;
		
		public MessageProcessor(
				ImapMessageChecker message_checker,
				EventQueue queue,
				EventType on_message)
		{
			this.messageChecker = message_checker;
			this.queue = queue;
			this.onMessage = on_message;
		}
		
		@Override
		public void messagesAdded(MessageCountEvent event) {
			Instant curr_time = Instant.now();
			long curr = curr_time.toEpochMilli();
			try {
				for ( Message msg : event.getMessages() ) {
					String sender = getSender(msg), subj = msg.getSubject(), body = getTextFromMessage(msg);
					Date sent = msg.getSentDate(), recv = msg.getReceivedDate();
					long smtp_time = recv.getTime() - sent.getTime();
					long imap_time = curr - recv.getTime();
					ImapMessage imap_msg = new ImapMessage(curr_time, sender, subj, body);
					if ( messageChecker.approve(imap_msg) ) {
						queue.enqueue(onMessage, new ImapMessageEventFactory(smtp_time, imap_time, imap_msg));
					}
				}
			} catch ( Exception e ) {
				logger.error("Unexpected exception: ", e);
			}
		}

		@Override
		public void messagesRemoved(MessageCountEvent e) {
			
		}

	}
	
	private static void close(Folder folder) {
		try {
			if ( folder != null && folder.isOpen() ) {
				folder.close(false);
			}
		} catch ( Exception e ) {
			logger.warn("Unexpected exception: ", e);
		}
    }

    private static void close(Store store) {
		try {
			if ( store != null && store.isConnected() ) {
				store.close();
			}
		} catch ( Exception e ) {
			logger.warn("Unexpected exception: ", e);
		}
    }
	
	private static String getSender(Message msg) throws MessagingException {
		Address[] from_list = msg.getFrom();
		return from_list != null && from_list.length > 0 ? from_list[0].toString() : ""; 
	}
	
	private static String getTextFromMessage(Message message)
			throws MessagingException, IOException
	{
		String result = "";
		if ( message.isMimeType("text/plain") ) {
			result = message.getContent().toString();
		} else if ( message.isMimeType("multipart/*") ) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			result = getTextFromMimeMultipart(mimeMultipart);
		}
		return result;
	}

	private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart)
			throws MessagingException, IOException
	{
		String result = "";
		int count = mimeMultipart.getCount();
		for ( int i = 0; i < count; i++ ) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if ( bodyPart.isMimeType("text/plain") ) {
				result = result + "\n" + bodyPart.getContent();
				break; // without break same text appears twice in my tests
			} else if ( bodyPart.isMimeType("text/html") ) {
				String html = (String) bodyPart.getContent();
				result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
			} else if ( bodyPart.getContent() instanceof MimeMultipart ) {
				result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
			}
		}
		return result;
	}

}
