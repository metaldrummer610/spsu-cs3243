package edu.spsu.cs3243;

import java.util.ArrayList;

public class ProcessQueue {

	private ArrayList<PCB> processes;

	public ProcessQueue() {
		processes = new ArrayList<PCB>();
	}

	public void add(PCB pcb) {
		processes.add(pcb);
	}

	public PCB get(int index) {
		return processes.get(index);
	}
}
