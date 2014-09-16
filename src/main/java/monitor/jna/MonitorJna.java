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

import monitor.Monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.platform.win32.Dxva2;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class MonitorJna implements Monitor, AutoCloseable
{
	private static final Logger logger = LoggerFactory.getLogger(MonitorJna.class);
	
	private final HANDLE handle;
	private String name;

	private int minBright;
	private int maxBright;

	private int curBright;
	
	/**
	 * @param hPhysicalMonitor Handle to the physical monitor.
	 * @param szPhysicalMonitorDescription 
	 */
	public MonitorJna(HANDLE hPhysicalMonitor, String szPhysicalMonitorDescription)
	{
		this.handle = hPhysicalMonitor;
		this.name = szPhysicalMonitorDescription;

		DWORDByReference minBrightness = new DWORDByReference();
		DWORDByReference curBrightness = new DWORDByReference();
		DWORDByReference maxBrightness = new DWORDByReference();
		
		check(Dxva2.INSTANCE.GetMonitorBrightness(handle, minBrightness, curBrightness, maxBrightness));
		
		minBright = minBrightness.getValue().intValue();
		maxBright = maxBrightness.getValue().intValue();

		curBright = curBrightness.getValue().intValue();
		
	}

	@Override
	public int convertLuminance(int avgLum)
	{
		return minBright + avgLum * (maxBright - minBright) / 255;
	}

	@Override
	public int getBrightness() throws Win32Exception
	{
		DWORDByReference minBrightness = new DWORDByReference();
		DWORDByReference curBrightness = new DWORDByReference();
		DWORDByReference maxBrightness = new DWORDByReference();
		
		check(Dxva2.INSTANCE.GetMonitorBrightness(handle, minBrightness, curBrightness, maxBrightness));
		
//		return curBright;
		return curBrightness.getValue().intValue();
	}
	
	private void check(BOOL retVal)
	{
		if (!retVal.booleanValue())
		{
			int err = Kernel32.INSTANCE.GetLastError();
			if (err != 0)
				throw new Win32Exception(err);
		}
	}

	@Override
	public void setBrightness(int i)
	{
		logger.info("Setting brightness {} for monitor {}", i, name);
		
//		check(Dxva2.INSTANCE.SetMonitorBrightness(handle, i));
		
		curBright = i;
	}

	@Override
	public void close() throws Exception
	{
		Dxva2.INSTANCE.DestroyPhysicalMonitor(handle);
	}

}
