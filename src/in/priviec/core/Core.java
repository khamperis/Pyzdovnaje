package in.priviec.core;

import in.priviec.core.net.Peer;
import in.priviec.windows.MainWindow;
import in.priviec.windows.MessageWindow;

public class Core {
	private MainWindow mainWin;
	private Sound snd;
	public MessageWindow messageWindow;

	public String userStatus;
	public boolean isLoggedIn;
	public String username;
	public Peer peer;

	public void init() {
		userStatus = "Offline";
		isLoggedIn = false;
	}

	public Core(MainWindow mainWin) {
		this.mainWin = mainWin;
	}

	public int listeningPort = 1515;

	public void logIn() {
		snd = new Sound();
		userStatus = "Online";
		isLoggedIn = true;
		mainWin.logIn();

		peer = new Peer(listeningPort, messageWindow);
		peer.start();
		System.out.println("listening on " + listeningPort);

		snd.playSound("/sounds/LOGIN.WAV", false);
	}

	public void showMessageWindow() {
		if (messageWindow == null) {
			messageWindow = new MessageWindow(this);
			peer.setMsgWindow(messageWindow);
		}
	}
}