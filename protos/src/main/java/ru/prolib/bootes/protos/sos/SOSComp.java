package ru.prolib.bootes.protos.sos;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.OrderDefinitionProvider;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig2;
import ru.prolib.bootes.lib.robo.Robot;
import ru.prolib.bootes.lib.robo.s3.S3CommonReports;
import ru.prolib.bootes.protos.PROTOSRobotState;

public class SOSComp implements AppComponent {	
	public static final String DEFAULT_ID = "sos";
	static final Logger logger = LoggerFactory.getLogger(SOSComp.class);
	
	protected final String id;
	protected final AppServiceLocator serviceLocator;
	protected final OrderDefinitionProvider signalProvider;
	protected Robot<PROTOSRobotState> robot;
	protected S3CommonReports reports;
	private final List<SOSExtension> initExtensions, startupExtensions, shutdownExtensions;
	
	public SOSComp(String id,
			AppServiceLocator service_locator,
			OrderDefinitionProvider signal_provider,
			List<SOSExtension> init_extensions,
			List<SOSExtension> startup_extensions,
			List<SOSExtension> shutdown_extensions)
	{
		this.id = id;
		this.serviceLocator = service_locator;
		this.signalProvider = signal_provider;
		this.initExtensions = init_extensions;
		this.startupExtensions = startup_extensions;
		this.shutdownExtensions = shutdown_extensions;
	}
	
	public S3CommonReports getReports() {
		return reports;
	}
	
	public String getID() {
		return id;
	}
	
	public AppServiceLocator getServiceLocator() {
		return serviceLocator;
	}
	
	public Robot<PROTOSRobotState> getRobot() {
		return robot;
	}
	
	public OrderDefinitionProvider getSignalProvider() {
		return signalProvider;
	}

	@Override
	public void init() throws Throwable {
		reports = new S3CommonReports(serviceLocator);
		robot = new SOSRobotBuilder(serviceLocator).build(id, signalProvider);
		robot.getAutomat().setId(id);
		robot.getAutomat().setDebug(true);
		reports.registerHandlers(robot.getState());
		
		robot.getAutomat().start();
		for ( SOSExtension ext : initExtensions ) {
			ext.apply(this);
		}
	}

	@Override
	public void startup() throws Throwable {
		for ( SOSExtension ext : startupExtensions ) {
			ext.apply(this);
		}
	}

	@Override
	public void shutdown() throws Throwable {
		SMStateMachine automat = robot.getAutomat();
		synchronized ( automat ) {
			if ( ! automat.finished() ) {
				SMStateHandlerEx sh = (SMStateHandlerEx) automat.getCurrentState();
				automat.input(sh.getInterrupt(), null);
			}
		}
		try {
			automat.waitForFinish(30, TimeUnit.SECONDS); // TODO: move timeouts to config
		} catch ( TimeoutException e ) {
			logger.warn("Has no more time to wait to robot finished: ", e);
		}
		AppConfig2 app_conf = serviceLocator.getConfig();
		reports.save(app_conf.getBasicConfig().getReportDirectory(), id + ".report");
		for ( SOSExtension ext : shutdownExtensions ) {
			ext.apply(this);
		}
	}

	@Override
	public void registerConfig(AppConfigService2 config_service) {
		
	}

}
