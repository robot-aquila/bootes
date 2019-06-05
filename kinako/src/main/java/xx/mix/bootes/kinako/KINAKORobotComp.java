package xx.mix.bootes.kinako;

import java.io.File;

import org.ini4j.Wini;

import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.OptionProvider;
import ru.prolib.bootes.lib.config.OptionProviderKvs;
import ru.prolib.bootes.lib.config.kvstore.KVStoreIni;
import xx.mix.bootes.kinako.service.ImapMessageService;

public class KINAKORobotComp implements AppComponent {
	private final AppServiceLocator serviceLocator;
	private ImapMessageService messageService;
	
	public KINAKORobotComp(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	@Override
	public void init() throws Throwable {
		messageService = new ImapMessageService(new OptionProviderKvs(
				new KVStoreIni(new Wini(new File("kinako.ini")).get("kinako"))
			));
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
