package Bidder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

class AuctionBidder {
	public static void main(String[] args) {
        String host = "localhost";
        int port = 1099;

        String connectionStr = "rmi://"+host+":"+port+"/auction";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            ConnectServer connection = new ConnectServer(connectionStr);
            
            System.out.print("What is your username? ");
            String ownerName = br.readLine();
            AuctionBidderWork client = new AuctionBidderWork(ownerName);
            System.out.println("Choose an option");
            System.out.println("l - List items");
            System.out.println("a - Add Item");
            System.out.println("b - Make a Bid");
            System.out.println("h - History");
            System.out.println("q - Quit");

            boolean end = false;
            while (!end) {
                String response = "";
                try {
                    switch (br.readLine().toLowerCase()) {
                        case "l":
                            response = connection.getServer().getOpenAuctions();
                            break;
                        case "a":
                            try {
                                System.out.print("Item name: ");
                                String name = br.readLine();
                                if (name.equals("")) throw new NumberFormatException();
                                System.out.print("Starting price: $");
                                float startPrice = Float.valueOf(br.readLine());
                                System.out.print("End auction in x seconds: ");
                                long endTime = Long.valueOf(br.readLine());
                                response = connection.getServer().createAuctionItem(client, name, startPrice, endTime);
                            } catch (NumberFormatException nfe) {
                                System.err.println("Incorrect input format. Please try again.");
                            }
                            break;
                        case "b":
                            try {
                                System.out.print("Auction item ID: ");
                                int auctionItemId = Integer.valueOf(br.readLine());
                                System.out.print("Amount: $");
                                float bidAmount = Float.valueOf(br.readLine());
                                response = connection.getServer().bid(client, ownerName, auctionItemId, bidAmount);
                            } catch (NumberFormatException nfe) {
                                System.err.println("Incorrect input format. Please try again.");
                            }
                            break;
                        case "h":
                            response = connection.getServer().getClosedAuctions();
                            break;
                        case "q":
                            end = true;
                            break;
                        default:
                            break;
                    }
                } catch (RemoteException e) {
                    System.out.println(e);
                }
                System.out.println(response);
            }
            System.exit(0);
        } catch (IOException e) {
            System.err.println("Error " + e);
            System.exit(2);
        }
    }	
}
