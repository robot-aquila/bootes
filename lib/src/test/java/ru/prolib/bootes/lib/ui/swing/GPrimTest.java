package ru.prolib.bootes.lib.ui.swing;

import static org.easymock.EasyMock.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class GPrimTest {
	private IMocksControl control;
	private Graphics2D deviceMock;
	private GPrim service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		deviceMock = control.createMock(Graphics2D.class);
		service = GPrim.getInstance();
	}

	@Test
	public void testDrawCircle6() {
		deviceMock.setColor(Color.BLUE);
		deviceMock.fillOval(12, 12, 6, 6);
		deviceMock.setStroke(new BasicStroke(1));
		deviceMock.setColor(Color.ORANGE);
		deviceMock.drawOval(12, 12, 6, 6);
		control.replay();
		
		service.drawCircle(deviceMock, 15, 15, 6, Color.BLUE, Color.ORANGE);
		
		control.verify();
	}

}
