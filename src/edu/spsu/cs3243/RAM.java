package edu.spsu.cs3243;

import java.util.Arrays;

@Deprecated
public class RAM {

	private String[] ram;
	//private int next_location;
	private int size;
	private int numFrames;
	private boolean[] full;
	private static RAM instance = null;

	public static synchronized RAM instance() {
		if (instance == null) {
			instance = new RAM();
		}

		return instance;
	}

	private RAM() {
		ram = new String[1024];
		// next_location = 0;
		size = 1024;

		numFrames = (size / 4);
		full = new boolean[numFrames];
		for (int i = 0; i < numFrames; i++) {
			full[i] = false;
		}

	}

	private RAM(int a) {
		ram = new String[a];
		size = ram.length;
		// next_location = 0;

		numFrames = (size / 4);
		full = new boolean[numFrames];
		for (int i = 0; i < numFrames; i++) {
			full[i] = false;
		}
	}

	public int nextFrame() {
		return Arrays.asList(full).indexOf(false);
	}

	public int size() {
		return size;
	}

	public int frames() {
		return numFrames;
	}

	public boolean empty() {
		// return next_location == 0;
		for (int j = 0; j < numFrames; j++) {
			if (full[j] == true) {
				return false;
			}

		}
		return true;
	}

	public boolean full() {
		// return next_location == size;

		for (int j = 0; j < numFrames; j++) {
			if (full[j] == false) {
				return false;
			}
		}

		return true;
	}

	public int usedFrames() {
		return numFrames - freeFrames();

	}

	public int freeFrames() {
		int free = numFrames;
		for (int j = 0; j < numFrames; j++) {
			if (full[j] == true) {
				free--;
			}
		}

		return free;
	}

	/*
	 * public int free() { return 1024 - next_location; }
	 */

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Memory Dump: \n");
		for (int i = 0; i < ram.length; i++) {
			if (ram[i] != null) {
				b.append(String.format("%d: %s\n", i, ram[i]));
			}
		}
		return b.toString();
	}

	// format hex string for RAM
	public static String hexFormat(String s) {
		if (s != null && s != "") {
			s = s.toUpperCase();
			for (int i = 0 + s.length(); i < 8; ++i)
				s = "0" + s;
			return s;
		}
		return "00000000";
	}

	// Erases data from RAM
	public void erase() {
		for (int i = 0; i < ram.length; i++) {
			ram[i] = "00000000";
		}
		// next_location = 0;

		for (int j = 0; j < numFrames; j++) {
			full[j] = false;
		}
	}

	// Read RAM
	public String read(int loc) {
		if (loc >= 0 && loc < 1024) {
			return ram[loc];
		}

		else {
			System.out.println("RAM Read Error: location:" + (loc + 0));
			return "";

		}
	}

	// DMA Read from RAM
	public String read(int b, int o) {
		if (b + o >= 0 && b + o < 1024) {
			return ram[b + o];
		}

		else {
			System.out.println("RAM Read Error: location:" + (b + o));
			return "";
		}
	}

	// Write data to next free ram location
	public int write(String w) {
		/*
		 * if (next_location > 1024) { System.out.println("Write Error: location" + next_location); } else { if (w != null) { ram[next_location] = hexFormat(w); int temp =
		 * next_location; next_location++; return temp; } } return -1;
		 */

		if (full()) {
			System.out.println("Write Error: RAM full");
		}

		else {

			if (w != null) {
				int writeRAM = 1;
				for (int j = 0; j < numFrames && writeRAM == -1; j++) {
					if (full[j] == false) {
						writeRAM = j;
					}
				}

			}
		}
		return -1;
	}

	// Write data to RAM
	public void write(String data, int location) {
		if (data != null) {
			data = hexFormat(data);
			if (location >= 0 && location < 1024) {
				ram[location] = data;
			}

			else {
				System.out.println("Write Error");
			}
		} else {
			System.out.println("Write Invalid");
		}

	}

	// DMA Write data to RAM
	public void write(String w, int b, int o) {
		if (w != null) {
			w = hexFormat(w);
			if (b + o >= 0 && b + o < 1024) {
				ram[b + o] = w;
			} else {
				System.out.println("RAM Write Error: location:" + (b + o));
			}
		}
	}

	public int writeFrame(String s, int b, int o) {
		if (s != null) {
			s = hexFormat(s);
			// / System.out.println("Writing:"+s+" into:"+b+"+"+o);
			if ((b * 4 + o) >= 0 && (b * 4 + o) < size) {
				if (full[b] == false) {
					full[b] = true;
				}
				ram[b * 4 + o] = s;

			} else {
				System.out.println("RAM Write Error @ Location:" + (b + o));
			}
		}
		return b;
	}

	/**
	 * Writes the data to RAM where the page is unknown
	 * 
	 * @param s
	 *            The string to be written
	 * @return The page that was written to
	 */
	public int writeFrame(String s) {
		if (s != null) {
			s = hexFormat(s);
			int b = this.nextFrame();
			// System.out.println("Writing:"+s+" into:"+b);
			if ((b * 4) >= 0 && (b * 4) < size) {
				if (full[b] == false) {
					full[b] = true;
				}
				ram[b * 4] = s;

			} else {
				System.out.println("RAM Write Error @ Location:" + b);
			}
			return (b * 4);
		}
		return -1;
	}

}
