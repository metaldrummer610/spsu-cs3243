package edu.spsu.cs3243;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Loader {
	private static File file1;
	private static BufferedReader in;
	private static PCB newPCB = null;
	private static int processIndex, lines;

	public static void load(ProcessQueue newQueue, String fileLocation) {
		file1 = new File(fileLocation);

		try {
			in = new BufferedReader(new FileReader(file1));

			boolean instructionStart = false;
			boolean dataStart = false;
			
			String input;
			while ((input = in.readLine()) != null) {
				// TODO Send to PCB
				if (input.contains("JOB")) {
					processIndex = process(input, newQueue);
					newPCB = newQueue.get(processIndex);
					instructionStart = true;
				} else if (input.contains("Data")) {
					lines = 0;
					data(input, newPCB);
					dataStart = true;
				} else if (input.contains("END")) {
					endData(newPCB, lines);
				} else if (input.length() > 0) {
					int location = Disk.instance().write(input.substring(2, 10));
					lines++;
					
					if(instructionStart) {
						newPCB.instDiskLoc = location;
						newPCB.pc = location;
						instructionStart = false;
					}
					
					if(dataStart) {
						newPCB.dataDiskLoc = location;
						dataStart = false;
					}
				}
			}
			in.close();
		} catch (IOException e) {
			System.err.println("Loader reader exception");
		}
	}

	private static int process(String s, ProcessQueue newQueue) {
		String tempString;
		int index, priority, size, id;
		PCB tempJob = new PCB();
		String idNum, sizeHex;
		index = s.indexOf('B');

		tempString = s.substring(index + 2);
		index = tempString.indexOf(' ');
		idNum = tempString.substring(0, index);
		id = Integer.parseInt(idNum, 16);

		tempString = tempString.substring(index + 1);
		index = tempString.indexOf(' ');
		sizeHex = tempString.substring(0, index);
		size = Integer.parseInt(sizeHex, 16);

		tempString = tempString.substring(index + 1);
		priority = Integer.parseInt(tempString, 16);

		tempJob.pid = id;
		tempJob.priority = priority;
		tempJob.processSize = size;

		newQueue.add(tempJob);
		return newQueue.size() - 1;

	}

	private static void data(String s, PCB p) {
		int inBuff, outBuff, tempBuff, index;
		String temp;

		index = s.indexOf('a') + 4;
		temp = s.substring(index);
		inBuff = Integer.parseInt((temp.substring(0, (index = temp.indexOf(' ')))), 16);

		temp = temp.substring(index + 1);
		outBuff = Integer.parseInt((temp.substring(0, (index = temp.indexOf(' ')))), 16);

		temp = temp.substring(index + 1);
		tempBuff = Integer.parseInt(temp, 16);

		p.inputBufferSize = inBuff;
		p.outputBufferSize = outBuff;
		p.tempBufferSize = tempBuff;
	}

	private static void endData(PCB p, int l) {
		p.dataSize = l;
	}

}
