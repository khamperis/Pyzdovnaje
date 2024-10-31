package in.priviec.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import in.priviec.core.Core;
import in.priviec.core.Sound;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	ImageIcon homeIcon;
	ImageIcon contactsIcon;
	JTabbedPane tabs = new JTabbedPane();
	JPanel loginPanel = new JPanel();
	JPanel topPanel = new JPanel();
	LoginWindow loginWin;
	Sound snd;
	Core core;
	AboutWindow aboutWindow = new AboutWindow();

	public MainWindow() {
		core = new Core(this);
		core.init();

		homeIcon = loadIcon("toolbar/HOUSE.png");
		contactsIcon = loadIcon("toolbar/CONTACT.png");
		tabs = new JTabbedPane();
		tabs.addTab(" ", loginPanel);
		ImageIcon homeIcon = loadIcon("toolbar/HOUSE.png");
		tabs.setIconAt(0, homeIcon);

		JLabel loginLabel = new JLabel("Click here to log in");

		loginLabel.setPreferredSize(new Dimension(270, 30));
		loginLabel.setBounds(5, 5, 5, 5);

		loginLabel.setForeground(Color.BLUE.darker());
		loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		loginPanel.add(loginLabel);

		loginLabel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (loginWin == null || loginWin.frame == null) {
					loginWin = new LoginWindow(core);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});

		setTitle("Minto");
		topPanel.setLayout(new BorderLayout());
		topPanel.add(createMenuBar(), BorderLayout.NORTH);
		topPanel.add(createToolBar(), BorderLayout.NORTH);
		setJMenuBar(createMenuBar());

		topPanel.add(tabs);

		getContentPane().add(topPanel);
		setSize(315, 480);
		setMinimumSize(new Dimension(220, 270));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(100, 100);

		setVisible(true);
	}

	public void logOff() {
		if (core.isLoggedIn) {
			core.isLoggedIn = false;
			tabs.removeTabAt(1);
			tabs.addTab(" ", loginPanel);
			tabs.setIconAt(1, homeIcon);
			tabs.removeTabAt(0);
//			loginWin = null;
			
			core.peer.stop();
			core.messageWindow.frame.dispose();
			loginWin.frame.dispose();
			aboutWindow.frame.dispose();

			setTitle("Minto");

			snd.playSound("/sounds/LOGOUT.WAV", false);
		} else {
			return;
		}
	}

	public void logIn() {
		ImageIcon contactsOnlineImage = loadIcon("toolbar/CONTACT_BIG.png");

		JPanel homePanel = new JPanel();
		JPanel contactsPanel = new JPanel();
		ImageIcon homeIcon = loadIcon("toolbar/HOUSE.png");
		ImageIcon contactsIcon = loadIcon("toolbar/CONTACT.png");
		JLabel contactsAvailable = new JLabel(contactsOnlineImage);
		JLabel noContactsAvailable = new JLabel("You have no users in your contacts list.");

		snd = new Sound();

		tabs.removeTabAt(0);
		tabs.addTab("Start", homePanel);
		tabs.addTab("Contacts", contactsPanel);
		tabs.setIconAt(0, homeIcon);
		tabs.setIconAt(1, contactsIcon);

		contactsAvailable.setForeground(Color.BLUE.darker());
		contactsAvailable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

//		JLabel contactsOnlineImg = new JLabel(contactsOnlineImage);
//		
//		contactsAvailable.setBounds(50, 50, 50, 50);
//		homePanel.add(contactsOnlineImg);
		contactsAvailable.setText("0 Contacts Online");
//		contactsAvailable.setPreferredSize(new Dimension(2, 50));
//		contactsAvailable.setBounds(5, 5, 5, 5);
		homePanel.add(contactsAvailable);

		contactsPanel.setLayout(new BorderLayout());
		contactsPanel.add(noContactsAvailable, BorderLayout.NORTH);

		contactsAvailable.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				tabs.setSelectedIndex(1);
			}
		});
		setTitle("Minto - Logged in to " + core.username);
	}

	private ImageIcon loadIcon(String location) {
		URL loadedImage = getClass().getClassLoader().getResource(location);
		return new ImageIcon(loadedImage);
	}

	private JMenuBar createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu help = new JMenu("Help");
		JMenu file = new JMenu("File");

		file.add(createMenuItem("Send message", e -> core.showMessageWindow(), true));
		file.add(createMenuItem("Log Off", e -> logOff(), true));
		file.add(createMenuItem("Close", e -> {
			dispose();
			if (core.isLoggedIn)
				snd.playSound("/sounds/LOGOUT.WAV", true);

			System.exit(0);
		}, true));
		help.add(createMenuItem("Help", e -> aboutWindow.open(), true));

		menubar.add(file);
		menubar.add(help);

		return menubar;
	}

	public JMenuItem createMenuItem(String name, ActionListener al, boolean isEnabled) {
		JMenuItem menuitem = new JMenuItem(name);
		menuitem.addActionListener(al);
		menuitem.setEnabled(isEnabled);
		return menuitem;
	}

	private JToolBar createToolBar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);

		int[] spacingSizes = { 1, 5, 6, 5, 5, 7, 6, 7, 6, 7 };
		String[] menuBarIcons = { "menubar/INFO_DISABLED.png", "menubar/ADDCONTACT_DISABLED.png",
				"menubar/REMOVECONTACT_DISABLED.png", "menubar/IM_DISABLED.png", "menubar/STARTCONFERENCE_DISABLED.png",
				"menubar/UNKNOWN.png", "menubar/MICROPHONE_DISABLED.png", "menubar/PAUSECALL_DISABLED.png",
				"menubar/ADDUSERTOCONTACTS_DISABLED.png", "menubar/SEARCH_DISABLED.png" };

		for (int i = 0; i < menuBarIcons.length; i++) {
			JButton button = createToolBarItem(menuBarIcons[i]);
			toolbar.add(Box.createHorizontalStrut(spacingSizes[i]));
			toolbar.add(button);
			if (i == 2 || i == 5 || i == 7)
				toolbar.addSeparator();
		}

		return toolbar;
	}

	private JButton createToolBarItem(String location) {
		JButton button = new JButton();
		button.setBorder(null);
		button.setOpaque(true);
		button.setContentAreaFilled(false);

		URL resource = getClass().getClassLoader().getResource(location);
		button.setIcon(new ImageIcon(resource));

		return button;
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(MainWindow::new);
	}
}