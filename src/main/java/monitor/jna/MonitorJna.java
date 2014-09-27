// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Information Visualization and Visual Analytics
//
// Copyright (c) Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package monitor.jna;

import jna.Dxva2;
import monitor.Monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;

/**
 * Connects to the physical monitor through JNA (v4.2+)
 * @author Martin Steiger
 */
public class MonitorJna implements Monitor, AutoCloseable
{
	private static final Logger logger = LoggerFactory.getLogger(MonitorJna.class);

	private final HANDLE handle;
	private final String name;

	private final int minBright;
	private final int maxBright;

	/**
	 * @param hPhysicalMonitor Handle to the physical monitor.
	 * @param szPhysicalMonitorDescription the physical monitor name
	 */
	public MonitorJna(HANDLE hPhysicalMonitor, String szPhysicalMonitorDescription)
	{
		this.handle = hPhysicalMonitor;
		this.name = szPhysicalMonitorDescription.trim(); // filled up to 255 chars with spaces

		DWORDByReference minBrightness = new DWORDByReference();
		DWORDByReference curBrightness = new DWORDByReference();
		DWORDByReference maxBrightness = new DWORDByReference();

		check(Dxva2.INSTANCE.GetMonitorBrightness(handle, minBrightness, curBrightness, maxBrightness));

		minBright = minBrightness.getValue().intValue();
		maxBright = maxBrightness.getValue().intValue();
	}

	@Override
	public int getBrightness() throws Win32Exception
	{
		DWORDByReference minBrightness = new DWORDByReference();
		DWORDByReference curBrightness = new DWORDByReference();
		DWORDByReference maxBrightness = new DWORDByReference();

		check(Dxva2.INSTANCE.GetMonitorBrightness(handle, minBrightness, curBrightness, maxBrightness));

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

		check(Dxva2.INSTANCE.SetMonitorBrightness(handle, i));
	}

	@Override
	public void close() throws Exception
	{
		Dxva2.INSTANCE.DestroyPhysicalMonitor(handle);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public int getMinBrightness()
	{
		return minBright;
	}

	@Override
	public int getMaxBrightness()
	{
		return maxBright;
	}

}
