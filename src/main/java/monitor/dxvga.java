package monitor;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface dxvga extends StdCallLibrary
{
	class LPDWORD extends DWORDByReference {
		
	};
	
	dxvga INSTANCE = (dxvga) 
			Native.loadLibrary("Dxva2", dxvga.class, W32APIOptions.DEFAULT_OPTIONS);
	
	final int MC_CAPS_NONE =                                            0x00000000;
	final int MC_CAPS_MONITOR_TECHNOLOGY_TYPE =                         0x00000001;
	final int MC_CAPS_BRIGHTNESS =                                      0x00000002; 
	final int MC_CAPS_CONTRAST =                                        0x00000004;
	final int MC_CAPS_COLOR_TEMPERATURE =                               0x00000008;
	final int MC_CAPS_RED_GREEN_BLUE_GAIN =                             0x00000010;
	final int MC_CAPS_RED_GREEN_BLUE_DRIVE =                            0x00000020;
	final int MC_CAPS_DEGAUSS =                                         0x00000040;
	final int MC_CAPS_DISPLAY_AREA_POSITION =                           0x00000080;
	final int MC_CAPS_DISPLAY_AREA_SIZE =                               0x00000100;
	final int MC_CAPS_RESTORE_FACTORY_DEFAULTS =                        0x00000400;
	final int MC_CAPS_RESTORE_FACTORY_COLOR_DEFAULTS =                  0x00000800;
	final int MC_RESTORE_FACTORY_DEFAULTS_ENABLES_MONITOR_SETTINGS =    0x00001000;         
	
	final int MC_SUPPORTED_COLOR_TEMPERATURE_NONE =                     0x00000000;
	final int MC_SUPPORTED_COLOR_TEMPERATURE_4000K =                    0x00000001;
	final int MC_SUPPORTED_COLOR_TEMPERATURE_5000K =                    0x00000002;
	final int MC_SUPPORTED_COLOR_TEMPERATURE_6500K =                    0x00000004;
	final int MC_SUPPORTED_COLOR_TEMPERATURE_7500K =                    0x00000008;
	final int MC_SUPPORTED_COLOR_TEMPERATURE_8200K =                    0x00000010;
	final int MC_SUPPORTED_COLOR_TEMPERATURE_9300K =                    0x00000020;
	final int MC_SUPPORTED_COLOR_TEMPERATURE_10000K =                   0x00000040;
	final int MC_SUPPORTED_COLOR_TEMPERATURE_11500K =                   0x00000080;

/******************************************************************************
    Enumerations
******************************************************************************/
enum MC_DISPLAY_TECHNOLOGY_TYPE
{
    MC_SHADOW_MASK_CATHODE_RAY_TUBE,
    MC_APERTURE_GRILL_CATHODE_RAY_TUBE,
    MC_THIN_FILM_TRANSISTOR,
    MC_LIQUID_CRYSTAL_ON_SILICON,
    MC_PLASMA,
    MC_ORGANIC_LIGHT_EMITTING_DIODE,
    MC_ELECTROLUMINESCENT,
    MC_MICROELECTROMECHANICAL,
    MC_FIELD_EMISSION_DEVICE
}

enum MC_DRIVE_TYPE
{
    MC_RED_DRIVE,
    MC_GREEN_DRIVE,
    MC_BLUE_DRIVE

}

enum MC_GAIN_TYPE
{
    MC_RED_GAIN,
    MC_GREEN_GAIN,
    MC_BLUE_GAIN

}

enum MC_POSITION_TYPE
{
    MC_HORIZONTAL_POSITION,
    MC_VERTICAL_POSITION 

}

enum MC_SIZE_TYPE
{
    MC_WIDTH,
    MC_HEIGHT

}

enum MC_COLOR_TEMPERATURE
{
    MC_COLOR_TEMPERATURE_UNKNOWN,
    MC_COLOR_TEMPERATURE_4000K, 
    MC_COLOR_TEMPERATURE_5000K, 
    MC_COLOR_TEMPERATURE_6500K, 
    MC_COLOR_TEMPERATURE_7500K, 
    MC_COLOR_TEMPERATURE_8200K, 
    MC_COLOR_TEMPERATURE_9300K, 
    MC_COLOR_TEMPERATURE_10000K,
    MC_COLOR_TEMPERATURE_11500K
    
}

/******************************************************************************
    Monitor capability functions 
******************************************************************************/
BOOL GetMonitorCapabilities
    ( 
    HANDLE hMonitor, 
    LPDWORD pdwMonitorCapabilities, 
    LPDWORD pdwSupportedColorTemperatures 
    );

/******************************************************************************
    Monitor setting persistence functions 
******************************************************************************/
BOOL SaveCurrentMonitorSettings( HANDLE hMonitor );

/******************************************************************************
    Monitor meta-data functions
******************************************************************************/
BOOL GetMonitorTechnologyType( HANDLE hMonitor, MC_DISPLAY_TECHNOLOGY_TYPE pdtyDisplayTechnologyType );

/******************************************************************************
    Monitor image calibration functions 
******************************************************************************/
BOOL GetMonitorBrightness
    ( 
    HANDLE hMonitor, 
    IntByReference pdwMinimumBrightness, 
    IntByReference pdwCurrentBrightness, 
    IntByReference pdwMaximumBrightness 
    );
BOOL GetMonitorContrast
    ( 
    HANDLE hMonitor, 
    LPDWORD pdwMinimumContrast, 
    LPDWORD pdwCurrentContrast, 
    LPDWORD pdwMaximumContrast 
    );
BOOL GetMonitorColorTemperature( HANDLE hMonitor, MC_COLOR_TEMPERATURE pctCurrentColorTemperature );
BOOL GetMonitorRedGreenOrBlueDrive
    ( 
    HANDLE hMonitor, 
    MC_DRIVE_TYPE dtDriveType, 
    LPDWORD pdwMinimumDrive,
    LPDWORD pdwCurrentDrive,
    LPDWORD pdwMaximumDrive
    );
BOOL GetMonitorRedGreenOrBlueGain
    ( 
    HANDLE hMonitor, 
    MC_GAIN_TYPE gtGainType, 
    LPDWORD pdwMinimumGain,
    LPDWORD pdwCurrentGain,
    LPDWORD pdwMaximumGain
    );

BOOL SetMonitorBrightness( HANDLE hMonitor, int dwNewBrightness );
BOOL SetMonitorContrast( HANDLE hMonitor, int dwNewContrast );
BOOL SetMonitorColorTemperature( HANDLE hMonitor, MC_COLOR_TEMPERATURE ctCurrentColorTemperature );
BOOL SetMonitorRedGreenOrBlueDrive( HANDLE hMonitor, MC_DRIVE_TYPE dtDriveType, int dwNewDrive );
BOOL SetMonitorRedGreenOrBlueGain( HANDLE hMonitor, MC_GAIN_TYPE gtGainType, int dwNewGain );
BOOL DegaussMonitor( HANDLE hMonitor );

/******************************************************************************
    Monitor image size and position calibration functions 
******************************************************************************/
BOOL GetMonitorDisplayAreaSize
    ( 
    HANDLE hMonitor, 
    MC_SIZE_TYPE stSizeType,
    LPDWORD pdwMinimumWidthOrHeight,
    LPDWORD pdwCurrentWidthOrHeight,
    LPDWORD pdwMaximumWidthOrHeight
    );
BOOL GetMonitorDisplayAreaPosition
    ( 
    HANDLE hMonitor, 
    MC_POSITION_TYPE ptPositionType, 
    LPDWORD pdwMinimumPosition,
    LPDWORD pdwCurrentPosition,
    LPDWORD pdwMaximumPosition
    );
BOOL SetMonitorDisplayAreaSize
    (
    HANDLE hMonitor,
    MC_SIZE_TYPE stSizeType,
    int dwNewDisplayAreaWidthOrHeight
    );
BOOL SetMonitorDisplayAreaPosition
    (
    HANDLE hMonitor,
    MC_POSITION_TYPE ptPositionType, 
    int dwNewPosition
    );

/******************************************************************************
    Restore to defaults functions
******************************************************************************/
BOOL RestoreMonitorFactoryColorDefaults( HANDLE hMonitor );
BOOL RestoreMonitorFactoryDefaults( HANDLE hMonitor );


// PhysicalMonitorEnumerationAPI.h

/******************************************************************************
Physical Monitor Constants
******************************************************************************/

public class HMONITOR extends HANDLE {
	
}
	
//A physical monitor description is always an array of 128 characters.  Some
//of the characters may not be used.
	
final int PHYSICAL_MONITOR_DESCRIPTION_SIZE =                   128;

/******************************************************************************
Physical Monitor Structures 
******************************************************************************/
class PHYSICAL_MONITOR extends Structure
{
	public HANDLE hPhysicalMonitor;
	public char[] szPhysicalMonitorDescription = new char[PHYSICAL_MONITOR_DESCRIPTION_SIZE];
	
	@Override
	protected List getFieldOrder()
	{
		return Arrays.asList("hPhysicalMonitor", "szPhysicalMonitorDescription");
	}
}

/******************************************************************************
Physical Monitor Enumeration Functions
******************************************************************************/
BOOL GetNumberOfPhysicalMonitorsFromHMONITOR( 
HMONITOR hMonitor, 
LPDWORD pdwNumberOfPhysicalMonitors
);

//HRESULT GetNumberOfPhysicalMonitorsFromIDirect3DDevice9
//(
//IDirect3DDevice9* pDirect3DDevice9,
//LPDWORD pdwNumberOfPhysicalMonitors
//);

BOOL GetPhysicalMonitorsFromHMONITOR
( 
HMONITOR hMonitor,
int dwPhysicalMonitorArraySize,
PHYSICAL_MONITOR[] pPhysicalMonitorArray
);

//HRESULT GetPhysicalMonitorsFromIDirect3DDevice9
//( 
//__in IDirect3DDevice9* pDirect3DDevice9,
//__in DWORD dwPhysicalMonitorArraySize,
//__out_ecount(dwPhysicalMonitorArraySize) LPPHYSICAL_MONITOR pPhysicalMonitorArray
//);

BOOL DestroyPhysicalMonitor( HANDLE hMonitor );
BOOL DestroyPhysicalMonitors
( 
int dwPhysicalMonitorArraySize,
PHYSICAL_MONITOR[] pPhysicalMonitorArray
);
}

