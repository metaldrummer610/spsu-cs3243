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
			runningQueue.processes.addAll(readyQueue.processes);
			readyQueue.processes.clear();
			break;
		}
	}
}