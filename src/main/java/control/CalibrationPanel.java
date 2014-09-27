/*
 * Copyright 2014 Martin Steiger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package control;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class CalibrationPanel extends JPanel
{
	private final Point point1;
	private final Point point2;
	private final Point offset = new Point(30, 30);
	private Point pressed;

	public CalibrationPanel()
	{
		int diff = 100;
		int brightmin = 50;
		int brightmax = 115;

		point1 = new Point(brightmin, diff / 2);
		point2 = new Point(brightmax, diff / 2);

		MouseAdapter ma = new MouseAdapter()
		{
			private int downX;
			private int downY;

			private Point notPressed;

			@Override
			public void mousePressed(MouseEvent e)
			{
				downX = e.getX() - offset.x;
				downY = e.getY() - offset.y;

				int diff1x = downX - point1.x;
				int diff1y = downY - point1.y;
				int diff2x = downX - point2.x;
				int diff2y = downY - point2.y;

				if (Math.sqrt(diff1x * diff1x + diff1y * diff1y) < 5)
				{
					pressed = point1;
					notPressed = point2;
					System.out.println("Punkt 1 wird verschoben");
				}
				else if (Math.sqrt(diff2x * diff2x + diff2y * diff2y) < 5)
				{
					pressed = point2;
					notPressed = point1;
					System.out.println("Punkt 2 wird verschoben");
				}
			}

			public void mouseReleased(MouseEvent e)
			{
				pressed = null;
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				pressed = null;
				repaint();
			}

			public void mouseDragged(MouseEvent e)
			{
				final int dx = e.getX() - offset.x - downX;
				final int dy = e.getY() - offset.y - downY;

				if (pressed == point1 && pressed.x > notPressed.x)
				{
					notPressed.setLocation(pressed.x, downY + dy);
				}
				else if (pressed == point2 && pressed.x < notPressed.x)
				{
					notPressed.setLocation(pressed.x, downY + dy);
				}

				pressed.setLocation(downX + dx, downY + dy);
				notPressed.setLocation(notPressed.x, downY + dy);

				repaint();
			}

		};

		addMouseListener(ma);
		addMouseMotionListener(ma);

	}

	public Point getPoint1()
	{
		return point1;
	}

	public Point getPoint2()
	{
		return point2;
	}

	public void paintComponent(Graphics g)
	{
		int diff = 100;
		super.paintComponent(g);

		g.translate(offset.x, offset.y);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 255, 100);
		g.setColor(Color.BLACK);

		g.drawLine(0, diff, point1.x, point1.y);
		g.drawLine(point1.x, point1.y, point2.x, point2.y);
		g.drawLine(point2.x, point2.y, 255, 0);

		g.drawLine(0 - 2, diff - 2, 0 + 2, diff + 2);
		g.drawLine(0 - 2, diff + 2, 0 + 2, diff - 2);

		g.drawLine(point1.x - 2, point1.y - 2, point1.x + 2, point1.y + 2);
		g.drawLine(point1.x - 2, point1.y + 2, point1.x + 2, point1.y - 2);

		g.drawLine(point2.x - 2, point2.y - 2, point2.x + 2, point2.y + 2);
		g.drawLine(point2.x - 2, point2.y + 2, point2.x + 2, point2.y - 2);

		g.drawLine(255 - 2, 0 - 2, 255 + 2, 0 + 2);
		g.drawLine(255 - 2, 0 + 2, 255 + 2, 0 - 2);

		g.drawLine(0, 100, 255, 100);
		g.drawLine(0, 100, 0, 0);
		g.drawLine(253, 98, 255, 100);
		g.drawLine(253, 102, 255, 100);
		g.drawLine(-2, 2, 0, 0);
		g.drawLine(2, 2, 0, 0);

		g.drawLine(-2, 25, 2, 25);
		g.drawString("75", -20, 30);
		g.drawLine(-2, 50, 2, 50);
		g.drawString("50", -20, 55);
		g.drawLine(-2, 75, 2, 75);
		g.drawString("25", -20, 80);
		g.drawString("0", -13, 105);

		g.drawLine(25, 98, 25, 102);
		g.drawLine(50, 98, 50, 102);
		g.drawString("50", 45, 115);
		g.drawLine(75, 98, 75, 102);
		g.drawLine(100, 98, 100, 102);
		g.drawString("100", 90, 115);
		g.drawLine(125, 98, 125, 102);
		g.drawLine(150, 98, 150, 102);
		g.drawString("150", 140, 115);
		g.drawLine(175, 98, 175, 102);
		g.drawLine(200, 98, 200, 102);
		g.drawString("200", 190, 115);
		g.drawLine(225, 98, 225, 102);
		g.drawLine(250, 98, 250, 102);

		if (pressed == point1)
		{
			g.drawString(point1.x + "/" + (100 - point1.y), point1.x - 35, point1.y - 5);
		}

		else if (pressed == point2)
		{
			g.drawString(point2.x + "/" + (100 - point2.y), point2.x + 5, point2.y + 15);
		}
	}
}
