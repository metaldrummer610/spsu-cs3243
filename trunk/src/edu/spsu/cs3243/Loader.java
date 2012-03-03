package edu.spsu.cs3243;

import java.io.*;
import java.util.*;

public class Loader 
{
	private File file1, file2;
	private BufferedReader in;

	public static void load() 
	{	
		file1 = "DataFile1.txt";
		file2 = "DataFile2.txt";
		
		in = new BufferedReader(new FileReader(file1));
		
		string input;
		while((input = in.ReadLine()) != null)
		{
			//TODO Send to PCB
			if(input.contains("JOB"))
			{
				this.process(input);
			}
			else if(input.contains("Data"))
			{
				this.data(input);
			}
			else if(input.contains("END"))
			{
				
			}
		}
		
	}
	
	private static void process(String s)
	{
		String tempString;
		int index, priority, size, id;
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
		
		
		
	}
	
	private static void data(String s)
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
	}
	
	private static void endData()
	{
		
	}

}
