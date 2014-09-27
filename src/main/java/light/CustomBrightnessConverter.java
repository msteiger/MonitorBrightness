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
package light;

import java.awt.Point;

/**
 * Converts measured brightness to monitor brightness linearly
 * @author Martin Steiger
 */
public class CustomBrightnessConverter implements BrightnessConverter
{
//	private final int targetMin;
//	private final int targetMax;
//	private final int sourceMin;
//	private final int sourceMax;

	final Point point1;
	final Point point2;

	public CustomBrightnessConverter(Point point1, Point point2)
	{
//		this.sourceMin = sourceMin;
//		this.sourceMax = sourceMax;
//
//		this.targetMin = targetMin;
//		this.targetMax = targetMax;

		this.point1 = point1;
		this.point2 = point2;
	}

	@Override
	public int convert(int camBright)
	{
		if (camBright < point1.x)
		{
			return (int) (camBright * (100.0 - point1.y) / point1.x);
		}

		else if (camBright < point2.x)
		{
			return 100 - point1.y;
		}

		else
		{
			return (int) (100.0 - ((0.0 - point2.y) / (255.0 - point2.x) * (camBright - point2.x) + point2.y));
		}
	}

}
