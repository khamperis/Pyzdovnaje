package in.priviec.core.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import in.priviec.windows.MessageWindow;

public class Peer {
	private int port;
	private MessageWindow messageWindow;
	private ServerSocket servSock;

	private Thread msgThread;

	private volatile boolean running = false;

	private List<ClientHandler> clientHandlers = new ArrayList<>();

	public Peer(int port, MessageWindow messageWindow) {
		this.port = port;
		this.messageWindow = messageWindow;
	}

	public void start() {
		running = true;
		try {
			servSock = new ServerSocket(port);
			msgThread = new Thread(() -> {
				while (running) {
					try {
						Socket sock = servSock.accept();
						ClientHandler handler = new ClientHandler(sock);
						clientHandlers.add(handler);
						Thread handlerThread = new Thread(handler);
						handlerThread.start();
					} catch (IOException e) {
						if (running)
							e.printStackTrace();
					}
				}
			});

			msgThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void stop() {
		if (!running)
			return;
		running = false;

		try {
			if (servSock != null) {
				servSock.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (ClientHandler handler : clientHandlers) {
			handler.stop();
		}

		if (msgThread != null) {
			try {
				msgThread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		clientHandlers.clear();
		msgThread = null;
	}

	public synchronized void sendMsg(String msg, String senderHost, int senderPort) {
		try (Socket sock = new Socket(senderHost, senderPort)) {
			PrintWriter send = new PrintWriter(sock.getOutputStream(), true);

			send.println(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setMsgWindow(MessageWindow messageWindow) {
		this.messageWindow = messageWindow;
	}

	private class ClientHandler implements Runnable {
		public Socket sock;

		public ClientHandler(Socket sock) {
			this.sock = sock;
		}

		@Override
		public void run() {
			try (BufferedReader input = new BufferedReader(new InputStreamReader(sock.getInputStream()))) {
				String msg;
				while (running && (msg = input.readLine()) != null) {
					String msgTemp = msg;

					SwingUtilities.invokeLater(() -> messageWindow.appendMessage(msgTemp, true));
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				stop();
			}
		}

		public synchronized void stop() {
			if (!sock.isClosed()) {

				running = false;
				try {
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else
				return;
		}
	}
}