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

package monitor.info;

import monitor.Dxva2;
import monitor.HighLevelMonitorConfigurationAPI.MC_DISPLAY_TECHNOLOGY_TYPE;
import monitor.MyUser32;
import monitor.MyWinUser.HMONITOR;
import monitor.MyWinUser.MONITORENUMPROC;
import monitor.MyWinUser.MONITORINFOEX;
import monitor.PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.RECT;

public class MyMain
{
	public static void main(String[] args)
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
				
				for (int i = 0; i < monitorCount; i++)
				{
					System.out.println(physMons[i].szPhysicalMonitorDescription);
				
					MC_DISPLAY_TECHNOLOGY_TYPE.ByReference techType = new MC_DISPLAY_TECHNOLOGY_TYPE.ByReference();
					Dxva2.INSTANCE.GetMonitorTechnologyType(physMons[0].hPhysicalMonitor, techType);
					System.out.println("TECHTYPE: " + techType.getValue());
				
					DWORDByReference caps = new DWORDByReference();
					DWORDByReference temps = new DWORDByReference();
					BOOL ret = Dxva2.INSTANCE.GetMonitorCapabilities(physMons[0].hPhysicalMonitor, caps, temps);
					System.out.println(caps.getValue());
					System.out.println(temps.getValue());
					
					DWORDByReference pdwMinimumBrightness = new DWORDByReference();
					DWORDByReference pdwCurrentBrightness = new DWORDByReference();
					DWORDByReference pdwMaximumBrightness = new DWORDByReference();
					ret = Dxva2.INSTANCE.GetMonitorBrightness(physMons[0].hPhysicalMonitor, pdwMinimumBrightness, pdwCurrentBrightness, pdwMaximumBrightness);
					int err = Kernel32.INSTANCE.GetLastError();
					if (err != 0)
						throw new Win32Exception(err);
					
					System.out.println("ERROR " + err);
					System.out.println("RETURN2 " + ret.intValue());
					
					System.out.println(pdwMinimumBrightness.getValue());
					System.out.println(pdwCurrentBrightness.getValue());
					System.out.println(pdwMaximumBrightness.getValue());
				}
				
//				ret = dxvga.INSTANCE.SetMonitorBrightness(physMons[0].hPhysicalMonitor, pdwCurrentBrightness.getValue() + 5);

				return 1;
			}
			
		}, new LPARAM(0));
	}
}
