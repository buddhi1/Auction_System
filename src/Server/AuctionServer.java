package Server;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.*;

class AuctionServer {
	
	String host;
    int port;
    static AuctionServerWork auction = null;
    static long delay = 6;
    static String filename = "server.srv";
    
    public AuctionServer(String h, int p) {
    	host = h;
    	port = p;
    }
    
        
    //recover server from last checkpoint
    public static AuctionServerWork loadState(String fileName) {
        Object obj = null;
        AuctionServerWork server = null;
        try {
        	FileInputStream fstream= new FileInputStream(fileName);
        	if(fstream.available() > 0) {
        		ObjectInputStream in = new ObjectInputStream(fstream);
        		obj = in.readObject();
        		if(obj == null) {
        			return server;
        		}
        		LinkedList<Item> la = (LinkedList<Item>) obj;
        		obj = in.readObject();
        		if(obj == null) {
        			return server;
        		}
        		LinkedList<Item> lc = (LinkedList<Item>) obj;
        		server = new AuctionServerWork(la, lc);
        	}else {
        		return null;
        	}
        } catch (IOException e) {
            System.err.println("Could not load file - " + e);
        } catch (ClassNotFoundException e) {
            System.err.println("Could not find class - " + e);
        }
        return server;
    }
    
    public static void displayInfo() {
    	System.out.println("*** Welcome to Auction ***");
    	System.out.println("Checkpoint delay: " + delay + "seconds");
        System.out.println("Choose an option");
        System.out.println("n - New server from scratch");
        System.out.println("t - Chnage checkpoint time");
        System.out.println("c - Continue from crash");
        System.out.println("q - Quit");
    }
    public void bindServer() {
    	try	{
	    	LocateRegistry.createRegistry(port);
	        Registry reg = LocateRegistry.getRegistry(host, port);
	        reg.rebind("auction", auction);
    	}catch (Exception e) {
            System.out.println("Server Error: " + e);
        }
    }
    
    public boolean inputHandle() {
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String fileName = "server.srv";
        String input;
        boolean rtn = true;
        try {
	    	switch (br.readLine()) {		        
		        case "t":
		        	System.out.print("Enter new delay time(seconds): ");
		        	delay = Long.parseLong(br.readLine());
		        	break;
		        case "c":
		            System.out.println("Recovering server...");
		            auction = loadState(fileName);
		            if(auction != null) {
		            	rtn = false;
		            	System.out.println("Recovering is successful!");
		            	break;		            	
		            } else {
		            	System.out.println("Recovery is not possible. Starting a new copy...");
		            }	
		        case "n":
		        	
		            auction = new AuctionServerWork();
		            rtn = false;
		            break;
		        case "q":
		            System.exit(0);
		            rtn = false;
		    }
        } catch(Exception e) {
            System.out.println("Server Error: " + e);
        }
		return rtn;
    }
    
	public static void main(String args[]) {
		AuctionServer server = new AuctionServer("localhost", 1099); 
        try {        	
        	do {
        		displayInfo();
        	}while (server.inputHandle());
            
            server.bindServer();
            System.out.println("Server started...");
            //check-pointing           
            Timer timer = new Timer();
            CheckPoint chk = new CheckPoint(auction, filename);
            timer.schedule(chk, delay*1000, delay*1000); 
                        
            /* make server state auto save
             * 


            servlet.getTimer().schedule(servlet.new SaveTask(auction, fileName), SAVE_DELAY);
            System.out.println("Server ready. Saving every "+ (float)SAVE_DELAY / 1000 / 60 +"mins to " + fileName);
            System.out.println("Press s to trigger save or q to quit");
            while (true) {
                String inp = br.readLine();
                if (inp.equals("s")) {
                    saveState(auction, fileName);
                } else if (inp.equals("q")) {
                    System.exit(0);
                }
            }
            */
        }
        catch (Exception e) {
            System.out.println("Server Error: " + e);
        }
    }
}
