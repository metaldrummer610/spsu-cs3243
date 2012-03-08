package edu.spsu.cs3243;

public class LongTermScheduler 
{
	public int current_job;
	PCB job;
	
	public LongTermScheduler()
	{
		current_job=-1;
		
	}
	
	
	public void load() 
	{
		if(!loadProcess())
		{
			System.out.println("Done");
			return;
		}
		
	for(current_job=0; current_job<Driver.newQueue.size(); current_job++)
		{
			job = Driver.newQueue.get(0);
			
			if(job.processSize()+job.dataSize() > RAM.instance().free())
			{
				System.out.println("No RAM left");
				return;
			}
			
		}
		
		int dataMemLoc = 0;
		int instMemLoc = 0;
		
		for(int current = job.instDiskLoc(), i = job.processSize(); i>0; i--, current++)
		{
			if(instMemLoc == -1)
			{
				job.setinstMemLoc(RAM.instance().write(Disk.instance().read(current)));
			}
			else
			{
				RAM.instance().write(Disk.instance().read(current));
			}
			
		}
		
		for(int current = job.dataDiskLoc(), i = job.dataSize(); i>0; i--, current++)
		{
			if(job.dataMemLoc == -1)
			{
				job.setinstMemLoc(RAM.instance().write(Disk.instance().read(current)));
			}
			
			else
			{
				RAM.instance().write(Disk.instance().read(current));
			}
		}
		
		Driver.newQueue.remove(job);
		Driver.readyQueue.add(job);
	
	}
	
	public boolean loadProcess()
	{
		if(Driver.newQueue.size() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	

}
