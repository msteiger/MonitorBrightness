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
package config;

import java.awt.Point;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 *
 * @author Martin Steiger
 */
public class Config
{
	private Point point1 = new Point(50, 50);
	private Point point2 = new Point(115, 50);

	private Map<String, Boolean> autoAdjust = Maps.newHashMap();

	/**
	 * @return the point1
	 */
	public Point getPoint1()
	{
		return point1;
	}

	/**
	 * @param point1 the point1 to set
	 */
	public void setPoint1(Point point1)
	{
		this.point1 = point1;
	}

	/**
	 * @return the point2
	 */
	public Point getPoint2()
	{
		return point2;
	}

	/**
	 * @param point2 the point2 to set
	 */
	public void setPoint2(Point point2)
	{
		this.point2 = point2;
	}

	/**
	 * @return the autoAdjust - false for non-existing
	 */
	public boolean getAutoAdjust(String monitorName)
	{
		Boolean val = autoAdjust.get(monitorName);
		return val == null ? false : val.booleanValue();
	}

	/**
	 * @param autoAdjust the autoAdjust to set
	 */
	public void setAutoAdjust(String monitorName, boolean autoAdjust)
	{
		this.autoAdjust.put(monitorName, Boolean.valueOf(autoAdjust));
	}


}
