package edu.spsu.cs3243;

public class Disk 
{
	private static String [] disk;
	private static int size;
	private static int next_location;
	private static Disk instance = null;
	
	public static Disk instance() {
		if(instance == null) {
			instance = new Disk();
		}
		
		return instance;
	}
	
	public Disk()
	{
		disk = new String[2048];
		size = 2048;
		next_location = 0;
	}
	
	public Disk(int d)
	{
		disk =  new String[d];
		size = disk.length;
		next_location = 0;
	}
	
	public int size()
	{
		return size;
	}
	
	public boolean empty()
	{
		return next_location == 0;
	}
	
	public boolean full()
	{
		return next_location == size;
	}
	
	public int free ()
	{
		return 2048-size;
	}
	
	public String toString()
	{
		String temp_disk = "Disk Contents:";
		for (int i = 0; i<disk.length; i++)
			if(disk[i]!=null)
			{
				temp_disk+=disk[i];
			}
		temp_disk+=next_location;
		return temp_disk;
		
		
	}
	
	  public void erase() 
	  {
          for(int i = 0;i < disk.length; i++) {
                  disk[i]="00000000";     
          }       
          next_location = 0;
	  }
	  
	  
	  //Write to Disk
	  public int write(String word)
	  {
		  if(full())
		  {
			  System.out.println("Disk Full");
			  return 0;
		  }
		  
		  else
		  {
			  disk[next_location] = word;
			  next_location++;
			  return next_location-1;
		  }
	  
	  }
	  
	  public String read(int address)
	  {
		  if(address>2048)
		  {
			  return disk[address];
		  }
		  
		  else
		  {
			  System.out.println("Disk Error");
			  return "";
		  }
	  }

	
	
}
