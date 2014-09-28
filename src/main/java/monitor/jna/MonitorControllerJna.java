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
import java.util.Set;

import jna.Dxva2;
import jna.HighLevelMonitorConfigurationAPI;
import jna.HighLevelMonitorConfigurationAPI.MC_CAPS;
import jna.MyUser32;
import jna.MyWinUser.HMONITOR;
import jna.MyWinUser.MONITORENUMPROC;
import jna.PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR;
import jna.util.EnumUtils;
import monitor.Monitor;
import monitor.MonitorController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser;

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
		logger.debug("Number of connected monitors: " + User32.INSTANCE.GetSystemMetrics(WinUser.SM_CMONITORS));

		MyUser32.INSTANCE.EnumDisplayMonitors(null, null, new MONITORENUMPROC() {

			@Override
			public int apply(HMONITOR hMonitor, HDC hdc, RECT rect, LPARAM lparam)
			{
				DWORDByReference pdwNumberOfPhysicalMonitors = new DWORDByReference();
				Dxva2.INSTANCE.GetNumberOfPhysicalMonitorsFromHMONITOR(hMonitor, pdwNumberOfPhysicalMonitors);
				int monitorCount = pdwNumberOfPhysicalMonitors.getValue().intValue();

				PHYSICAL_MONITOR[] physMons = new PHYSICAL_MONITOR[monitorCount];
				Dxva2.INSTANCE.GetPhysicalMonitorsFromHMONITOR(hMonitor, monitorCount, physMons);

				for (PHYSICAL_MONITOR mon : physMons)
				{
					addMonitor(mon);
				}

				return 1;
			}
		}, new LPARAM(0));
	}

	/**
	 * Package private - for enumeration only!
	 */
	void addMonitor(PHYSICAL_MONITOR mon)
	{
		String desc = new String(mon.szPhysicalMonitorDescription);
		logger.debug("Found monitor {}", desc.trim());

		DWORDByReference tempsVal = new DWORDByReference();
		DWORDByReference capVal = new DWORDByReference();
		Dxva2.INSTANCE.GetMonitorCapabilities(mon.hPhysicalMonitor, capVal, tempsVal);

		Set<MC_CAPS> caps = EnumUtils.setFromInteger(capVal.getValue().intValue(), HighLevelMonitorConfigurationAPI.MC_CAPS.class);
		logger.debug("CAPS " + caps);

		if (caps.contains(MC_CAPS.MC_CAPS_BRIGHTNESS))
		{
			@SuppressWarnings("resource")
			MonitorJna monitor = new MonitorJna(mon.hPhysicalMonitor, desc);

			monitors.add(monitor);
		}
		else
		{
			logger.warn("Monitor does not support changing the brightness. Try installing vendor drivers and enable DDC/CI.");
		}
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
