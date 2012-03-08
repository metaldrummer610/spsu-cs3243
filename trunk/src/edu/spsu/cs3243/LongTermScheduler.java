package edu.spsu.cs3243;

public class LongTermScheduler {
	public static void load(ProcessQueue newQueue, ProcessQueue readyQueue) {
		if (!loadProcess(newQueue)) {
			System.out.println("Done");
			return;
		}

		for (int j = 0; j < newQueue.processes.size(); j++) {
			PCB job = newQueue.processes.get(j);
			if (job.processSize + job.dataSize > RAM.instance().free()) {
				System.out.println("No RAM left");
				return;
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
			
			readyQueue.add(job);
			newQueue.remove(job);
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
