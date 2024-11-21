package in.priviec.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Options {
	public static boolean playSounds;
	public static boolean createTrayIcon;

	private String getPathForOS() {
		String user = System.getProperty("user.name");
		if (System.getProperty("os.name").contains("Linux")) {
			return "/home/" + user + "/.config/Pyzdovnaje/Pyzdovnaje.cfg";
		} else if (System.getProperty("os.name").contains("Windows")) {
			return "C:\\Users\\" + user + "\\AppData\\Roaming\\Pyzdovnaje\\Pyzdovnaje.cfg";
		}

		throw new UnsupportedOperationException();
	}

	public void saveOptions() {
		PrintWriter writer;
		File file = new File(getPathForOS());
		file.getParentFile().mkdirs();

		try {
			writer = new PrintWriter(file);
			writer.println("playSounds=" + playSounds);
			writer.println("createTrayIcon=" + createTrayIcon);

			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void readOptions() {
		File file = new File(getPathForOS());
		if (!file.exists()) {
			if(Core.IS_DEBUG)
				System.out.println("Config file has not been created yet!");
			return;
		}

		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String option = scanner.nextLine();
				if (option.startsWith("playSounds=")) {
					playSounds = Boolean.parseBoolean(option.split("=")[1]);
				}
				if (option.startsWith("createTrayIcon=")) {
					createTrayIcon = Boolean.parseBoolean(option.split("=")[1]);
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}