package edu.spsu.cs3243;

import java.util.ArrayList;
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

	/**
	 * A String that represents a blank word in memory
	 */
	public static final String EMPTY_MEMORY = "00000000";
	
	/**
	 * The size of a word in memory
	 */
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
	 * An array that represents which process owns a specific page
	 */
	private PCB[] pageManagement;
	
	/**
	 * Each entry in this list is the amount of memory being used after a paging operation has been executed
	 */
	private ArrayList<Integer> memoryUsageList;

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
		pageManagement = new PCB[numFrames];
		memoryUsageList = new ArrayList<Integer>();

		erase();
	}

	/**
	 * Where the next available frame is
	 * 
	 * @return The index of the next available frame, or -1 if it is full
	 */
	public synchronized int nextPage() {
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
	public synchronized boolean empty() {
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
	public synchronized boolean full() {
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
	public synchronized int usedFrames() {
		return numFrames - freeFrames();
	}

	/**
	 * The number of free frames
	 * 
	 * @return The number of free frames
	 */
	public synchronized int freeFrames() {
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
	public synchronized String toString() {
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
	public synchronized void erase() {
		Arrays.fill(full, false);
		Arrays.fill(memory, EMPTY_MEMORY);
		Arrays.fill(pageManagement, null);
	}

	/**
	 * Reads a word from the given address
	 * 
	 * @param address
	 *            The address to read from
	 * @return The word that we read in
	 */
	public String read(int address) {
		if (address >= 0 && address < size)
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
	 * Reads a page based on the given base address.
	 * 
	 * @param baseAddress
	 *            The base address of the process that owns the page we want to read
	 * @param owner
	 *            The process that wants to read the page
	 * @return The filled page
	 */
	public synchronized String[] readPage(int baseAddress, PCB owner) {
		int pageAddress = (baseAddress);
		int page = baseAddress / Driver.WORDS_PER_PAGE;
		if (pageAddress >= 0 && pageAddress < size) {
			if (pageManagement[page] == owner) {
				String[] ret = new String[Driver.WORDS_PER_PAGE];

				for (int i = 0; i < Driver.WORDS_PER_PAGE; i++) {
					ret[i] = read(pageAddress, i);
				}

				return ret;
			} else {
				Logger.log("Oh snap 1");
			}
		}

		return null;
	}

	/**
	 * Writes the given data to the given process's page.
	 * 
	 * @param data
	 *            The data we want to write
	 * @param baseAddress
	 *            The base address of the page we want to write to
	 * @param owner
	 *            The owner of the page we want to write to
	 */
	public synchronized void writePage(String[] data, int baseAddress, PCB owner) {
		int pageAddress = (baseAddress);
		int page = baseAddress / Driver.WORDS_PER_PAGE;
		if (pageAddress >= 0 && pageAddress < size && data != null && data.length == 4) {
			if (pageManagement[page] == owner) {
				for (int i = 0; i < Driver.WORDS_PER_PAGE; i++) {
					write(data[i], pageAddress, i);
				}
			} else {
				Logger.log("Oh snap 2");
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
	public synchronized int writeNextAvailablePage(String[] data, PCB owner) {
		if (data != null && data.length == 4) {
			for (int j = 0; j < numFrames; j++) {
				if (full[j] == false && pageManagement[j] == null) {
					for (int i = 0; i < Driver.WORDS_PER_PAGE; i++) {
						write(data[i], j * Driver.WORDS_PER_PAGE, i);
					}

					full[j] = true;
					pageManagement[j] = owner;
					memoryUsageList.add(getCurrentUsage());
					return j * Driver.WORDS_PER_PAGE;
				}
			}
		}

		return -1;
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

	/**
	 * Clears out the pages that this process owns
	 * 
	 * @param process
	 *            The process whose pages we're clearing out
	 */
	public synchronized void removeProcessPages(PCB process) {
		for (Entry<Integer, Integer> entry : process.pageTable.entrySet()) {
			Integer ramLocation = entry.getValue();

			full[ramLocation / Driver.WORDS_PER_PAGE] = false;
			pageManagement[ramLocation / Driver.WORDS_PER_PAGE] = null;
			for (int i = 0; i < Driver.WORDS_PER_PAGE; i++) {
				write(EMPTY_MEMORY, ramLocation, i);
			}
		}
		
		memoryUsageList.add(getCurrentUsage());
	}

	/**
	 * Manually marking a page as having the given owner.
	 * 
	 * @param process
	 *            The process that owns the page
	 * @param page
	 *            The page we need to mark
	 */
	public synchronized void markFrameOwner(PCB process, int page) {
		pageManagement[page] = process;
	}
	
	public int getCurrentUsage() {
		int ret = numFrames - freeFrames();
		
		return ret;
	}
	
	public void printUsage() {
		StatsLogger.log("Memory Usage:");
		for(Integer i : memoryUsageList) {
			StatsLogger.log("%d", i);
		}
	}
}
