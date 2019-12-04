package Server;

import Bidder.AuctionBidderInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;

public interface AuctionServerInterface extends Remote {
	/**
     * Create an auction item
     * owner client object
     * name item name
     * minVal minimum bid
     * closingTime closing time in seconds
     * return success/error message
     * 
     */
    String createAuctionItem(AuctionBidderInterface owner, String name, float minVal, long closingTime) throws RemoteException;

    /**
     * Make a bid
     * owner client object
     * ownerName client name
     * auctionItemId item id
     * amount bid amount
     * @return success/error message
     * 
     */
    String bid(AuctionBidderInterface owner, String ownerName, int auctionItemId, float amount) throws RemoteException;

    /**
     * Returns a nicely formatted string that contains a list of open auctions
     * @return list of open auctions
     * 
     */
    String getOpenAuctions() throws RemoteException;
    /**
     * Returns a nicely formatted string that contains a list of closed auctions
     * @return list of all closed auctions
     * 
     */
    String getClosedAuctions() throws RemoteException;
    
  //returns the active bid list
//    public LinkedList<Item> getActiveItemBuffer();

}
