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

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;

/**
 *
 * @author Martin Steiger
 */
public class SarxosWebcamWrapper implements WebcamWrapper
{
	private static final Logger logger = LoggerFactory.getLogger(WebcamLuminance.class);

	private static final int MILLIS_PER_NANO = 1000000;

	private static final Dimension IMAGE_SIZE = new Dimension(320, 240);

	private final Webcam webcam;

	private BufferedImage image = new BufferedImage(IMAGE_SIZE.width, IMAGE_SIZE.height, BufferedImage.TYPE_INT_RGB);
	private int brightness = 0;

	private long lastUpdate = 0;

	private LuminanceProvider provider;

	public SarxosWebcamWrapper(Webcam webcam, LuminanceProvider provider)
	{
		this.webcam = webcam;
		this.provider = provider;
		this.webcam.setViewSize(IMAGE_SIZE);
		this.webcam.open();
	}

	private void update()
	{
		long curTime = System.nanoTime();

		if (curTime - lastUpdate < 20 * MILLIS_PER_NANO)
			return;

		lastUpdate = curTime;

		if (!webcam.isOpen())
		{
			logger.warn("Webcam was closed - attempting to re-open..");
			webcam.open();
		}

		BufferedImage freshImage = webcam.getImage();

		if (freshImage == null)
		{
			logger.warn("No image available");
			return;
		}

		logger.trace("Image received");

		this.image = freshImage;
		this.brightness = provider.getLuminance(freshImage);

		logger.debug("Average image luminance {}", brightness);
	}

	@Override
	public int getBrightness()
	{
		update();
		return brightness;
	}

	@Override
	public BufferedImage getImage()
	{
		update();
		return image;
	}

	@Override
	public void close() throws Exception
	{
		webcam.close();
	}

}
