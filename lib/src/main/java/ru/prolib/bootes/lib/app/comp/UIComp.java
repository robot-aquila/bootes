package ru.prolib.bootes.lib.app.comp;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig2;
import ru.prolib.bootes.lib.service.UIService;

public class UIComp extends CommonComp {
	private static final String DEFAULT_ID = "BASIC-UI";
	
	protected boolean headless;
	protected UIService uis;

	public UIComp(AppServiceLocator serviceLocator, String serviceID) {
		super(serviceLocator, serviceID);
	}
	
	public UIComp(AppServiceLocator serviceLocator) {
		this(serviceLocator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		AppConfig2 app_conf = serviceLocator.getConfig();
		headless = app_conf.getBasicConfig().isHeadless();
		if ( headless ) {
			return;
		}
		serviceLocator.setUIService(uis = new UIService(serviceLocator.getZoneID()));
		uis.getFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				serviceLocator.getRuntimeService().shutdown();
			}
		});
	}

	@Override
	public void startup() throws Throwable {
		if ( headless ) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				uis.getFrame().pack();
				uis.getFrame().setVisible(true);
			}
		});
	}

	@Override
	public void shutdown() throws Throwable {
		if ( headless ) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				uis.getFrame().setVisible(false);
				uis.getFrame().dispose();
			}
		});
	}

	@Override
	public void registerConfig(AppConfigService2 config_service) {
		
	}

}
