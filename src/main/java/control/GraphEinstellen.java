package control;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GraphEinstellen {
	
	
	public static void main(String[] args)
	{
		
		final JFrame frame = new JFrame(); 
				
		frame.setTitle("Einstellen der Umwandlung Helligkeit des Raumes in Bildschirmhelligkeit");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(500,200);
		frame.setLayout(new BorderLayout());
		
		final JPanel panel = new DrawPanel();
		
		panel.setPreferredSize(new Dimension(255,100));
		panel.setBorder(BorderFactory.createEmptyBorder(10 , 10 , 10 , 10));
		frame.add(panel, BorderLayout.CENTER);

		frame.pack();
		frame.setVisible(true);
	}
	
	    
}