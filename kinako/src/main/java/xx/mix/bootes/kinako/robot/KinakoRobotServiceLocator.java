package xx.mix.bootes.kinako.robot;

import xx.mix.bootes.kinako.service.ImapMessageService;
import xx.mix.bootes.kinako.service.KinakoBotService;
import xx.mix.bootes.kinako.service.VVSignalParser;

public class KinakoRobotServiceLocator {
	private ImapMessageService messageService;
	private KinakoBotService botService;
	private VVSignalParser signalParser;
	
	public ImapMessageService getMessageService() {
		if ( messageService == null ) {
			throw new NullPointerException();
		}
		return messageService;
	}
	
	public KinakoBotService getBotService() {
		if ( botService == null ) {
			throw new NullPointerException();
		}
		return botService;
	}
	
	public VVSignalParser getSignalParser() {
		if ( signalParser == null ) {
			throw new NullPointerException();
		}
		return signalParser;
	}
	
	public void setMessageService(ImapMessageService service) {
		this.messageService = service;
	}
	
	public void setBotService(KinakoBotService service) {
		this.botService = service;
	}
	
	public void setSignalParser(VVSignalParser service) {
		this.signalParser = service;
	}

}
