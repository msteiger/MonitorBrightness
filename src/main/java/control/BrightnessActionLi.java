package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;

final class BrightnessActionLi implements ActionListener {
		private final Webcam webcam;
		private BufferedImage image;
		private int brightness;

		public BrightnessActionLi(Webcam webcam) {
			this.webcam = webcam;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			image = webcam.getImage();

			//---------------------------
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
			brightness = avg;
		}

		public BufferedImage getImage() {
			return image;
		}

		public int getBrightness() {
			return brightness;
		}
	}