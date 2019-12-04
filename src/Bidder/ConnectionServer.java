package Bidder;

import Server.AuctionServerInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

class ConnectServer {
    private String connectionStr;
    private boolean connected = false;
    private AuctionServerInterface server;

    //constructs an object
    public ConnectServer(String connectionStr) {
        this.connectionStr = connectionStr;
        connect();
    }

    private void connect() {
        if (!isConnected()) {
            try {
                server = (AuctionServerInterface) Naming.lookup(connectionStr);
                // Flag used by the servlet
                setConnected(true);
            } catch (MalformedURLException e) {
                System.err.println("Malformed URL - " + e);
            } catch (NotBoundException e) {
                System.err.println("Unable to bind the server - " + e);
            } catch (RemoteException e) {
                System.err.println("Unable to contact the server - " + e);
            }
        }
    }

    public void reconnect() {
        connect();
        if (isConnected()) {
            System.out.println("Reconnected!");
        }
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    public AuctionServerInterface getServer() throws RemoteException {
        if (isConnected()) {
            return server;
        } else {
            throw new RemoteException("Server is dead.");
        }
    }

    public void setServer(AuctionServerInterface server) {
        this.server = server;
    }
}
