package Server;

import Bidder.AuctionBidderInterface;

import java.io.Serializable;
import java.util.*;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;

class Item implements Serializable {
	private static final long serialVersionUID = 1L;
    private static int counter = 0;
    private int id;
    private AuctionBidderInterface owner;
    private String ownerName;
    private LinkedList<Bid> bids;
    private Set<AuctionBidderInterface> observers;
    private String name;
    private final float minBid;
    private final Date startDate, closingDate;
    private long closingTime;
    
    /* class constructor
     * 
     */
    public Item(AuctionBidderInterface owner, String OwnerName, String name, float minBid, long closingTime) {
        this.owner = owner;
        this.ownerName = ownerName;
        this.startDate = new Date(System.currentTimeMillis());
        this.closingDate = new Date(System.currentTimeMillis() + 1000 * closingTime);
        this.closingTime = closingTime * 1000;
        synchronized(this) {
            this.id = counter++;
        }
        this.name = name;
        this.bids = new LinkedList<>();
        this.observers = new HashSet<>();
        this.observers.add(owner);
        this.minBid = minBid;
    }
    
    /*notify all the clients
     * 
     */
    public void notifyObservers(String message) {
//        for (AuctionBidderInterface client : observers) {
    	for (AuctionBidderInterface client : observers) {
            try {
                client.callback(message + "Owner: " + ownerName);
            } catch (RemoteException e) {
                System.err.println("Unable to access client - " + e);
            }
        }
    }
    
    /* make a bid
     * 
     */
    public synchronized String makeBid(Bid b) {
    	String msg = "Bidding is successfull!";
        if (closingDate.getTime() - startDate.getTime() < 0) {
        	msg = "Bidding is closed on this item. Try something else";            
        } else if (b.getAmount() <= minBid) {
        	msg = "Bidding less than the starting value. Try again";
        } else {
	        bids.push(b);
	        observers.add(b.getOwner());
        }
        return msg;
    }

    //returns the id
    public int getId() {
    	return id;
    }
    
    //return item name
    public String getName() {
    	return name;
    }
    
    //returns the owner
    public AuctionBidderInterface getOwner() {
    	return owner;
    }
    
    //returns the max bid index
    public Bid getMaxBidIndex() {
    	if(bids.size() <= 0) {
    		return null;
    	}
    	int mi = 0;
    	float maxAmount = bids.get(mi).getAmount();
    	for(int i=1; i < bids.size(); ++i) {
    		if(maxAmount < bids.get(i).getAmount()) {
    			maxAmount = bids.get(i).getAmount();
    			mi = i;
    		}
    	}
    	return bids.get(mi);
    }
    
    //return current bid
    public synchronized Bid getCurrentBid() {
        if (bids.size() > 0) {
            return bids.peek();
        }
        return null;
    }
    
    //returns the closing date of the bid item
    public Date getClosingDate() {
    	return closingDate;    	
    }
    
  //returns the closing time of the bid item
    public long getClosingTime() {
    	return closingTime;    	
    }
    
  //returns the start date of the bid item
    public Date getStartDate() {
    	return startDate;    	
    }
    
    //return auction item info in a string
    public String toString() { // complete method from the begining 
    	synchronized(this) {
            Bid currentBid = getCurrentBid();
            SimpleDateFormat dF = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

            long timeDiff = closingDate.getTime() - System.currentTimeMillis();
            boolean hasEnded = timeDiff <= 0;
            String timeLeftStr = "";
            if (!hasEnded) {
                if (timeDiff < 60 * 1000) {
                    timeLeftStr = String.valueOf(timeDiff / 1000) + "s";
                } else if (timeDiff >= 60 * 1000 && timeDiff < 60 * 60 * 1000) {
                    timeLeftStr = String.valueOf(timeDiff / 1000 / 60) + "min " + (timeDiff / 1000) % 60 + "s";
                } else if (timeDiff >= 60 * 60 * 1000) {
                    timeLeftStr = String.valueOf(timeDiff / 1000 / 60 / 60) + "h " + (timeDiff / 1000 / 60) % 60 + "min";
                }
            }
            StringBuilder result = new StringBuilder("Auction Item #");
            result.append(id).append(": ").append(name).append("\n");
            result.append("Minimum bid: $").append(minBid).append("\n");
            if (hasEnded && getCurrentBid() != null) {
                result.append("Winning bid: $").append(currentBid.getAmount())
                        .append(" by ").append(currentBid.getOwnerName()).append("\n");
            } else {
                result.append("Current bid: ").append(currentBid == null ? "none" : "$"+currentBid.getAmount()).append("\n");
            }
            result.append("Start date: ").append(dF.format(startDate)).append("\n");
            result.append("Closing date: ").append(dF.format(closingDate)).append("\n");
            if (!hasEnded) {
                result.append("Time left: ").append(timeLeftStr);
            }
            return result.append("\n").toString();
        }
    }
}
