package in.priviec.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
	public void playSound(String location, boolean waitForSound) {
		try {
			InputStream audioStream = getClass().getResourceAsStream(location);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			int bytesRead;
			while ((bytesRead = audioStream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, bytesRead);
			}
			buffer.flush();

			byte[] audioData = buffer.toByteArray();
			InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(byteArrayInputStream);

			if (waitForSound) {
				Clip clip = AudioSystem.getClip();

				clip.open(audioInputStream);

				clip.start();
				clip.drain();

				clip.close();
			} else {
				Clip clip = AudioSystem.getClip();

				clip.open(audioInputStream);

				clip.start();

				new Thread(() -> {
					clip.drain();
					clip.close();
				});
			}

			audioStream.close();
			byteArrayInputStream.close();
		} catch (UnsupportedAudioFileException e) {
			System.out.println("The specified audio file is not supported.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error playing the audio file.");
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			System.out.println("Audio line for playing back is unavailable.");
			e.printStackTrace();
		}
	}
}