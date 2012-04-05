package edu.spsu.cs3243;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	private static boolean LOGGING_ENABLED = true;

	private static LogType type = LogType.FILE;
	private static String FILENAME = "output.log";

	public enum LogType {
		SOUT, FILE
	}
	
	static {
		File f = new File(FILENAME);
		if(f.exists())
			f.delete();
	}

	public static synchronized void log(String format, Object... args) {
		if (LOGGING_ENABLED) {
			switch (type) {
			case SOUT:
				System.out.println(String.format(format, args));
				break;

			case FILE: {
				BufferedWriter writer;
				try {
					writer = new BufferedWriter(new FileWriter(FILENAME, true));
					writer.write(String.format(format, args));
					writer.newLine();
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			default:
				break;
			}
		}
	}
}
