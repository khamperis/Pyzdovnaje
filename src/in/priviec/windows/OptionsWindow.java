package in.priviec.windows;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

import in.priviec.core.Core;
import in.priviec.core.Options;

public class OptionsWindow {
	public JFrame frame;
	private Core core;

	public OptionsWindow(Core core) {
		this.core = core;
		open();
	}

	private void open() {
		JCheckBox playSoundsBox = new JCheckBox("Play sounds", null, Options.playSounds);
		JCheckBox createTrayIconBox = new JCheckBox("Create a tray icon", null, Options.createTrayIcon);
		frame = new JFrame("Options");
		frame.setResizable(false);
		frame.setSize(400, 400);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new GridLayout(3, 1));
		frame.add(playSoundsBox);
		frame.add(createTrayIconBox);

		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame = null;
				if (Options.playSounds)
					core.initSound();
				if (Options.createTrayIcon)
					core.mainWind.updateSystemTray();
			}
		});

		createTrayIconBox.setToolTipText("Requires a restart to fully take effect");

		playSoundsBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Options.playSounds = playSoundsBox.isSelected();
			}
		});
		createTrayIconBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Options.createTrayIcon = createTrayIconBox.isSelected();
			}
		});
	}
}