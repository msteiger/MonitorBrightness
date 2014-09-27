// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Information Visualization and Visual Analytics
//
// Copyright (c) Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package monitor.jna;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jna.Dxva2;
import jna.MyUser32;
import jna.MyWinUser.HMONITOR;
import jna.MyWinUser.MONITORENUMPROC;
import jna.MyWinUser.MONITORINFOEX;
import jna.PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser;

import monitor.Monitor;
import monitor.MonitorController;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class MonitorControllerJna implements MonitorController
{
	private static final Logger logger = LoggerFactory.getLogger(MonitorControllerJna.class);

	private final Collection<MonitorJna> monitors = new ArrayList<MonitorJna>();

	/**
	 * Enumerates all physical monitors
	 */
	public MonitorControllerJna()
	{
		logger.debug("Monitors: " + User32.INSTANCE.GetSystemMetrics(WinUser.SM_CMONITORS));

		MyUser32.INSTANCE.EnumDisplayMonitors(null, null, new MONITORENUMPROC() {

			@Override
			public int apply(HMONITOR hMonitor, HDC hdc, RECT rect, LPARAM lparam)
			{
				System.out.println("Monitor handle: " + hMonitor);

				MONITORINFOEX info = new MONITORINFOEX();
				MyUser32.INSTANCE.GetMonitorInfo(hMonitor, info);
				System.out.println(info.rcMonitor);

				DWORDByReference pdwNumberOfPhysicalMonitors = new DWORDByReference();
				Dxva2.INSTANCE.GetNumberOfPhysicalMonitorsFromHMONITOR(hMonitor, pdwNumberOfPhysicalMonitors);
				int monitorCount = pdwNumberOfPhysicalMonitors.getValue().intValue();

				System.out.println("Physical monitors for " + hMonitor + ": " + monitorCount);

				PHYSICAL_MONITOR[] physMons = new PHYSICAL_MONITOR[monitorCount];
				Dxva2.INSTANCE.GetPhysicalMonitorsFromHMONITOR(hMonitor, monitorCount, physMons);

				for (PHYSICAL_MONITOR mon : physMons)
				{
					String desc = new String(mon.szPhysicalMonitorDescription);
					@SuppressWarnings("resource")
					MonitorJna monitor = new MonitorJna(mon.hPhysicalMonitor, desc);
					addMonitor(monitor);
				}

				return 1;
			}
		}, new LPARAM(0));
	}

	/**
	 * Package private - for enumeration only!
	 */
	void addMonitor(MonitorJna monitor)
	{
		monitors.add(monitor);
	}

	@Override
	public Collection<? extends Monitor> getMonitors()
	{
		return Collections.unmodifiableCollection(monitors);
	}

	@Override
	public void close() throws Exception
	{
		for (MonitorJna monitor : monitors)
		{
			monitor.close();
		}
	}

}
