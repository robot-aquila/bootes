package xx.mix.bootes.kinako.robot;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import xx.mix.bootes.kinako.service.ImapMessageService;
import xx.mix.bootes.kinako.service.KinakoBotService;
import xx.mix.bootes.kinako.service.VVSignalParser;

public class KinakoRobotServiceLocatorTest {
	private IMocksControl control;
	private KinakoRobotServiceLocator service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service = new KinakoRobotServiceLocator();
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetMessageService_ThrowsIfNotDefined() {
		service.getMessageService();
	}
	
	@Test
	public void testGetMessageService() {
		ImapMessageService imsMock = control.createMock(ImapMessageService.class);
		service.setMessageService(imsMock);
		
		assertSame(imsMock, service.getMessageService());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetBotService_ThrowsIfNotDefined() {
		service.getBotService();
	}
	
	@Test
	public void testGetBotService() {
		KinakoBotService botMock = control.createMock(KinakoBotService.class);
		service.setBotService(botMock);
		
		assertSame(botMock, service.getBotService());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetSignalParser_ThrowsIfNotDefined() {
		service.getSignalParser();
	}

	@Test
	public void testGetSignalParser() {
		VVSignalParser parserMock = control.createMock(VVSignalParser.class);
		service.setSignalParser(parserMock);
		
		assertSame(parserMock, service.getSignalParser());
	}

}
