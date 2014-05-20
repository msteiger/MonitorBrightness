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

import jna.MyWinUser.HMONITOR;
import jna.MyWinUser.MONITORENUMPROC;
import jna.MyWinUser.MONITORINFO;
import jna.MyWinUser.MONITORINFOEX;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Multimonitor API
 * @author Martin Steiger
 */
public interface MyUser32 extends StdCallLibrary
{
	/**
	 * The JNA instance of the native user32.dll library
	 */
	MyUser32 INSTANCE = (MyUser32) 
			Native.loadLibrary("User32", MyUser32.class, W32APIOptions.DEFAULT_OPTIONS);


	/**
	 * Retrieves a handle to the display monitor that contains a specified point.
	 * @param pt A POINT structure that specifies the point of interest in virtual-screen 
	 *        coordinates.
	 * @param dwFlags Determines the function's return value if the window does not intersect 
	 *        any display monitor. This parameter can be one of the following values.
	 *        <li>MONITOR_DEFAULTTONEAREST</li>
	 *        <li>MONITOR_DEFAULTTONULL</li>
	 *        <li>MONITOR_DEFAULTTOPRIMARY</li>
	 * @return If the point is contained by a display monitor, the return value is an HMONITOR 
	 *        handle to that display monitor. If the point is not contained by a display monitor, 
	 *        the return value depends on the value of dwFlags.
	 */
	HMONITOR MonitorFromPoint(POINT pt, int dwFlags);

	/**
	 * Retrieves a handle to the display monitor that has the largest area of intersection with 
	 * a specified rectangle.
	 * @param lprc A pointer to a RECT structure that specifies the rectangle of interest in 
	 *        virtual-screen coordinates.
	 * @param dwFlags Determines the function's return value if the window does not intersect 
	 *        any display monitor. This parameter can be one of the following values.
	 *        <li>MONITOR_DEFAULTTONEAREST</li>
	 *        <li>MONITOR_DEFAULTTONULL</li>
	 *        <li>MONITOR_DEFAULTTOPRIMARY</li>
	 * @return If the rectangle intersects one or more display monitor rectangles, the return 
	 *        value is an HMONITOR handle to the display monitor that has the largest area of 
	 *        intersection with the rectangle. If the rectangle does not intersect a display 
	 *        monitor, the return value depends on the value of dwFlags.
	 */
	HMONITOR MonitorFromRect(RECT lprc, int dwFlags);

	/**
	 * Retrieves a handle to the display monitor that has the largest area of intersection with 
	 * the bounding rectangle of a specified window.
	 * <br/><br/>
	 * If the window is currently minimized, MonitorFromWindow uses the rectangle of the window 
	 * before it was minimized.
	 * @param hwnd A handle to the window of interest.
	 * @param dwFlags Determines the function's return value if the window does not intersect 
	 *        any display monitor. This parameter can be one of the following values.
	 *        <li>MONITOR_DEFAULTTONEAREST</li>
	 *        <li>MONITOR_DEFAULTTONULL</li>
	 *        <li>MONITOR_DEFAULTTOPRIMARY</li>
	 * @return If the window intersects one or more display monitor rectangles, the return value
	 *        is an HMONITOR handle to the display monitor that has the largest area of 
	 *        intersection with the window. If the window does not intersect a display monitor, 
	 *        the return value depends on the value of dwFlags.
	 */
	HMONITOR MonitorFromWindow(HWND hwnd, int dwFlags);
	
	/**
	 * Retrieves information about a display monitor.
	 * @param hMonitor A handle to the display monitor of interest.
	 * @param lpmi A pointer to a {@link MyWinUser.MONITORINFO} structure that receives information about 
	 *        the specified display monitor.
	 * @return If the function succeeds, the return value is nonzero. If the function 
	 *        fails, the return value is zero.
	 */
	BOOL GetMonitorInfo(HMONITOR hMonitor, MONITORINFO lpmi);
	
	/**
	 * Retrieves information about a display monitor.
	 * @param hMonitor A handle to the display monitor of interest.
	 * @param lpmi A pointer to a {@link MyWinUser.MONITORINFOEX} structure that receives information about 
	 *        the specified display monitor.
	 * @return If the function succeeds, the return value is nonzero. If the function 
	 *        fails, the return value is zero.
	 */
	BOOL GetMonitorInfo(HMONITOR hMonitor, MONITORINFOEX lpmi);

	/**
	 * Enumerates display monitors (including invisible pseudo-monitors associated with the mirroring drivers) 
	 * that intersect a region formed by the intersection of a specified clipping rectangle and the visible 
	 * region of a device context. EnumDisplayMonitors calls an application-defined MonitorEnumProc callback 
	 * function once for each monitor that is enumerated. Note that GetSystemMetrics (SM_CMONITORS) counts 
	 * only the display monitors.
	 * @param hdc A handle to a display device context that defines the visible region of interest. If this 
	 *        parameter is NULL, the hdcMonitor parameter passed to the callback function will be NULL, and 
	 *        the visible region of interest is the virtual screen that encompasses all the displays on the 
	 *        desktop.
	 * @param lprcClip A pointer to a RECT structure that specifies a clipping rectangle. The region of 
	 *        interest is the intersection of the clipping rectangle with the visible region specified by hdc.
	 *        If hdc is non-NULL, the coordinates of the clipping rectangle are relative to the origin of the 
	 *        hdc. If hdc is NULL, the coordinates are virtual-screen coordinates. This parameter can be NULL 
	 *        if you don't want to clip the region specified by hdc.
	 * @param lpfnEnum A pointer to an application-defined callback function.
	 * @param dwData Application-defined data that EnumDisplayMonitors passes directly to the lpfnEnum function.
	 * @return If the function succeeds, the return value is nonzero. If the function fails, the return value 
	 *        is zero.
	 */
	BOOL EnumDisplayMonitors(HDC hdc, RECT lprcClip, MONITORENUMPROC lpfnEnum, LPARAM dwData);	
}
