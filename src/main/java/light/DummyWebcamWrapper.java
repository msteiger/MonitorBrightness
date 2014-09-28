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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author Martin Steiger
 */
public class DummyWebcamWrapper implements WebcamWrapper
{
	private static final int HEIGHT = 240;
	private static final int WIDTH = 320;

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int bright = 128;

	/**
	 *
	 */
	public DummyWebcamWrapper()
	{
		Graphics g = image.getGraphics();
		g.setColor(new Color(bright, bright, bright));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.dispose();
	}

	@Override
	public int getBrightness()
	{
		return bright;
	}

	@Override
	public BufferedImage getImage()
	{
		return image;
	}

	@Override
	public String getName()
	{
		return "Dummy Webcam";
	}

	@Override
	public void close() throws Exception
	{
		// ignore
	}
}
