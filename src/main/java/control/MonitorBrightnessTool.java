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

package control;

import java.awt.AWTException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

/**
 * The main class
 * @author Martin Steiger
 */
public class MonitorBrightnessTool
{
	/**
	 * @param args (ignored)
	 */
	public static void main(String[] args)
	{
		try
		{
			App app = new App();
//			app.run();
		}
		catch (IOException | AWTException e)
		{
			showError("Could not initialize application", e);
			return;
		}
	}

	private static void showError(String text, Exception e)
	{
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String msg = text + System.lineSeparator() + sw.toString();

		JOptionPane.showMessageDialog(null, msg, "An error occurred", JOptionPane.ERROR_MESSAGE);
	}

}
