package ru.prolib.bootes.lib.report.s3rep.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.bootes.lib.report.s3rep.IS3Report;
import ru.prolib.bootes.lib.report.s3rep.IS3ReportListener;
import ru.prolib.bootes.lib.report.s3rep.S3RRecord;

public class S3ReportCounterView extends JPanel implements IS3ReportListener, Runnable {
	private static final long serialVersionUID = 1L;
	private final IMessages messages;
	private final IS3Report report;
	private final JLabel
		labCreatedCount = new JLabel(),
		valCreatedCount = new JLabel(),
		labDeletedCount = new JLabel(),
		valDeletedCount = new JLabel(),
		labRatio = new JLabel(),
		valRatio = new JLabel();
	
	public S3ReportCounterView(IMessages messages, IS3Report report) {
		super(new MigLayout());
		this.messages = messages;
		this.report = report;
		labCreatedCount.setText(messages.get(S3ReportMsg.RECORDS_CREATED));
		valCreatedCount.setText(messages.get(S3ReportMsg.N_A));
		labDeletedCount.setText(messages.get(S3ReportMsg.RECORDS_DELETED));
		valDeletedCount.setText(messages.get(S3ReportMsg.N_A));
		labRatio.setText(messages.get(S3ReportMsg.RECORDS_DELETED_CREATED_RATIO));
		valRatio.setText(messages.get(S3ReportMsg.N_A));
		add(labCreatedCount);
		add(valCreatedCount);
		add(labDeletedCount, "gap 30");
		add(valDeletedCount);
		add(labRatio, "gap 30");
		add(valRatio);
		report.addListener(this);
	}

	@Override
	public void recordCreated(S3RRecord record) {
		SwingUtilities.invokeLater(this);
	}

	@Override
	public void recordUpdated(S3RRecord record) {
		SwingUtilities.invokeLater(this);
	}

	@Override
	public void recordDeleted(S3RRecord record) {
		SwingUtilities.invokeLater(this);
	}

	@Override
	public void run() {
		CDecimal created = CDecimalBD.of(report.getRecordsCreated());
		CDecimal deleted = CDecimalBD.of(report.getRecordsDeleted());
		valCreatedCount.setText(created.toString());
		valDeletedCount.setText(deleted.toString());
		if ( created.compareTo(CDecimalBD.ZERO) == 0 ) {
			valRatio.setText(messages.get(S3ReportMsg.N_A));
		} else {
			CDecimal ratio = deleted.withScale(4).divide(created);
			valRatio.setText(ratio.toString());
		}
	}

}
