package in.priviec.windows;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

public class AboutWindow extends Canvas {
	private static final long serialVersionUID = 1L;
	public JFrame frame;
	
	public void open() {
		frame = new JFrame("About Minto");
		AboutWindow aw = new AboutWindow();
		frame.setUndecorated(true);
		frame.add(aw);
		frame.pack();
		frame.setSize(250, 250);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {

			}

			@Override
			public void windowIconified(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				frame.dispose();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				
			}

			@Override
			public void windowClosed(WindowEvent e) {

			}

			@Override
			public void windowActivated(WindowEvent e) {

			}
		});
	}

	public void paint(Graphics g) {
		g.drawString("Rimdo!", 105, 105);
		g.dispose();
	}
}