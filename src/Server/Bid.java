package Server;

import Bidder.AuctionBidderInterface;

import java.io.Serializable;

class Bid implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final AuctionBidderInterface owner;
	private String ownerName;
    private final float amount;
    private final long timestamp;

    public Bid(AuctionBidderInterface owner, float amount, String ownerName) {
        this.owner = owner;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.ownerName = ownerName;
    }

    //returns the bid amount
    public float getAmount() {
    	return amount;
    }
    
    //returns the owner name 
    public String getOwnerName() {
    	return ownerName;
    }
    
    //returns the owner
    public AuctionBidderInterface getOwner() {
    	return owner;
    }
}
