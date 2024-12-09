package in.priviec.core;

import in.priviec.core.net.AudioPeer;
import in.priviec.core.net.Peer;
import in.priviec.windows.MainWindow;
import in.priviec.windows.MessageWindow;

public class Core {
	public String verNumber = "0.0.1";
	public static boolean IS_DEBUG = false;

	private MainWindow mainWin;
	private Options options;
	public Sound snd;
	public MessageWindow messageWindow;

	public String userStatus;
	public boolean isLoggedIn;
	public String username;
	public Peer peer;
	public AudioPeer audioPeer;

	public int listeningPort = 1515;

	public void init() {
		options = new Options();
		options.readOptions();
		userStatus = "OFFLINE";
		isLoggedIn = false;
		audioPeer = new AudioPeer();
	}

	public void startCall() {
		audioPeer.start();
	}

	public void endCall() {
		audioPeer.stop();
	}

	public Core(MainWindow mainWin) {
		this.mainWin = mainWin;
	}

	public void logIn() {
		isLoggedIn = true;
		userStatus = "ONLINE";
		mainWin.logIn();

		peer = new Peer(listeningPort, messageWindow);
		peer.start();

		if (IS_DEBUG)
			System.out.println("Listening on " + listeningPort);

		if (Options.playSounds) {
			if (snd == null) {
				snd = new Sound();
			}

			snd.playSound("/sounds/LOGIN.WAV", false);
		}
	}

	public void showMessageWindow() {
		if (messageWindow == null) {
			messageWindow = new MessageWindow(this);
			peer.setMsgWindow(messageWindow);
		}
	}
}