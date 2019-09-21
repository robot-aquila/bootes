package xx.mix.bootes.kinako.robot;

import xx.mix.bootes.kinako.service.ImapMessageService;
import xx.mix.bootes.kinako.service.KinakoBotService;
import xx.mix.bootes.kinako.service.VVSignalParser;

public class KinakoRobotServiceLocator {
	private ImapMessageService messageService;
	private KinakoBotService botService;
	private VVSignalParser signalParser;
	
	public synchronized ImapMessageService getMessageService() {
		if ( messageService == null ) {
			throw new NullPointerException();
		}
		return messageService;
	}
	
	public synchronized KinakoBotService getBotService() {
		if ( botService == null ) {
			throw new NullPointerException();
		}
		return botService;
	}
	
	public synchronized VVSignalParser getSignalParser() {
		if ( signalParser == null ) {
			throw new NullPointerException();
		}
		return signalParser;
	}
	
	public synchronized void setMessageService(ImapMessageService service) {
		this.messageService = service;
	}
	
	public synchronized void setBotService(KinakoBotService service) {
		this.botService = service;
	}
	
	public synchronized void setSignalParser(VVSignalParser service) {
		this.signalParser = service;
	}

}
