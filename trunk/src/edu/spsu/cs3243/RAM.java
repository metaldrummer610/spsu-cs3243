package edu.spsu.cs3243;

public class RAM {

	private String[] ram;
	private int next_location;
	private int size;

	private static RAM instance = null;

	public static RAM instance() {
		if (instance == null) {
			instance = new RAM();
		}

		return instance;
	}

	private RAM() {
		ram = new String[1024];
		next_location = 0;
		size = 1024;

	}

	private RAM(int a) {
		ram = new String[a];
		size = ram.length;
		next_location = 0;
	}

	public int size() {
		return size;
	}

	public boolean empty() {
		return next_location == 0;
	}

	public boolean full() {
		return next_location == size;
	}

	public int free() {
		return 1024 - next_location;
	}

	public String toString() {
		String temp = "Memory Dump: \n";
		for (int i = 0; i < ram.length; i++) {
			if (ram[i] != null) {
				temp += ram[i] + "\n";
			}
		}
		return temp;
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
		next_location = 0;
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
		if (next_location > 1024) {
			System.out.println("Write Error: location" + next_location);
		}

		else {
			if (w != null) {
				ram[next_location] = hexFormat(w);
				int temp = next_location;
				next_location++;
				return temp;
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

}
