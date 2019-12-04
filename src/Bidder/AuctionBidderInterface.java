package Bidder;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface AuctionBidderInterface extends Remote {
	String getName() throws RemoteException;

	void callback(String message) throws RemoteException;
}
