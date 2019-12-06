package Server;

import Bidder.AuctionBidderInterface;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import Bidder.AuctionBidderInterface;

class AuctionServerWork extends UnicastRemoteObject implements AuctionServerInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static LinkedList<Item> activeItemBuffer;
	private static LinkedList<Item> closedItemBuffer;
	private static LinkedList<Bid> bidders;
	private static final String bfileName = "bidders.srv";
	private transient Timer timer;
	
	public AuctionServerWork() throws RemoteException {
        super();
//        timer = new Timer();
        activeItemBuffer = new LinkedList<Item>();
        closedItemBuffer = new LinkedList<Item>();
        bidders = new LinkedList<Bid>();   
        flushBidders();
    }
	
	//overload costructor for recovering the server
	public AuctionServerWork(LinkedList<Item> la, LinkedList<Item> lc) throws RemoteException {
        super();
//        timer = new Timer();
        closedItemBuffer = lc;
        
        //call reloader 
        activeItemBuffer = la;
        
        
        Object obj = null;
        
        FileInputStream fstream;
		try {
			fstream = new FileInputStream(bfileName);
			if(fstream.available() > 0) {
	    		ObjectInputStream in = new ObjectInputStream(fstream);
	    		obj = in.readObject();	    		
	    		if(obj == null) {
	    			bidders = new LinkedList<Bid>();
	    			flushBidders();
	    		} else {
	    			bidders = (LinkedList<Bid>) obj;
	    		}
    		}
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bidders = new LinkedList<Bid>();
			flushBidders();
    	}catch (IOException e) {
            System.err.println("Could not load file - " + e);
            bidders = new LinkedList<Bid>();
			flushBidders();
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bidders = new LinkedList<Bid>();
			flushBidders();
		}
		
		if(bidders.size() > 0) {
			//reloadActiveAuctionItems();
		} else {
			flushBidders();
		}
    }
	
	//flush bidder file
	public void flushBidders() {
		FileOutputStream out;
		try {
			out = new FileOutputStream(bfileName);
			ObjectOutputStream oos = new ObjectOutputStream(out);
	        oos.writeObject(bidders);
	        oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//returns bidder from the bidders
	public AuctionBidderInterface getBidder(String name) {
		String n="";
		System.out.println("server work: bidders*****: " + bidders.size());
		for(int i=0; i < bidders.size(); ++i) {
			n = bidders.get(i).getOwnerName();
			System.out.println("server work: bidders**: " + n);
			if(n.equals(name)) {
				return bidders.get(i).getOwner(); 
			}
		}
		return null;
	}
	
	//transfer problamatic bids to closed
	public void closeBid(int i) { 
		Item item;
		if ((item = activeItemBuffer.remove(i)) != null) {
            // Move auction to closed buffer
        	closedItemBuffer.add(item);
		}
	}
		
	//reschedule item, if closing time is not yet passed. else, move the item to closed
	public void reloadActiveAuctionItems() {
		AuctionBidderInterface temp;
		System.out.println("server work: bidders: " + bidders.toString());
		
		for(int i=0; i <= activeItemBuffer.size(); ++i) {
			Item item = activeItemBuffer.get(i);
			Long remainingTime = item.getClosingDate().getTime() - System.currentTimeMillis();
			System.out.println("server work: owner nae: " + item.getOwnerName());
			if(remainingTime > 0) {
				temp = getBidder(item.getOwnerName());
				System.out.println("server work: owner: " + temp);
				if(temp != null) {
					activeItemBuffer.get(i).setOwner(temp, item.getOwnerName());
					//check observers					
					//check bidders list
				} else {
					closeBid(i);
				}				
			} else {
				closeBid(i);
			}			
		}
    }
	
	//sign in for bidder
    public String signIn(String name, String pwd) throws RemoteException{
    	Object obj = null;    	
    	
    	FileInputStream fstream;
		try {
			fstream = new FileInputStream(bfileName);
			if(fstream.available() > 0) {
	    		ObjectInputStream in = new ObjectInputStream(fstream);
	    		obj = in.readObject();
	    		if(obj == null) {
	    			return "error";
	    		}
	    		LinkedList<Bid> list = (LinkedList<Bid>) obj;
	    		
	    		for(Bid bid: list) {
	    			if(name.contentEquals(bid.getOwnerName())) {
	    				//encrypt the password
	    				//pwd = 
	    				if(pwd.contentEquals(bid.getPwd())) {
	    					return "true";
	    				}
	    			}
	    					
	    		}
	    		return "false";
    		}
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    	}catch (IOException e) {
            System.err.println("Could not load file - " + e);
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	return "error";
    }
    
  //sign up for bidder
    public String signUp(AuctionBidderInterface owner,String name, String pwd) throws RemoteException{
    	Bid bidder = new Bid(owner, name, pwd);
    	bidder.changeStatus();
    	bidders.add(bidder);
    	FileOutputStream out;
		try {
			out = new FileOutputStream(bfileName);
			ObjectOutputStream oos = new ObjectOutputStream(out);
	        oos.writeObject(bidders);
	        oos.close();
	        return "true";
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    	return "error";
    }
	
	//create an auction item
	public String createAuctionItem(AuctionBidderInterface owner, String ownerName, String name, float minVal, long closingTime) throws RemoteException {
		String msg="";
		Boolean flag=false;
		//validation
		if (owner == null) {  
			System.out.println("Owner is null");	
			flag = true;
		}
        if (name == null) {
        	System.out.println("Item name is null");
        	flag = true;
        }
        if (name.length() == 0) {
        	System.out.println("Item name is empty");
        	flag = true;
        }
        if (minVal < 0) {
        	System.out.println("Bid is too low");
        	flag = true;
        }
        if (closingTime < 0) {
        	System.out.println("Illegal closing time");
        	flag = true;
        }
        //create item if it passes the validations 
        if(!flag) {
        	Item item = new Item(owner, ownerName, name, minVal, closingTime);   
        	AuctionItemBuffer buffer = new AuctionItemBuffer(activeItemBuffer, closedItemBuffer);
        	buffer.addItem(item); 
        	timer = new Timer();
        	timer.schedule(buffer, closingTime * 1000);
        	
        	msg = "New item created succesfully";
        }
		return msg;
	}

	//method to make a bid
    public String bid(AuctionBidderInterface owner, String ownerName, int auctionItemId, float amount) throws RemoteException {
    	String msg="";
    	int index = 0;
    	boolean flag = true;
    	
    	if (owner == null) {
    		msg = "Owner is null";
    	} else {    
    		if(auctionItemId >= activeItemBuffer.size() || auctionItemId < 0) {
	        	return "Invalid auction item";
	        }
	        // Validate item exists and doesn't belong to the bidder
    		for(int i=0; i < activeItemBuffer.size() && flag; i++) {
        		if(activeItemBuffer.get(i).getId() == auctionItemId) {
        			index = i;
        			flag = false;
        		}
        	} 		
	        
	        Item item = activeItemBuffer.get(index);
	        if (item == null) 
	        	return "Invalid auction item";
	        if (item.getOwner().getName().equals(ownerName))
	        	return "This item is owned by you";	        
	        //construct bid object	        
	        Bid b = new Bid(owner, amount, ownerName);
	        return item.makeBid(b);
    	}
		return msg;
    }

    //returns a string containing info about all auction items
    public String getOpenAuctions() throws RemoteException {
    	if (activeItemBuffer.size() <= 0) 
    		return "No available auctions";
        StringBuilder msg = new StringBuilder();
        String separator = "-----------------------\n";
        for (Item item : activeItemBuffer) {
            msg.append(item.toString());
            msg.append(separator);
        }
        return msg.toString();
    }
    
    //returns a string containing info about all closed auction items
    public String getClosedAuctions() throws RemoteException {
        if (closedItemBuffer.size() == 0) 
        	return "No closed auctions";
        StringBuilder msg = new StringBuilder();
        String separator = "-----------------------\n";
        for (Item item : closedItemBuffer) {
            msg.append(item.toString());
            msg.append(separator);
        }
        return msg.toString();
    }
    
    //returns the active bid list
    public LinkedList<Item> getActiveItemBuffer(){
    	return activeItemBuffer;
    }
    
  //returns the closed bid list
    public LinkedList<Item> getClosedItemBuffer(){
    	return closedItemBuffer;
    }
    
  //returns the bibber list
    public LinkedList<Bid> getBidders(){
    	return bidders;
    }
    
    //testing function
    public String toString() {
    	return " active_list : " + activeItemBuffer + " closed_list : " + closedItemBuffer;
    }
}
