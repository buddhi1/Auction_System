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
	private transient Timer timer;
	
	public AuctionServerWork() throws RemoteException {
        super();
//        timer = new Timer();
        activeItemBuffer = new LinkedList<Item>();
        closedItemBuffer = new LinkedList<Item>();
    }
	public void reloadTimer() {
//        timer = new Timer();
//        for (Map.Entry<LifecycleAuctionItemTask, Long> t: timerTasks.entrySet()) {
//            // Reschedule task to initial value subtracted how much has already elapsed
//            long timeLeft = t.getValue() - t.getKey().getTimeLeft();
//            timer.schedule(t.getKey(), timeLeft < 0 ? 0 : timeLeft);
//        }
    }
	
	//create an auction item
	public String createAuctionItem(AuctionBidderInterface owner, String name, float minVal, long closingTime) throws RemoteException {
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
        	Item item = new Item(owner, name, minVal, closingTime);   
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
	        // Validate item exists and doesn't belong to the bidder
    		for(int i=0; i < activeItemBuffer.size() && flag; i++) {
        		if(activeItemBuffer.get(i).getId() == auctionItemId) {
        			index = i;
        			flag = false;
        		}
        	}
//	        if(auctionItemId >= activeItemBuffer.size()) {
//	        	return "Invalid auction item";
//	        }
	        Item item = activeItemBuffer.get(index);
	        if (item == null) 
	        	return "Invalid auction item";
	        if (item.getOwner() == owner) 
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
    
    //testing function
    public String toString() {
    	return " active_list : " + activeItemBuffer + " closed_list : " + closedItemBuffer;
    }
}
