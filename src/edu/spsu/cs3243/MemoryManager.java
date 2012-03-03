package edu.spsu.cs3243;

public class MemoryManager 
{
	private static Disk disk;
	private static RAM ram;
	
	public MemoryManager()
	{
		ram = new RAM(1024);
		disk = new Disk(2048);
	}
	
	public static void writeDisk(String data)
	{
		disk.write(data);
	}
	
	public static String readDisk(int r)
	{
		return disk.read(r);
	}
	
	public void writeRAM (String data, int location)
	{
		ram.write(data, location);
	}
	
	public String readRAM(int r)
	{
		return ram.read(r);
	}
	
	public String printDisk ()
	{
		return disk.toString();
	}
	
	public String printRAM()
	{
		return ram.toString();
	}
	
}
