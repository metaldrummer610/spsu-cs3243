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
			if (((4*2) > RAM.instance().freeFrames())) {
				System.out.println("No RAM left");
				break;
			}
			
			int currentPage = 0;
			
			for (int current = job.instDiskLoc; currentPage < 4; currentPage++) {
				for (int currentOffset = 0; currentOffset < 4; currentOffset++) {
					if ( currentPage == 0 && currentOffset == 0) {
						job.instMemLoc = (RAM.instance().write(Disk.instance().read(current)));
						job.pageTable.add(job.pageTable.size(), (job.instMemLoc));
						job.pageFaultTable.add(job.pageFaultTable.size(), true);
					}
					
					else if (currentOffset == 0) {
						int wroteTo = RAM.instance().write(Disk.instance().read(current));
						job.pageTable.add(job.pageTable.size(), (wroteTo));
						job.pageFaultTable.add(job.pageFaultTable.size(), true);
					}
					
					else { 
						RAM.instance().write(Disk.instance().read(current), job.instMemLoc / 4 + currentPage, currentOffset);;
					}
				}
			}
			
			for (;currentPage < Driver.getRunningQueue().largestJob()/4; currentPage++ ) {
				job.pageTable.add(job.pageTable.size(), -1);
				job.pageFaultTable.add(job.pageFaultTable.size(), false);
			}	

		processes.add(job);
		

		newQueue.processes.removeAll(processes);
		readyQueue.processes.addAll(processes);
	}
}

	private static boolean loadProcess(ProcessQueue newQueue) {
		if (newQueue.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
}

	//Phase 1 Long Term Scheduler

			/*for (int current = job.instDiskLoc, i = job.processSize; i > 0; i--, current++) {
				if (job.instMemLoc == -1) {
					job.instMemLoc = RAM.instance().write(Disk.instance().read(current));
				} else {
					RAM.instance().write(Disk.instance().read(current));
				}

			}*/
			
			

			/*for (int current = job.dataDiskLoc, i = job.dataSize; i > 0; i--, current++) {
				if (job.dataMemLoc == -1) {
					job.dataMemLoc = RAM.instance().write(Disk.instance().read(current));
				} else {
					RAM.instance().write(Disk.instance().read(current));
				}
			}*/