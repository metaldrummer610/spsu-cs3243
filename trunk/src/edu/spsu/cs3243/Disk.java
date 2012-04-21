package edu.spsu.cs3243;

public class Disk {
	private String[] disk;
	private int size;
	//private int next_location;
	private static Disk instance = null;
	private int numPage;
	private boolean[] full;

	public static Disk instance() {
		if (instance == null) {
			instance = new Disk();
		}

		return instance;
	}

	private Disk() {
		disk = new String[2048];
		size = 2048;
		//next_location = 0;
		numPage = (size/4);
		for (int j = 0; j < numPage; j++) {
			full[j] = false;
		}
	}

	public Disk (int s) {
        disk = new String[s];   
        size = disk.length;
        //next_location = 0;
        numPage=(s/4);
        full = new boolean[numPage];
        for (int j = 0; j<numPage; j++) 
        {
                full[j]=false;        
        }
}
	
	public int nextPage() 
    {
            for (int i=0;i<numPage;++i) 
            {
                    if(full[i]==false)
                    {
                            return i;
                    }
            }
            return -1;
    }



	public int size() {
		return size;
	}

	public boolean empty() {
		//return next_location == 0;
		
		 for (int j = 0;j < numPage; j++)
         {
                 if (full[j]==true) 
                 {
                         return false;
                 }
         }
         return true; 
		
	
	}

	public boolean full() {
		//return next_location == size;	
		for (int j = 0;j < numPage;j++)
        {
                if (full[j]==false)
                {
                        return false;   
                }                               
        }               
        return true; 
	}

	/*public int free() {
		return 2048 - next_location;
	}*/

	public String toString() {
		String temp_disk = "Disk Contents:";
		for (int i = 0; i < disk.length; i++)
			if (disk[i] != null) {
				temp_disk += disk[i] + "\n";
			}
		//temp_disk += next_location;
		return temp_disk;

	}

	public void erase() {
		for (int i = 0; i < disk.length; i++) {
			disk[i] = "00000000";
		}
		//next_location = 0;     
    	for(int j = 0;j < numPage; j++) {
    	
             full[j]=false;
     }

	}

	// Write to Disk
	/*public int write(String word) {
		if (full()) {
			System.out.println("Disk Full");
			return 0;
		}

		else {
			disk[next_location] = word;
			next_location++;
			return next_location - 1;
		}

	}*/
	
	public void write(String word, int here) {
        if (here > -1 && here < size) {
                disk[here] = word;
                if (!full[here/4]) {
                  full[here/4] = true;     
                }
        }
        else 
                System.out.println("Invalid Disk Writing Location");    
}
	
	public void write(String word, int p, int o) 
    {
   
            if ((p*4+o) < size() && (p*4+o) >= 0) {
                    disk[(p*4+o)]=word;
                    full[p] = true;         
            }
            else {
                    System.out.println("Invalid Disk Writing Location");    
            }
    }



	public String read(int address) {
		if (address < 2048) {
			return disk[address];
		}

		else {
			System.out.println("Disk Error");
			return "";
		}
	}

}
