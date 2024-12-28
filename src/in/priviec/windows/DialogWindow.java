package in.priviec.windows;

import javax.swing.JOptionPane;

public class DialogWindow {
	private String message;

	public DialogWindow(String message) {
		this.message = message;
		open();
	}

	private void open() {
		JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
	}
}