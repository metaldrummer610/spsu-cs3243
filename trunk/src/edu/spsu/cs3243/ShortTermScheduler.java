package edu.spsu.cs3243;

public class ShortTermScheduler {
	private ScheduleType type;

	public enum ScheduleType {
		FIFO
	}

	public ShortTermScheduler() {
		type = ScheduleType.FIFO;
	}

	public void load(ProcessQueue readyQueue, ProcessQueue runningQueue) {
		// Takes a process from the readyQueue and puts it on the runningQueue

		switch (type) {
		case FIFO:
			PCB pcb = readyQueue.get(0);
			// o.setStatus(ProcessStatus.Running);
			runningQueue.add(pcb);
			break;
		}
	}
}