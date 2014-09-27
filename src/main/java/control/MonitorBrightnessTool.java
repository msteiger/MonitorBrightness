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

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import light.BrightnessConverter;
import light.CustomBrightnessConverter;
import light.DummyWebcamWrapper;
import light.SarxosWebcamWrapper;
import light.WebcamLuminance;
import light.WebcamWrapper;
import monitor.Monitor;
import monitor.MonitorController;
import monitor.jna.MonitorControllerJna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;

/**
 * The main class
 * @author Martin Steiger
 */
public class MonitorBrightnessTool
{
	private static final Logger logger = LoggerFactory.getLogger(MonitorBrightnessTool.class);

	private final MonitorController monitorControl = new MonitorControllerJna();

	private final WebcamWrapper webcamWrapper;

	/**
	 * @param args (ignored)
	 */
	public static void main(String[] args)
	{
		try
		{
			new MonitorBrightnessTool();
		}
		catch (IOException | AWTException e)
		{
			showError("Could not initialize application", e);
			return;
		}
	}

	public MonitorBrightnessTool() throws AWTException, IOException
	{
		Webcam webcam = Webcam.getDefault();

		if (webcam != null)
		{
			webcamWrapper = new SarxosWebcamWrapper(webcam, new WebcamLuminance());
		}
		else
		{
			logger.warn("No webcam has been detected - using dummy instead");
			webcamWrapper = new DummyWebcamWrapper();
		}

		final Timer timer = new Timer(100, null);
		timer.setInitialDelay(0);
        timer.start();

        final MainWindow frame = new MainWindow(webcamWrapper, timer);

		final CalibrationWindow calibWindow = new CalibrationWindow();

		createTray(frame, calibWindow, webcamWrapper, timer);

		final Point point1 = calibWindow.getPoint1();
		final Point point2 = calibWindow.getPoint2();

		BrightnessConverter converter = new CustomBrightnessConverter(point1, point2);

		for (final Monitor mon : monitorControl.getMonitors())
		{
			frame.addMonitor(mon, converter);
		}

		calibWindow.pack();
		calibWindow.setVisible(true);
		frame.pack();
		frame.setVisible(true);
	}

	public void createTray(final MainWindow frame, final JFrame drawFrame, final WebcamWrapper webcam, final Timer timer) throws IOException, AWTException
	{
		BufferedImage icon = loadImage("icons/lightbulb16.png");
		final TrayIcon trayIcon = new TrayIcon(icon, "Monitor Brightness Adjuster");
		trayIcon.setImageAutoSize(true);

        final SystemTray tray = SystemTray.getSystemTray();

		final PopupMenu popup = new PopupMenu("Config");
        final MenuItem settings = new MenuItem("Main Window");
		final CheckboxMenuItem cb = new CheckboxMenuItem("Auto-adjust all");
		final MenuItem graph = new MenuItem("Calibrate");
		final MenuItem exitItem = new MenuItem("Exit");

		settings.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(true);
			}
		});

		cb.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				boolean yesno = cb.getState();
				frame.autoAdjustAll(yesno);
			}
		});

		graph.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				drawFrame.setVisible(true);
			}
		});

		exitItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					timer.stop();
					monitorControl.close();
					webcamWrapper.close();
					frame.dispose();
					drawFrame.dispose();

					tray.remove(trayIcon);
					logger.info("Finished");
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
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
        tray.add(trayIcon);
	}

	private static BufferedImage loadImage(String fname) throws IOException
	{
		String fullPath = "/" + fname;
		URL rsc = MonitorBrightnessTool.class.getResource(fullPath);
		if (rsc == null)
		{
			throw new FileNotFoundException(fullPath);
		}
		return ImageIO.read(rsc);
	}

	private static void showError(String text, Exception e)
	{
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String msg = text + System.lineSeparator() + sw.toString();

		JOptionPane.showMessageDialog(null, msg, "An error occurred", JOptionPane.ERROR_MESSAGE);
	}

}
