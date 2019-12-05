package Server;

import Bidder.AuctionBidderInterface;

import java.io.Serializable;
import java.sql.Date;

class Bid implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final AuctionBidderInterface owner;
	private String ownerName;
    private float amount;
    private long timestamp;
    private boolean status;
    private Date lastLoginTime;
    private String pwd;

    public Bid(AuctionBidderInterface owner, float amount, String ownerName) {
        this.owner = owner;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.ownerName = ownerName;
    }
    
    public Bid(AuctionBidderInterface owner, String ownerName, String pwd) {
        this.owner = owner;
        this.lastLoginTime = new Date(System.currentTimeMillis());
        this.ownerName = ownerName;
        this.pwd = pwd;
    }

    //logs in a bidder
    public void changeStatus() {
    	if (status) {
    		status = false;
    	}else {
    		status = true;
    	}
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
    
    //read password
    protected String getPwd() {
    	return pwd;
    }
}
