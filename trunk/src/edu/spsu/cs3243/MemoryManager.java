package edu.spsu.cs3243;

import java.util.Arrays;
import java.util.Map.Entry;

public class MemoryManager {
	// Static members
	/**
	 * The static instance for the Ram
	 */
	private static MemoryManager ramInstance = null;

	/**
	 * The static instance for the Disk
	 */
	private static MemoryManager diskInstance = null;

	public static final String EMPTY_MEMORY = "00000000";
	public static final int WORD_SIZE = 8;

	// Instance members
	/**
	 * Where we're actually storing the memory
	 */
	private String[] memory;

	/**
	 * How big the memory is
	 */
	private int size;

	/**
	 * How many frames we have
	 */
	private int numFrames;

	/**
	 * Which frames are being used
	 */
	private boolean[] full;

	/**
	 * Gives you the Ram instance
	 * 
	 * @return The Ram instance
	 */
	public static synchronized MemoryManager ram() {
		if (ramInstance == null) {
			ramInstance = new MemoryManager(1024);
		}

		return ramInstance;
	}

	/**
	 * Gives you the Disk instance
	 * 
	 * @return The Disk instance
	 */
	public static synchronized MemoryManager disk() {
		if (diskInstance == null) {
			diskInstance = new MemoryManager(2048);
		}

		return diskInstance;
	}

	/**
	 * Creates a new instance with the given size
	 * 
	 * @param size
	 *            How much memory we will allocate
	 */
	private MemoryManager(int size) {
		this.size = size;
		memory = new String[size];
		numFrames = size / 4;
		full = new boolean[numFrames];
		erase();
	}

	/**
	 * Where the next available frame is
	 * 
	 * @return The index of the next available frame, or -1 if it is full
	 */
	public int nextFrame() {
		int i = 0;
		for (boolean b : full) {
			if (!b) {
				return i;
			}
			i++;
		}

		return -1;
	}

	/**
	 * How big our memory is
	 * 
	 * @return The size of our memory
	 */
	public int size() {
		return size;
	}

	/**
	 * How many frames we have
	 * 
	 * @return The number of frames we have
	 */
	public int frames() {
		return numFrames;
	}

