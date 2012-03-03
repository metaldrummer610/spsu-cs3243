package edu.spsu.cs3243;

import java.util.ArrayList;

public class Driver {

	private ArrayList<CPU> cpus;
	private LongTermScheduler longTermScheduler;
	private ShortTermScheduler shortTermScheduler;
	
	public static void main(String args[]) {
		new Driver().run(args);
	}
	
	public void run(String args[]) {
		longTermScheduler = new LongTermScheduler();
		shortTermScheduler = new ShortTermScheduler();
		
		cpus = new ArrayList<CPU>();
		cpus.add(new CPU());
		
		Loader.load();
		while(true) {
			longTermScheduler.load();
			shortTermScheduler.load();
			
			for(CPU cpu : cpus) {
				cpu.run();
			}
		}
	}
}
