package ru.prolib.bootes.lib.app.comp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableRowSorter;

import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.TerminalRegistry;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.qforts.ui.QFPortfolioListTableModel;
import ru.prolib.aquila.qforts.ui.QFPositionListTableModel;
import ru.prolib.aquila.ui.TableModelController;
import ru.prolib.aquila.ui.form.MenuFactory;
import ru.prolib.aquila.ui.form.OrderListTableModel;
import ru.prolib.aquila.ui.form.PortfolioListTableModel;
import ru.prolib.aquila.ui.form.PositionListTableModel;
import ru.prolib.aquila.ui.form.SecurityListMarketDepthActivator;
import ru.prolib.aquila.ui.form.SecurityListTableModel;
import ru.prolib.aquila.ui.msg.CommonMsg;
import ru.prolib.aquila.ui.subman.SSDescRepo;
import ru.prolib.aquila.ui.subman.SSDescDialog;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig2;
import ru.prolib.bootes.lib.service.UIService;

public class TerminalUIComp extends CommonComp {
	private SSDescRepo manualSymbolSubscr;

	public TerminalUIComp(AppServiceLocator serviceLocator, String serviceID) {
		super(serviceLocator, serviceID);
	}

	@Override
	public void init() throws Throwable {
		AppConfig2 app_config = serviceLocator.getConfig();
		if ( app_config.getBasicConfig().isHeadless() ) {
			return;
		}
		UIService uis = serviceLocator.getUIService();
		JTabbedPane tabPanel = uis.getTabPanel();
		IMessages messages = uis.getMessages();
		JFrame frame = uis.getFrame();
		Terminal terminal = serviceLocator.getTerminal();
		MenuFactory menu_factory = new MenuFactory(messages);
		
		SecurityListTableModel securityTableModel = new SecurityListTableModel(messages);
		securityTableModel.add(terminal);
		JTable table = new JTable(securityTableModel);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<SecurityListTableModel>(securityTableModel));
		table.addMouseListener(new SecurityListMarketDepthActivator(messages));
		tabPanel.add("Securities", new JScrollPane(table));
		new TableModelController(securityTableModel, frame);
		
		OrderListTableModel orderTableModel = new OrderListTableModel(messages);
		orderTableModel.add(terminal);
		table = new JTable(orderTableModel);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<>(orderTableModel));
		tabPanel.add("Orders", new JScrollPane(table));
		new TableModelController(orderTableModel, frame);
		menu_factory.createOrderTablePopupMenu(table);
		
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
		
		TerminalRegistry registry = new TerminalRegistry();
		registry.register(terminal);
		manualSymbolSubscr = new SSDescRepo(registry, serviceLocator.getEventQueue());
		JMenuItem item = null;
		JMenu menu = new JMenu(messages.get(CommonMsg.MANAGE));
		menu.add(item = new JMenuItem(messages.get(CommonMsg.MANUAL_SYMBOL_SUBSCRIPTIONS)));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TerminalRegistry registry = new TerminalRegistry();
				registry.register(serviceLocator.getTerminal());
				SSDescDialog dialog = new SSDescDialog(frame, messages, registry, manualSymbolSubscr);
				dialog.setResizable(false);
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		});
		uis.getMainMenu().add(menu);
	}

	@Override
	public void startup() throws Throwable {
		
	}

	@Override
	public void shutdown() throws Throwable {
		
	}

	@Override
	public void registerConfig(AppConfigService2 config_service) {
		
	}

}
