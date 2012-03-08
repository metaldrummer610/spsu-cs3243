package edu.spsu.cs3243;

import java.util.ArrayList;

public class Driver {

	private ArrayList<CPU> cpus;
	private ProcessQueue newQueue;
	private ProcessQueue readyQueue;
	private ProcessQueue runningQueue;
	private ProcessQueue terminatedQueue;

	public static void main(String args[]) {
		new Driver().run(args);
	}

	public void run(String args[]) {
		if (args.length != 1) {
			System.out.println("program.jar file_path_to_data_file");
			return;
		}

		String filename = args[0];

		RAM.instance();
		Disk.instance();
		newQueue = new ProcessQueue();
		readyQueue = new ProcessQueue();
		runningQueue = new ProcessQueue();
		terminatedQueue = new ProcessQueue();

		cpus = new ArrayList<CPU>();
		cpus.add(new CPU());

		Loader.load(newQueue, filename);
		do {
			LongTermScheduler.load(newQueue, readyQueue);
			ShortTermScheduler.load(readyQueue, runningQueue);

			for (CPU cpu : cpus) {
				// Grab the next process off the running queue
				PCB pcb = runningQueue.get(0);
				cpu.run(pcb, runningQueue, terminatedQueue);
			}
		} while (newQueue.size() > 0 || readyQueue.size() > 0 || runningQueue.size() > 0);
	}
}
