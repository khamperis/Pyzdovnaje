package in.priviec.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import in.priviec.core.Core;

public class LoginWindow {
	private Core core;
	public JFrame frame;

	public LoginWindow(Core core) {
		this.core = core;
		open();
	}

	public void open() {
		frame = new JFrame("Minto - Log in to Minto");
		JPanel panel = new JPanel();
		JButton button = new JButton("Log In");
		JTextField userNameField = new JTextField();
//		JTextField passwordField = new JTextField();

		panel.setLayout(new BorderLayout());
		panel.add(userNameField, BorderLayout.NORTH);
//		panel.add(passwordField, BorderLayout.CENTER);
		panel.add(button, BorderLayout.SOUTH);
		frame.add(panel);
		frame.pack();
		frame.setSize(572, 588);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = userNameField.getText();
				if (username.length() <= 12 && username.length() >= 3) {
					core.username = username;
					core.logIn();
					frame.dispose();
				} else {
					System.err.println("username too short or too long");
				}
			}
		});

		userNameField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_ENTER) {
					core.username = userNameField.getText();
					core.logIn();
					frame.dispose();
				}
			}
		});

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
			}

			@Override
			public void windowClosing(WindowEvent e) {
				frame = null;
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
	}
}