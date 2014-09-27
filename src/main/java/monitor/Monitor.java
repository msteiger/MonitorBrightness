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
	/**
	 * @return the minimum brightness
	 */
	int getMinBrightness();

	/**
	 * @return the current brightness
	 */
	int getBrightness();

	/**
	 * @return the minimum brightness
	 */
	int getMaxBrightness();

	/**
	 * @param bright
	 */
	void setBrightness(int bright);

	/**
	 * @return the name (as reported by the driver)
	 */
	String getName();
}
