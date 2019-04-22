package ru.prolib.bootes.lib.robo.s3;

import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.bootes.lib.report.blockrep.Block;
import ru.prolib.bootes.lib.report.blockrep.BlockReport;
import ru.prolib.bootes.lib.report.blockrep.IBlockReport;
import ru.prolib.bootes.lib.report.blockrep.IBlockReportStorage;
import ru.prolib.bootes.lib.robo.s3.statereq.IS3Speculative;

public class S3BlockReportHandler implements S3RobotStateListener {
	private static final String ID_OPEN = "OPEN";
	private static final String ID_CLOSE = "CLOSE";
	private static final String ID_TAKE_PROFIT = "TAKE_PROFIT";
	private static final String ID_STOP_LOSS = "STOP_LOSS";
	private static final String ID_BREAK_EVEN = "BREAK_EVEN";
	private final IS3Speculative state;
	private final IBlockReportStorage reportStorage;
	private IBlockReport currSpecReport;
	
	public S3BlockReportHandler(IS3Speculative state,
								IBlockReportStorage reportStorage)
	{
		this.state = state;
		this.reportStorage = reportStorage;
	}
	
	void setCurrReport(IBlockReport report) {
		currSpecReport = report;
	}
	
	IBlockReport getCurrReport() {
		return currSpecReport;
	}
	
	private S3Speculation getSpeculation() {
		return state.getActiveSpeculation();
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
		S3Speculation spec = getSpeculation();
		synchronized ( spec ) {
			Tick en_p = spec.getEntryPoint(); 
			currSpecReport = new BlockReport(new Block(ID_OPEN, en_p.getPrice(), en_p.getTime()));
		}
		reportStorage.addReport(currSpecReport);
	}

	@Override
	public void speculationUpdate() {
		S3Speculation spec = getSpeculation();
		synchronized ( spec ) {
			currSpecReport.setBlock(new Block(ID_TAKE_PROFIT, spec.getTakeProfit(), null));
			currSpecReport.setBlock(new Block(ID_STOP_LOSS, spec.getStopLoss(), null));
			currSpecReport.setBlock(new Block(ID_BREAK_EVEN, spec.getBreakEven(), null));			
		}
	}

	@Override
	public void speculationClosed() {
		S3Speculation spec = getSpeculation();
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
