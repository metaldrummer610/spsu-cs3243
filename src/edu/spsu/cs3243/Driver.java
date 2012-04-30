package edu.spsu.cs3243;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.spsu.cs3243.ShortTermScheduler.ScheduleType;

public class Driver {
	private static int NUM_CPUS = 4;
	public static final int WORDS_PER_PAGE = 4;
	public static final int NUM_REGISTERS = 16;
	private ArrayList<CPU> cpus;
	private static ProcessQueue newQueue;
	private static ProcessQueue readyQueue;
	private static ProcessQueue runningQueue;
	private static ProcessQueue terminatedQueue;

	public static void main(String args[]) {
		new Driver().run(args);
	}

	public void run(String args[]) {
		if (args.length < 1) {
			Logger.log("program.jar file_path_to_data_file");
			return;
		}

		String filename = args[0];
		NUM_CPUS = Integer.parseInt(args[1]);

		switch (Integer.parseInt(args[2])) {
		case 1:
			ShortTermScheduler.type = ScheduleType.FIFO;
			break;
		case 2:
			ShortTermScheduler.type = ScheduleType.SJF;
			break;
		case 3:
			ShortTermScheduler.type = ScheduleType.PRIORITY;
			break;
		}

		StatsLogger.FILENAME = args[3];

		Logger.open();
		StatsLogger.open();

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

		long startTime = System.nanoTime();

		// Start the threads...
		for (CPU c : cpus) {
			c.start();
		}

		do {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (newQueue.size() > 0 || readyQueue.size() > 0 || runningQueue.size() > 0);

		long endTime = System.nanoTime();
		long runningTime = endTime - startTime;

		StatsLogger.log("Run time: %d", runningTime);

		StatsLogger.log("Process dump:");
		StatsLogger.log(PCB.printHeader());
		Collections.sort(terminatedQueue.processes, new PIDComparer());
		for (PCB p : terminatedQueue.processes) {
			StatsLogger.log(p.printForStats());
		}

		MemoryManager.ram().printUsage();

		Logger.close();
		StatsLogger.close();
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

	public static class PIDComparer implements Comparator<PCB> {
		@Override
		public int compare(PCB p1, PCB p2) {
			int pid1 = p1.pid;
			int pid2 = p2.pid;

			if (pid1 < pid2)
				return -1;
			else if (pid1 > pid2)
				return 1;
			else
				return 0;
		}
	}
}
