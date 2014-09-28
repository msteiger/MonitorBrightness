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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
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
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import light.BrightnessConverter;
import light.WebcamWrapper;
import monitor.Monitor;
import config.Config;

public class MainWindow extends JFrame
{
	/**
	 * Brightness will be changed only if difference is large enough
	 */
	private static final double MIN_BRIGHTNESS_CHANGE_DELTA = 5;

	private final List<JCheckBox> list = new ArrayList<JCheckBox>();

	private final JPanel rightPanel;

	private final WebcamWrapper webcamWrapper;

	private final Timer timer;

	private Config config;

	public MainWindow(final WebcamWrapper webcamWrapper, Timer timer, Config config)
	{
		setTitle("Adjust Monitor Brightness");
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setLocation(500, 200);
		setLayout(new BorderLayout(5, 5));

		this.timer = timer;
		this.config = config;

		this.webcamWrapper = webcamWrapper;
		BufferedImage firstImage = webcamWrapper.getImage();

		JPanel left = new JPanel(new BorderLayout());
		left.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		final ImageIcon imageIcon = new ImageIcon(firstImage);
		final JLabel imageLabel = new JLabel(imageIcon);
		imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		imageLabel.setPreferredSize(new Dimension(firstImage.getWidth(), firstImage.getHeight()));
		left.add(imageLabel, BorderLayout.SOUTH);
		final JLabel brightnessLabel = new JLabel();
		left.add(brightnessLabel, BorderLayout.NORTH);
		add(left, BorderLayout.WEST);

		timer.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				BufferedImage image = webcamWrapper.getImage();
				if (image != null)
				{
					imageIcon.setImage(image);
					imageLabel.repaint();
					brightnessLabel.setText(webcamWrapper.getName() + " measured a brightness of " + webcamWrapper.getBrightness());
				}
			}
		});

		rightPanel = new JPanel(new GridLayout(0, 1, 10, 10));
		add(rightPanel, BorderLayout.EAST);
	}

	public void autoAdjustAll(boolean yesno)
	{
		for (JCheckBox check : list)
		{
			check.setSelected(yesno);
		}
	}

	public void addMonitor(final Monitor mon, final BrightnessConverter converter)
	{
		final JSlider slider = new JSlider();
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(5);
		slider.createStandardLabels(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		slider.setPreferredSize(new Dimension(400, 100));
		slider.setMinimum(mon.getMinBrightness());
		slider.setMaximum(mon.getMaxBrightness());
		slider.setValue(mon.getBrightness());
		slider.setPreferredSize(new Dimension(140, 30));

		final JPanel panel = new JPanel(new BorderLayout());

		final JCheckBox checkbox = new JCheckBox("Auto-adjust");
		list.add(checkbox);

		boolean autoAdjust = config.getAutoAdjust(mon.getName());
		checkbox.setSelected(autoAdjust);
		checkbox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				config.setAutoAdjust(mon.getName(), checkbox.isSelected());
			}
		});

		final JLabel recLabel = new JLabel();
		recLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(recLabel, BorderLayout.NORTH);

		timer.addActionListener(new ActionListener()
		{
			private int oldBright = -100;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				int camBright = webcamWrapper.getBrightness();
				int monBright = converter.convert(camBright);

				recLabel.setText("The recommended monitor brightness is " + monBright);

				if (!checkbox.isSelected())
					return;

				int j = monBright - oldBright;
				if (Math.abs(j) < MIN_BRIGHTNESS_CHANGE_DELTA)
				{
					return;
				}

				slider.setValue(monBright);
				mon.setBrightness(monBright);

				oldBright = monBright;
//				trayIcon.displayMessage("Monitor brightness", "was adjusted to " + monBright, TrayIcon.MessageType.INFO);
			}
		});

		ChangeListener changeListener = new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				JSlider source;
				source = (JSlider) e.getSource();
				mon.setBrightness(source.getValue());
			}
		};

		slider.addChangeListener(changeListener);
		Border border = BorderFactory.createTitledBorder(mon.getName());
		border = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), border);
		panel.setBorder(border);
		panel.add(slider, BorderLayout.CENTER);
		panel.add(checkbox, BorderLayout.SOUTH);
		rightPanel.add(panel);
	}

}
