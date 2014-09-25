package control;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
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


public class EinstellungsDingsmitGraph {
	
	
	public static void main(String[] args) throws IOException {
		
		MonitorController monitorListe = new MonitorControllerJna();
		
		final JFrame frame = new JFrame(); 
		final JFrame frame2 = new JFrame(); 
		
		frame2.setTitle("Einstellen der Umwandlung Helligkeit des Raumes in Bildschirmhelligkeit");
		frame2.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame2.setLocation(100,200);
		frame2.setLayout(new BorderLayout());
		
		final DrawPanel panel2 = new DrawPanel();
		final Point point1 = panel2.getPoint1();
		final Point point2 = panel2.getPoint2();

		panel2.setPreferredSize(new Dimension(320,160));
		panel2.setBorder(BorderFactory.createEmptyBorder(20 , 20 , 20 , 20));
		frame2.add(panel2, BorderLayout.CENTER);
		
		//frame2.setSize(new Dimension(280,150));
		frame2.pack();
		frame2.setVisible(true);
		
		frame.setTitle("Bildschirmhelligkeit anpassen");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setLocation(500,200);
		frame.setLayout(new GridLayout(0,2));
		final JPanel p = new JPanel(new GridLayout(monitorListe.getMonitors().size(),0));
		
		final Webcam webcam = setupWebcam();
		final BrightnessActionLi timerListener = new BrightnessActionLi(webcam);
		final Timer timer = setupTimer(timerListener);
		setupVideo(frame, timer, timerListener);

		final List<JCheckBox> list = new ArrayList<JCheckBox>();

		BufferedImage icon = loadImage("icons/lightbulb16.png");
		final TrayIcon trayIcon = new TrayIcon(icon);
        final SystemTray tray = SystemTray.getSystemTray();
		final PopupMenu popup = new PopupMenu();

		
        for (final Monitor mon : monitorListe.getMonitors())
		{
			final JSlider slider = new JSlider();
			final JPanel panel = new JPanel(new BorderLayout());

			slider.setMinimum(mon.getminBrightness());
			slider.setMaximum(mon.getmaxBrightness());
			slider.setMajorTickSpacing(20);
			slider.setMinorTickSpacing(1);
			slider.createStandardLabels(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);
			slider.setValue(mon.getBrightness());
			slider.setPreferredSize(new Dimension(400, 100));

			final JCheckBox checkbox = new JCheckBox("Automatische Anpassung");
			list.add(checkbox);

			final JLabel label1 = new JLabel("-");
			panel.add(label1, BorderLayout.NORTH);
			final JLabel label2 = new JLabel("-");
			panel.add(label2, BorderLayout.SOUTH);

			timer.addActionListener(new ActionListener() {
				
				private int oldBright = -100;
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if (!checkbox.isSelected())
						return;
					
					int camBright = timerListener.getBrightness();
					int monBright = 0;
					
					if (camBright <  point1.x)
					{
						//monBright = 100 - (((point1.y -100) / point1.x) * camBright + 100);
						monBright = (int) (camBright * (100.0 - point1.y) / point1.x);
					}

					else if (camBright < point2.x)
					{
						monBright = 100 - point1.y;
					}
					
					else
					{
						//monBright = 100 - ((0 - point2.y)/(255 - point2.x) * (camBright - point2.x) + point2.y);
						monBright = (int) (100.0 - ((0.0 - point2.y)/(255.0 - point2.x) * (camBright - point2.x) + point2.y));
					}
											
					//int monBright = mon.getminBrightness() + camBright * (mon.getmaxBrightness() - mon.getminBrightness()) / 255;
					
					int j = monBright - oldBright;
					if (Math.abs(j) < 5)
					{
						return;
					}
					
					mon.setBrightness(monBright);		
					
					oldBright = monBright;

					System.out.println(monBright);
					slider.setValue(monBright);
				
					label1.setText("Die gemessene Helligkeit beträgt " + camBright);
					label2.setText("Die empfohlene Bildschirmhelligkeit beträgt " + monBright);
					trayIcon.displayMessage("Die Bildschirmhelligkeit wurde auf " + monBright + " eingestellt", "Jippie :D", TrayIcon.MessageType.INFO);					
				}
			});
			
			
			ActionListener actionListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{				
							
				}
			};
			 
			checkbox.addActionListener(actionListener);
			   
			ChangeListener changeListener = new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
				{
            		JSlider source;
            		source = (JSlider)e.getSource();
            		mon.setBrightness(source.getValue());
				}
			};
		    				
			slider.addChangeListener(changeListener);
			slider.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			slider.setBorder(BorderFactory.createTitledBorder(mon.getName()));
			panel.add(slider, BorderLayout.CENTER);
			panel.add(checkbox, BorderLayout.EAST);
			panel.setBorder(BorderFactory.createEmptyBorder(10 , 10 , 10 , 10));
			p.add(panel);

		}

        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
		
		final MenuItem settings = new MenuItem("Settings");
		settings.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(true);			
			}
		});

		final CheckboxMenuItem cb = new CheckboxMenuItem("Automatische Helligkeitsanpassung");
		cb.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				boolean x = cb.getState();
				for (JCheckBox check : list)
					{
						check.setSelected(x);
					}
			}
		});
		
		final MenuItem graph = new MenuItem("Graph anzeigen");
		graph.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame2.setVisible(true);
			}
		});		
		
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				webcam.close();
				tray.remove(trayIcon);
				frame.dispose();
				frame2.dispose();
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
				
        frame.add(p);
		frame.pack();
		frame.setVisible(true);
	}
	
	private static void repaint() {
		// TODO Auto-generated method stub
		
	}

	private static BufferedImage loadImage(String fname) throws IOException
	{
		String fullPath = "/" + fname;
		URL rsc = App.class.getResource(fullPath);
		if (rsc == null)
		{
			throw new FileNotFoundException(fullPath);
		}
		return ImageIO.read(rsc);
	}	
	
	private static Webcam setupWebcam()
	{
		final Webcam webcam;
		webcam = Webcam.getDefault();
		if (webcam == null)
			throw new IllegalStateException("No webcam has been detected!");
		webcam.setViewSize(new Dimension(640, 480));
		webcam.open();
		return webcam;
	}
	
	private static Timer setupTimer(BrightnessActionLi imageProvider)
	{
		final Timer timer = new Timer(100, imageProvider);
		timer.setInitialDelay(0);
        timer.start();
        
        return timer;
	}
	
	private static void setupVideo(JFrame frame, Timer timer, final BrightnessActionLi imageProvider)
	{
		final ImageIcon imageIcon = new ImageIcon();
		final JLabel jLabel = new JLabel(imageIcon);						
		jLabel.setPreferredSize(new Dimension(640,480));
		frame.add(jLabel);

        timer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage image = imageProvider.getImage();
				if (image != null)
				{
					imageIcon.setImage(image);
					jLabel.repaint();
				}
			}
		});
        
	}
}