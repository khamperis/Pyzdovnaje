package in.priviec.core.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioPeer {
	private String serverIp = "127.0.0.1";
	private int hostPort = 2526;
	private int receivePort = 2525;
	private int bufferSize = 4096;
	private DatagramSocket sendSocket;
	private DatagramSocket receiveSocket;
	public boolean inCall = false;

	public synchronized void stop() {
		inCall = false;
		receiveSocket.close();
		sendSocket.close();
	}

	public synchronized void start() {
		inCall = true;
		try {
			AudioFormat audioFormat = new AudioFormat(22050, 16, 1, true, false);
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
			TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(info);

			targetDataLine.open(audioFormat);
			targetDataLine.start();

			DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
			SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();

			sendSocket = new DatagramSocket();
			InetAddress serverAddress = InetAddress.getByName(serverIp);
			byte[] buffer = new byte[bufferSize];

			receiveSocket = new DatagramSocket(receivePort);

			Thread senderThread = new Thread(() -> {
				try {
					while (inCall) {
						int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
						if (bytesRead > 0) {
							DatagramPacket packet = new DatagramPacket(buffer, bytesRead, serverAddress, hostPort);
							sendSocket.send(packet);
						}
					}
				} catch (Exception e) {
					if (inCall)
						e.printStackTrace();
				}
			});

			Thread receiverThread = new Thread(() -> {
				try {
					while (inCall) {
						DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
						receiveSocket.receive(packet);
						sourceDataLine.write(packet.getData(), 0, packet.getLength());
					}
				} catch (Exception e) {
					if (inCall)
						e.printStackTrace();
				}
			});

			senderThread.start();
			receiverThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
