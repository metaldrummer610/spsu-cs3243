package edu.spsu.cs3243;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StatsLogger {

	private static boolean LOGGING_ENABLED = true;

	private static LogType type = LogType.FILE;
	public static String FILENAME = "stats.log";
	private static BufferedWriter writer;

	public enum LogType {
		SOUT, FILE
	}

	static {
		File f = new File(FILENAME);
		if (f.exists())
			f.delete();
	}
	
	public static void open() {
		try {
			writer = new BufferedWriter(new FileWriter(FILENAME, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void log(String format, Object... args) {
		if (LOGGING_ENABLED) {
			switch (type) {
			case SOUT:
				System.out.println(String.format(format, args));
				break;

			case FILE: {
				try {
					writer.write(String.format(format, args));
					writer.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			default:
				break;
			}
		}
	}
	
	public static void close() {
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
