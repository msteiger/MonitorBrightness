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

package jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinNT.HANDLE;

/**
 * Partly conversion of WinUser.h v7.0A
 * Multimonitor API
 * @author Martin Steiger
 */
public interface MyWinUser
{
	/**
	 * Each physical display is represented by a monitor handle of type HMONITOR. A valid HMONITOR 
	 * is guaranteed to be non-NULL. A physical display has the same HMONITOR as long as it is part 
	 * of the desktop.
	 */
	public class HMONITOR extends HANDLE {
		
        /**
         * Instantiates a new HMONITOR.
         */
        public HMONITOR() 
        {
        }

        /**
         * Instantiates a new HMONITOR.
         * @param p the pointer
         */
        public HMONITOR(Pointer p) 
        {
            super(p);
        }
	}
	
	
	/**
	 * Returns NULL.
	 */
	final int MONITOR_DEFAULTTONULL =        0x00000000;
	
	/**
	 * Returns a handle to the primary display monitor.
	 */
	final int MONITOR_DEFAULTTOPRIMARY =     0x00000001;
	
	/**
	 * Returns a handle to the display monitor that is nearest to the window.
	 */
	final int MONITOR_DEFAULTTONEAREST =     0x00000002;

	/**
	 * This is the primary display monitor.
	 */
	final int MONITORINFOF_PRIMARY =         0x00000001;

	/**
	 * Length of the device name in MONITORINFOEX
	 */
	final int CCHDEVICENAME =  32;

	/**
	 * The MONITORINFO structure contains information about a display monitor.<br/>
	 * The {@link MyUser32#GetMonitorInfo(HMONITOR, MONITORINFO)} function stores 
	 * information into a MONITORINFO structure<br/><br/>
	 * The MONITORINFO structure is a subset of the MONITORINFOEX structure. 	 
	 */
	class MONITORINFO extends Structure
	{
		/**
		 * The size, in bytes, of the structure.
		 */
	    public int     cbSize = size();
	    
	    /**
	     * Specifies the display monitor rectangle, expressed in virtual-screen coordinates. 
	     * Note that if the monitor is not the primary display monitor, some of the 
	     * rectangle's coordinates may be negative values.
	     */
	    public RECT    rcMonitor;
	    
	    /**
	     * Specifies the work area rectangle of the display monitor that can be used by 
	     * applications, expressed in virtual-screen coordinates. Windows uses this rectangle 
	     * to maximize an application on the monitor. The rest of the area in rcMonitor 
	     * contains system windows such as the task bar and side bars. Note that if the 
	     * monitor is not the primary display monitor, some of the rectangle's coordinates 
	     * may be negative values.
	     */
	    public RECT    rcWork;
	    
	    /**
	     * The attributes of the display monitor. This member can be the following value.
	     * <li>MONITORINFOF_PRIMARY</li>
	     */
	    public int     dwFlags;
	    
		@Override
		protected List<String> getFieldOrder()
		{
			return Arrays.asList("cbSize", "rcMonitor", "rcWork", "dwFlags");
		}
	}

	/**
	 * The MONITORINFOEX structure contains information about a display monitor.<br/>
	 * The {@link MyUser32#GetMonitorInfo(HMONITOR, MONITORINFOEX)} function stores 
	 * information into a MONITORINFOEX structure<br/><br/>
	 * The MONITORINFOEX structure is a superset of the MONITORINFO structure. 
	 * The MONITORINFOEX structure adds a string member to contain a name for the display monitor. 
	 */
	class MONITORINFOEX extends Structure
	{
		/**
		 * The size, in bytes, of the structure.
		 */
	    public int     cbSize;
	    
	    /**
	     * Specifies the display monitor rectangle, expressed in virtual-screen coordinates. 
	     * Note that if the monitor is not the primary display monitor, some of the 
	     * rectangle's coordinates may be negative values.
	     */
	    public RECT    rcMonitor;
	    
	    /**
	     * Specifies the work area rectangle of the display monitor that can be used by 
	     * applications, expressed in virtual-screen coordinates. Windows uses this rectangle 
	     * to maximize an application on the monitor. The rest of the area in rcMonitor 
	     * contains system windows such as the task bar and side bars. Note that if the 
	     * monitor is not the primary display monitor, some of the rectangle's coordinates 
	     * may be negative values.
	     */
	    public RECT    rcWork;
	    
	    /**
	     * The attributes of the display monitor. This member can be the following value.
	     * <li>MONITORINFOF_PRIMARY</li>
	     */
	    public int     dwFlags;
	    
	    /**
	     * A string that specifies the device name of the monitor being used. Most 
	     * applications have no use for a display monitor name, and so can save some bytes 
	     * by using a MONITORINFO structure.
	     */
	    public char[]  szDevice;
	    
	    public MONITORINFOEX()
		{
	    	szDevice = new char[CCHDEVICENAME];
	    	cbSize = size();
		}

		@Override
		protected List<String> getFieldOrder()
		{
			return Arrays.asList("cbSize", "rcMonitor", "rcWork", "dwFlags", "szDevice");
		}
	}

	/**
	 * An application-defined callback function that is called by the {@link MyUser32#EnumDisplayMonitors} function.
	 * <br/><br/>
	 * You can use the EnumDisplayMonitors function to enumerate the set of display monitors that intersect 
	 * the visible region of a specified device context and, optionally, a clipping rectangle. To do this, 
	 * set the hdc parameter to a non-NULL value, and set the lprcClip parameter as needed.
	 * <br/><br/>
	 * You can also use the EnumDisplayMonitors function to enumerate one or more of the display monitors on 
	 * the desktop, without supplying a device context. To do this, set the hdc parameter of 
	 * EnumDisplayMonitors to NULL and set the lprcClip parameter as needed.
	 * <br/><br/>
	 * In all cases, EnumDisplayMonitors calls a specified MonitorEnumProc function once for each display 
	 * monitor in the calculated enumeration set. The MonitorEnumProc function always receives a handle to 
	 * the display monitor. If the hdc parameter of EnumDisplayMonitors is non-NULL, the MonitorEnumProc 
	 * function also receives a handle to a device context whose color format is appropriate for the 
	 * display monitor. You can then paint into the device context in a manner that is optimal for the 
	 * display monitor.
	 */
	public interface MONITORENUMPROC extends Callback
	{
		/**
		 * @param hMonitor A handle to the display monitor. This value will always be non-NULL.
		 * @param hdcMonitor A handle to a device context. The device context has color attributes that are 
		 *        appropriate for the display monitor identified by hMonitor. The clipping area of the device 
		 *        context is set to the intersection of the visible region of the device context identified 
		 *        by the hdc parameter of EnumDisplayMonitors, the rectangle pointed to by the lprcClip 
		 *        parameter of EnumDisplayMonitors, and the display monitor rectangle.
		 * @param lprcMonitor A pointer to a RECT structure. If hdcMonitor is non-NULL, this rectangle is the 
		 *        intersection of the clipping area of the device context identified by hdcMonitor and the 
		 *        display monitor rectangle. The rectangle coordinates are device-context coordinates.
		 *        If hdcMonitor is NULL, this rectangle is the display monitor rectangle. The rectangle 
		 *        coordinates are virtual-screen coordinates.
		 * @param dwData Application-defined data that EnumDisplayMonitors passes directly to the enumeration 
		 *        function.
		 * @return To continue the enumeration, return TRUE. To stop the enumeration, return FALSE.
		 */
		public int apply(HMONITOR hMonitor, HDC hdcMonitor, RECT lprcMonitor, LPARAM dwData);
	}

}
