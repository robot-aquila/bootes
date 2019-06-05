package xx.mix.bootes.kinako;

import java.io.File;

import org.ini4j.Wini;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.OptionProvider;
import ru.prolib.bootes.lib.config.OptionProviderKvs;
import ru.prolib.bootes.lib.config.kvstore.KVStoreIni;
import xx.mix.bootes.kinako.service.ImapMessageService;
import xx.mix.bootes.kinako.service.KinakoBotService;
import xx.mix.bootes.kinako.service.KinakoEvent;

public class KINAKORobotComp implements AppComponent {
	private final AppServiceLocator serviceLocator;
	private ImapMessageService messageService;
	private KinakoBotService kinakoService;
	private TelegramBotsApi bots;
	
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
		messageService.onSignalDetected().addListener(new EventListener() {

			@Override
			public void onEvent(Event event) {
				kinakoService.sendNotification((KinakoEvent) event);
			}
			
		});
	}

	@Override
	public void startup() throws Throwable {
		messageService.startup();
	}

	@Override
	public void shutdown() throws Throwable {
		messageService.close();
	}

}
