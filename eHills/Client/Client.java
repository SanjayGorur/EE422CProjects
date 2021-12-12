// Copy-paste this file at the top of every file you turn in.
/*
* EE422C Final Project submission by
* Replace <...> with your actual data.
* <Sanjay Gorur>
* <sg52879>
* <17805>
* Fall 2021
*/

package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.logging.*;

import javax.swing.JPasswordField;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class Client extends Application 
{  
	private PrintWriter writer = null; 
	private BufferedReader reader = null;
	
	public String username; 
	public String password;
	
	public boolean checkLoginServer; 
	public boolean checkLoginClient;
	
	private Socket clientSocket;
	private Thread clientInAuction; 
	
	private LoginWindow loginPhase; 
	private Client user; 
	
	public static ObservableList<LoginWindow.ClientProduct> auctionHouse; // This is weird, but explained in the classes' description(s).
	
	/*
	 * main method essentially launches the JavaFX application.
	 */	
	public static void main(String[] args) 
	{
		launch(args);
	}
	
	/*
	 * Constructor accounts for client not logging in yet.
	 */	
	public Client() 
	{
		checkLoginServer = false; 
		checkLoginClient = false;
	}
	
	/*
	 * Starts application for Client GUI
	 * @param - userWindow --> stage for client window to use
	 */
	@Override
	public void start(Stage userWindow) 
	{	
		user = new Client(); 
		
		try 
		{
			user.connectToServer(); 
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		
		LoginWindow initialDisplay = new LoginWindow(); 
		loginPhase = initialDisplay; 
		
		loginPhase.showLogin(userWindow, user); 
	}
	
	/*
	 * enforces client to connect to server
	 * throws Exception, will be accounted for in console.
	 */	
	private void connectToServer() throws Exception 
	{ 
		int port = 8888; 
		clientSocket = new Socket("localhost", port); 
		reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
		writer = new PrintWriter(clientSocket.getOutputStream()); 
		
		clientInAuction = new Thread(new Runnable() 
		{
	      @Override
	      public void run() {
	        String readServer;
	        try 
	        {
	          while ((readServer = reader.readLine()) != null) 
	          {
	            getMessages(readServer);
	          }
	        } 
	        catch(IOException e)
	        {
	        	//e.printStackTrace();
	        }
	        catch (Exception e) 
	        {
	        	//e.printStackTrace();
	        }
	      }
	    });

	    clientInAuction.start();
	}
	
	/*
	 * Reading messages from server
	 * @param - serverMsg --> message from Server
	 */
	public void getMessages(String serverMsg) 
	{  
		Gson gson = new Gson();
		ClientResponse serverMessage = gson.fromJson(serverMsg, ClientResponse.class);
		try 
		{
			if(serverMessage.getResponse().equals("login"))
			{
				if(serverMessage.getLog()) 
				{
					username = serverMessage.name;
					checkLoginServer = true;
					checkLoginClient = true; 
					sendToServer("{ response: 'getItems', name: '" + username + "'}");
				}
				else 
				{
					this.checkLoginServer = false; 
					this.checkLoginClient = true; 
				} 					
			}
					
			else if(serverMessage.getResponse().equals("history"))		
			{ 
				try 
				{
					Platform.runLater(new Runnable() {
					    @Override
					    public void run() 
					    {
					    	if(serverMessage.getName().equals("bids") || serverMessage.getName().equals("boughts"))
					    	{
						    	String t = "";
						    	Scanner scan = new Scanner(serverMessage.getHist());
						    	while(scan.hasNext())
						    	{
						    		String keep = scan.next();
						    		try
						    		{
						    			Double.parseDouble(keep);
						    			t += keep + "\n";
						    		}
						    		catch(Exception e)
						    		{
						    			t += keep + " ";
						    		}
						    	}
								Text bidHist = new Text(t);
								bidHist.setFill(Color.BLUE);
								bidHist.setFont(Font.font("Impact", 14));
								GridPane g =  new GridPane();
								g.add(bidHist, 0, 0);
								Scene sc = new Scene(g, 400, 400);
								Stage st = new Stage();
								if(serverMessage.getName().equals("bids")) {g.setStyle("-fx-background-color: brown"); st.setTitle("Bid History");}
								else if(serverMessage.getName().equals("boughts")) {g.setStyle("-fx-background-color: black"); st.setTitle("Buy History");}
								st.setScene(sc);
								st.show();					    		
					    	}
					    	else if(serverMessage.getName().equals("Userdoesnotexist"))
					    	{
								Text bidHist = new Text("User does not exist");
								bidHist.setFill(Color.RED);
								bidHist.setFont(Font.font("Impact", 15));
								GridPane g =  new GridPane();
								g.add(bidHist, 0, 0);
								Scene sc = new Scene(g, 128, 88);
								Stage st = new Stage();
								g.setStyle("-fx-background-color: teal");
								st.setScene(sc);
								st.setTitle("Uh Oh!");
								st.show();					    		
					    	}
					    	else
					    	{
						    	String s = serverMessage.getInput();
						    	String t = "";
						    	Scanner scan = new Scanner(s);
						    	while(scan.hasNext())
						    	{
						    		String keep = scan.next();
						    		if(keep.equals("."))
						    		{
						    			t += "\n\n";
						    		}
						    		else
						    		{
						    			t += keep + " ";
						    		}
						    	}
								Text newHist = new Text(t);
								newHist.setFill(Color.ORANGE);
								newHist.setFont(Font.font("Impact", 14));
								GridPane g =  new GridPane();
								g.add(newHist, 0, 0);
								Scene sc = new Scene(g, 1000, 280);
								Stage st = new Stage();
								g.setStyle("-fx-background-color: indigo");
								st.setScene(sc);
								st.setTitle("Customer " + serverMessage.getName() + ";s History:");
								st.show();						    		
					    	}
					    }
					});
				} 
				catch(Exception e) 
				{
					e.printStackTrace();
				}
				return; 
			} 
			
			
			else if(serverMessage.getResponse().equals("getItems"))
			{
				List<LoginWindow.ClientProduct> jsonList = new Gson().fromJson(serverMessage.getInput(), new TypeToken<List<LoginWindow.ClientProduct>>() {}.getType()); 
				auctionHouse = FXCollections.observableArrayList(jsonList);
				return; 
			}

			else if(serverMessage.getResponse().equals("bid"))
			{
				for(LoginWindow.ClientProduct item: auctionHouse) 
				{
					if(item.getName().equals(serverMessage.getInput())) 
					{
						int place = auctionHouse.indexOf(item); 
						
						item.setBidsLeft(serverMessage.getAmount());
						item.setHighestBidder(serverMessage.getName());
						item.setBidPrice((double) serverMessage.currPrice);
						if(item.getBuyPrice() <=  item.getBidPrice()) 
						{
							item.setAuction("Ended");
						}
						else 
						{
							item.setAuction("Ongoing");
						}
						auctionHouse.set(place, item); 
					}
				}
					
				return;
			}
			
			else if(serverMessage.getResponse().equals("error"))
			{
				
			}
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		
	}
	
	/*
	 * client sends message to server for information.
	 * @param - string --> message to send to server.
	 */		
	public void sendToServer(String string) 
	{
	    writer.println(string);
	    writer.flush();
	  }
	
	/*
	 * if client clicks logout button, he quits the program.
	 */		
	public void quit() throws IOException 
	{
		try
		{
			clientInAuction.stop();
			clientSocket.close();
			System.exit(0);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * The ClientResponse class evaluates a JSON string message response from the server.
	 */	
	class ClientResponse 
	{
		  String response;
		  boolean loggedIn; 
		  String name; 
		  String password; 
		  String input;
		  double currPrice;
		  double bid; 
		  int amount;
		  String hist;
	
		  public ClientResponse() 
		  {
		    this.response = "";
		    this.loggedIn = false; 
		    this.name = "guest"; 
			this.password = ""; 
		    this.input = "";
		    this.currPrice= 0.0;
		    this.bid = 0.0;
		    this.amount  = 0;
		    this.hist = "";
		  }
	
		  public ClientResponse(String response, boolean loggedIn, String name, String password, String input, double number, double bid, int amount, String hist) 
		  {
		    this.response = response;
		    this.loggedIn = loggedIn;
		    this.name = name; 
		    this.password = password;
		    this.input = input;
		    this.currPrice = number;
		    this.bid = bid; 
		    this.amount = amount;
		    this.hist = hist;
		  }
		  
		  public String getResponse()
		  {
			  return response;
		  }
		  
		  public boolean getLog()
		  {
			  return this.loggedIn;
		  }
		  
		  public String getName()
		  {
			  return name;
		  }
		  
		  public String getInput()
		  {
			  return input;
		  }
		  
		  public int getAmount()
		  {
			  return amount;
		  }
		  
		  public String getHist()
		  {
			  return hist;
		  }
		  
		  public String getPassword()
		  {
			  return password;
		  }
	}
	
	/*
	 * The LoginWindow class acts as the main frame for the login stage of the client application.
	 * It incorporates the ClientProduct class for each Client for purposes of simplicity.
	 */	
	class LoginWindow 
	{	
		private Stage primaryStage; 
		private Client client; 
		
		public TextField username; 
		public TextField password;
		private Text logError = new Text("Please try again");
		
		public boolean checkLoginServer; 
		public boolean checkLoginClient; 
		
		public void showLogin(Stage primary, Client cli) 
		{
			client = cli; 
			primaryStage = primary; 
						
			Text userTxt = new Text("Username: ");
			Text passTxt = new Text("Password: ");
			userTxt.setStyle("-fx-fill: pink");
			passTxt.setStyle("-fx-fill: pink");
			userTxt.setFont(Font.font("Impact", 15));
			passTxt.setFont(Font.font("Impact", 15));
			username = new TextField();
			username.setMaxWidth(98);
			PasswordField temp = new PasswordField();
			temp.setMaxWidth(98);
			password = new TextField();
			Button loginBtn = new Button("Login");
			loginBtn.setAlignment(Pos.CENTER);
			Button guestBtn = new Button("Guest Login"); 
			//Button createAcc =  new Button("Create Account");
			
			logError.setVisible(false);
			
			// Attempt to log in with credentials entered in username and password boxes
			loginBtn.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					try 
					{
						client.sendToServer("{ response: 'login', name: '" + username.getText() + "', password: '" + temp.getText() + "'}");
						client.checkLoginClient = false; 
						while(client.checkLoginClient == false) 
						{
							System.out.print("");
						}
						
						if(client.checkLoginServer) 
						{
							client.username = username.getText();
							client.password = temp.getText();
							showMarket(); 
						}
						else 
						{
							loginError(); 
						}
										
					} 
					catch(Exception e) 
					{ 
						e.printStackTrace();
					}	
				}
			});
			
			guestBtn.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override 
				public void handle(ActionEvent event)
				{
					try 
					{
						client.sendToServer("{ response: 'login', name: 'guest'}");
						client.checkLoginClient = false; 
						while(client.checkLoginClient == false) 
						{
							System.out.print("");
						}
						
						if(client.checkLoginServer) 
						{
							client.username = "guest";
							client.password = "";
							showMarket(); 
						}
						else 
						{
							loginError(); 
						}
										
					} 
					catch(Exception e) 
					{
						e.printStackTrace();
					}					
				}
			});
			
			GridPane login = new GridPane();
			login.setMinSize(280,180); 
			login.setPadding(new Insets(1, 1, 1, 1));
			login.setVgap(1);
			login.setHgap(28);
			login.setAlignment(Pos.CENTER);
			
			login.add(userTxt, 1, 0);
			login.add(username, 2, 0);
			login.add(passTxt, 1, 1);
			login.add(temp, 2, 1);
			login.add(guestBtn, 1, 3);
			login.add(loginBtn, 2, 3);
			login.add(logError, 2, 4);
			//login.addRow(3, guestBtn);
			//login.addRow(3, loginBtn);
			
			login.setStyle("-fx-background-image: "
					+ "url('https://knowtechie.com/wp-content/uploads/2018/04/amazon-penis.jpg'); "
					+ "-fx-background-repeat: no-repeat; "
					+ "-fx-background-size: 280 180; "
					+ "-fx-background-position: center center;");
			// Create a scene and place it in the stage 
			Scene scene = new Scene(login);
			primaryStage.setScene(scene); //set the scene
			primaryStage.setTitle("Amazon Gorur");
			primaryStage.show(); //display the result
		}
		
		private void loginError() 
		{
			logError.setFill(Color.RED); 
			logError.setVisible(true);
		}
		
		@SuppressWarnings("rawtypes")
		public void showMarket() 
		{
			GridPane background = new GridPane(); 
			background.setPadding(new Insets(8, 8, 8, 8));
			background.setVgap(5);
			background.setHgap(3);
			background.setAlignment(Pos.CENTER);
			
			TableView<ClientProduct> eHills = new TableView<ClientProduct>(); 
			eHills.setEditable(true);
			eHills.setMinSize(600, 88);
			
			TableColumn<ClientProduct, String> prodNameCol = new TableColumn<ClientProduct, String>("Product"); 
			prodNameCol.setCellValueFactory(
					new PropertyValueFactory<ClientProduct, String>("name"));
			
			TableColumn<ClientProduct, String> prodDetCol = new TableColumn<ClientProduct, String>("Details"); 
			prodDetCol.setCellValueFactory(
					new PropertyValueFactory<ClientProduct, String>("details"));
			prodDetCol.setMaxWidth(288);
			
			TableColumn<ClientProduct, String> bidCol = new TableColumn<ClientProduct, String>("Bid Price"); 
			bidCol.setCellValueFactory(
					new PropertyValueFactory<ClientProduct, String>("bidPrice"));
			
			TableColumn<ClientProduct, String> trackerCol = new TableColumn<ClientProduct, String>("Tracker"); 
			trackerCol.setCellValueFactory(
					new PropertyValueFactory<ClientProduct, String>("auction"));
			trackerCol.setMaxWidth(128);
			
			TableColumn<ClientProduct, String> salesCol = new TableColumn<ClientProduct, String>("Buy Price"); 
			salesCol.setCellValueFactory(
					new PropertyValueFactory<ClientProduct, String>("buyPrice"));
			
			TableColumn<ClientProduct, String> historyCol = new TableColumn<ClientProduct, String>("Your Bids"); 
			historyCol.setCellValueFactory(
					new PropertyValueFactory<ClientProduct, String>("history"));
			
			TableColumn<ClientProduct, String> highBidCol = new TableColumn<ClientProduct, String>("High Bidder"); 
			highBidCol.setCellValueFactory(
					new PropertyValueFactory<ClientProduct, String>("highestBidder"));
			
			TableColumn<ClientProduct, String> numBidsCol = new TableColumn<ClientProduct, String>("Bids Left"); 
			numBidsCol.setCellValueFactory(
					new PropertyValueFactory<ClientProduct, String>("bidsLeft"));
			
			eHills.setItems(client.auctionHouse);
			eHills.getColumns().addAll(prodNameCol, prodDetCol, bidCol, salesCol, trackerCol, numBidsCol, historyCol, highBidCol);
			
			
			// Control Panel
			GridPane control = new GridPane(); 
			
			Image image = new Image("File:src/gavel.png");
			ImageView pic = new ImageView();
			pic.setFitWidth(280);
			pic.setFitHeight(80);
			pic.setImage(image);
			
			Text selectedItemText = new Text("Select an Item: "); 
			Text selectedItemPrice = new Text("None");
			Font font = Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 14);
			selectedItemText.setFont(font);
			selectedItemPrice.setFont(font);
			selectedItemText.setFill(Color.WHITE);
			selectedItemPrice.setFill(Color.WHITE);
			Text bidErrorText = new Text(""); 
			bidErrorText.setFont(Font.font("Impact", 18));
			bidErrorText.setFill(Color.PINK);
			
			Text bidVal = new Text("Bid Amount: ");
			bidVal.setFont(font);
			bidVal.setFill(Color.WHITE);
			Text selectClients =  new Text("Clients: ");
			selectClients.setFont(font);
			selectClients.setFill(Color.WHITE);
			TextField bidInput = new TextField(); 
			
			Button getClient = new Button("Search");
			Button sendBid = new Button("Bid or Buy");
			
			Button history = new Button("Bid History");
			Button buyHist = new Button("Buy History");
			
			Button logout = new Button("Log Out"); 
			
			TextField clients = new TextField();
			clients.setMaxWidth(88);
			
			control.setHgap(8);
			control.setVgap(18);
			
			control.add(bidVal, 0, 0);
			control.add(bidInput, 1, 0);
			control.add(sendBid, 1, 1);
			
			control.add(selectedItemText, 0, 4);
			control.add(selectedItemPrice, 1, 4);
			
			control.add(selectClients, 0, 5);
			control.add(clients, 1, 5);
			control.add(getClient, 2, 5);
			
			control.add(bidErrorText, 0, 3);
			control.add(logout, 2, 6);
			
			control.add(history, 1, 6);
			control.add(buyHist, 0, 6);
			
			background.add(eHills, 0, 0);
			background.add(control, 1, 0);
			background.add(pic, 1, 1);
			
			getClient.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					client.sendToServer("{ response: 'history', name: '" + clients.getText() + "'}");
				}
			});
			
			history.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					client.sendToServer("{ response: 'history', name: 'allBids'}");
				}
			});
			
			buyHist.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					client.sendToServer("{ response: 'history', name: 'allBoughts'}");
				}
			});
			
			logout.setOnAction(event ->{
				try 
				{
					client.quit();
					Thread.sleep(500);
					System.exit(0);
				}
				catch(IOException e)
				{
					//e.printStackTrace();
				}
				catch(Exception e) 
				{
					//e.printStackTrace();
				}
			});
			
			sendBid.setOnAction(event ->{
				try 
				{
					if(selectedItemText.getText().equals("Select an Item: ")) {throw new Exception();}
					for(ClientProduct product: client.auctionHouse) 
					{
						if (selectedItemText.getText().equals(product.getName())) 
						{
							if(product.getAuction().equals("Ended") || product.getBidsLeft() <= 0) 
							{
								bidErrorText.setText("Auction ended");
							}
							
							else if(Double.parseDouble(bidInput.getText()) <= product.getBidPrice()) 
							{
								bidErrorText.setText("Bid too low");
							}
							
							else if(bidInput.getText().equals("") || bidInput.getText().equals(null))
							{
								throw new Exception();
							}
							
							else if(Double.parseDouble(bidInput.getText()) > product.getBidPrice()) 
							{
								String musicFile = "src/ping.mp3";

								Media sound = new Media(new File(musicFile).toURI().toString());
								MediaPlayer mediaPlayer = new MediaPlayer(sound);
								mediaPlayer.play();
								
								bidErrorText.setText("");
								auctionHouse.get(auctionHouse.indexOf(product)).setHistory(Double.parseDouble(bidInput.getText()));
							}
							
							else
							{
								throw new Exception();
							}
						}
					}
					client.sendToServer("{ response: 'bid', name: '" + client.username + "', input: '" + selectedItemText.getText() + "', bid: '" + bidInput.getText() + "' }");
					bidInput.clear();	
				}
				catch(Exception e) 
				{
					bidErrorText.setText("Invalid Entry");
				}
			});
			
			eHills.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() 
			{
				@Override
			    public void changed(ObservableValue observableValue, Object oldValue, Object newValue) 
				{
			        //Check whether item is selected and set value of selected item to Label
			        if(eHills.getSelectionModel().getSelectedItem() != null) 
			        {    
			           TableViewSelectionModel selectionModel = eHills.getSelectionModel();
			           ClientProduct selectedItem = (ClientProduct) selectionModel.getSelectedItem();

			           selectedItemText.setText(selectedItem.getName());
			           selectedItemPrice.setText(String.valueOf(selectedItem.getBidPrice()));
			         }
		         }
			});
			
			
			// Create a scene and place it in the stage 
			background.setStyle("-fx-background-image: "
					+ "url('https://images.unsplash.com/photo-1478428036186-d435e23988ea?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MXwxMTEyODQ1fHxlbnwwfHx8fA%3D%3D&w=1000&q=80'); ");
			Scene market = new Scene(background, 1089, 538);
			primaryStage.setTitle("Amazon Gorur Is Now Open: Welcome " + client.username + "!");
			primaryStage.setScene(market); //set the scene
			
			primaryStage.show(); //display the result
		}	
		
		/*
		 * The ClientProduct class keeps track of each item for each client.
		 * It's included in the LoginWindow class to act as a pathway from the login stage to the auction/main stage.
		 */
		public class ClientProduct 
		{		
			private String name;
			private int bidsLeft;
			private String highestBidder;
			private String details; 
		    private double bidPrice;
		    private double buyPrice; 
		    private String auction;
		    private ArrayList<Double> history;

		    public ClientProduct() 
		    {
		    	this.name = ""; 
		    	this.bidsLeft = 0;
		    	this.highestBidder = "";
		    	this.details = ""; 
		    	this.bidPrice = 0.0; 
		    	this.buyPrice = 5.0; 
		    	this.auction = ""; 
		    	history = new ArrayList<Double>();
		    }

		    public ClientProduct(String name, int bidsLeft, String highestBidder, String details, double bidPrice, double buyPrice, String auction) 
		    {
		        this.name = name;
		        this.bidsLeft = bidsLeft;
		        this.highestBidder = highestBidder;
		        this.details = details;
		        this.bidPrice = bidPrice;
		        this.buyPrice = buyPrice; 
		        this.auction = auction; 
		        history = new ArrayList<Double>();
		    }

		    public String getName() 
		    {
		    	return this.name; 
		    }
		    
		    public int getBidsLeft()
		    {
		    	return this.bidsLeft;
		    }
		    
		    public String getDetails() 
		    {
		    	return this.details;
		    }
		    
		    public String getHighestBidder()
		    {
		    	return this.highestBidder;
		    }
		    
		    public double getBidPrice() 
		    {
		    	return this.bidPrice;
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
		    	
		    public String toString() 
		    {
		    	return(this.name + ", " + this.details + ", " + this.auction);
		    }

			public double getBuyPrice() 
			{
				return this.buyPrice;
			}

			public void setBuyPrice(double buyPrice) 
			{
				this.buyPrice = buyPrice;
			}
			
			public void setHighestBidder(String highestBidder)
			{
				this.highestBidder = highestBidder;
			}
			
			public void setBidsLeft(int bidsLeft)
			{
				this.bidsLeft = bidsLeft;
			}
			
			public String getAuction() 
			{
				return auction; 
			}
			
			public Button getHistory()
			{
				Button hist =  new Button("Check");
				
				hist.setOnAction(new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						String getAll = "";
						if(history != null && history.size() > 0)
						{
							Iterator it = history.iterator();
							int time = 1;
							while(it.hasNext())
							{
								getAll += "Bid " + time + ": " + it.next() + "\n";
								time++;
							}
						}
						else
						{
							getAll = "No history available";
						}
						Text resp = new Text(getAll);
						resp.setFont(Font.font("Verdana", 14));
						GridPane gp = new GridPane();
						gp.add(resp, 1, 1);
						gp.setStyle("-fx-background-color: cyan");
						Scene sc = new Scene(gp, 300, 300);
						sc.setFill(Color.ORANGE);
						Stage histStage = new Stage();
						histStage.setScene(sc);
						histStage.setTitle("Your Bids: " + name);
						histStage.show();
					}
				});
				
				return hist;
			}
			
			public void setAuction(String auction) 
			{
				this.auction = auction; 
			}
			
			public void setHistory(double bid)
			{
				if(this.history == null) {this.history = new ArrayList<Double>();}		
				this.history.add(bid);
			}
			
		}
	}
}