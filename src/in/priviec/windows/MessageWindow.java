package in.priviec.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import in.priviec.core.Core;
import in.priviec.core.Options;
import in.priviec.core.Sound;

public class MessageWindow {
	Core core;
	Sound snd;
	public JFrame frame;

	public MessageWindow(Core core) {
		this.core = core;
		if (core.username == null) {
			return;
		}
		open();
	}

	JLabel chat;
	public JTextArea textArea;

	private JMenuBar createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");

		file.add(createMenuItem("Close", e -> frame.dispose(), true));

		menubar.add(file);

		return menubar;
	}

	private JMenuItem createMenuItem(String name, ActionListener al, boolean isEnabled) {
		JMenuItem menuitem = new JMenuItem(name);
		menuitem.addActionListener(al);
		menuitem.setEnabled(isEnabled);
		return menuitem;
	}

	public void open() {
		frame = new JFrame("Message");
		JPanel panel = new JPanel();
		JTextField textField = new JTextField();

		textArea = new JTextArea();
		textArea.setEditable(false);

		panel.setLayout(new BorderLayout());
		panel.add(createMenuBar(), BorderLayout.NORTH);
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		panel.add(textField, BorderLayout.SOUTH);

		frame.add(panel);
		frame.pack();
		frame.setSize(400, 400);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				String msg;
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_ENTER) {
					msg = textField.getText().trim();

					if (!msg.isEmpty() && msg != " ") {
						synchronized (this) {
							core.peer.sendMsg(core.username + ": " + msg, "127.0.0.1", 1515);
							if (!frame.hasFocus())
								appendMessage(core.username + ": " + textField.getText(), false);
						}
						textField.setText("");
					}
				}
			}
		});
	}

	public void appendMessage(String message, boolean playSound) {
		textArea.append(message + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());

		if (playSound && Options.playSounds) {
			snd = core.snd;
			snd.playSound("/sounds/IM.WAV", false);
		}
	}
}
