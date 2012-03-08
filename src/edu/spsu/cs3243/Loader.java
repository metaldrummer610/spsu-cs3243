package edu.spsu.cs3243;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Loader 
{
	private File file1;
	private BufferedReader in;
	private PCB newPCB = null;
	private int processIndex, lines;

	public void load(Disk d, ArrayList<PCB> unReadyQueue) 
	{
		file1 = new File("DataFile1.txt");

		try 
		{
			in = new BufferedReader(new FileReader(file1));

			String input;
			while ((input = in.readLine()) != null) 
			{
				// TODO Send to PCB
				if (input.contains("JOB")) 
				{
					processIndex = process(input);
					newPCB = unReadyQueue.get(processIndex);
				} 
				else if (input.contains("Data")) 
				{
					lines = 0;
					data(input, newPCB);
				} 
				else if (input.contains("END")) 
				{
					endData(newPCB, lines);
				}
				else if(input.length() > 0)
				{
					d.write(input.substring(2, 10));
					lines++;
				}
			}
			in.close();
		} 
		catch (IOException e) 
		{
			System.err.println("Loader reader exception");
		}
	}

	private static int process(String s) 
	{
		String tempString;
		int index, priority, size, id;
		PCB tempJob = null;
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

		tempString = s.substring(index + 1);
		priority = Integer.parseInt(tempString, 16);
		
		tempJob.pid = id;
		tempJob.priority = priority;
		tempJob.processSize = size;
		
		Driver.newQueue.add(tempJob);
		return Driver.newQueue.size()-1;

	}

	private static void data(String s, PCB p) 
	{
		int inBuff, outBuff, tempBuff, index;
		String temp, out, in;

		index = s.indexOf('a') + 4;
		temp = s.substring(index);
		inBuff = Integer.parseInt((temp.substring(0, (index = temp.indexOf(' ')))), 16);

		temp = temp.substring(index + 1);
		outBuff = Integer.parseInt((temp.substring(0, (index = temp.indexOf(' ')))), 0);

		temp = temp.substring(index + 1);
		tempBuff = Integer.parseInt(temp, 16);
		
		p.inputBufferSize = inBuff;
		p.outputBufferSize = outBuff;
		p.tempBufferSize = tempBuff;
	}

	private static void endData(PCB p, int l)
	{
		p.dataSize = l;
	}

}
