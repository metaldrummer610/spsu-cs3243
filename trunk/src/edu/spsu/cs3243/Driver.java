package edu.spsu.cs3243;

import java.util.ArrayList;

public class Driver {
	private static final int NUM_CPUS = 4;
	public static final int WORDS_PER_PAGE = 4;
	public static final int NUM_REGISTERS = 16;
	private ArrayList<CPU> cpus;
	private static ProcessQueue newQueue;
	private static ProcessQueue readyQueue;
	private static ProcessQueue runningQueue;
	private static ProcessQueue terminatedQueue;
	private double percent = 0;
	private double average = 0;
	private double totalPercent = 0;

	// sum percent is the percentage of ram that is filled total(overall % use)
	// private double sumPercent;

	public static void main(String args[]) {
		new Driver().run(args);
	}

	public void run(String args[]) {
		if (args.length != 1) {
			System.out.println("program.jar file_path_to_data_file");
			return;
		}

		String filename = args[0];

		MemoryManager.ram();
		MemoryManager.disk();
		newQueue = new ProcessQueue();
		readyQueue = new ProcessQueue();
		runningQueue = new ProcessQueue();
		terminatedQueue = new ProcessQueue();

		Loader.load(filename);
		LongTermScheduler.load();

		cpus = new ArrayList<CPU>();
		for (int i = 0; i < NUM_CPUS; i++) {
			cpus.add(new CPU(readyQueue.largestJob()));
		}

		long startTime = System.currentTimeMillis();

		// Start the threads...
		for (CPU c : cpus) {
			c.start();
		}
		do {
			// Double check to see if we need to clear the RAM and put more processes into it...
			if (readyQueue.processes.isEmpty() && !newQueue.processes.isEmpty()) {
				percentageRAM(0, cpus.get(0).getTotalCyclesRun());
				percentageRAM(1, cpus.get(0).getTotalCyclesRun());

				MemoryManager.ram().erase();
				LongTermScheduler.load();
				continue;
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (newQueue.size() > 0 || readyQueue.size() > 0 || runningQueue.size() > 0);

		long endTime = System.currentTimeMillis();

		long runningTime = endTime - startTime;

		percentageRAM(0, cpus.get(0).getTotalCyclesRun());
		percentageRAM(1, cpus.get(0).getTotalCyclesRun());
		Logger.log("Run time: %d", runningTime);

		Logger.log("Process dump:");

		for (PCB p : terminatedQueue.processes) {
			Logger.log(p.toString());
			// Logger.log("%d\t%d", p.cyclesRan, p.cyclesWaited);
			// Logger.log("%d\t%d", p.realRunTime, p.realWaitTime);
		}
		Logger.log("%d", cpus.get(0).getTotalTimeRunning());
	}

	public void percentageRAM(int avg, int totalCycleCounter) {
		switch (avg) {
		case 0:
			average = (MemoryManager.ram().size() - MemoryManager.ram().freeFrames());
			percent = average / MemoryManager.ram().size();
			Logger.log("Percentage of RAM used: " + percent * 100);
			break;
		case 1:
			average = (MemoryManager.ram().size() - MemoryManager.ram().freeFrames()); // TODO: Make sure freeFrames is the correct function to use!
			totalPercent = average / totalCycleCounter;
			Logger.log("Total percentage used on RAM:   " + totalPercent * 100);
			break;
		}
	}

	public static synchronized ProcessQueue getNewQueue() {
		return newQueue;
	}

	public static synchronized ProcessQueue getReadyQueue() {
		return readyQueue;
	}

	public static synchronized ProcessQueue getRunningQueue() {
		return runningQueue;
	}

	public static synchronized ProcessQueue getTerminatedQueue() {
		return terminatedQueue;
	}
}
