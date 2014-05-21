// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Information Visualization and Visual Analytics
//
// Copyright (c) Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package monitor.jna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jna.Dxva2;
import jna.MyUser32;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import monitor.Monitor;

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
