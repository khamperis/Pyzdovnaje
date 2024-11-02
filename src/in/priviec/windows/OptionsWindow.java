package in.priviec.windows;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

import in.priviec.core.Options;

public class OptionsWindow {
	private JFrame frame;

	public OptionsWindow() {
		open();
	}

	private void open() {
		JCheckBox playSoundsBox = new JCheckBox("Play sounds", null, Options.playSounds);
		frame = new JFrame("Options");
		frame.setResizable(false);
		frame.setSize(400, 400);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new GridLayout(3, 1));
		frame.add(playSoundsBox);

		frame.setVisible(true);

		playSoundsBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Options.playSounds = playSoundsBox.isSelected();
			}
		});
	}
}