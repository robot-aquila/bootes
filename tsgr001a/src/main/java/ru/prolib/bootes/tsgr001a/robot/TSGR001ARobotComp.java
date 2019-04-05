package ru.prolib.bootes.tsgr001a.robot;

import java.io.File;

import org.apache.commons.io.FileUtils;

import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.report.ReportPrinter;
import ru.prolib.bootes.tsgr001a.robot.report.BlockReportHandler;
import ru.prolib.bootes.tsgr001a.robot.report.EquityCurveReportHandler;
import ru.prolib.bootes.tsgr001a.robot.report.S3ReportHandler;
import ru.prolib.bootes.tsgr001a.robot.report.SummaryReportDumpAtShutdown;
import ru.prolib.bootes.tsgr001a.robot.report.SummaryReportHandler;
import ru.prolib.bootes.tsgr001a.robot.sh.CommonHandler;
import ru.prolib.bootes.tsgr001a.robot.ui.RobotUIService;

public class TSGR001ARobotComp implements AppComponent {
	private final AppConfig appConfig;
	private final AppServiceLocator serviceLocator;
	private RoboServiceLocator roboServices;
	private Robot robot;
	
	public TSGR001ARobotComp(AppConfig appConfig, AppServiceLocator serviceLocator) {
		this.appConfig = appConfig;
		this.serviceLocator = serviceLocator;
	}

	@Override
	public void init() throws Throwable {
		roboServices = new RoboServiceLocator(serviceLocator);
		RobotStateListenerComp stateListener = new RobotStateListenerComp();
		robot = new TSGR001ARobotBuilder(serviceLocator, roboServices)
				.build(stateListener, appConfig.getBasicConfig().isNoOrders());
		RobotState state = robot.getState();
		stateListener.addListener(new SummaryReportHandler(state, roboServices.getSummaryReportTracker()));
		stateListener.addListener(new SummaryReportDumpAtShutdown(roboServices.getSummaryReportTracker()));
		stateListener.addListener(new S3ReportHandler(state, roboServices.getTradesReport(), true));
		stateListener.addListener(new S3ReportHandler(state, roboServices.getShortDurationTradesReport()));
		stateListener.addListener(new S3ReportHandler(state, roboServices.getMidDayClearingTradesReport()));
		stateListener.addListener(new EquityCurveReportHandler(state, roboServices.getEquityCurveReportS(), true));
		stateListener.addListener(new EquityCurveReportHandler(state, roboServices.getEquityCurveReportL()));
		if ( ! appConfig.getBasicConfig().isHeadless() ) {
			stateListener.addListener(new BlockReportHandler(state, roboServices.getBlockReportStorage()));
			stateListener.addListener(new RobotUIService(serviceLocator, roboServices, state));
		}
		robot.getAutomat().start();
	}

	@Override
	public void startup() throws Throwable {
		// Do not start automat here.
		// All triggers should be registered prior to terminal start.
	}

	@Override
	public void shutdown() throws Throwable {
		SMStateMachine automat = robot.getAutomat();
		synchronized ( automat ) {
			if ( ! automat.finished() ) {
				CommonHandler sh = (CommonHandler) automat.getCurrentState();
				automat.input(sh.getInputOfInterruption(), null);
			}
		}
		// TODO: Here! Wait for automat finished work.

		// Save report
		File report_dir = appConfig.getBasicConfig().getReportsDirectory(); 
		FileUtils.forceMkdir(report_dir);
		File report_file = new File(report_dir, new StringBuilder()
				.append("tsgr001a-")
				.append(robot.getState().getContractName())
				.append(".report")
				.toString());  
		new ReportPrinter(serviceLocator.getZoneID())
			.add(roboServices.getSummaryReportTracker().getCurrentStats(), "Summary")
			.add(roboServices.getTradesReport(), "Trades")
			.save(report_file);
	}

}
