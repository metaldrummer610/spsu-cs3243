package edu.spsu.cs3243;

import java.util.ArrayList;

public class LongTermScheduler {

	public static void load() {
		ProcessQueue newQueue = Driver.getNewQueue();
		ProcessQueue readyQueue = Driver.getReadyQueue();
		if (!loadProcess(newQueue)) {
			System.out.println("Done");
			return;
		}

		ArrayList<PCB> processes = new ArrayList<PCB>();
		for (int j = 0; j < newQueue.processes.size(); j++) {
			PCB job = newQueue.processes.get(j);
			if (((Driver.WORDS_PER_PAGE * 2) > MemoryManager.ram().freeFrames())) {
				System.out.println("No RAM left");
				break;
			}

			int currentPage = 0;
			for (int current = job.instDiskLoc; currentPage < Driver.WORDS_PER_PAGE; currentPage++) {
				String[] pageData = MemoryManager.disk().readPage(current, job);
				current += Driver.WORDS_PER_PAGE;

				if (currentPage == 0) {
					job.instMemLoc = MemoryManager.ram().writeNextAvailablePage(pageData, job);
					job.pc = job.instMemLoc;
					job.pageTable.put(currentPage, job.instMemLoc);
				} else {
					int wroteTo = MemoryManager.ram().writeNextAvailablePage(pageData, job);
					job.pageTable.put(currentPage, wroteTo);
				}
			}
			
			processes.add(job);
		}
		
		newQueue.processes.removeAll(processes);
		readyQueue.processes.addAll(processes);
	}

	private static boolean loadProcess(ProcessQueue newQueue) {
		if (newQueue.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
}