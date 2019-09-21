package xx.mix.bootes.kinako;

import java.io.File;

import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.config.KVStoreIni;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.aquila.core.config.OptionProviderKvs;
import ru.prolib.aquila.ui.FastOrder.FastOrderPanel;
import ru.prolib.aquila.ui.form.SecurityListDialog;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.Robot;
import ru.prolib.bootes.lib.service.UIService;
import xx.mix.bootes.kinako.service.ImapMessage;
import xx.mix.bootes.kinako.service.ImapMessageEvent;
import xx.mix.bootes.kinako.service.ImapMessageService;
import xx.mix.bootes.kinako.service.KinakoBotService;
import xx.mix.bootes.kinako.service.VVSignalParser;
import xx.mix.bootes.kinako.robot.KinakoRobotBuilder;
import xx.mix.bootes.kinako.robot.KinakoRobotData;
import xx.mix.bootes.kinako.robot.KinakoRobotServiceLocator;

public class KINAKORobotComp implements AppComponent {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(KINAKORobotComp.class);
	}
	
	private final AppServiceLocator serviceLocator;
	private final KinakoRobotServiceLocator kinakoServiceLocator;
	private ImapMessageService messageService;
	private KinakoBotService botService;
	private TelegramBotsApi bots;
	private FastOrderPanel orderPanel;
	private Robot<KinakoRobotData> robot;
	
	public KINAKORobotComp(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
		this.kinakoServiceLocator = new KinakoRobotServiceLocator();
	}

	@Override
	public void init() throws Throwable {
		OptionProvider options = new OptionProviderKvs(
				new KVStoreIni(new Wini(new File("kinako.ini")).get("kinako"))
			);
		ApiContextInitializer.init();
		bots = new TelegramBotsApi();
		bots.registerBot(botService = new KinakoBotService(
				options.getStringNotNull("tg.bot_username", null),
				options.getStringNotNull("tg.bot_token", null),
				options.getStringNotNull("tg.bot_chat_id", null)
			));
		messageService = new ImapMessageService(serviceLocator.getEventQueue(), options);

		kinakoServiceLocator.setSignalParser(new VVSignalParser());
		kinakoServiceLocator.setBotService(botService);
		kinakoServiceLocator.setMessageService(messageService);
		
		messageService.onMessage().addListener(new EventListener() {

			@Override
			public void onEvent(Event e) {
				//ImapMessageEvent event = (ImapMessageEvent) e;
				//ImapMessage imap_msg = event.getMessage();
				//Object args[] = {
				//		imap_msg.getReceived(),
				//		imap_msg.getSender(),
				//		imap_msg.getSubject(),
				//		imap_msg.getBody()
				//};
				//logger.debug("message dispatched: time={} sender={} subject={} body={}", args);
			}
			
		});
		
		robot = new KinakoRobotBuilder(serviceLocator, kinakoServiceLocator).build();
		//robot.getAutomat().setId("PROTOS");
		//robot.getAutomat().setDebug(true);
		
		if ( ! serviceLocator.getConfig().getBasicConfig().isHeadless() ) {
			UIService uis = serviceLocator.getUIService();
			orderPanel = new FastOrderPanel(
					serviceLocator.getTerminal(),
					new SecurityListDialog(uis.getFrame(), SecurityListDialog.TYPE_SELECT, uis.getMessages())
				);
			uis.getTopPanel().add(orderPanel);
		}
		
		robot.getAutomat().start();
	}

	@Override
	public void startup() throws Throwable {
		messageService.startup();
		if ( orderPanel != null ) {
			orderPanel.start();
		}
	}

	@Override
	public void shutdown() throws Throwable {
		if ( orderPanel != null ) {
			orderPanel.stop();
			orderPanel = null;
		}
		messageService.close();
	}

	@Override
	public void registerConfig(AppConfigService2 config_service) {
		
	}

}
