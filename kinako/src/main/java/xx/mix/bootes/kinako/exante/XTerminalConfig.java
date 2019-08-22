package xx.mix.bootes.kinako.exante;

import java.io.File;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.OptionProvider;

public class XTerminalConfig {
	private final OptionProvider options;
	
	public XTerminalConfig(OptionProvider options) {
		this.options = options;
	}

	public File getSettingFilename() throws ConfigException {
		return options.getFileNotNull(XTerminalConfigSection.LOPT_CONFIG);
	}

}
