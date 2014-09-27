package control;

import static control.AllesSetOptimalMonitorBrightness.jSlider;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import monitor.Monitor;
import monitor.MonitorController;

public class JHauptFenster extends JFrame {
	
	private final List<JCheckBox> list = new ArrayList<JCheckBox>();

	private final JPanel p;

	private ImageIcon imageIcon;

	private JLabel jLabel;
	
	public void selectAll(boolean yesno)
	{
		for (JCheckBox check : list)
		{
			check.setSelected(yesno);
		}	
	}
	
	public JHauptFenster()
	{
		setTitle("Bildschirmhelligkeit anpassen");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLocation(500,200);
		setLayout(new GridLayout(0,2));
		
		imageIcon = new ImageIcon();
		jLabel = new JLabel(imageIcon);						
		jLabel.setPreferredSize(new Dimension(640,480));
		add(jLabel);

		p = new JPanel(new GridLayout(0, 1));
		add(p);
	}
	
	public void setupVideo(Timer timer, final BrightnessActionLi imageProvider)
	{
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
	
	public void addMonitor(final Monitor mon, final Timer timer, final BrightnessActionLi timerListener, final Point point1, final Point point2, final TrayIcon trayIcon)
	{
		final JSlider slider = jSlider();
		slider.setMinimum(mon.getminBrightness());
		slider.setMaximum(mon.getmaxBrightness());
		slider.setValue(mon.getBrightness());
		final JPanel panel = new JPanel(new BorderLayout());
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
					monBright = (int) (camBright * (100.0 - point1.y) / point1.x);
				}

				else if (camBright < point2.x)
				{
					monBright = 100 - point1.y;
				}
				
				else
				{
					monBright = (int) (100.0 - ((0.0 - point2.y)/(255.0 - point2.x) * (camBright - point2.x) + point2.y));
				}
										
				int j = monBright - oldBright;
				if (Math.abs(j) < 5)
				{
					return;
				}
				
				mon.setBrightness(monBright);		
				label1.setText("Die gemessene Helligkeit betr�gt " + camBright);
				label2.setText("Die empfohlene Bildschirmhelligkeit betr�gt " + monBright);				
				trayIcon.displayMessage("Die Bildschirmhelligkeit wurde auf " + monBright + " eingestellt", "Jippie :D", TrayIcon.MessageType.INFO);					
				

				System.out.println(monBright);
				
				slider.setValue(monBright);
				
				oldBright = monBright;

			}
		});
		
		
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
		slider.setBorder(BorderFactory.createTitledBorder(mon.getName()));
		panel.add(slider, BorderLayout.CENTER);
		panel.add(checkbox, BorderLayout.EAST);
		panel.setBorder(BorderFactory.createEmptyBorder(10 , 10 , 10 , 10));
		p.add(panel);
	}

	
}
