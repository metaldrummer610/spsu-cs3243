package edu.spsu.cs3243;

import java.util.ArrayList;

public class ProcessQueue {

	public ArrayList<PCB> processes;

	public ProcessQueue() {
		processes = new ArrayList<PCB>();
	}

	public void add(PCB pcb) {
		processes.add(pcb);
	}

	public void remove(PCB pcb) {
		processes.remove(pcb);
	}

	public PCB get(int index) {
		return processes.get(index);
	}

	public int size() {
		return processes.size();
	}

	public int largestJob() {
		int ret = Integer.MIN_VALUE;
		for(PCB p : processes) {
			if(ret < (p.dataSize + p.processSize))
				ret = (p.dataSize + p.processSize);
		}
		
		return ret;
	}
}
