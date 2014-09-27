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

import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Computes a moving average
 * @author Martin Steiger
 */
public class EqualizedProvider implements LuminanceProvider
{
	private static final Logger logger = LoggerFactory.getLogger(EqualizedProvider.class);

	private final int queueLength;

	private final Deque<Integer> avgLuminances;

	private LuminanceProvider base;

	/**
	 * Uses a queue length of 10
	 * @param base the original luminance provider
	 */
	public EqualizedProvider(LuminanceProvider base)
	{
		this(base, 10);
	}

	/**
	 * @param base the original luminance provider
	 * @param queueLength the queue length
	 */
	public EqualizedProvider(LuminanceProvider base, int queueLength)
	{
		this.base = base;
		this.queueLength = queueLength;
		this.avgLuminances = new ArrayDeque<Integer>(queueLength);
	}

	@Override
	public int getLuminance()
	{
		if (avgLuminances.size() >= queueLength)
	        avgLuminances.removeFirst();

		avgLuminances.addLast(base.getLuminance());

		logger.trace("Average image luminance queue {}", avgLuminances);

		int avgLum = 0;
		for (Integer lum : avgLuminances)
		{
			avgLum += lum.intValue();
		}

		avgLum = avgLum / avgLuminances.size();

		return avgLum;
	}

	@Override
	public void close() throws Exception
	{
		// unused
	}

}
