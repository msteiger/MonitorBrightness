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

import static control.AllesSetOptimalMonitorBrightness.geniousTray;
import static control.AllesSetOptimalMonitorBrightness.setupTimer;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import light.LuminanceProvider;
import light.WebcamLuminance;
import monitor.Monitor;
import monitor.MonitorController;
import monitor.jna.MonitorControllerJna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;
import com.google.common.collect.Maps;

public class App implements AutoCloseable
{
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	private TrayIcon trayIcon = null;
	private Thread thread;

	/**
	 * Minium time between two brightness change events in seconds
	 */
	private static final double MAX_CHANGE_FREQ = 10;

	/**
	 * Brightness will be changed only if difference is large enough
	 */
	private static final double MIN_BRIGHTNESS_CHANGE_DELTA = 5;


	private final LuminanceProvider luminanceProvider;
	private final MonitorController monitorControl = new MonitorControllerJna();

	final AtomicBoolean quit = new AtomicBoolean(false);

	private Map<Monitor, Integer> targetBrightnessMap = Maps.newHashMap();

	public App() throws AWTException, IOException
	{
		Webcam webcam = Webcam.getDefault();

		if (webcam == null)
			throw new IllegalStateException("No webcam has been detected!");

		luminanceProvider = new WebcamLuminance(webcam);

		final BrightnessActionLi timerListener = new BrightnessActionLi(webcam);
		final Timer timer = setupTimer(timerListener);
		final MainWindow frame = new MainWindow();
		frame.setupVideo(timer, timerListener);

		final CalibrationWindow drawFenster = new CalibrationWindow();
		// final JFrame drawFrame = drawFrame();
		// final DrawPanel drawPanel = drawPanel();
		trayIcon = geniousTray(frame, drawFenster, webcam);

		final Point point1 = drawFenster.getPoint1();
		final Point point2 = drawFenster.getPoint2();

		for (final Monitor mon : monitorControl.getMonitors())
			frame.addMonitor(mon, timer, timerListener, point1, point2, trayIcon);

		drawFenster.pack();
		drawFenster.setVisible(true);
		frame.pack();
		frame.setVisible(true);
	}

//	public void run()
//	{
//		for (Monitor monitor : monitorControl.getMonitors())
//		{
//			int currentBrightness = monitor.getBrightness();
//			targetBrightnessMap.put(monitor, currentBrightness);
//		}
//
//		while (!quit.get())
//		{
//			for (Monitor monitor : monitorControl.getMonitors())
//			{
//				int camBright = equalizedProvider.get();
//				int monBright = 		return minBright + avgLum * (maxBright - minBright) / 255;
//
//				int currentBrightness = monitor.getBrightness();
//				int desiredBrightness = monitor.convertLuminance(avgLum);
//				int brightnessDelta = desiredBrightness - currentBrightness;
//
//				if (Math.abs(brightnessDelta) > MIN_BRIGHTNESS_CHANGE_DELTA)
//				{
//					targetBrightnessMap.put(monitor, desiredBrightness);
//					String infoText = "Adjusting brightness to " + desiredBrightness;
//					trayIcon.displayMessage("Monitor Brightness", infoText, MessageType.INFO);
//				}
//
//				int targetBrightness = targetBrightnessMap.get(monitor);
//
//				if (currentBrightness != targetBrightness)
//				{
//					int delta = Math.min(2, Math.max(-2, targetBrightness - currentBrightness));
//
//					monitor.setBrightness(currentBrightness + delta);
//				}
//			}
//
//			try
//			{
//				Thread.sleep(2000);
//			}
//			catch (InterruptedException e)
//			{
//				Thread.currentThread().interrupt();
//			}
//		}
//	}

	private PopupMenu createPopup()
	{
		final MenuItem config = new MenuItem("Config");
		config.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
//				quit.set(true);
			}
		});

		PopupMenu menu = new PopupMenu();

		MenuItem quitItem = new MenuItem("Quit");
		quitItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				quit.set(true);
			}
		});

		menu.add(config);
		menu.addSeparator();
		menu.add(quitItem);
		return menu;
	}

	@Override
	public void close() throws Exception
	{
		try
		{
			if (thread != null)
			{
				logger.info("Waiting for thread to terminate");
				thread.join();
			}
		}
		catch (InterruptedException e)
		{
			logger.warn("Thread interrupted", e);
			Thread.currentThread().interrupt();
		}

		monitorControl.close();
		luminanceProvider.close();

		SystemTray.getSystemTray().remove(trayIcon);
		logger.info("Finished");
	}

}
