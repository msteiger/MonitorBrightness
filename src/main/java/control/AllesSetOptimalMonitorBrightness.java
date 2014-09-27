package control;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import monitor.Monitor;
import monitor.MonitorController;
import monitor.jna.MonitorControllerJna;

import com.github.sarxos.webcam.Webcam;


public class AllesSetOptimalMonitorBrightness {
	
	public static BufferedImage loadImage(String fname) throws IOException
	{
		String fullPath = "/" + fname;
		URL rsc = App.class.getResource(fullPath);
		if (rsc == null)
		{
			throw new FileNotFoundException(fullPath);
		}
		return ImageIO.read(rsc);
	}	
	
	public static Webcam setupWebcam()
	{
		final Webcam webcam;
		webcam = Webcam.getDefault();
		if (webcam == null)
			throw new IllegalStateException("No webcam has been detected!");
		webcam.setViewSize(new Dimension(640, 480));
		webcam.open();
		return webcam;
	}
	
	public static Timer setupTimer(BrightnessActionLi imageProvider)
	{
		final Timer timer = new Timer(100, imageProvider);
		timer.setInitialDelay(0);
        timer.start();
        
        return timer;
	}
	
	public static TrayIcon geniousTray(final JHauptFenster frame, final JFrame drawFrame, final Webcam webcam) throws IOException
	{
		BufferedImage icon = loadImage("icons/lightbulb16.png");
		final TrayIcon trayIcon = new TrayIcon(icon);
        final SystemTray tray = SystemTray.getSystemTray();
		final PopupMenu popup = new PopupMenu();
        final MenuItem settings = new MenuItem("Settings");
		final CheckboxMenuItem cb = new CheckboxMenuItem("Automatische Helligkeitsanpassung");
		final MenuItem graph = new MenuItem("Graph anzeigen");
		final MenuItem exitItem = new MenuItem("Exit");

		settings.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(true);			
			}
		});
		
		cb.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				boolean x = cb.getState();
				frame.selectAll(x);
			}
		});
		
		graph.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				drawFrame.setVisible(true);
			}
		});		
		
		exitItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				webcam.close();
				tray.remove(trayIcon);
				frame.dispose();
				drawFrame.dispose();
			}
		});
		
		popup.add(settings);
        popup.add(graph);
		popup.addSeparator();
		popup.add(cb);
		popup.addSeparator();
		popup.add(exitItem);
		
		trayIcon.setPopupMenu(popup);
		
		try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }		
		
		return trayIcon;		
	}

	public static JSlider jSlider()
	{
		final JSlider slider = new JSlider();
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(1);
		slider.createStandardLabels(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		slider.setPreferredSize(new Dimension(400, 100));
		slider.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		return slider;
	}
//	
//	public static DrawPanel drawPanel()
//	{
//		final DrawPanel drawPanel = new DrawPanel();
//		drawPanel.setPreferredSize(new Dimension(320,160));
//		drawPanel.setBorder(BorderFactory.createEmptyBorder(20 , 20 , 20 , 20));
//		return drawPanel;
//	}
//	
//	public static JFrame drawFrame()
//	{
//		final JFrame drawFrame = new JFrame(); 
//		drawFrame.setTitle("Einstellen der Umwandlung Helligkeit des Raumes in Bildschirmhelligkeit");
//		drawFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//		drawFrame.setLocation(100,200);
//		drawFrame.setLayout(new BorderLayout());
//		drawFrame.add(drawPanel(), BorderLayout.CENTER);
//		drawFrame.pack();
//		return drawFrame;
//	}
//	
}