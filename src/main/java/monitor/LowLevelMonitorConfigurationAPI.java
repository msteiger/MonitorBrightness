package monitor;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.BYTE;
import com.sun.jna.platform.win32.WinDef.DWORD;

/**
 * Conversion of LowLevelMonitorConfigurationAPI.h
 * @author Martin Steiger
 */
public interface LowLevelMonitorConfigurationAPI
{
	/**
	 * Contains information from a monitor's timing report.
	 */
	class MC_TIMING_REPORT extends Structure
	{
		/**
		 * The monitor's horizontal synchronization frequency in Hz.
		 */
	    DWORD dwHorizontalFrequencyInHZ;
	    
	    /**
	     * The monitor's vertical synchronization frequency in Hz.
	     */
	    DWORD dwVerticalFrequencyInHZ;
	    
	    /**
	     * Timing status byte. For more information about this value, see the Display Data Channel Command 
	     * Interface (DDC/CI) standard.
	     */
	    BYTE bTimingStatusByte;
		
	    @Override
		protected List<String> getFieldOrder()
		{
			return Arrays.asList("dwHorizontalFrequencyInHZ", "dwVerticalFrequencyInHZ", "bTimingStatusByte");
		}
	}
   
	/**
	 * Describes a Virtual Control Panel (VCP) code type.
	 */
	enum MC_VCP_CODE_TYPE
	{
		/**
		 * Momentary VCP code. Sending a command of this type causes the monitor to initiate a self-timed 
		 * operation and then revert to its original state. Examples include display tests and degaussing.
		 */
	    MC_MOMENTARY,
	    
	    /**
	     * Set Parameter VCP code. Sending a command of this type changes some aspect of the monitor's operation.
	     */
	    MC_SET_PARAMETER
	}

}
