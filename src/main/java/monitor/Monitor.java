// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Information Visualization and Visual Analytics
//
// Copyright (c) Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package monitor;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public interface Monitor
{
	int convertLuminance(int avgLum);
	
	/**
	 * @return
	 */
	int getBrightness();

	/**
	 * @param i
	 */
	void setBrightness(int i);

}
