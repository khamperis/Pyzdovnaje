package in.priviec.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import in.priviec.core.Core;

public class LoginWindow {
	private Core core;
	public JFrame frame;
	private JTextField userNameField;
	private JTextField passwordField;

	public LoginWindow(Core core) {
		this.core = core;
		open();
	}

	public void open() {
		frame = new JFrame("Minto - Log in to Minto");
		userNameField = new JTextField();
		passwordField = new JTextField();

		JLabel usernameLabel = new JLabel("Username:");
		JLabel passwordLabel = new JLabel("Password:");
		JButton button = new JButton("Log In");
		JPanel panel = new JPanel();

		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		userNameField.setMaximumSize(new Dimension(572 * 2, userNameField.getPreferredSize().height));
		passwordField.setMaximumSize(new Dimension(572 * 2, passwordField.getPreferredSize().height));

		userNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
		passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

		panel.add(usernameLabel);
		panel.add(userNameField);
		panel.add(passwordLabel);
		panel.add(passwordField);
		panel.add(Box.createVerticalStrut(10));
		panel.add(button);

		frame.add(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setSize(572, 588);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);

		button.addActionListener(e -> check());

		userNameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					check();
				}
			}
		});

		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					check();
				}
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame = null;
			}
		});
	}

	// Temporary of course
	private void check() {
		String username = userNameField.getText();
		String password = passwordField.getText();
		if (username.length() <= 12 && username.length() >= 3 && password.equals("test!!")) {
			core.username = username;
			core.logIn();
			frame.dispose();
		} else {
			System.err.println("username too short or too long or password incorrect!");
		}
	}
}