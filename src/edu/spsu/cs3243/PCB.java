package edu.spsu.cs3243;

import java.util.ArrayList;

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

}
