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

package monitor.jna;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import monitor.Monitor;
import monitor.MonitorController;

import com.sun.jna.platform.win32.Dxva2;
import com.sun.jna.platform.win32.PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.WinUser.MONITORENUMPROC;
import com.sun.jna.platform.win32.WinUser.MONITORINFOEX;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class MonitorControllerJna implements MonitorController
{
	final Collection<Monitor> monitors = new ArrayList<Monitor>();

	public MonitorControllerJna()
	{
		System.out.println("Monitors: " + User32.INSTANCE.GetSystemMetrics(User32.SM_CMONITORS));
		
		User32.INSTANCE.EnumDisplayMonitors(null, null, new MONITORENUMPROC() {

			@Override
			public int apply(HMONITOR hMonitor, HDC hdc, RECT rect, LPARAM lparam)
			{
				System.out.println("Monitor handle: " + hMonitor);

				MONITORINFOEX info = new MONITORINFOEX();
				User32.INSTANCE.GetMonitorInfo(hMonitor, info);
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
