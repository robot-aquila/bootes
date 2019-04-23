package ru.prolib.bootes.tsgr001a.robot;

import java.io.File;

import org.apache.commons.io.FileUtils;

import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.report.ReportPrinter;
import ru.prolib.bootes.lib.robo.rh.EquityCurveReportHandler;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListenerComp;
import ru.prolib.bootes.lib.robo.s3.rh.S3BlockReportHandler;
import ru.prolib.bootes.lib.robo.s3.rh.S3ReportHandler;
import ru.prolib.bootes.lib.robo.s3.rh.S3SummaryReportHandler;
import ru.prolib.bootes.tsgr001a.robot.report.SummaryReportDumpAtShutdown;
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
		robot = new TSGR001ARobotBuilder(serviceLocator, roboServices)
				.build(appConfig.getBasicConfig().isNoOrders());
		RobotState state = robot.getState();
		S3RobotStateListenerComp stateListener = state.getStateListener();
		stateListener.addListener(new S3SummaryReportHandler(state, roboServices.getSummaryReportTracker()));
		stateListener.addListener(new SummaryReportDumpAtShutdown(roboServices.getSummaryReportTracker()));
		stateListener.addListener(new S3ReportHandler(state, roboServices.getTradesReport(), true));
		stateListener.addListener(new S3ReportHandler(state, roboServices.getShortDurationTradesReport()));
		stateListener.addListener(new S3ReportHandler(state, roboServices.getMidDayClearingTradesReport()));
		stateListener.addListener(new EquityCurveReportHandler(state, roboServices.getEquityCurveReportS(), true));
		stateListener.addListener(new EquityCurveReportHandler(state, roboServices.getEquityCurveReportL()));
		if ( ! appConfig.getBasicConfig().isHeadless() ) {
			stateListener.addListener(new S3BlockReportHandler(state, roboServices.getBlockReportStorage()));
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
				SMStateHandlerEx sh = (SMStateHandlerEx) automat.getCurrentState();
				automat.input(sh.getInterrupt(), null);
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
			.add(roboServices.getEquityCurveReportS(), "Equity")
			.save(report_file);
	}

}
