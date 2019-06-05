package xx.mix.bootes.kinako.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import ru.prolib.bootes.lib.config.OptionProvider;

public class ImapMessageService {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(ImapMessageService.class);
	}
	
	private final OptionProvider options;
	private IMAPStore store;
	private IMAPFolder inbox;
	private MessageListener listener;
	private Worker worker;
	
	public ImapMessageService(OptionProvider options) {
		this.options = options;
	}
	
	public void startup() throws Throwable {
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "imaps");
		props.put("mail.imaps.host", options.getStringNotNull("imap.host", null));
		props.put("mail.imaps.port", options.getStringNotNull("imap.port", null));
		props.put("mail.imaps.timeout", options.getString("imap.timeout", "10000"));
		final String login = options.getStringNotNull("imap.login", null);
		final String password = options.getStringNotNull("imap.password", null);
		final String folder_name = options.getString("imap.folder_name", "INBOX");
		final String worker_name = options.getString("imap.worker_name", "IMAP-SIGNAL_SRC");
		
		Session session = Session.getInstance(props, null);
		store = (IMAPStore) session.getStore("imaps");
		store.connect(login, password);
		if ( ! store.hasCapability("IDLE") ) {
			throw new RuntimeException("IDLE not supported");
		}
		inbox = (IMAPFolder) store.getFolder(folder_name);
		inbox.addMessageCountListener(listener = new MessageListener());
		inbox.open(Folder.READ_ONLY);
		worker = new Worker(inbox, login, password);
		worker.setName(worker_name);
		worker.start();
		logger.debug("MailService started");
	}
	
	public void close() throws Throwable {
		logger.debug("MailService stopping...");
		if ( worker != null ) {
			worker.kill();
			worker = null;
		}
		close(inbox);
		close(store);
		logger.debug("MailService stopped");
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
			logger.debug("MailService worker started");
			while ( running ) {
				try {
					ensureOpen();
					folder.idle();
				} catch ( Exception e ) {
                	logger.error("Unexxpected exception: ", e);
                    try {
						Thread.sleep(100);
					} catch ( InterruptedException e1 ) {
						logger.error("Unexpected exception: ", e1);
						break;
					}
				}
			}
			logger.debug("MailService worker ended");
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
	
	public static class MessageListener extends MessageCountAdapter {
		
		@Override
		public void messagesAdded(MessageCountEvent event) {
			System.out.println("INCOMING EVENT: MESSAGES ADDED");
			try {
				for ( Message msg : event.getMessages() ) {
					System.out.println("-------------------------------------");
					Address[] from_list = msg.getFrom();
					if ( from_list != null ) {
						for ( Address address : from_list ) {
							System.out.println("From: " + address.toString());
						}
					} else {
						System.out.println("From: N/A");
					}
					
					System.out.println(" Subject: " + msg.getSubject());
					System.out.println("    Sent: " + msg.getSentDate());
					System.out.println("Received: " + msg.getReceivedDate());
				}
			} catch ( Exception e ) {
				System.out.println("Unexpected exception: " + e.getMessage());
				e.printStackTrace(System.out);
			}
		}
		
	}
	
	public static void close(Folder folder) {
		try {
			if ( folder != null && folder.isOpen() ) {
				folder.close(false);
			}
		} catch ( Exception e ) {
			logger.warn("Unexpected exception: ", e);
		}
    }

    public static void close(Store store) {
		try {
			if ( store != null && store.isConnected() ) {
				store.close();
			}
		} catch ( Exception e ) {
			logger.warn("Unexpected exception: ", e);
		}
    }

}
