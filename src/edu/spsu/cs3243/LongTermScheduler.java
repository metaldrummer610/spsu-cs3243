package edu.spsu.cs3243;

public class LongTermScheduler {
	public static void load(ProcessQueue newQueue, ProcessQueue readyQueue) {
		if (!loadProcess(newQueue)) {
			System.out.println("Done");
			return;
		}

		for (PCB job : newQueue.processes) {
			System.out.println(job.processSize() + job.dataSize());
			System.out.println(RAM.instance().free());

			if (job.processSize() + job.dataSize() > RAM.instance().free()) {
				System.out.println("No RAM left");
				return;
			}

			for (int current = job.instDiskLoc(), i = job.processSize(); i > 0; i--, current++) {
				if (job.instMemLoc == -1) {
					job.instMemLoc = RAM.instance().write(Disk.instance().read(current));
				} else {
					RAM.instance().write(Disk.instance().read(current));
				}

			}

			for (int current = job.dataDiskLoc(), i = job.dataSize(); i > 0; i--, current++) {
				if (job.dataMemLoc == -1) {
					job.dataMemLoc = RAM.instance().write(Disk.instance().read(current));
				} else {
					RAM.instance().write(Disk.instance().read(current));
				}
			}
		}
		
		readyQueue.processes.addAll(newQueue.processes);
		newQueue.processes.clear();
	}

	private static boolean loadProcess(ProcessQueue newQueue) {
		if (newQueue.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

}
