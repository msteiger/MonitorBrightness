package control;

import java.awt.Point;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;

import javax.swing.Timer;

import monitor.Monitor;
import monitor.MonitorController;
import monitor.jna.MonitorControllerJna;

import com.github.sarxos.webcam.Webcam;
import static control.AllesSetOptimalMonitorBrightness.*;

public class SetOptimalMonitorBrightness {
	
	
	public static void main(String[] args) throws IOException {
		
		if (!SystemTray.isSupported())
		{
            System.out.println("SystemTray is not supported");
            return;
        }
		
		MonitorController monitorListe = new MonitorControllerJna();
				
		final Webcam webcam = setupWebcam();		
		final BrightnessActionLi timerListener = new BrightnessActionLi(webcam);
		final Timer timer = setupTimer(timerListener);
		final JHauptFenster frame = new JHauptFenster();
		frame.setupVideo(timer, timerListener);
		
		final JDrawFenster drawFenster = new JDrawFenster();
//		final JFrame drawFrame = drawFrame();
//		final DrawPanel drawPanel = drawPanel();
		final TrayIcon trayIcon = geniousTray(frame, drawFenster, webcam);
		
		final Point point1 = drawFenster.getPoint1();
		final Point point2 = drawFenster.getPoint2();
	
		for (final Monitor mon : monitorListe.getMonitors())
			frame.addMonitor(mon, timer, timerListener, point1, point2, trayIcon);

		drawFenster.pack();
	    drawFenster.setVisible(true);
		frame.pack();
		frame.setVisible(true);
	}
	
}