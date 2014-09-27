
package control;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class CalibrationWindow extends JFrame
{

	CalibrationPanel drawPanel;

	public CalibrationWindow()
	{
		drawPanel = new CalibrationPanel();
		drawPanel.setPreferredSize(new Dimension(320, 160));
		drawPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		setTitle("Einstellen der Umwandlung Helligkeit des Raumes in Bildschirmhelligkeit");
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setLocation(100, 200);
		setLayout(new BorderLayout());
		add(drawPanel, BorderLayout.CENTER);
	}

	public Point getPoint1()
	{
		return drawPanel.getPoint1();
	}

	public Point getPoint2()
	{
		return drawPanel.getPoint2();
	}

}
