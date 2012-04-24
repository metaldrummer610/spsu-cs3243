package edu.spsu.cs3243;

public class PageFaultException extends Exception {
	private static final long serialVersionUID = 3641325341399555731L;
	private PCB process;
	private CPU cpu;
	private int page;

	public PageFaultException(PCB currentProcess, CPU cpu, int page) {
		process = currentProcess;
		this.cpu = cpu;
		this.page = page;
	}

	public CPU getCpu() {
		return cpu;
	}

	public PCB getProcess() {
		return process;
	}

	public int getPage() {
		return page;
	}
}
