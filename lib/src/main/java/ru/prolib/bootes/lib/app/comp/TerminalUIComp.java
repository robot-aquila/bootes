package ru.prolib.bootes.lib.app.comp;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableRowSorter;

import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.qforts.ui.QFPortfolioListTableModel;
import ru.prolib.aquila.qforts.ui.QFPositionListTableModel;
import ru.prolib.aquila.ui.TableModelController;
import ru.prolib.aquila.ui.form.OrderListTableModel;
import ru.prolib.aquila.ui.form.PortfolioListTableModel;
import ru.prolib.aquila.ui.form.PositionListTableModel;
import ru.prolib.aquila.ui.form.SecurityListTableModel;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.service.UIService;

public class TerminalUIComp extends CommonComp {
	private static final String DEFAULT_ID = "TERMINAL-UI";

	public TerminalUIComp(AppConfig appConfig, AppServiceLocator serviceLocator, String serviceID) {
		super(appConfig, serviceLocator, serviceID);
	}
	
	public TerminalUIComp(AppConfig appConfig, AppServiceLocator serviceLocator) {
		this(appConfig, serviceLocator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		if ( appConfig.getBasicConfig().isHeadless() ) {
			return;
		}
		UIService uis = serviceLocator.getUIService();
		JTabbedPane tabPanel = uis.getTabPanel();
		IMessages messages = uis.getMessages();
		JFrame frame = uis.getFrame();
		Terminal terminal = serviceLocator.getTerminal();
		
		SecurityListTableModel securityTableModel = new SecurityListTableModel(messages);
		securityTableModel.add(terminal);
		JTable table = new JTable(securityTableModel);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<SecurityListTableModel>(securityTableModel));
		tabPanel.add("Securities", new JScrollPane(table));
		new TableModelController(securityTableModel, frame);
		
		OrderListTableModel orderTableModel = new OrderListTableModel(messages);
		orderTableModel.add(terminal);
		table = new JTable(orderTableModel);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<>(orderTableModel));
		tabPanel.add("Orders", new JScrollPane(table));
		new TableModelController(orderTableModel, frame);
		
		QFPortfolioListTableModel portfolioTableModel = new QFPortfolioListTableModel(messages);
		portfolioTableModel.add(terminal);
		table = new JTable(portfolioTableModel);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<PortfolioListTableModel>(portfolioTableModel));
		tabPanel.add("Accounts", new JScrollPane(table));
		new TableModelController(portfolioTableModel, frame);
		
		QFPositionListTableModel positionTableModel = new QFPositionListTableModel(messages);
		positionTableModel.add(terminal);
		table = new JTable(positionTableModel);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<PositionListTableModel>(positionTableModel));
		tabPanel.add("Positions", new JScrollPane(table));
		new TableModelController(positionTableModel, frame);
	}

	@Override
	public void startup() throws Throwable {
		
	}

	@Override
	public void shutdown() throws Throwable {
		
	}

}
