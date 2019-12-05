package Server;

import java.util.*;
import java.io.*;

class AuctionItemBuffer extends TimerTask implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static LinkedList<Item> activeItemBuffer;
	private static LinkedList<Item> closedItemBuffer;
	int id;

    //assign the item buffer
    public AuctionItemBuffer(LinkedList<Item> active, LinkedList<Item> closed) {
    	activeItemBuffer = active;
    	closedItemBuffer = closed;    	
    }
    
    //add an item to the buffer
    public void addItem(Item item) {
    	this.id = item.getId();
    	activeItemBuffer.add(item);
    }
    
        
    //scan the buffer for expired items
    public void run() {
        Item item;
        int index = 0;
        boolean flag = true;
        synchronized(this) {  
//        	System.out.println("(for testing)Expired item id: " + id);
        	for(int i=0; i < activeItemBuffer.size() && flag; i++) {
        		if(activeItemBuffer.get(i).getId() == id) {
        			index = i;
        			flag = false;
        		}
        	}
            if ((item = activeItemBuffer.remove(index)) != null) {
                // Move auction to closed buffer
            	closedItemBuffer.add(item);
                 
                // Notify observers
            	String msg = "Auction for item " + item.getName();
//                StringBuilder m = new StringBuilder("Auction for item " + item.getName() + " ended");
            	Bid maxBid = item.getMaxBidIndex();
                if (maxBid != null) {
                    msg += "\nWinning bid: $" + maxBid.getAmount();
                    msg += " by " + maxBid.getOwnerName();
                } else {
                    msg = "No winner!";
                }
                item.notifyObservers(msg);
            } 
            // Remove this task from map
//            timerTasks.remove(this);
        }
    }
}
