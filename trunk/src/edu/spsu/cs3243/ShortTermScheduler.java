package edu.spsu.cs3243;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;

public class ShortTermScheduler {
	private static ScheduleType type = ScheduleType.SJF;

	public enum ScheduleType {
		FIFO, SJF, PRIORITY
	}

	public static synchronized PCB scheduleNextProcess() {
		ProcessQueue readyQueue = Driver.getReadyQueue();
		ProcessQueue runningQueue = Driver.getRunningQueue();
		// Takes a process from the readyQueue and puts it on the runningQueue

		switch (type) {
		case FIFO:
			if (!readyQueue.processes.isEmpty()) {
				PCB p = readyQueue.processes.get(0);
				runningQueue.processes.add(p);
				readyQueue.processes.remove(p);
				return p;
			}
			break;
		case PRIORITY:
			if (!readyQueue.processes.isEmpty()) {
				Collections.sort(readyQueue.processes, new PriorityComparator());

				PCB p = readyQueue.processes.get(0);
				runningQueue.processes.add(p);
				readyQueue.processes.remove(p);
				System.out.println("Returning process: " + p.toString());
				return p;
			}
			break;
		case SJF:
			if (!readyQueue.processes.isEmpty()) {
				Collections.sort(readyQueue.processes, new SJFComparator());

				PCB p = readyQueue.processes.get(0);
				runningQueue.processes.add(p);
				readyQueue.processes.remove(p);
				System.out.println("Returning process: " + p.toString());
				return p;
			}
			break;
		}

		return null;
	}

	public static class PriorityComparator implements Comparator<PCB> {
		@Override
		public int compare(PCB p1, PCB p2) {
			int priority1 = p1.priority;
			int priority2 = p2.priority;

			if (priority1 < priority2)
				return 1;
			else if (priority1 > priority2)
				return -1;
			else
				return 0;
		}
	}

	public static class SJFComparator implements Comparator<PCB> {
		@Override
		public int compare(PCB p1, PCB p2) {
			int processSize1 = p1.processSize;
			int processSize2 = p2.processSize;

			if (processSize1 > processSize2)
				return 1;
			else if (processSize1 < processSize2)
				return -1;
			else
				return 0;
		}
	}

	/**
	 * This handles a data fault that is generated when a process tries to access a page that has faulted
	 * 
	 * @param e
	 */
	public static synchronized void handleDataFault(DataFaultException e) {
		CPU cpu = e.getCpu();
		PCB process = e.getCurrentProcess();
		int page = e.getPage();

		// Add another fault...
		process.pageFaults++;

		saveState(cpu, process);
		// restoreState(cpu, process);

		int pos = process.instDiskLoc + (page * Driver.WORDS_PER_PAGE);
		String[] data = MemoryManager.disk().readPage(pos);

		int wroteTo = MemoryManager.ram().writeNextAvailablePage(data);
		process.pageFaultTable.put(page, true);
		process.pageTable.put(page, wroteTo);
	}

	public static synchronized void handlePageFault(PageFaultException e) {
		CPU cpu = e.getCpu();
		PCB process = e.getProcess();
		int page = e.getPage();

		saveState(cpu, process);

		String[] data = MemoryManager.disk().readPage(process.instDiskLoc);
		int wroteTo = MemoryManager.ram().writeNextAvailablePage(data);
		process.pageFaultTable.put(page, true);
		process.pageTable.put(page, wroteTo);
	}

	public static void restoreState(CPU cpu, PCB process) {
		Arrays.fill(cpu.getCache(), MemoryManager.EMPTY_MEMORY);
		for (Entry<Integer, Integer> entry : process.pageTable.entrySet()) {
			Integer page = entry.getKey();
			Integer ramLocation = entry.getValue();

			String[] pageData = MemoryManager.ram().readPage(ramLocation);
			for (int offset = 0; offset < Driver.WORDS_PER_PAGE; offset++) {
				cpu.getCache()[(page * Driver.WORDS_PER_PAGE) + offset] = MemoryManager.hexFormat(pageData[offset]);
			}
		}

		cpu.setRegisters(process.registers);
		cpu.setPc(process.pc);
	}

	public static void saveState(CPU cpu, PCB process) {
		@SuppressWarnings("unused")
		int i = 0;
		for (Entry<Integer, Integer> entry : process.pageTable.entrySet()) {
			Integer page = entry.getKey();
			Integer ramLocation = entry.getValue();

			String[] cacheData = new String[Driver.WORDS_PER_PAGE];
			for (int j = 0; j < Driver.WORDS_PER_PAGE; j++) {
				cacheData[j] = cpu.getCache()[(page * Driver.WORDS_PER_PAGE) + j];
			}

			MemoryManager.ram().writePage(cacheData, ramLocation);
		}

		process.registers = cpu.getRegisters();
		process.pc = cpu.getPc();
	}
}