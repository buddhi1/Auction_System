package Bidder;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AuctionBidderWork extends UnicastRemoteObject implements AuctionBidderInterface {
	private String name;
	
	//constructs a bidder object
	public AuctionBidderWork(String name) throws RemoteException {
        super();
        this.name = name;
    }
	
	//returns the name of bidder
	public String getName() throws RemoteException {
        return name;
    }
	
	//
	public void callback(String message) throws RemoteException {
        System.out.println(message);
    }
}
