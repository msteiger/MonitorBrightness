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

package monitor;

import monitor.MyWinUser.MONITORENUMPROC;
import monitor.MyWinUser.MONITORINFO;
import monitor.dxvga.HMONITOR;
import monitor.dxvga.LPDWORD;
import monitor.dxvga.PHYSICAL_MONITOR;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.ptr.IntByReference;

public class MyMain
{
	public static void main(String[] args)
	{
		System.out.println(User32.INSTANCE.GetSystemMetrics(User32.SM_CMONITORS));
		
		System.out.println(MyWinUser.INSTANCE);
		MyWinUser.INSTANCE.EnumDisplayMonitors(null, null, new MONITORENUMPROC() {

			@Override
			public int apply(HMONITOR m, HDC hdc, RECT rect, LPARAM lparam)
			{
				System.out.println(m);

				MONITORINFO info = new MONITORINFO();
				MyWinUser.INSTANCE.GetMonitorInfo(m, info);
				System.out.println(info.rcMonitor);
				
				
				PHYSICAL_MONITOR[] physMons = new PHYSICAL_MONITOR[] { new PHYSICAL_MONITOR() };
				dxvga.INSTANCE.GetPhysicalMonitorsFromHMONITOR(m, 1, physMons);
				System.out.println(physMons[0].szPhysicalMonitorDescription);
				
				LPDWORD caps = new LPDWORD();
				LPDWORD temps = new LPDWORD();
				BOOL ret = dxvga.INSTANCE.GetMonitorCapabilities(physMons[0].hPhysicalMonitor, caps, temps);
				System.out.println("RETURN " + ret.intValue());
				System.out.println(caps.getValue());
				System.out.println(temps.getValue());
				
				IntByReference pdwMinimumBrightness = new IntByReference();
				IntByReference pdwCurrentBrightness = new IntByReference();
				IntByReference pdwMaximumBrightness = new IntByReference();
				ret = dxvga.INSTANCE.GetMonitorBrightness(physMons[0].hPhysicalMonitor, pdwMinimumBrightness, pdwCurrentBrightness, pdwMaximumBrightness);
				int err = Kernel32.INSTANCE.GetLastError();
				if (err != 0)
					throw new Win32Exception(err);
				
				System.out.println("ERROR " + err);
				System.out.println("RETURN2 " + ret.intValue());
				
				System.out.println(pdwMinimumBrightness.getValue());
				System.out.println(pdwCurrentBrightness.getValue());
				System.out.println(pdwMaximumBrightness.getValue());

				
//				ret = dxvga.INSTANCE.SetMonitorBrightness(physMons[0].hPhysicalMonitor, pdwCurrentBrightness.getValue() + 5);

				return 1;
			}
			
		}, new LPARAM(0));
	}
}
