package edu.spsu.cs3243;

import java.util.ArrayList;

public class LongTermScheduler {
	
	private static double percent;
	private static double average;
	private static double totalPercent;
	private static double sumPercent;
	
	public static void load(ProcessQueue newQueue, ProcessQueue readyQueue) {
		if (!loadProcess(newQueue)) {
			System.out.println("Done");
			return;
		}

		ArrayList<PCB> processes = new ArrayList<PCB>();
		for (int j = 0; j < newQueue.processes.size(); j++) {
			PCB job = newQueue.processes.get(j);
			if (job.processSize + job.dataSize > RAM.instance().free()) {
				System.out.println("No RAM left");
				break;
			}

			for (int current = job.instDiskLoc, i = job.processSize; i > 0; i--, current++) {
				if (job.instMemLoc == -1) {
					job.instMemLoc = RAM.instance().write(Disk.instance().read(current));
				} else {
					RAM.instance().write(Disk.instance().read(current));
				}

			}

			for (int current = job.dataDiskLoc, i = job.dataSize; i > 0; i--, current++) {
				if (job.dataMemLoc == -1) {
					job.dataMemLoc = RAM.instance().write(Disk.instance().read(current));
				} else {
					RAM.instance().write(Disk.instance().read(current));
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
	
	private static void percentageRAM(int avg) {
		switch (avg)
        {
                case 0:
                        average=(RAM.instance().size()-RAM.instance().free());
                        percent=average/RAM.instance().size();             
                        System.out.println("Percentage of RAM used: " +percent*100);
                        break;
                /*case 1:
                        totalPercent=sumPercent/totalCycleCounter;
                        System.out.println("Total percentage used on RAM:   " + totalPercent*100);
                        break;*/
        }

	}

}
