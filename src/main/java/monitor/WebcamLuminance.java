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

package monitor;

import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;

/**
 * Provides environmental luminance
 * @author Martin Steiger
 */
public class WebcamLuminance implements LuminanceProvider
{
	static final Logger logger = LoggerFactory.getLogger(WebcamLuminance.class);
	
	private final Webcam webcam;

	public WebcamLuminance()
	{
		webcam = Webcam.getDefault();
		if (webcam == null)
			throw new IllegalStateException("No webcam has been detected!");

		webcam.open();
	}
	
	/**
	 * This operation is thread-safe	
	 * @return the average luminance in [0..255]
	 */
	@Override
	public int getLuminance()
	{
		if (!webcam.isOpen())
			webcam.open();

		BufferedImage img = webcam.getImage();
		
		if (img == null)
		{
			logger.warn("No image available");
			return 128;
		}
		
		int[] data = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
		
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
		logger.debug("Average image luminance {}", avg);
		
		return avg;
	}

	@Override
	public void close() throws Exception
	{
		webcam.close();
	}
}
