package edu.spsu.cs3243;

import java.util.ArrayList;
import java.util.Arrays;

public class PCB 
{
	int pid, processSize, priority, inputBufferSize, outputBufferSize, tempBufferSize, pc, pageFaults, dataSize, dataMemLoc,
	dataDiskLoc, instMemLoc, instDiskLoc, cyclesRan, cyclesWaited, IOCount;
	long lastStateSwitch, realWaitTime, realRunTime, faultTime;
	String[] inputBuffer, outputBuffer, tempBuffer;
	int[] registers;
	ArrayList<Integer> pageTable;
	ArrayList<Boolean> pageValidity;
	
	public PCB()
	{
		pid = -1;
		processSize = -1;
		priority = -1;
		inputBufferSize = -1;
		outputBufferSize = -1;
		tempBufferSize = -1;
		pc = -1;
		dataDiskLoc=-1;
		instDiskLoc=-1;
		dataSize=-1;
		dataMemLoc=-1;
		instMemLoc=-1;	
		cyclesRan=0;
		cyclesWaited=0;
		realRunTime=0;
		realWaitTime=0;
		IOCount=0;
		pageTable=new ArrayList<Integer>();
		pageValidity=new ArrayList<Boolean>();
		pageFaults=0;
		faultTime=0;
		lastStateSwitch=0;
	}
	
	public int instDiskLoc()
	{
		return instDiskLoc;
	}
	
	public int dataDiskLoc()
	{
		return dataDiskLoc;
	}
	
	public int dataSize()
	{
		return dataSize;
	}
	
	public int processSize()
	{
		return processSize;
	}
	
	public void setinstMemLoc(int i)
	{
		instMemLoc = i;
	}

	@Override
	public String toString() {
		return "PCB [pid=" + pid + ", processSize=" + processSize
				+ ", priority=" + priority + ", inputBufferSize="
				+ inputBufferSize + ", outputBufferSize=" + outputBufferSize
				+ ", tempBufferSize=" + tempBufferSize + ", pc=" + pc
				+ ", pageFaults=" + pageFaults + ", dataSize=" + dataSize
				+ ", dataMemLoc=" + dataMemLoc + ", dataDiskLoc=" + dataDiskLoc
				+ ", instMemLoc=" + instMemLoc + ", instDiskLoc=" + instDiskLoc
				+ ", cyclesRan=" + cyclesRan + ", cyclesWaited=" + cyclesWaited
				+ ", IOCount=" + IOCount + ", lastStateSwitch="
				+ lastStateSwitch + ", realWaitTime=" + realWaitTime
				+ ", realRunTime=" + realRunTime + ", faultTime=" + faultTime
				+ ", inputBuffer=" + Arrays.toString(inputBuffer)
				+ ", outputBuffer=" + Arrays.toString(outputBuffer)
				+ ", tempBuffer=" + Arrays.toString(tempBuffer)
				+ ", registers=" + Arrays.toString(registers) + ", pageTable="
				+ pageTable + ", pageValidity=" + pageValidity + "]";
	}
}
