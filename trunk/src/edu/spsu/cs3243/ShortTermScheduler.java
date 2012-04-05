package edu.spsu.cs3243;

import java.util.Collections;
import java.util.Comparator;

public class ShortTermScheduler {
	private static ScheduleType type = ScheduleType.PRIORITY;

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
}