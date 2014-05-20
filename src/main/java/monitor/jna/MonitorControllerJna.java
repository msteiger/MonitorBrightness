// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Information Visualization and Visual Analytics
//
// Copyright (c) Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package monitor.jna;

import java.util.Collection;
import java.util.Collections;

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

import monitor.Monitor;
import monitor.MonitorController;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class MonitorControllerJna implements MonitorController
{
	private Collection<Monitor> monitors;

	public MonitorControllerJna()
	{
		System.out.println("Monitors: " + User32.INSTANCE.GetSystemMetrics(User32.SM_CMONITORS));
		
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
					monitors.add(new MonitorJna(mon.hPhysicalMonitor, desc));
				}
				
				return 1;
			}
		}, new LPARAM(0));
	}
	
	@Override
	public Collection<Monitor> getMonitors()
	{
		return Collections.unmodifiableCollection(monitors);
	}

}