	/**
	 * Determines if we have any empty space
	 * 
	 * @return False if the entire array is empty, true if it contains anything at all
	 */
	public boolean empty() {
		for (int j = 0; j < numFrames; j++) {
			if (full[j] == true) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determines if we have any empty space
	 * 
	 * @return True if we have no empty space, false if we have any at all
	 */
	public boolean full() {
		for (int j = 0; j < numFrames; j++) {
			if (full[j] == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * The number of frames we have used up
	 * 
	 * @return The number of used frames
	 */
	public int usedFrames() {
		return numFrames - freeFrames();
	}

	/**
	 * The number of free frames
	 * 
	 * @return The number of free frames
	 */
	public int freeFrames() {
		int free = numFrames;
		for (int j = 0; j < numFrames; j++) {
			if (full[j] == true) {
				free--;
			}
		}
		return free;
	}

	/**
	 * Manually mark a frame as full
	 * 
	 * @param page
	 *            Which page we are marking as full
	 */
	public void markFrameFull(int page) {
		if (page >= 0 && page < numFrames) {
			full[page] = true;
		}
	}

	/**
	 * Handy toString override
	 */
	@Override()
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Memory Dump: \n");
		for (int i = 0; i < memory.length; i++) {
			if (memory[i] != null) {
				b.append(String.format("%d: %s\n", i, memory[i]));
			}
		}
		return b.toString();
	}

	/**
	 * Erases the contents of our memory (What? I forgot what I was talking about...)
	 */
	public void erase() {
		Arrays.fill(full, false);
		Arrays.fill(memory, EMPTY_MEMORY);
	}
	
	/**
	 * Reads a word from the given address
	 * 
	 * @param address
	 *            The address to read from
	 * @return The word that we read in
	 */
	public String read(int address) {
		if (address > 0 && address <= size)
			return memory[address];
		else
			return null;
	}

	/**
	 * Writes the given word to the given address in memory
	 * 
	 * @param word
	 *            The word we want to write
	 * @param address
	 *            The location we're writing to
	 */
	public void write(String word, int address) {
		if (word != null && word.length() > 0 && word.length() <= WORD_SIZE) {
			if (address >= 0 && address < size) {
				memory[address] = word;
			}
		}
	}

	/**
	 * Helper for reading words with offsets. This is the DMA way
	 * 
	 * @param baseAddress
	 *            The base address to read from
	 * @param offset
	 *            The offset to use
	 * @return The word that we find at (baseAddress + offset)
	 */
	public String read(int baseAddress, int offset) {
		return read(baseAddress + offset);
	}

	/**
	 * Writes the given word to (baseAddress + offset). This is the DMA way
	 * 
	 * @param word
	 *            The word to write
	 * @param baseAddress
	 *            The base address to write to
	 * @param offset
	 *            The offset to use
	 */
	public void write(String word, int baseAddress, int offset) {
		write(word, baseAddress + offset);
	}

	/**
	 * Reads a page based on the given base address. For example: Process p's base address is 20. To read page 4, pass this function: (20, 4).
	 * 
	 * @param baseAddress
	 *            The base address of the process that owns the page we want to read
	 * @param page
	 *            The page we want to read
	 * @return The filled page calculated by (baseAddress + (page * Driver.WORDS_PER_PAGE))
	 */
	public String[] readPage(int baseAddress) {
		int pageAddress = (baseAddress);
		if (pageAddress >= 0 && pageAddress < size) {
			String[] ret = new String[Driver.WORDS_PER_PAGE];

			for (int i = 0; i < Driver.WORDS_PER_PAGE; i++) {
				ret[i] = read(pageAddress, i);
			}

			return ret;
		}

		return null;
	}

	/**
	 * Writes the given data to the given process's page. Calculated by (baseAddress + (page * Driver.WORDS_PER_PAGE))
	 * 
	 * @param data
	 *            The data we want to write
	 * @param baseAddress
	 *            The base address of the process that owns this page
	 * @param page
	 *            The page we're wanting to write to
	 */
	public void writePage(String[] data, int baseAddress) {
		int pageAddress = (baseAddress);
		if (pageAddress >= 0 && pageAddress < size && data != null && data.length == 4) {
			for (int i = 0; i < Driver.WORDS_PER_PAGE; i++) {
				write(data[i], pageAddress, i);
			}
		}
	}

	/**
	 * Writes the given data into the next available page in memory
	 * 
	 * @param data
	 *            The data we're writing to memory
	 * @return The base address we wrote to, or -1 if we can't write to a page
	 */
	public int writeNextAvailablePage(String[] data) {
		if (data != null && data.length == 4) {
			for (int j = 0; j < numFrames; j++) {
				if (full[j] == false) {

					for (int i = 0; i < Driver.WORDS_PER_PAGE; i++) {
						write(data[i], j * Driver.WORDS_PER_PAGE, i);
					}

					full[j] = true;
					return j * Driver.WORDS_PER_PAGE;
				}
			}
		}

		return -1;
	}

	private class OldCode {
		/**
		 * Reads a single entry of memory at the given location
		 * 
		 * @param loc
		 *            The location to read memory from
		 * @return The entry at the given location
		 */
		public String read(int loc) {
			if (loc >= 0 && loc < size) {
				return memory[loc];
			} else {
				System.out.println("Memory Read Error: location:" + loc);
				return "";
			}
		}

		/**
		 * Reading words from memory the DMA way
		 * 
		 * @param b
		 *            The base address
		 * @param o
		 *            The offset from the base address
		 * @return The word we read
		 */
		public String read(int b, int o) {
			if (b + o >= 0 && b + o < size) {
				return memory[b + o];
			} else {
				System.out.println("RAM Read Error: location:" + (b + o));
				return "";
			}
		}

		/**
		 * Reads an entire page from memory using the given page address
		 * 
		 * @param page
		 *            The page we want to read from memory
		 * @return The page we read from memory
		 */
		public String[] readFrame(int base, int page) {
			if (page >= 0 && page < numFrames) {
				String[] ret = new String[Driver.WORDS_PER_PAGE];

				for (int i = 0; i < Driver.WORDS_PER_PAGE; i++) {
					ret[i] = read(base + (page * Driver.WORDS_PER_PAGE), i);
				}

				return ret;
			}

			return null;
		}

		public String[] readFrame(int address) {
			if (address >= 0 && address < size) {
				String[] ret = new String[Driver.WORDS_PER_PAGE];

				for (int i = 0; i < Driver.WORDS_PER_PAGE; i++) {
					ret[i] = read(address, i);
				}

				return ret;
			}

			return null;
		}

		/**
		 * Writes a page of data into memory
		 * 
		 * @param w
		 *            The page of data
		 * @return The location in memory where we started writing the data
		 */
		public int writeFrame(String[] w) {
			if (full()) {
				System.out.println("Write Error: Memory full!");
			} else {
				if (w != null) {
					for (int j = 0; j < numFrames; j++) {
						if (full[j] == false) {

							for (int i = 0; i < w.length; i++) {
								write(w[i], j * Driver.WORDS_PER_PAGE + i);
							}

							full[j] = true;
							return j * Driver.WORDS_PER_PAGE;
						}
					}
				}
			}
			return -1;
		}

		/**
		 * Writes over the frame at the given index with the new frame passed in
		 * 
		 * @param frame
		 *            The new frame to write
		 * @param address
		 *            The base address for the page to overwrite
		 */
		public void overwriteFrame(String[] frame, int address) {
			if (address >= 0 && address < size) {
				if (frame != null) {
					for (int i = 0; i < frame.length; i++) {
						write(frame[i], address + i);
					}
				}
			}
		}

		// Write data to Memory
		public void write(String data, int location) {
			if (data != null) {
				data = hexFormat(data);
				if (location >= 0 && location < size) {
					memory[location] = data;
				} else {
					System.out.println("Write Error");
				}
			} else {
				System.out.println("Write Invalid");
			}
		}

		// DMA Write data to Memory
		public void write(String w, int b, int o) {
			if (w != null) {
				w = hexFormat(w);
				if (b + o >= 0 && b + o < size) {
					memory[b + o] = w;
				} else {
					System.out.println("Memory Write Error: location:" + (b + o));
				}
			}
		}
	}

	/**
	 * Puts the given String into hex format
	 * 
	 * @param s
	 *            The String to be formatted
	 * @return The formatted String
	 */
	public static String hexFormat(String s) {
		if (s != null && s != "") {
			s = s.toUpperCase();
			for (int i = 0 + s.length(); i < 8; ++i)
				s = "0" + s;
			return s;
		}
		return EMPTY_MEMORY;
	}

	public void removeProcessPages(PCB process) {
		for (Entry<Integer, Integer> entry : process.pageTable.entrySet()) {
			Integer ramLocation = entry.getValue();

			full[ramLocation / Driver.WORDS_PER_PAGE] = false;
			for (int i = 0; i < Driver.WORDS_PER_PAGE; i++) {
				write(EMPTY_MEMORY, ramLocation, i);
			}
		}
	}
}
