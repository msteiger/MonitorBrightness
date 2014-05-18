package monitor;

import java.util.Arrays;
import java.util.List;

import monitor.dxvga.HMONITOR;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface MyWinUser extends StdCallLibrary
{
	MyWinUser INSTANCE = (MyWinUser) 
			Native.loadLibrary("User32", MyWinUser.class, W32APIOptions.DEFAULT_OPTIONS);
	
	/*
	 * Multimonitor API.
	 */

	final int MONITOR_DEFAULTTONULL =        0x00000000;
	final int MONITOR_DEFAULTTOPRIMARY =     0x00000001;
	final int MONITOR_DEFAULTTONEAREST =     0x00000002;

	HMONITOR
	MonitorFromPoint(
	    POINT pt,
	    int dwFlags);

	HMONITOR
	MonitorFromRect(
	    RECT lprc,
	    int dwFlags);

	HMONITOR
	MonitorFromWindow(
	    HWND hwnd,
	    int dwFlags);

	final int MONITORINFOF_PRIMARY =         0x00000001;

	final int CCHDEVICENAME =  32;

	class MONITORINFO extends Structure
	{
	    public int cbSize = 40;
	    
	    public RECT    rcMonitor;
	    public RECT    rcWork;
	    public int   dwFlags;
	    
		@Override
		protected List<String> getFieldOrder()
		{
			return Arrays.asList("cbSize", "rcMonitor", "rcWork", "dwFlags");
		}
	}

	class MONITORINFOEXA extends MONITORINFO
	{
	    char[]        szDevice = new char[CCHDEVICENAME];
	};
	
	BOOL
	GetMonitorInfo(
	    HMONITOR hMonitor,
	    MONITORINFO lpmi);

	public interface MONITORENUMPROC extends Callback
	{
		public int apply(HMONITOR m, HDC hdc, RECT rect, LPARAM lparam);
	}

	BOOL
	EnumDisplayMonitors(
	    HDC hdc,
	    RECT lprcClip,
	    MONITORENUMPROC lpfnEnum,
	    LPARAM dwData);

}
