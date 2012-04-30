package edu.spsu.cs3243;

import java.util.Arrays;
import java.util.HashMap;

public class PCB {
	int pid, processSize, priority, inputBufferSize, outputBufferSize, tempBufferSize, pc, pageFaults, dataSize, dataDiskLoc, instMemLoc, instDiskLoc, cyclesRan, cyclesWaited, IOCount, dataFaults;
	long realWaitTime, realRunTime, faultTime;
	HashMap<Integer, Integer> pageTable; // This contains all the indices of the pages we own.
	int[] registers;

	public PCB() {
		pid = -1;
		processSize = -1;
		priority = -1;
		inputBufferSize = -1;
		outputBufferSize = -1;
		tempBufferSize = -1;
		pc = 0;
		dataDiskLoc = -1;
		instDiskLoc = -1;
		dataSize = -1;
		instMemLoc = -1;
		cyclesRan = 0;
		cyclesWaited = 0;
		realRunTime = 0;
		realWaitTime = 0;
		IOCount = 0;
		pageFaults = 0;
		faultTime = 0;
		dataFaults = 0;
		pageTable = new HashMap<Integer, Integer>();
		registers = new int[Driver.NUM_REGISTERS];
		Arrays.fill(registers, 0);
	}

	public int getSize() {
		return processSize + dataSize;
	}

	public static String printHeader() {
		return "pid\tprocessSize\tpriority\tpageFaults\tdataSize\tcyclesRan\tcyclesWaited\tIOCount\tdataFaults\trealWaitTime\trealRunTime\tfaultTime";
	}

	public String printForStats() {
		return pid + "\t" + processSize + "\t" + priority + "\t" + pageFaults + "\t" + dataSize + "\t" + cyclesRan + "\t" + cyclesWaited + "\t" + IOCount + "\t" + dataFaults + "\t"
				+ realWaitTime / 1000 + "\t" + realRunTime / 1000 + "\t" + faultTime / 1000;
	}

	@Override
	public String toString() {
		return "PCB [pid=" + pid + ", processSize=" + processSize + ", priority=" + priority + ", inputBufferSize=" + inputBufferSize + ", outputBufferSize=" + outputBufferSize + ", tempBufferSize="
				+ tempBufferSize + ", pc=" + pc + ", pageFaults=" + pageFaults + ", dataSize=" + dataSize + ", dataDiskLoc=" + dataDiskLoc + ", instMemLoc=" + instMemLoc + ", instDiskLoc="
				+ instDiskLoc + ", cyclesRan=" + cyclesRan + ", cyclesWaited=" + cyclesWaited + ", IOCount=" + IOCount + ", dataFaults=" + dataFaults + ", realWaitTime=" + realWaitTime
				+ ", realRunTime=" + realRunTime + ", faultTime=" + faultTime + ", pageTable=" + pageTable + ", registers=" + Arrays.toString(registers) + "]";
	}
}
