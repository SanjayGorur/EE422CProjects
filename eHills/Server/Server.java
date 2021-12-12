// Copy-paste this file at the top of every file you turn in.
/*
* EE422C Final Project submission by
* Replace <...> with your actual data.
* <Sanjay Gorur>
* <sg52879>
* <17805>
* Fall 2021
*/

package server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import com.google.gson.*;
import com.google.gson.reflect.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Server extends Observable 
{
    public static Server bidWar;
    public static List<NewProduct> trackItems;
    public static List<ClientHandler> clientHandlers =  new ArrayList<ClientHandler>();
    public static AllClients c;
    public static Server.ConvertAllItems auction;
    
	/*
	 * Starts the server, and acts as the functionality for the eHills auction house.
	 */
    public static void main(String[] args) 
    {
    	bidWar = new Server();
        
        auction = bidWar.new ConvertAllItems();
        
        c = bidWar.new AllClients(); 
        
        auction.getAccounts();
        auction.getProducts();
        
        bidWar.presentShop();
        bidWar.startServer();
    }
    
	/*
	 * Checks if there is an account that corresponds to the given account and password.
	 * @param - account --> username of the account
	 * @param - pwd --> password of the given account
	 */   
    private boolean getAccounts(String account, String pwd) 
    {
    	try 
    	{
        	Reader reader = Files.newBufferedReader(Paths.get("allaccounts.json")); 
        	List<ClientUser> readUser = new Gson().fromJson(reader, new TypeToken<List<ClientUser>>() {}.getType());
        	
        	for(ClientUser user: readUser) 
        	{
        		if(user.getName().equals(account)) 
        		{
        			if(!(user.getName().equals("guest")) && user.getPassword().equals(pwd))
        			{
        				c.getClients().add(user);
        				return true;
        			}
        			else if(user.getName().equals("guest"))
        			{
        				c.getClients().add(user);
        				return true;
        			}
        		}
        	}
        	return false; 
        			
    	} 
    	catch(Exception e) 
    	{
    		e.printStackTrace();
    	}
		return false;
    	
    }
    
	/*
	 * presents all products to the client by extracting items from a JSON file.
	 */ 
    private void presentShop() 
    {	
    	try 
    	{
    	    Reader getAllItems = Files.newBufferedReader(Paths.get("allproducts.json"));
    	    trackItems = new Gson().fromJson(getAllItems, new TypeToken<List<NewProduct>>() {}.getType());
    	    getAllItems.close();   
    	} 
    	catch (Exception e) 
    	{
    	    e.printStackTrace();
    	}	
	}
    
	/*
	 * Starts the server up --> called from main in Server class(bidWar)
	 */ 
	private void startServer() 
	{
        int port = 8888; // lucky number
        try 
        {
        	int time = 1;
        	//Thread timerThread = new Thread(new Timer());
        	//timerThread.start();
            ServerSocket topServer = new ServerSocket(port);
            while (true) 
            {
                Socket newClient = topServer.accept();
                System.out.println(time + " client(s) connected to server");
                time++;
                
                ClientHandler handleClient = new ClientHandler(this, newClient);
                clientHandlers.add(handleClient);
                this.addObserver(handleClient);
                
                Thread t = new Thread(handleClient);
                t.start();               
            }
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }
	
	/*
	 * Assesses message from client, and updates all other clients accordingly.
	 * @param - account --> string containing needed information from client
	 * 		--> response: what does the client want?
	 */ 
    public String checkResponse(String account) 
    {
        Gson gson = new Gson();
        NewInquiry clientMessage = new NewInquiry(); 

        try 
        {
        	clientMessage = gson.fromJson(account, NewInquiry.class);	
        } 
        catch(Exception e) 
        {
        	e.printStackTrace();
        }
        
        if(clientMessage.getResponse().equals("login"))
        {
        	if(this.getAccounts(clientMessage.getName(), clientMessage.getPassword()))
        	{
        		return "{ response: 'login', loggedIn: true, name: '"+ clientMessage.getName() +"'}";
        	}
        	else 
        	{
        		return "{ response: 'login', loggedIn: false, name: '"+ clientMessage.getName() +"'}";
        	} 
         }
        
         else if(clientMessage.getResponse().equals("history")) 		
         {
        	 if(clientMessage.getName().equals("allBids"))
        	 {
        		return "{ response: 'history', name: 'bids', hist: '" + c.allClientsBids() + "' }";  
        	 }
        	 else if(clientMessage.getName().equals("allBoughts"))
        	 {
        		return "{ response: 'history', name: 'boughts', hist: '" + c.allClientsSales() + "' }";  
        	 }
        	 else
        	 {
        		for(ClientUser u: c.getClients())
        		 {
        			  if(u.getName().equals(clientMessage.getName()))
        			  {
        				  return "{ response: 'history', name: '" + clientMessage.getName() + "', input: '" + u.getInfo() + "' }";
        			  }
        		  }
        		return "{ response: 'history', name: 'Userdoesnotexist' }";
        	 }
          }

          else if(clientMessage.getResponse().equals("getItems"))
          {
        	  String itemsToJSON = new Gson().toJson(trackItems);
    		  return  "{ response: 'getItems', input: '" + itemsToJSON + "'}";      	  
          }

          else if(clientMessage.getResponse().equals("bid"))
          {
              for(NewProduct product: trackItems) 
              {
            	  if(product.getName().equals(clientMessage.getClientInfo())) 
            	  {
            		  if(product.getAuction().equals("Ended") || product.getBidsLeft() <= 0) 
            		  {
            			  return "{response: 'error', input: 'Auction has ended'}";
            		  }
            		  
            		  if(product.getBidPrice() < clientMessage.getBid()) 
            		  {
            			  String find = "";
            			  product.setBidsLeft(product.getBidsLeft() - 1);
            			  for(ClientUser u: c.getClients())
            			  {
            				  if(clientMessage.getName().equals(u.getName()))
            				  {
            					  product.setHighestBidder(u.getName());
            					  u.addBid(product);
            					  find = u.getName();
            					  c.addBid(u.getName() + " bid on " + product.getName() + " for " + clientMessage.getBid());
            				  }
            			  }
            			  
            			  product.setBidPrice(clientMessage.getBid());
            			  
            			  if(product.getBidPrice() >= product.getBuyPrice()) 
            			  {
            				  product.updateItem("Ended");
                			  for(ClientUser u: c.getClients())
                			  {
                				  if(clientMessage.getName().equals(u.getName()))
                				  {
                					  product.setHighestBidder(u.getName());
                					  u.addBought(product);
                					  find = u.getName();
                					  c.addSale(u.getName() + " bought " + product.getName() + " for " + clientMessage.getBid());
                				  }
                			  }
            			  }
            			  
            			  this.setChanged();
            			  this.notifyObservers("{ response: bid, name: '" + find + "', input: '" + product.getName() + "', currPrice: '" + product.getBidPrice() + "', amount:'" + product.getBidsLeft() + "' }");
            			  return "{ response: bid, name: '" + find + "', input: '" + product.getName() + "', currPrice: '" + product.getBidPrice() + "', amount:'" + product.getBidsLeft() + "' }";
            		  }
            		  else 
            		  {
            			  return "{response: 'error', input: 'Bid must be higher than current price'}"; 
            		  }
            	  }
              }

              return "{ response: 'error', input: 'Invalid bid value'}"; 
          }
          
          return "Not a valid command";
    }
    
    /* This was an attempt to incorporate an actual countdown timer, ended up using a bid restriction for each product.
    class Timer implements Runnable
    {
    	@Override
    	public void run()
    	{
    		for(NewProduct p: auction.allProducts)
    		{
    			p.setTime(p.getTime() - 1);
    			bidWar.notifyObservers("{ type: time, username:'" + p.getName() + "', input: '" + p.getTime() + "'}");
    			for(ClientHandler a: clientHandlers)
    			{
    				a.sendToClient("{ type: time, username:'" + p.getName() + "', input: '" + p.getTime() + "'}");
    			}
    		}
    	}
    }
    */

	/*
	 * The NewProduct class allows the server to keep track of each product included in the auction.
	 */ 
    class NewProduct 
    {
    	private String name;
    	private int bidsLeft;
    	private String highestBidder;
    	private String details; 
        private double bidPrice;
        private double buyPrice; 
        private String auction;

        public NewProduct() 
        {
        	this.name = ""; 
        	this.bidsLeft = 0;
        	this.highestBidder = "";
        	this.details = ""; 
        	this.bidPrice = 0.0; 
        	this.buyPrice = 0.0;
        	this.auction = ""; 
        }

        public NewProduct(String name, int bidsLeft, String highestBidder, String details, double bidPrice, double buyPrice, String auction) 
        {
            this.name = name;
            this.bidsLeft = bidsLeft;
            this.highestBidder = highestBidder;
            this.details = details;
            this.bidPrice = bidPrice;
            this.buyPrice = buyPrice; 
            this.auction = auction; 
        }

        
        public String getName() 
        {
        	return this.name; 
        }
        
        public int getBidsLeft()
        {
        	return this.bidsLeft;
        }
        
        public String getHighestBidder()
        {
        	return this.highestBidder;
        }
        
        public String getDetails() 
        {
        	return this.details;
        }
        
        public double getBidPrice() 
        {
        	return this.bidPrice;
        }
        
    	public double getBuyPrice() 
    	{
    		return this.buyPrice;
    	}
    
    	public String getAuction() 
    	{
    		return this.auction; 
    	}

        public void setName(String name) 
        {
        	this.name = name; 
        }
        
        public void setDetails(String details) 
        {
        	this.details = details;
        }
        
        public void setBidPrice(double bidPrice) 
        {
        	this.bidPrice = bidPrice; 
        }
        
    	public void setBuyPrice(double buyPrice) 
    	{
    		this.buyPrice = buyPrice;
    	}
    	
    	public void updateItem(String auction) 
    	{
    		this.auction = auction; 
    	}
    	
    	public void setHighestBidder(String highestBidder)
    	{
    		this.highestBidder = highestBidder;
    	}
    	
    	public void setBidsLeft(int bidsLeft)
    	{
    		this.bidsLeft = bidsLeft;
    	}
          
        public String toString() 
        {
        	return(this.name + ", " + this.details + ", " + this.auction);
        }   	
    }
    
	/*
	 * The NewInquiry class allows the creation of objects that can interact with the client.
	 * This helps the server to account for changes in the program, and inform the clients as needed. 
	 */ 
    class NewInquiry 
    {
    	private String response; 
    	private String name;
    	private String password;
    	private String input;
    	private double currPrice;
    	private double bid; 

    	public NewInquiry() 
    	{
    		this.response = ""; 
    		this.name = "guest"; 
    		this.password = ""; 
    		this.input = "";
    		this.currPrice = 0.0;
    		this.bid = 0.0; 
    	}
    	
    	public NewInquiry(String response, String name, String password, String input, double number, double bid) 
    	{
    	    this.response = response;
    		this.name = name; 
    	    this.password = password; 
    	    this.input = input;
    	    this.currPrice = number;
    	    this.bid = bid; 
    	}
    	
    	public String getResponse()
    	{
    		return this.response;
    	}
    	
    	public String getName()
    	{
    		return this.name;
    	}
    	
    	public String getClientInfo()
    	{
    		return this.input;
    	}
    	
    	public double getBid()
    	{
    		return this.bid;
    	}
    	
    	public String getPassword()
    	{
    		return this.password;
    	}
    }

	/*
	 * The ConvertAllItems class converts a given list of NewProduct objects to a json file, 
	 * and the client reads it in for the user to interact in the auction.
	 */ 
    class ConvertAllItems 
    {
    	public List<NewProduct> allProducts; 
    	public List<ClientUser> allAccounts; 
    	
    	public void getAccounts() 
    	{
    		try 
    		{
    			// Username with password
    			allAccounts = Arrays.asList(new ClientUser("sanjay", "chess"), new ClientUser("gorur", "basketball"), new ClientUser("om", "biking"), new ClientUser("wafee", "anime"), new ClientUser("roshan", "scream"), new ClientUser("nikhil", "eat"), new ClientUser("mj", "goat"),new ClientUser("amma", "cook"),new ClientUser("guest"));
    			
    			Writer convertAccountsJSON = new FileWriter("allaccounts.json"); 
    			new Gson().toJson(allAccounts, convertAccountsJSON); 
    			convertAccountsJSON.close();	
    		} 
    		catch(Exception e) 
    		{
    			e.printStackTrace();
    		}		
    	}
    	
    	public void getProducts() 
    	{	
    		try 
    		{
    		    allProducts = Arrays.asList(new NewProduct("Jordan Jersey", 18, "NA", "1996 Game-Worn Clothing From Bulls vs. Sonics", 882.0, 1289.5, "Ongoing"), new NewProduct("Chess Set", 17, "NA", "Historic Game Board Used By Kasparov and Kramnik", 412.8, 602.9, "Ongoing"), new NewProduct("Chewing Gum", 16, "NA", "Juicy Fruit", 1.28, 2.43, "Ongoing"), new NewProduct("26 West Apartment", 15, "NA", "Dirty Room on 26th Street", 888.8, 1238.0, "Ongoing"), new NewProduct("Falafel Wrap", 14, "NA", "Get A Taste of Mediterannean!", 9.17, 12.28, "Ongoing"), new NewProduct("Wilson Football", 13, "NA", "The Mannings Like the Laces", 17.15, 31.6, "Ongoing"),new NewProduct("Alabama vs. Georgia", 12, "NA", "Tickets to watch the SEC Champ!", 98.12, 146.6, "Ongoing"),new NewProduct("Special Snowtorm", 11, "NA", "Enjoy a treat of February 2021", 238.12, 443.1, "Ongoing"),new NewProduct("Gaming Chair", 10, "NA", "Two hands on the armrest", 48.09, 93.1, "Ongoing"),new NewProduct("Brand New Bike", 9, "NA", "Cycle to Zilker!", 821.3, 974.0, "Ongoing"),new NewProduct("Television?", 8, "NA", "For Sports Maniacs", 657, 1002.5, "Ongoing"),new NewProduct("UT Registration", 7, "NA", "Sign up to fail...", 3.45, 6.82, "Ongoing"));

    		    Writer convertItemsJSON = new FileWriter("allproducts.json");
    		    new Gson().toJson(allProducts, convertItemsJSON);
    		    convertItemsJSON.close();
    		} 
    		catch (Exception e) 
    		{
    		    //e.printStackTrace();
    		}
    		
    	}
    }
    
	/*
	 * The AllClients class is used to keep track of all clients and their actions associated with the server. 
	 */    
    class AllClients
    {
    	private ArrayList<ClientUser> clients;
    	private String clientsBids;
    	private String clientsSales;
    	
    	public AllClients()
    	{
    		this.clients = new ArrayList<ClientUser>();
    		this.clientsBids = "";
    		this.clientsSales = "";
    	}
    	
    	public String allClientsBids()
    	{
    		return this.clientsBids;
    	}
    	
    	public String allClientsSales()
    	{
    		return this.clientsSales;
    	}
    	
    	public ArrayList<ClientUser> getClients()
    	{
    		return clients;
    	}
    	
    	public void addBid(String bid)
    	{
    		 this.clientsBids += bid + " ";
    	}
    	
    	public void addSale(String sale)
    	{
    		 this.clientsSales += sale + " ";
    	}
    }

	/*
	 * The ClientUser class is used to create an object for each client that is connected to the server
	 * at any point in time. This is preset, and it is called when the corresponding user logs in to their account. 
	 */ 
    class ClientUser 
    {
    	private String name; 
    	private String password;
    	private ArrayList<NewProduct> biddedItems;
    	private ArrayList<NewProduct> boughtItems;

    	public ClientUser() 
    	{
    		name = "guest"; 
    		password = ""; 
    		biddedItems = new ArrayList<NewProduct>();
    		boughtItems = new ArrayList<NewProduct>();
    	}
    	
    	public ClientUser(String name) 
    	{
    		this.name = name;
    		this.password = "";
    		biddedItems = new ArrayList<NewProduct>();
    		boughtItems = new ArrayList<NewProduct>();
    	}
    	
    	public ClientUser(String name, String password)
    	{
    		this.name = name;
    		this.password = password;
    		biddedItems = new ArrayList<NewProduct>();
    		boughtItems = new ArrayList<NewProduct>();
    	}
    	
    	public String getName() 
    	{
    		return this.name;
    	}
    	public String getPassword() 
    	{
    		return this.password;
    	}
    	
    	public void setPassword(String newPassword) 
    	{
    		this.password = newPassword;
    	}
    	
    	public String toString()
    	{
    		return "username = " + name + " and password = " + password;
    	}
    	
    	public ArrayList<NewProduct> getBids()
    	{
    		return this.biddedItems;
    	}
    	
    	public ArrayList<NewProduct> getBoughts()
    	{
    		return this.boughtItems;
    	}
    	
    	public void addBid(NewProduct p)
    	{
    		this.biddedItems.add(p);
    	}
    	
    	public void addBought(NewProduct p)
    	{
    		this.boughtItems.add(p);
    	}
    	
    	public String getInfo()
    	{
    		String start = this.name + " has bid on the following items: ";
    		for(int i = 0; i < this.biddedItems.size(); i++)
    		{
    			start += this.biddedItems.get(i).getName() + ", ";
    			if(i + 1 == this.biddedItems.size())
    			{
    				start += " .";
    			}
    		}
    		start += " and this user has bought the following items: ";
    		for(int i = 0; i < this.boughtItems.size(); i++)
    		{
    			start += this.boughtItems.get(i).getName() + ", ";
    			if(i + 1 == this.boughtItems.size())
    			{
    				start += " .";
    			}
    		} 	
    		
    		return start;
    	}
    }
    
    // This is from class lecture.
	/*
	 * The ClientHandler class handles all interaction between the server and client(s)
	 * through reading and writing --> BufferedReader and PrintWriter.
	 */ 
    class ClientHandler implements Runnable, Observer 
    {
      private Server server;
      private Socket newConnect;
      private BufferedReader clientReader;
      private PrintWriter clientWriter;

      public ClientHandler(Server server, Socket newConnect) 
      {
        this.server = server;
        this.newConnect = newConnect;
        try 
        {
          clientReader = new BufferedReader(new InputStreamReader(this.newConnect.getInputStream()));
          clientWriter = new PrintWriter(this.newConnect.getOutputStream());
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
      }

      public void sendToClient(String string) 
      {
        clientWriter.println(string);
        clientWriter.flush();
      }

      @Override
      public void run() 
      {
        String input;
        String output; 
        try 
        {
          while ((input = clientReader.readLine()) != null) 
          {
            output = server.checkResponse(input);
            sendToClient(output); 
          }
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
      }

      @Override
      public void update(Observable o, Object arg) 
      {
        sendToClient((String) arg);								
      }
    }
}