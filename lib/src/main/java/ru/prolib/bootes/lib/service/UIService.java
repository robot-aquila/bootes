package ru.prolib.bootes.lib.service;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.time.ZoneId;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.Messages;

public class UIService {
	private static final String DEFAULT_TITLE = "Bootes App";

	protected final ZoneId zoneID = ZoneId.of("Europe/Moscow"); // TODO: Move to loadable configuration
	protected final IMessages messages = new Messages();
	protected final JFrame frame = new JFrame();
	protected final JPanel mainPanel = new JPanel();
	protected final JPanel topPanel = new JPanel();
	protected final JTabbedPane tabPanel = new JTabbedPane();;
	
	public UIService() {
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(800,  600);
		frame.setTitle(DEFAULT_TITLE);

		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new BorderLayout());
        frame.getContentPane().add(mainPanel);
        
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        mainPanel.add(topPanel, BorderLayout.PAGE_START);
        mainPanel.add(tabPanel, BorderLayout.CENTER);
	}
	
	public IMessages getMessages() {
		return messages;
	}
	
	public ZoneId getZoneID() {
		return zoneID;
	}
	
	/**
	 * Get main frame of the application.
	 * <p>
	 * @return frame
	 */
	public JFrame getFrame() {
		return frame;
	}
	
	/**
	 * Get main panel inside the frame.
	 * <p>
	 * @return main panel
	 */
	public JPanel getMainPanel() {
		return mainPanel;
	}
	
	/**
	 * Get top panel inside the frame.
	 * <p>
	 * @return top panel
	 */
	public JPanel getTopPanel() {
		return topPanel;
	}

	/**
	 * Get tab panel inside the main panel.
	 * <p>
	 * @return tab panel
	 */
	public JTabbedPane getTabPanel() {
		return tabPanel;
	}

}
