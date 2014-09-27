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
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import monitor.Monitor;
import monitor.MonitorController;

public class MainWindow extends JFrame {

	private final List<JCheckBox> list = new ArrayList<JCheckBox>();

	private final JPanel rootPanel;

	private ImageIcon imageIcon;

	private JLabel jLabel;

	public MainWindow()
	{
		setTitle("Bildschirmhelligkeit anpassen");
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setLocation(500,200);
		setLayout(new BorderLayout(5, 5));

		imageIcon = new ImageIcon();
		jLabel = new JLabel(imageIcon);
		jLabel.setPreferredSize(new Dimension(320,240));
		add(jLabel, BorderLayout.WEST);

		rootPanel = new JPanel(new GridLayout(0, 1));
		add(rootPanel, BorderLayout.CENTER);
	}

	public void autoAdjustAll(boolean yesno)
	{
		for (JCheckBox check : list)
		{
			check.setSelected(yesno);
		}
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
		final JSlider slider = new JSlider();
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(1);
		slider.createStandardLabels(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		slider.setPreferredSize(new Dimension(400, 100));
		slider.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		slider.setMinimum(mon.getMinBrightness());
		slider.setMaximum(mon.getMaxBrightness());
		slider.setValue(mon.getBrightness());

		final JPanel panel = new JPanel(new BorderLayout());
		final JCheckBox checkbox = new JCheckBox("Auto-adjust");
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
				label1.setText("Measured a brightness of " + camBright);
				label2.setText("The recommended monitor brightness is " + monBright);
				trayIcon.displayMessage("Monitor brightness", "was adjusted to " + monBright, TrayIcon.MessageType.INFO);

				slider.setValue(monBright);

				oldBright = monBright;

			}
		});


		ChangeListener changeListener = new ChangeListener()
		{
			@Override
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
		rootPanel.add(panel);
	}


}
