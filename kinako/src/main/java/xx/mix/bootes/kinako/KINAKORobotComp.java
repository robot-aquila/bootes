package xx.mix.bootes.kinako;

import java.io.File;

import org.ini4j.Wini;
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
import ru.prolib.bootes.lib.service.UIService;
import xx.mix.bootes.kinako.service.ImapMessageService;
import xx.mix.bootes.kinako.service.KinakoBotService;
import xx.mix.bootes.kinako.service.ImapMessageEvent;

public class KINAKORobotComp implements AppComponent {
	private final AppServiceLocator serviceLocator;
	private ImapMessageService messageService;
	private KinakoBotService kinakoService;
	private TelegramBotsApi bots;
	private FastOrderPanel orderPanel;
	
	public KINAKORobotComp(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	@Override
	public void init() throws Throwable {
		OptionProvider options = new OptionProviderKvs(
				new KVStoreIni(new Wini(new File("kinako.ini")).get("kinako"))
			);
		ApiContextInitializer.init();
		bots = new TelegramBotsApi();
		bots.registerBot(kinakoService = new KinakoBotService(
				options.getStringNotNull("tg.bot_username", null),
				options.getStringNotNull("tg.bot_token", null),
				options.getStringNotNull("tg.bot_chat_id", null)
			));
		messageService = new ImapMessageService(
				serviceLocator.getEventQueue(),
				options
			);
		messageService.onMessage().addListener(new EventListener() {

			@Override
			public void onEvent(Event event) {
				kinakoService.sendNotification((ImapMessageEvent) event);
			}
			
		});
		
		if ( ! serviceLocator.getConfig().getBasicConfig().isHeadless() ) {
			UIService uis = serviceLocator.getUIService();
			orderPanel = new FastOrderPanel(
					serviceLocator.getTerminal(),
					new SecurityListDialog(uis.getFrame(), SecurityListDialog.TYPE_SELECT, uis.getMessages())
				);
			uis.getTopPanel().add(orderPanel);
		}
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
