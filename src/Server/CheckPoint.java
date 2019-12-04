package Server;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.TimerTask;

class CheckPoint extends TimerTask{
	private AuctionServerWork auction;
    private String fileName;
    CheckPoint(AuctionServerWork auction, String fileName) {
        this.auction = auction;
        this.fileName = fileName;
    }
    
    //check-pointing server state
    public  void makeCheckpoint(AuctionServerWork auction, String fileName) {
        try {
            FileOutputStream out = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(out);
            System.out.println(auction);
            oos.writeObject(auction);
            oos.close();
            System.out.println("Successfully checkpoint " + fileName);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find file " + e);
        } catch (IOException e) {
            System.err.println("Unable to write to file " + e);
        }
    }

    //run method
    public void run() {
    	System.out.println("inside checkPoint - auction obj: " + auction.toString());
    	makeCheckpoint(auction, fileName);
    }
}
