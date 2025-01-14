package in.priviec.windows;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

	private ImageIcon homeIcon;
	private ImageIcon contactsIcon;
	private ImageIcon statusBarStatusIcon;
	private ImageIcon startCallIconEnabled;
	private ImageIcon endCallIconEnabled;
	private ImageIcon startCallIconDisabled;
	private ImageIcon endCallIconDisabled;
	private Image statusTrayImage;
	private JTabbedPane tabs = new JTabbedPane();
	private JPanel loginPanel = new JPanel();
	private JPanel topPanel = new JPanel();
	private JPanel callButtonsPanel = new JPanel();
	private JPanel statusPanel = new JPanel();
	private JLabel statusLabel = new JLabel();
	private Options options = new Options();
	private JLabel startCall;
	private JLabel endCall;
	private JLabel statusIconLabel;
	private Core core;
	private TrayIcon trayIcon;
	private LoginWindow loginWind;
	private AboutWindow aboutWind;
	private OptionsWindow optionsWind;
	private JMenu changeStatus;
	public Sound snd;

	public MainWindow() {
		core = new Core(this);
		core.init();

		homeIcon = loadIcon("toolbar/HOUSE.png");
		contactsIcon = loadIcon("toolbar/CONTACT.png");
		tabs = new JTabbedPane();
		tabs.addTab(" ", loginPanel);
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
				if (loginWind == null || loginWind.frame == null) {
					loginWind = new LoginWindow(core);
				}
			}
		});

		setTitle(String.format("Pyzdovnaje %s", core.verNumber));
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
				exit();
			}
		});
	}

	private JPanel bottomPanel() {
		startCallIconDisabled = loadIcon("other/STARTCALL_DISABLED.png");
		endCallIconDisabled = loadIcon("other/ENDCALL_DISABLED.png");
		startCallIconEnabled = loadIcon("other/STARTCALL_ENABLED.png");
		endCallIconEnabled = loadIcon("other/ENDCALL_ENABLED.png");
		startCall = new JLabel(startCallIconDisabled);
		endCall = new JLabel(endCallIconDisabled);

		startCall.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (core.isLoggedIn && !core.audioPeer.inCall) {
					core.startCall();
					endCall.setIcon(endCallIconEnabled);
					startCall.setIcon(startCallIconDisabled);
				}
			}
		});

		endCall.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (core.isLoggedIn && core.audioPeer.inCall) {
					core.endCall();
					endCall.setIcon(endCallIconDisabled);
					startCall.setIcon(startCallIconEnabled);
				}
			}
		});

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
		statusLabel.setText(core.userStatus);

		statusBarStatusIcon = loadIcon("status/" + core.userStatus + ".png");
		if (statusIconLabel != null) {
			statusIconLabel.setIcon(statusBarStatusIcon);
		} else {
			statusIconLabel = new JLabel(statusBarStatusIcon);
		}

		if (Options.createTrayIcon)
			updateSystemTray();
		else if (!Options.createTrayIcon && trayIcon != null) {
			updateSystemTray();
		}

		changeStatus.setEnabled(core.isLoggedIn);
	}

	public void updateSystemTray() {
		if (!SystemTray.isSupported()) {
			return;
		}
		SystemTray tray = SystemTray.getSystemTray();

		statusTrayImage = Toolkit.getDefaultToolkit()
				.getImage(getClass().getClassLoader().getResource("status/" + core.userStatus + ".png"));

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

		online.addActionListener(e -> {
			core.userStatus = "ONLINE";
			updateStatusItems();
		});

		invisible.addActionListener(e -> {
			core.userStatus = "INVISIBLE";
			updateStatusItems();
		});

		away.addActionListener(e -> {
			core.userStatus = "AWAY";
			updateStatusItems();
		});

		busy.addActionListener(e -> {
			core.userStatus = "BUSY";
			updateStatusItems();
		});

		quit.addActionListener(e -> {
			exit();
		});

		trayPopupMenu.add(changeStatus);
		trayPopupMenu.add(quit);

		if (!Options.createTrayIcon && trayIcon != null)
			tray.remove(trayIcon);
		else if (trayIcon != null) {
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

	public void logOff() {
		if (core.isLoggedIn) {
			core.userStatus = "OFFLINE";
			core.isLoggedIn = false;
			tabs.removeTabAt(1);
			tabs.addTab(" ", loginPanel);
			tabs.setIconAt(1, homeIcon);
			tabs.removeTabAt(0);
			updateStatusItems();

			startCall.setIcon(startCallIconDisabled);
			endCall.setIcon(endCallIconDisabled);

			core.peer.stop();

			disposeAllWindows();

			setTitle(String.format("Pyzdovnaje %s", core.verNumber));
			updateStatusItems();
			if (Options.playSounds) {
				snd.playSound("/sounds/LOGOUT.WAV", false);
			}
		} else {
			return;
		}
	}

	private void disposeAllWindows() {
		if (core.messageWind != null && core.messageWind.frame != null) {
			core.messageWind.frame.dispose();
			core.messageWind = null;
		}

		if (loginWind != null && loginWind.frame != null) {
			loginWind.frame.dispose();
			loginWind = null;
		}
	}

	public void logIn() {
		ImageIcon contactsOnlineImage = loadIcon("toolbar/CONTACT_BIG.png");
		updateStatusItems();

		JPanel homePanel = new JPanel();
		JPanel contactsPanel = new JPanel();
		JLabel contactsAvailable = new JLabel(contactsOnlineImage);
//		JLabel noContactsAvailable = new JLabel("You have no users in your contacts list.");

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
//		contactsPanel.add(noContactsAvailable, BorderLayout.NORTH);

		Object[][] contacts = { { "khamperis", "OFFLINE" }, { "khamperis", "ONLINE" }, { "khamperis", "AWAY" },
				{ "khamperis", "BUSY" } };

		DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
				Object[] contact = (Object[]) value;
				String contactName = (String) contact[0];
				String contactStatus = (String) contact[1];
				ImageIcon contactStatusIcon = loadIcon("status/" + contactStatus + ".png");

				label.setHorizontalTextPosition(SwingConstants.RIGHT);
				label.setIcon(contactStatusIcon);
				label.setText(contactName);
				return label;
			}
		};

		JList<Object[]> contactsList = new JList<>(contacts);
		contactsList.setCellRenderer(renderer);

		contactsList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = contactsList.locationToIndex(e.getPoint());

					if (index >= 0) {
//						Object[] selectedContact = contacts[index];
//						String contactName = (String) selectedContact[0];
//						String contactStatus = (String) selectedContact[1];

						core.showMessageWindow();
					}
				}
			}
		});
		JScrollPane contactsScrollPane = new JScrollPane(contactsList);
		contactsPanel.add(contactsScrollPane, BorderLayout.CENTER);

		contactsAvailable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tabs.setSelectedIndex(1);
			}
		});

		startCall.setIcon(startCallIconEnabled);

		setTitle(String.format("Pyzdovnaje %s - Logged in to %s", core.verNumber, core.username));
	}

	private void exit() {
		disposeAllWindows();
		dispose();
		options.saveOptions();

		if (core.isLoggedIn && Options.playSounds) {
			snd.playSound("/sounds/LOGOUT.WAV", true);
		}
		System.exit(0);
	}

	private ImageIcon loadIcon(String location) {
		URL loadedImage = getClass().getClassLoader().getResource(location);
		return new ImageIcon(loadedImage);
	}

	private JMenuBar createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu help = new JMenu("Help");
		JMenu file = new JMenu("File");

		changeStatus = new JMenu("Change status");
		changeStatus.add(createMenuItem("Online", e -> {
			core.userStatus = "ONLINE";
			updateStatusItems();
		}, true));
		changeStatus.add(createMenuItem("Invisible", e -> {
			core.userStatus = "INVISIBLE";
			updateStatusItems();
		}, true));
		changeStatus.add(createMenuItem("Away", e -> {
			core.userStatus = "AWAY";
			updateStatusItems();
		}, true));
		changeStatus.add(createMenuItem("Busy", e -> {
			core.userStatus = "BUSY";
			updateStatusItems();
		}, true));
		file.add(changeStatus);
		file.addSeparator();

		file.add(createMenuItem("Log Off", e -> logOff(), true));
		file.addSeparator();
		file.add(createMenuItem("Options...", e -> {
			if (optionsWind == null || optionsWind.frame == null) {
				optionsWind = new OptionsWindow(core);
			}
		}, true));
		file.add(createMenuItem("Close", e -> {
			exit();
		}, true));

		help.add(createMenuItem("Help", e -> {
			try {
				URI help1 = new URI("https://github.com/khamperis/Pyzdovnaje");
				Desktop.getDesktop().browse(help1);
			} catch (URISyntaxException | IOException e1) {
				e1.printStackTrace();
			}
		}, true));
		help.add(createMenuItem("FAQ", e -> {
			try {
				URI faq = new URI("https://github.com/khamperis/Pyzdovnaje/discussions");
				Desktop.getDesktop().browse(faq);
			} catch (URISyntaxException | IOException e1) {
				e1.printStackTrace();
			}
		}, true));
		help.addSeparator();
		help.add(createMenuItem("Check for Update", e -> {
			try {
				URI checkForUpdates = new URI("https://github.com/khamperis/Pyzdovnaje/releases");
				Desktop.getDesktop().browse(checkForUpdates);
			} catch (URISyntaxException | IOException e1) {
				e1.printStackTrace();
			}
		}, true));
		help.add(createMenuItem("Report a Problem", e -> {
			try {
				URI reportProblem = new URI("https://github.com/khamperis/Pyzdovnaje/issues");
				Desktop.getDesktop().browse(reportProblem);
			} catch (URISyntaxException | IOException e1) {
				e1.printStackTrace();
			}
		}, true));
		help.addSeparator();
		help.add(createMenuItem("About", e -> {
			if (aboutWind == null) {
				aboutWind = new AboutWindow();
			}
			aboutWind.open();
		}, true));

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

		byte[] spacingSizes = { 2, 5, 6, 4, 5, 6, 3, 6, 4, 6 };
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
		if (args.length > 0 && args[0].equals("debug"))
			Core.IS_DEBUG = true;

		try {
			UIManager.put("MenuItem.margin", new Insets(-1, 0, -1, 2));
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(MainWindow::new);
	}
}