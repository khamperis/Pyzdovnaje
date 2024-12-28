package in.priviec.windows;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class AboutWindow extends Canvas {
	private static final long serialVersionUID = 1L;
	public JFrame frame;

	public void open() {
		frame = new JFrame("About Pyzdovnaje");
		AboutWindow aw = new AboutWindow();
		frame.setUndecorated(true);
		frame.add(aw);
		frame.pack();
		frame.setSize(250, 250);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowDeactivated(WindowEvent e) {
				frame.dispose();
			}
		});
	}

	public void paint(Graphics g) {
		g.drawString("Pyzdovnaje!", (getWidth() / 2) - 34, (getHeight() / 2));
		g.dispose();
	}
}