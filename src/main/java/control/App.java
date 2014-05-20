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
import java.awt.Component;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
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

import light.LuminanceProvider;
import light.WebcamLuminance;
import monitor.Monitor;
import monitor.MonitorController;
import monitor.jna.MonitorControllerJna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class App implements AutoCloseable
{
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	
	private TrayIcon trayIcon = null;
	private Thread thread;
	
	private static final int QUEUE_LENGTH = 10;

	/**
	 * Minium time between two brightness change events in seconds
	 */
	private static final double MAX_CHANGE_FREQ = 10;
	
	/**
	 * Brightness will be changed only if difference is large enough
	 */
	private static final double MIN_BRIGHTNESS_CHANGE_DELTA = 5;
	
	final Deque<Integer> avgLuminances = new ArrayDeque<Integer>(QUEUE_LENGTH);

	private final LuminanceProvider luminanceProvider = new WebcamLuminance();
	private final MonitorController monitorControl = new MonitorControllerJna();

	final AtomicBoolean quit = new AtomicBoolean(false);

	private Map<Monitor, Integer> targetBrightnessMap = Maps.newHashMap();
	
	public App() throws AWTException, IOException
	{
		if (!SystemTray.isSupported())
			throw new UnsupportedOperationException("No system tray!");

		BufferedImage icon = loadImage("icons/lightbulb16.png");
	
		trayIcon = new TrayIcon(icon, "Monitor Brightness Adjuster");
		trayIcon.setImageAutoSize(true);

		SystemTray.getSystemTray().add(trayIcon);

		trayIcon.setPopupMenu(createPopup());
	}

	public void run()
	{
		while (!quit.get())
		{
			if (avgLuminances.size() >= QUEUE_LENGTH)
				avgLuminances.removeFirst();

			avgLuminances.addLast(luminanceProvider.getLuminance());
			
			logger.debug("Average image luminance queue {}", avgLuminances);
			
			int avgLum = 0;
			for (Integer lum : avgLuminances)
			{
				avgLum += lum.intValue();
			}
			
			for (Monitor monitor : monitorControl.getMonitors())
			{
				int currentBrightness = monitor.getBrightness();
				int desiredBrightness = monitor.convertLuminance(avgLum);
				int brightnessDelta = desiredBrightness - currentBrightness;
				
				if (Math.abs(brightnessDelta) > MIN_BRIGHTNESS_CHANGE_DELTA)
				{
					targetBrightnessMap.put(monitor, desiredBrightness);
				}
				
				int targetBrightness = targetBrightnessMap.get(monitor);

				if (currentBrightness != targetBrightness)
				{
					int delta = Math.min(2, Math.max(-2, targetBrightness - currentBrightness));
					
					monitor.setBrightness(currentBrightness + delta);
				}
			}

			try
			{
				Thread.sleep(2000);
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
	}
	
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

		luminanceProvider.close();

		SystemTray.getSystemTray().remove(trayIcon);
		logger.info("Finished");
	}

}
