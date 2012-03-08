package edu.spsu.cs3243;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	private static boolean LOGGING_ENABLED = true;

	private static LogType type = LogType.FILE;

	public enum LogType {
		SOUT, FILE
	}

	public static void log(String format, Object... args) {
		if (LOGGING_ENABLED) {
			switch (type) {
			case SOUT:
				System.out.println(String.format(format, args));
				break;

			case FILE: {
				BufferedWriter writer;
				try {
					writer = new BufferedWriter(new FileWriter("output.log", true));
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
