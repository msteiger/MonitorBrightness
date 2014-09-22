package control;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import monitor.Monitor;
import monitor.MonitorController;
import monitor.jna.MonitorControllerJna;

import com.github.sarxos.webcam.Webcam;

import light.WebcamLuminance;


public class EinstellungsDingsFinal {
	
	
	public static void main(String[] args) {
		
		MonitorController monitorListe = new MonitorControllerJna();
		
		JFrame frame = new JFrame(); 
		
		frame.setTitle("Bildschirmhelligkeit anpassen");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(500,200);
		frame.setLayout(new GridLayout(0,2));
		
		final ImageIcon imageIcon = new ImageIcon();
		final JLabel jLabel = new JLabel(imageIcon);						
		
		frame.add(jLabel);
		final JPanel p = new JPanel(new GridLayout(monitorListe.getMonitors().size(),0));

		final Webcam webcam;
		webcam = Webcam.getDefault();
		if (webcam == null)
			throw new IllegalStateException("No webcam has been detected!");
		webcam.open();

		ActionListener timerListener = new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				//Webcam default1 = Webcam.getDefault();
				BufferedImage image = webcam.getImage();

				//---------------------------
				int[] data = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
				
				int sum = 0;
				
				for (int argb : data)
				{
					int r = (argb >> 16) & 0xFF;
					int g = (argb >> 8)  & 0xFF;
					int b = (argb >> 0)  & 0xFF;
					int y = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b + 0.5);
					sum += y;
				}
				
				int avg = sum / data.length;
				
//				int brightness = mon.getminBrightness() + avg * (mon.getmaxBrightness() - mon.getminBrightness()) / 255;
//				mon.setBrightness(brightness);		
				
//				slider.setValue(mon.getBrightness());
				
//				panel.add(new JLabel("Die gemessene Helligkeit beträgt " + Integer.toString(avg)), BorderLayout.NORTH);
//				panel.add(new JLabel("Die empfohlene Bildschirmhelligkeit beträgt " + Integer.toString(brightness)), BorderLayout.SOUTH);
//				panel.repaint();
//				p.repaint();
				//---------------------------
				
				if (image != null)
				{
					imageIcon.setImage(image);
					jLabel.repaint();
				}
			}
		};
		
		final Timer timer = new Timer(100, timerListener);
		timer.setInitialDelay(0);
        timer.start();

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
			
			ActionListener actionListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent e) {
					
//					if (checkbox.isSelected())
//						timer.start(); else
//						timer.stop();
					
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
		frame.add(p);
		frame.pack();
		frame.setVisible(true);
	}
}