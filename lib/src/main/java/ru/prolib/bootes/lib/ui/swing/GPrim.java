package ru.prolib.bootes.lib.ui.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Some graphics primitives.
 */
public class GPrim {
	private static final GPrim instance = new GPrim();
	
	public static GPrim getInstance() {
		return instance;
	}

	/**
	 * Draw filled circle with 1px border.
	 * <p>
	 * @param device - graphics device
	 * @param x - X coordinate of center of circle
	 * @param y - Y coordinate of center of circle
	 * @param diameter - circle diameter
	 * @param fill_color - color to fill circle
	 * @param border_color - circle border color
	 */
	public void drawCircle(Graphics2D device,
			int x,
			int y,
			int diameter,
			Color fill_color,
			Color border_color)
	{
		int r = diameter / 2;
		if ( r <= 0 ) {
			return;
		}
		x -= r;
		y -= r; 
		device.setColor(fill_color);
		device.fillOval(x, y, diameter, diameter);
		device.setStroke(new BasicStroke(1));
		device.setColor(border_color);
		device.drawOval(x, y, diameter, diameter);
	}

}
