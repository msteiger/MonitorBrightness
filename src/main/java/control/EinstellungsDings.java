package control;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



import jna.Dxva2;
import jna.MyUser32;
import jna.MyWinUser.HMONITOR;
import jna.MyWinUser.MONITORENUMPROC;
import jna.MyWinUser.MONITORINFOEX;
import jna.PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR;





import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class EinstellungsDings {
	
	
	static void enumerate(HMONITOR hMonitor)
	{
		System.out.println("Found HMONITOR: " + hMonitor.getPointer().toString());

		MONITORINFOEX info = new MONITORINFOEX();
		MyUser32.INSTANCE.GetMonitorInfo(hMonitor, info);
		
		DWORDByReference pdwNumberOfPhysicalMonitors = new DWORDByReference();
		Dxva2.INSTANCE.GetNumberOfPhysicalMonitorsFromHMONITOR(hMonitor, pdwNumberOfPhysicalMonitors);
		int monitorCount = pdwNumberOfPhysicalMonitors.getValue().intValue();
		
		PHYSICAL_MONITOR[] physMons = new PHYSICAL_MONITOR[monitorCount];
		Dxva2.INSTANCE.GetPhysicalMonitorsFromHMONITOR(hMonitor, monitorCount, physMons);
		
		for (int i = 0; i < monitorCount; i++)
		{
			HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;
			System.out.println("Monitor " + i + " - " + new String(physMons[i].szPhysicalMonitorDescription));
		
			enumeratePhysicalMonitor(hPhysicalMonitor);
			break;
		}
	}
	
	private static void enumeratePhysicalMonitor(HANDLE hPhysicalMonitor)
	{
				// Brightness
		DWORDByReference pdwMinimumBrightness = new DWORDByReference();
		DWORDByReference pdwCurrentBrightness = new DWORDByReference();
		DWORDByReference pdwMaximumBrightness = new DWORDByReference();
		Dxva2.INSTANCE.GetMonitorBrightness(hPhysicalMonitor, pdwMinimumBrightness, pdwCurrentBrightness, pdwMaximumBrightness);
		
		System.out.println("Brightness Min: " + pdwMinimumBrightness.getValue());
		System.out.println("Brightness Current: " + pdwCurrentBrightness.getValue());
		System.out.println("Brightness Max: " + pdwMaximumBrightness.getValue());
		
		Dxva2.INSTANCE.SetMonitorBrightness(hPhysicalMonitor, pdwCurrentBrightness.getValue().intValue() + 10);

		System.out.println("--------------------------------------");
	}
	
	public static void main(String[] args) {
		System.out.println("Installed Physical Monitors: " + User32.INSTANCE.GetSystemMetrics(User32.SM_CMONITORS));
		
		MyUser32.INSTANCE.EnumDisplayMonitors(null, null, new MONITORENUMPROC() {

			public int apply(HMONITOR hMonitor, HDC hdc, RECT rect, LPARAM lparam)
			{
				enumerate(hMonitor);

				return 1;
			}
			
		}, new LPARAM(0));
		
		JFrame frame = new JFrame(); 
		JSlider slider = new JSlider();
		
		frame.setTitle("Bildschirmhelligkeit anpassen");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300,100);
		frame.setLocation(500,200);
		frame.setLayout(new BorderLayout());
								
		slider.setMinimum(0);
		slider.setMaximum(100);
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(10);
		slider.createStandardLabels(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		         		
		ChangeListener changeListener = new ChangeListener()
        {
             public void stateChanged(ChangeEvent e)
             {
            		JSlider source;
            		source = (JSlider)e.getSource();
            		if ( !source.getValueIsAdjusting()) {
            		int value = 0;            		
            		
            		HMONITOR hMonitor = null;
					int monitorCount = 0;
					PHYSICAL_MONITOR[] physMons = null;
					Dxva2.INSTANCE.GetPhysicalMonitorsFromHMONITOR(hMonitor, monitorCount, physMons);
            			
            		for (int i = 0; i < monitorCount; i++)
            			{
            				HANDLE hPhysicalMonitor = physMons[0].hPhysicalMonitor;
            				System.out.println("Monitor " + i + " - " + new String(physMons[i].szPhysicalMonitorDescription));
            			
            				enumeratePhysicalMonitor(hPhysicalMonitor);
            				break;
            			}
            		            		
            		DWORDByReference pdwMinimumBrightness = new DWORDByReference();
            		DWORDByReference pdwCurrentBrightness = new DWORDByReference();
            		DWORDByReference pdwMaximumBrightness = new DWORDByReference();
            		HANDLE hPhysicalMonitor = null;
					Dxva2.INSTANCE.GetMonitorBrightness(hPhysicalMonitor, pdwMinimumBrightness, pdwCurrentBrightness, pdwMaximumBrightness);
					
            		Dxva2.INSTANCE.SetMonitorBrightness(hPhysicalMonitor, value = 10);
            		
            		} else {
            			System.out.println("Error");
            		}
             }
             
        };
		             
        slider.addChangeListener(changeListener);
		frame.add(slider, BorderLayout.CENTER);
		frame.setVisible(true);
		} ;
}
