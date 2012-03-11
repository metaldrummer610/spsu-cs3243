package edu.spsu.cs3243;

public class ShortTermScheduler {
	private static ScheduleType type = ScheduleType.FIFO;

	public enum ScheduleType {
		FIFO
	}

	public static void load(ProcessQueue readyQueue, ProcessQueue runningQueue) {
		// Takes a process from the readyQueue and puts it on the runningQueue

		switch (type) {
		case FIFO:
			PCB p = readyQueue.processes.get(0);
			runningQueue.processes.add(p);
			readyQueue.processes.remove(p);
			break;
		}
	}
}