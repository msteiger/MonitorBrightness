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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import config.Config;

public class CalibrationWindow extends JFrame
{
	private final CalibrationPanel drawPanel;

	public CalibrationWindow(Config config)
	{
		drawPanel = new CalibrationPanel(config);
		drawPanel.setPreferredSize(new Dimension(320, 160));
		drawPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		setTitle("Calibrate webcam brightness to monitor brightness");
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setLocation(100, 200);
		setLayout(new BorderLayout());
		add(drawPanel, BorderLayout.CENTER);
	}

	public Point getPoint1()
	{
		return drawPanel.getPoint1();
	}

	public Point getPoint2()
	{
		return drawPanel.getPoint2();
	}

}
