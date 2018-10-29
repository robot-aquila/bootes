package ru.prolib.bootes.lib.app.comp;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.service.UIService;

public class UIComp extends CommonComp {
	private static final String DEFAULT_ID = "BASIC-UI";
	
	protected boolean headless;
	protected UIService uis;

	public UIComp(AppConfig appConfig, AppServiceLocator serviceLocator, String serviceID) {
		super(appConfig, serviceLocator, serviceID);
	}
	
	public UIComp(AppConfig appConfig, AppServiceLocator serviceLocator) {
		this(appConfig, serviceLocator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		headless = appConfig.getBasicConfig().isHeadless();
		if ( headless ) {
			return;
		}
		serviceLocator.setUIService(uis = new UIService());
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
				// TODO: pack?
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

}
