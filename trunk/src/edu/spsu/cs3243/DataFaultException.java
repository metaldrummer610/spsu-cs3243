package edu.spsu.cs3243;

@Deprecated
public class DataFaultException extends Exception {
	private static final long serialVersionUID = -3492439969492179091L;
	private CPU cpu;
	private PCB currentProcess;
	private int page;

	public DataFaultException(CPU cpu, PCB currentProcess, int page) {
		this.cpu = cpu;
		this.currentProcess = currentProcess;
		this.page = page;
	}

	public CPU getCpu() {
		return cpu;
	}

	public PCB getCurrentProcess() {
		return currentProcess;
	}

	public int getPage() {
		return page;
	}
}
