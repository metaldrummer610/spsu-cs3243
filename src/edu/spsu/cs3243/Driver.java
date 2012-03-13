package edu.spsu.cs3243;

import java.util.ArrayList;

public class Driver {

	private ArrayList<CPU> cpus;
	private ProcessQueue newQueue;
	private ProcessQueue readyQueue;
	private ProcessQueue runningQueue;
	private ProcessQueue terminatedQueue;
	private double percent = 0;
	private double average = 0;
	private double totalPercent = 0;
	//sum percent is the percentage of ram that is filled total(overall % use)
	//private double sumPercent;

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

		Loader.load(newQueue, filename);
		
		cpus = new ArrayList<CPU>();
		cpus.add(new CPU(newQueue.largestJob()));
		
		long startTime = System.currentTimeMillis();
		do {
			LongTermScheduler.load(newQueue, readyQueue);
			// Double check to see if we need to clear the RAM and put more processes into it...
			if(readyQueue.processes.isEmpty()) {
				percentageRAM(0, cpus.get(0).getTotalCyclesRun());
				percentageRAM(1, cpus.get(0).getTotalCyclesRun());
				RAM.instance().erase();
				continue;
			}
			
			ShortTermScheduler.load(readyQueue, runningQueue);

			for (CPU cpu : cpus) {
				// Grab the next process off the running queue
				PCB pcb = runningQueue.get(0);
				cpu.run(pcb, runningQueue, terminatedQueue);
				
				for(PCB p : readyQueue.processes) {
					p.realWaitTime = cpu.getTotalTimeRunning();
					p.cyclesWaited = cpu.getTotalCyclesRun();
				}
			}
		} while (newQueue.size() > 0 || readyQueue.size() > 0 || runningQueue.size() > 0);
		
		long endTime = System.currentTimeMillis();
		
		long runningTime = endTime - startTime;
		
		percentageRAM(0, cpus.get(0).getTotalCyclesRun());
		percentageRAM(1, cpus.get(0).getTotalCyclesRun());
		Logger.log("Run time: %d", runningTime);
		
		Logger.log("Process dump:");
		
		for(PCB p : terminatedQueue.processes) {
			Logger.log(p.toString());
		}
	}
	
	public void percentageRAM(int avg, int totalCycleCounter) {
		switch (avg) {
		case 0:
			average = (RAM.instance().size() - RAM.instance().free());
			percent = average / RAM.instance().size();
			Logger.log("Percentage of RAM used: " + percent * 100);
			break;
		case 1:
			average = (RAM.instance().size() - RAM.instance().free());
			totalPercent = average / totalCycleCounter;
			Logger.log("Total percentage used on RAM:   " + totalPercent * 100);
			break;
		}

	}
}
