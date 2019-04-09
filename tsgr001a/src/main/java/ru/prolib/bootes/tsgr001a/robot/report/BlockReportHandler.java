package ru.prolib.bootes.tsgr001a.robot.report;

import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.bootes.lib.report.blockrep.Block;
import ru.prolib.bootes.lib.report.blockrep.BlockReport;
import ru.prolib.bootes.lib.report.blockrep.IBlockReport;
import ru.prolib.bootes.lib.report.blockrep.IBlockReportStorage;
import ru.prolib.bootes.lib.s3.S3RobotStateListener;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class BlockReportHandler implements S3RobotStateListener {
	private static final String ID_OPEN = "OPEN";
	private static final String ID_CLOSE = "CLOSE";
	private static final String ID_TAKE_PROFIT = "TAKE_PROFIT";
	private static final String ID_STOP_LOSS = "STOP_LOSS";
	private static final String ID_BREAK_EVEN = "BREAK_EVEN";
	private final RobotState state;
	private final IBlockReportStorage storage;
	private IBlockReport currSpecReport;
	
	public BlockReportHandler(RobotState state, IBlockReportStorage storage) {
		this.state = state;
		this.storage = storage;
	}
	
	private Speculation getSpeculation() {
		synchronized ( state ) {
			return state.getActiveSpeculation();
		}
	}

	@Override
	public void robotStarted() {
		
	}

	@Override
	public void accountSelected() {
		
	}

	@Override
	public void contractSelected() {
		
	}

	@Override
	public void sessionDataAvailable() {
		
	}

	@Override
	public void riskManagementUpdate() {
		
	}

	@Override
	public void speculationOpened() {
		Speculation spec = getSpeculation();
		synchronized ( spec ) {
			Tick en_p = spec.getEntryPoint(); 
			currSpecReport = new BlockReport(new Block(ID_OPEN, en_p.getPrice(), en_p.getTime()));
		}
		storage.addReport(currSpecReport);
	}

	@Override
	public void speculationUpdate() {
		Speculation spec = getSpeculation();
		synchronized ( spec ) {
			currSpecReport.setBlock(new Block(ID_TAKE_PROFIT, spec.getTakeProfit(), null));
			currSpecReport.setBlock(new Block(ID_STOP_LOSS, spec.getStopLoss(), null));
			currSpecReport.setBlock(new Block(ID_BREAK_EVEN, spec.getBreakEven(), null));			
		}
	}

	@Override
	public void speculationClosed() {
		Speculation spec = getSpeculation();
		synchronized ( spec ) {
			Tick ex_p = spec.getExitPoint();
			currSpecReport.setBlock(new Block(ID_CLOSE, ex_p.getPrice(), ex_p.getTime()));
			currSpecReport.setBlock(new Block(ID_TAKE_PROFIT, spec.getTakeProfit(), ex_p.getTime()));
			currSpecReport.setBlock(new Block(ID_STOP_LOSS, spec.getStopLoss(), ex_p.getTime()));
			currSpecReport.setBlock(new Block(ID_BREAK_EVEN, spec.getBreakEven(), ex_p.getTime()));
			currSpecReport = null;
		}
	}

	@Override
	public void sessionDataCleanup() {
		// TODO: add cleanup data outside of T0 chart view
	}

	@Override
	public void robotStopped() {
		
	}

}
