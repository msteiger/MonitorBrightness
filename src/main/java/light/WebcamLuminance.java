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

import java.awt.image.BufferedImage;

/**
 * Provides environmental luminance
 * @author Martin Steiger
 */
public class WebcamLuminance implements LuminanceProvider
{
	@Override
	public int getLuminance(BufferedImage image)
	{
		int[] data = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

		int sum = 0;

		for (int argb : data)
		{
			int r = (argb >> 16) & 0xFF;
			int g = (argb >> 8)  & 0xFF;
			int b = (argb >> 0)  & 0xFF;
			int y = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b + 0.5);
			sum += y;
		}

		int avg = sum / data.length;
		return avg;
	}

}
