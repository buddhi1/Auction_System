package Bidder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

class AuctionBidder {
	public static void main(String[] args) {
        String host = "localhost";
        int port = 1099;
        String input="";
        String response = "";
        String ownerName = "";
        AuctionBidderWork client;

        String connectionStr = "rmi://"+host+":"+port+"/auction";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));       

        try {
            ConnectServer connection = new ConnectServer(connectionStr);            
            
            System.out.println("*** Welcome to the Auction System ***");
            do {
	            System.out.println("Choose option");
	            System.out.println("i - Sign in");
	            System.out.println("u - Sign up");
	            input = br.readLine().toLowerCase();
            }while(!(input.equals("i") || input.equals("u")));
                    
            
            do {
            	System.out.print("Username: ");
                ownerName = br.readLine();
                System.out.print("Password: ");
                String pwd = br.readLine();
                client = new AuctionBidderWork(ownerName);
	            if(input.equals("i")) {
	            	response = connection.getServer().signIn(ownerName, pwd);
	            }else {
	            	response = connection.getServer().signUp(client, ownerName, pwd);
	            }
	            System.out.println("Login " + response);
            }while(!"true".equals(response));
            
            
            System.out.println("Choose an option");
            System.out.println("l - List items");
            System.out.println("a - Add Item");
            System.out.println("b - Make a Bid");
            System.out.println("h - History");
            System.out.println("q - Quit");
            
            boolean end = false;
            while (!end) {
                response = "";
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
                                response = connection.getServer().createAuctionItem(client, client.getName(), name, startPrice, endTime);
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
