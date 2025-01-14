package in.priviec.core;

import in.priviec.core.net.AudioPeer;
import in.priviec.core.net.Peer;
import in.priviec.windows.MainWindow;
import in.priviec.windows.MessageWindow;

public class Core {
	public final String verNumber = "0.0.1a";
	public static boolean IS_DEBUG = false;

	public MainWindow mainWind;
	public Options options;
	public Sound snd;
	public MessageWindow messageWind;

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

		if (Options.playSounds) {
			initSound();
		}
	}

	public void startCall() {
		audioPeer.start();
	}

	public void endCall() {
		audioPeer.stop();
	}

	public Core(MainWindow mainWind) {
		this.mainWind = mainWind;
	}

	public void logIn() {
		isLoggedIn = true;
		userStatus = "ONLINE";
		mainWind.logIn();

		peer = new Peer(listeningPort);
		peer.start();

		if (IS_DEBUG)
			System.out.println("Listening on " + listeningPort);

		if (Options.playSounds) {
			snd.playSound("/sounds/LOGIN.WAV", false);
		}
	}

	public void initSound() {
		snd = new Sound();
		mainWind.snd = snd;
	}

	public void showMessageWindow() {
		if (messageWind == null) {
			messageWind = new MessageWindow(this);
			peer.setMsgWindow(messageWind);
		}
	}
}