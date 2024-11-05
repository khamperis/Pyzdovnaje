package in.priviec.windows;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import in.priviec.core.Core;
import in.priviec.core.Options;
import in.priviec.core.Sound;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	ImageIcon homeIcon;
	ImageIcon contactsIcon;
	ImageIcon statusBarStatusIcon;
	Image statusTrayImage;
	JTabbedPane tabs = new JTabbedPane();
	JPanel loginPanel = new JPanel();
	JPanel topPanel = new JPanel();
	JPanel callButtonsPanel = new JPanel();
	JPanel statusPanel = new JPanel();
	JLabel statusLabel = new JLabel();
	JLabel startCall;
	JLabel endCall;
	JLabel statusIconLabel;
	LoginWindow loginWin;
	Sound snd;
	Core core;
	TrayIcon trayIcon;
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

		loginLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (loginWin == null || loginWin.frame == null) {
					loginWin = new LoginWindow(core);
				}
			}
		});

		setTitle(String.format("Minto %s", core.verNumber));
		topPanel.setLayout(new BorderLayout());
		topPanel.add(createMenuBar(), BorderLayout.NORTH);
		topPanel.add(createToolBar(), BorderLayout.NORTH);
		setJMenuBar(createMenuBar());

		topPanel.add(tabs);

		updateStatusItems();
		setLayout(new BorderLayout());
		add(bottomPanel(), BorderLayout.SOUTH);
		pack();
		getContentPane().add(topPanel);
		setSize(315, 480);
		setMinimumSize(new Dimension(220, 270));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(100, 100);

		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Options options = new Options();
				options.saveOptions();
				if (core.isLoggedIn && Options.playSounds) {
					snd = core.snd;
					snd.playSound("/sounds/LOGOUT.WAV", true);
				}

				System.exit(0);
			}
		});
	}

	private JPanel bottomPanel() {
		ImageIcon startCallImage = loadIcon("other/STARTCALL_DISABLED.png");
		ImageIcon endCallImage = loadIcon("other/ENDCALL_DISABLED.png");

		startCall = new JLabel(startCallImage);
		endCall = new JLabel(endCallImage);

		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
		callButtonsPanel.add(startCall);
		callButtonsPanel.add(Box.createHorizontalStrut(70));
		callButtonsPanel.add(endCall);

		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusPanel.setPreferredSize(new Dimension(getWidth(), 20));
		statusLabel.setText(core.userStatus);
		statusPanel.add(Box.createHorizontalStrut(2));
		statusPanel.add(statusIconLabel);
		statusPanel.add(Box.createHorizontalStrut(15));
		statusPanel.add(new JSeparator(SwingConstants.VERTICAL));
		statusPanel.add(statusLabel);

		bottom.add(callButtonsPanel);

		bottom.add(new JSeparator());
		bottom.add(statusPanel);

		return bottom;
	}

	private void updateStatusItems() {
		statusBarStatusIcon = loadIcon("status/" + core.userStatus + ".png");
		statusIconLabel = new JLabel(statusBarStatusIcon);
		statusLabel.setText(core.userStatus);
		updateSystemTray();
	}

	private void updateSystemTray() {
		SystemTray tray = SystemTray.getSystemTray();
		if (!SystemTray.isSupported()) {
			return;
		}

		setTrayIconImage();

		PopupMenu trayPopupMenu = new PopupMenu();

		MenuItem quit = new MenuItem("Quit");
		MenuItem invisible = new MenuItem("Invisible");
		MenuItem online = new MenuItem("Online");
		MenuItem away = new MenuItem("Away");
		MenuItem busy = new MenuItem("Busy");

		Menu changeStatus = new Menu("Change Status");

		changeStatus.setEnabled(core.isLoggedIn);

		changeStatus.add(online);
		changeStatus.add(invisible);
		changeStatus.add(away);
		changeStatus.add(busy);

		changeStatus.add(quit);

		online.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				core.userStatus = "ONLINE";
				updateStatusItems();
			}
		});

		invisible.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				core.userStatus = "INVISIBLE";
				updateStatusItems();
			}
		});

		away.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				core.userStatus = "AWAY";
				updateStatusItems();
			}
		});

		busy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				core.userStatus = "BUSY";
				updateStatusItems();
			}
		});

		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (core.isLoggedIn)
					logOff();
				System.exit(0);
			}
		});

		trayPopupMenu.add(changeStatus);
		trayPopupMenu.add(quit);

		if (trayIcon != null) {
			trayIcon.setImage(statusTrayImage);
			trayIcon.setPopupMenu(trayPopupMenu);
		} else {
			trayIcon = new TrayIcon(statusTrayImage, null, trayPopupMenu);
			trayIcon.setImageAutoSize(true);
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}

	private void setTrayIconImage() {
		switch (core.userStatus) {
		case "OFFLINE":
			statusTrayImage = Toolkit.getDefaultToolkit()
					.getImage(getClass().getClassLoader().getResource("status/OFFLINE.png"));
			break;
		case "INVISIBLE":
			statusTrayImage = Toolkit.getDefaultToolkit()
					.getImage(getClass().getClassLoader().getResource("status/INVISIBLE.png"));
			break;
		case "ONLINE":
			statusTrayImage = Toolkit.getDefaultToolkit()
					.getImage(getClass().getClassLoader().getResource("status/ONLINE.png"));
			break;
		case "AWAY":
			statusTrayImage = Toolkit.getDefaultToolkit()
					.getImage(getClass().getClassLoader().getResource("status/AWAY.png"));
			break;
		case "BUSY":
			statusTrayImage = Toolkit.getDefaultToolkit()
					.getImage(getClass().getClassLoader().getResource("status/BUSY.png"));
			break;
		}
	}

	public void logOff() {
		if (core.isLoggedIn) {
			core.userStatus = "OFFLINE";
			core.isLoggedIn = false;
			tabs.removeTabAt(1);
			tabs.addTab(" ", loginPanel);
			tabs.setIconAt(1, homeIcon);
			tabs.removeTabAt(0);
			updateStatusItems();

			core.peer.stop();

			disposeAllWindows();

			setTitle("Minto");
			updateStatusItems();
			if (Options.playSounds) {
				snd = core.snd;
				snd.playSound("/sounds/LOGOUT.WAV", false);
			}
		} else {
			return;
		}
	}

	public void disposeAllWindows() {
		if (core.messageWindow != null && core.messageWindow.frame != null) {
			core.messageWindow.frame.dispose();
			core.messageWindow = null;
		}

		if (loginWin != null && loginWin.frame != null) {
			loginWin.frame.dispose();
			loginWin = null;
		}
	}

	public void logIn() {
		ImageIcon contactsOnlineImage = loadIcon("toolbar/CONTACT_BIG.png");
		updateStatusItems();

		JPanel homePanel = new JPanel();
		JPanel contactsPanel = new JPanel();
		ImageIcon homeIcon = loadIcon("toolbar/HOUSE.png");
		ImageIcon contactsIcon = loadIcon("toolbar/CONTACT.png");
		JLabel contactsAvailable = new JLabel(contactsOnlineImage);
		JLabel noContactsAvailable = new JLabel("You have no users in your contacts list.");

		tabs.removeTabAt(0);
		tabs.addTab("Start", homePanel);
		tabs.addTab("Contacts", contactsPanel);
		tabs.setIconAt(0, homeIcon);
		tabs.setIconAt(1, contactsIcon);

		contactsAvailable.setForeground(Color.BLUE.darker());
		contactsAvailable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		contactsAvailable.setText("0 Contacts Online");
		homePanel.add(contactsAvailable);

		contactsPanel.setLayout(new BorderLayout());
		contactsPanel.add(noContactsAvailable, BorderLayout.NORTH);

		contactsAvailable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tabs.setSelectedIndex(1);
			}
		});
		setTitle(String.format("Minto %s - Logged in to %s", core.verNumber, core.username));
	}

	private ImageIcon loadIcon(String location) {
		URL loadedImage = getClass().getClassLoader().getResource(location);
		return new ImageIcon(loadedImage);
	}

	private JMenuBar createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu help = new JMenu("Help");
		JMenu file = new JMenu("File");

		file.add(createMenuItem("Send message", e -> {
			if (core.isLoggedIn)
				core.showMessageWindow();
		}, true));
		file.add(createMenuItem("Options", e -> {
			new OptionsWindow();
		}, true));
		file.add(createMenuItem("Log Off", e -> logOff(), true));
		file.add(createMenuItem("Close", e -> {
			dispose();
			Options options = new Options();
			options.saveOptions();
			if (core.isLoggedIn && Options.playSounds) {
				snd = core.snd;
				snd.playSound("/sounds/LOGOUT.WAV", true);
			}

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