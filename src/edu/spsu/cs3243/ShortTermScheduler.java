package edu.spsu.cs3243;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;

public class ShortTermScheduler {
	public static ScheduleType type = ScheduleType.SJF;

	public enum ScheduleType {
		FIFO, SJF, PRIORITY
	}

	public static synchronized PCB scheduleNextProcess() {
		ProcessQueue readyQueue = Driver.getReadyQueue();
		ProcessQueue runningQueue = Driver.getRunningQueue();
		// Takes a process from the readyQueue and puts it on the runningQueue

		synchronized (runningQueue) {
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
					// Logger.log("Returning process: " + p.toString());
					return p;
				}
				break;
			case SJF:
				if (!readyQueue.processes.isEmpty()) {
					Collections.sort(readyQueue.processes, new SJFComparator());

					PCB p = readyQueue.processes.get(0);
					runningQueue.processes.add(p);
					readyQueue.processes.remove(p);
					// Logger.log("Returning process: " + p.toString());
					return p;
				}
				break;
			}
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

	public static void handleDataFault(DataFaultException e) {
		long start = System.nanoTime();
		CPU cpu = e.getCpu();
		PCB process = e.getCurrentProcess();
		int page = e.getPage();

		// Add another fault...
		process.dataFaults++;

		saveState(cpu, process);

		int pos = process.instDiskLoc + (page * Driver.WORDS_PER_PAGE);
		String[] data = MemoryManager.disk().readPage(pos, process);

		int wroteTo = MemoryManager.ram().writeNextAvailablePage(data, process);
		if (wroteTo != -1) {
			process.pageTable.put(page, wroteTo);
		}

		long end = System.nanoTime();
		long total = end - start;
		process.faultTime += total;
	}

	public static void handlePageFault(PageFaultException e) {
		long start = System.nanoTime();
		CPU cpu = e.getCpu();
		PCB process = e.getProcess();
		int page = e.getPage();

		process.pageFaults++;

		saveState(cpu, process);

		int pos = process.instDiskLoc + (page * Driver.WORDS_PER_PAGE);
		String[] data = MemoryManager.disk().readPage(pos, process);

		int wroteTo = MemoryManager.ram().writeNextAvailablePage(data, process);
		if (wroteTo != -1) {
			process.pageTable.put(page, wroteTo);
		}

		long end = System.nanoTime();
		long total = end - start;
		process.faultTime += total;
	}

	public static void restoreState(CPU cpu, PCB process) {
		Arrays.fill(cpu.getCache(), MemoryManager.EMPTY_MEMORY);
		for (Entry<Integer, Integer> entry : process.pageTable.entrySet()) {
			Integer page = entry.getKey();
			Integer ramLocation = entry.getValue();

			String[] pageData = MemoryManager.ram().readPage(ramLocation, process);
			for (int offset = 0; offset < Driver.WORDS_PER_PAGE; offset++) {
				cpu.getCache()[(page * Driver.WORDS_PER_PAGE) + offset] = MemoryManager.hexFormat(pageData[offset]);
			}
		}

		cpu.setRegisters(process.registers);
		cpu.setPc(process.pc);
	}

	public static void saveState(CPU cpu, PCB process) {
		for (Entry<Integer, Integer> entry : process.pageTable.entrySet()) {
			Integer page = entry.getKey();
			Integer ramLocation = entry.getValue();

			String[] cacheData = new String[Driver.WORDS_PER_PAGE];
			for (int j = 0; j < Driver.WORDS_PER_PAGE; j++) {
				cacheData[j] = cpu.getCache()[(page * Driver.WORDS_PER_PAGE) + j];
			}

			MemoryManager.ram().writePage(cacheData, ramLocation, process);
		}

		process.registers = cpu.getRegisters();
		process.pc = cpu.getPc();
	}
}