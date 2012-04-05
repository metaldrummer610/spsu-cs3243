package edu.spsu.cs3243;

public class ShortTermScheduler {
	private static ScheduleType type = ScheduleType.FIFO;

	public enum ScheduleType {
		FIFO
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
		}

		return null;
	}
}