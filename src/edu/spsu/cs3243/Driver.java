package edu.spsu.cs3243;

import java.util.ArrayList;

public class Driver {

	private ArrayList<CPU> cpus;
	private LongTermScheduler longTermScheduler;
	private ShortTermScheduler shortTermScheduler;
	public static ProcessQueue newQueue;
	public static ProcessQueue readyQueue;
	public static ProcessQueue runningQueue;
	public static ProcessQueue terminatedQueue;
	
	public static void main(String args[]) {
		new Driver().run(args);
	}

	public void run(String args[]) {
		RAM.instance();
		Disk.instance();
		longTermScheduler = new LongTermScheduler();
		shortTermScheduler = new ShortTermScheduler();
		newQueue = new ProcessQueue();
		readyQueue = new ProcessQueue();
		runningQueue = new ProcessQueue();
		terminatedQueue = new ProcessQueue();

		cpus = new ArrayList<CPU>();
		cpus.add(new CPU());

		Loader.load(newQueue);
		while (true) {
			longTermScheduler.load();
			shortTermScheduler.load(readyQueue, runningQueue);

			for (CPU cpu : cpus) {
				// Grab the next process off the running queue
				PCB pcb = runningQueue.get(0);
				cpu.run(pcb, terminatedQueue);
			}
		}
	}
}