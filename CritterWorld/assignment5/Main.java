/*
 * CRITTERS Point.java
 * EE422C Project 5 submission by
 * Replace <...> with your actual data.
 * <Sanjay Gorur>
 * <sg52879>
 * <17805>
 * Keshav Narasimhan
 * kn9558
 * 17805
 * Fall 2021
 */
/*
   Describe here known bugs or issues in this file. If your issue spans multiple
   files, or you are not sure about details, add comments to the README.txt file.
 */
package assignment5;

import java.io.File;
import java.io.IOException;
//import java.lang.module.Configuration;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.Reflection;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Main extends Application {
	
	private Stage stage;
	private boolean runAnimate = true;
	private static final int WIDTH_SIZE = 800;
	private static final int HEIGHT_SIZE = 600;
	final static int BOX_SIZE_WIDTH = (Params.WORLD_WIDTH > 80) ? 10 : (WIDTH_SIZE - 50) / Params.WORLD_WIDTH;
	final static  int BOX_SIZE_HEIGHT = (Params.WORLD_HEIGHT > 60) ? 10 : (HEIGHT_SIZE - 50) / Params.WORLD_HEIGHT;
	static AnimationTimer timer;
	static AnimationTimer timer2;
	static boolean runningAnimation = false;

    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Adds files to ArrayList if file is a directory
     * 
     * @param - f: directory to add files from to list.
     * @param - files: arraylist in which to add files from directory.  
     */    
    private static void checkDirectory(File f, ArrayList<File> files)
    {
    	if(f.isDirectory())
    	{
    		File[] newFiles = f.listFiles();
    		for(int i = 0; i < newFiles.length; i++)
    		{
    			if(newFiles[i].isDirectory())
    			{
    				checkDirectory(newFiles[i], files);
    			}
    			else if(newFiles[i].isFile())
    			{
    				files.add(newFiles[i]);
    			}
    		}
    	}
    }
    
    /**
     * start() method is used to run the JavaFX program
     * 
     * @param - primaryStage: the main stage used for the program.
     * 			We chose to denote the primary stage for the world simulation. 
     */
	@Override
	public void start(Stage primaryStage) 
	{
		// TODO Auto-generated method stub	
		File root = new File("./src/assignment5");
		File[] files = root.listFiles();
		
		ArrayList<File> trueFiles =  new ArrayList<File>();
		for(int i = 0; i < files.length; i++)
		{
			if(files[i].isDirectory())
			{
				checkDirectory(files[i], trueFiles);
			}
			else if(files[i].isFile())
			{
				trueFiles.add(files[i]);
			}
		}
		
		Button view = new Button("Display"); // Used later
		
		ArrayList<Class> subTypes = new ArrayList<Class>();
		for(File f: trueFiles)
		{
			//System.out.println(f.getName());
			if(f.getName().substring(f.getName().length() - 5).equals(".java") || f.getName().substring(f.getName().length() - 6).equals(".class"))
			{
				String checkIfCritter = (f.getName().substring(f.getName().length() - 5).equals(".java")) ? f.getName().substring(0, f.getName().length() - 5) : f.getName().substring(0, f.getName().length() - 6);
				Class subCritter = null;
				try
				{
					subCritter = Class.forName("assignment5." + checkIfCritter);
				}
				catch(ClassNotFoundException e)
				{
					
				}
				if(subCritter != null && Critter.class.isAssignableFrom(subCritter) && !(Critter.class.equals(subCritter)) && !(subTypes.contains(subCritter)))
				{
					try
					{
						Object o = subCritter.newInstance();
						subTypes.add(subCritter);
					}
					catch(Exception e)
					{
						
					}
				}
			}
		}
		
		Text checkError = new Text();
		
		stage = primaryStage;
		stage.setTitle("Critter Simulation");
		GridPane pane = new GridPane();
		ScrollPane scroll = new ScrollPane(pane);
		scroll.setFitToWidth(true);
		scroll.setFitToHeight(true);
		Background paneBckgrnd = new Background(new BackgroundFill [] {
				new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)
		});
		pane.setBackground(paneBckgrnd);
		
		// create secondary pane for buttons, additional U
		GridPane buttonPane = new GridPane();
		
		// create a third pane for stats
		GridPane statsPane = new GridPane();
		Background buttonpaneBckgrnd = new Background(new BackgroundFill [] {
				new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)
		});
		buttonPane.setBackground(buttonpaneBckgrnd);

		// runStats --> for specific Critter.
		Text stats = new Text("  Check critter's stats: ");
		stats.setFont(Font.font("Verdana", FontPosture.ITALIC,  12));
		ComboBox critterStats = new ComboBox();
		critterStats.setPromptText("Select Critter");
		for(int i = 0; i < subTypes.size(); i++)
		{
			critterStats.getItems().add(subTypes.get(i).getName().substring("assignment5.".length()));
		}
		TextField diffCritter = new TextField();
		diffCritter.setPromptText("Enter another Critter");
		Button runCritter = new Button("Run Stats");
		runCritter.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				checkError.setText("");
                String className = "";
                try
                {
                	statsPane.getChildren().clear();
                    className = (String) critterStats.getValue();
                    Class c = Class.forName("assignment5." + className);
                    Object obj = c.newInstance();
                    Method getStats = c.getMethod("runStats", List.class);
                    String response = (String) getStats.invoke(obj, Critter.getInstances(className));
					Text printStats = new Text(response);
					statsPane.add(printStats, 1, 1);
                }
                catch(InvalidCritterException e)
                {
					Text printStats = new Text("error processing: stats " + (String) critterStats.getValue());
					checkError.setText("error processing: stats " + (String) critterStats.getValue());
					checkError.setFill(Color.RED);
					statsPane.add(printStats, 1, 1);
                }
                catch(Exception | NoClassDefFoundError e)
                {
					Text printStats = new Text("error processing: stats " + (String) critterStats.getValue());
					checkError.setText("error processing: stats " + (String) critterStats.getValue());
					checkError.setFill(Color.RED);
					statsPane.add(printStats, 1, 1);
                }
			}
		});
		
		// Set seed
		Text seed = new Text("  Set a new seed: ");
		seed.setFont(Font.font("Verdana", FontPosture.ITALIC,  12));
		TextField getSeed = new TextField();
		getSeed.setPromptText("Enter a number");
		Button setSeed =  new Button("Set");
		setSeed.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				checkError.setText("");
                int count = 0;
                try
                {
                   count = Integer.parseInt(getSeed.getText());
                   if(count <= 0)
                   {
                	   throw new Exception();
                   }
                   Critter.setSeed(count);
                }
                catch(IllegalFormatException e)
                {
                    System.out.println("error processing: seed " + getSeed.getText());
                    checkError.setText("error processing: seed " + getSeed.getText());
                    checkError.setFill(Color.RED);
                }
                catch(Exception e)
                {
                	System.out.println("error processing: seed " + getSeed.getText());
                	checkError.setText("error processing: seed " + getSeed.getText());
                	checkError.setFill(Color.RED);
                }
            }
		}
	);
		
		// create Critter(s)
		Text create = new Text("  Create a critter: ");
		create.setFont(Font.font("Verdana", FontPosture.ITALIC,  12));
		ComboBox selectCritter = new ComboBox();
		selectCritter.setPromptText("Select Critter");
		for(int i = 0; i < subTypes.size(); i++)
		{
			selectCritter.getItems().add(subTypes.get(i).getName().substring("assignment5.".length()));
		}
		TextField anotherCritter = new TextField();
		anotherCritter.setPromptText("Enter another Critter");
		TextField numCritter = new TextField();
		numCritter.setPromptText("How many Critters?");
		Button createCritter = new Button("Create");
		createCritter.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				 checkError.setText("");
	             String type = "";
	             try 
	             {
	                 type = (String) selectCritter.getValue();
	             }
	             catch(Exception e)
	             {
	                 System.out.println("error processing: create " + selectCritter.getValue());
	                 checkError.setText("error processing: create " + selectCritter.getValue());
	                 checkError.setFill(Color.RED);
	             }
	             int count = 0;
	             try
	             {
	                count = Integer.parseInt(numCritter.getText());
	             }
	             catch(Exception e)
	             {
	                   System.out.println("error processing: create " + selectCritter.getValue() + " " + numCritter.getText());
	                   checkError.setText("error processing: create " + selectCritter.getValue() + " " + numCritter.getText());
	                   checkError.setFill(Color.RED);
	             }
	             while(count > 0)
	             {
	                 try 
	                 {
	                    Critter.createCritter(type);
	                    String className = "";
	                    if(critterStats.getValue() != null)
	                    {
		                  	statsPane.getChildren().clear();
		                    className = (String) critterStats.getValue();
		                    Class c = Class.forName("assignment5." + className);
		                    Object obj = c.newInstance();
		                    Method getStats = c.getMethod("runStats", List.class);
		                    String response = (String) getStats.invoke(obj, Critter.getInstances(className));
							Text printStats = new Text(response);	
							statsPane.add(printStats, 1, 1);
	                    }
	                 }
	                 catch (InvalidCritterException e) 
	                 {
	                	   System.out.println("error processing: create " + selectCritter.getValue() + " " + numCritter.getText());
	                	   checkError.setText("error processing: create " + selectCritter.getValue() + " " + numCritter.getText());
	                	   checkError.setFill(Color.RED);
	                       break;
	                 }
	                 catch (Exception e) 
	                 {
	                	   System.out.println("error processing: create " + selectCritter.getValue() + " " + count);
	                	   checkError.setText("error processing: create " + selectCritter.getValue() + " " + count);
	                	   checkError.setFill(Color.RED);
	                       break;
	                 }
	                 count--;
	             }
	             Critter.displayWorld(pane);
            }
		});
		
		// step function; progress world 
		Text step = new Text("  World steps: ");
		step.setFont(Font.font("Verdana", FontPosture.ITALIC,  12));
		TextField numSteps =  new TextField();
		numSteps.setPromptText("Enter world steps");
		Button runSteps =  new Button("Run World");
		runSteps.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				checkError.setText("");
	            int count = 0;
	            try
	            {
	               count = Integer.parseInt(numSteps.getText());
	               if(count <= 0)
	               {
	            	   throw new Exception();
	               }
	            }
	            catch(IllegalFormatException e)
	            {
	                    System.out.println("error processing: step " + numSteps.getText());
	                    checkError.setText("error processing: step " + numSteps.getText());
	                    checkError.setFill(Color.RED);
	            }
	            catch(Exception e)
	            {
	                    System.out.println("error processing: step " + numSteps.getText());
	                    checkError.setText("error processing: step " + numSteps.getText());
	                    checkError.setFill(Color.RED);
	            }
	            while(count > 0)
	            {
	            	try
	            	{
	            		Critter.worldTimeStep();
	                    String className = "";
	                    if(critterStats.getValue() != null)
	                    {
		                  	statsPane.getChildren().clear();
		                    className = (String) critterStats.getValue();
		                    Class c = Class.forName("assignment5." + className);
		                    Object obj = c.newInstance();
		                    Method getStats = c.getMethod("runStats", List.class);
		                    String response = (String) getStats.invoke(obj, Critter.getInstances(className));
							Text printStats = new Text(response);	
							statsPane.add(printStats, 1, 1);
	                    }
	            	}
	            	catch(Exception e)
	            	{
	            		System.out.println("error processing: step " + numSteps.getText());
	            		checkError.setText("error processing: step " + numSteps.getText());
	            		checkError.setFill(Color.RED);
	            	}
	            	count--;
	            }	
	            
	            Critter.displayWorld(pane);
			}
		});
		
		// quit Game
		Text quit =  new Text("  Quit the game: ");
		quit.setFont(Font.font("Verdana", FontPosture.ITALIC,  12));
		Button quitGame = new Button("Quit");
		quitGame.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) 
			{
				checkError.setText("");
				System.exit(0);
				// return instead?
			}
			
		});
		
		// Clear the grid
		Text clear = new Text("  Clear the grid: ");
		clear.setFont(Font.font("Verdana", FontPosture.ITALIC,  12));		
		Button erase = new Button("Clear");
		erase.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					checkError.setText("");
                    String className = "";
					Critter.clearWorld();
                    try
                    {
                        if(critterStats.getValue() != null)
                        {
    	                  	statsPane.getChildren().clear();
    	                    className = (String) critterStats.getValue();
    	                    Class c = Class.forName("assignment5." + className);
    	                    Object obj = c.newInstance();
    	                    Method getStats = c.getMethod("runStats", List.class);
    	                    String response = (String) getStats.invoke(obj, Critter.getInstances(className));
    						Text printStats = new Text(response);	
    						statsPane.add(printStats, 1, 1);
                        }
                    }
                    catch(Exception e)
                    {
                    	
                    }
					pane.getChildren().clear();
					Critter.displayWorld(pane);
				}
			}
		);
		
		// Animation --> progress specific number of steps in world.
		Text animation = new Text("  Animation: ");
		animation.setFont(Font.font("Verdana", FontPosture.ITALIC,  12));	
		Slider slide = new Slider();
		slide.setPrefWidth(88);
		slide.setMin(1);
		slide.setMax(10);
		slide.setShowTickLabels(true);
		slide.setShowTickMarks(true);
		slide.setBlockIncrement(1);
		slide.setMinorTickCount(1);
		slide.setMajorTickUnit(5);
		Button runAnimation = new Button("Run");
		Button stopAnimation = new Button("Stop");
		
		// stop the animation
		stopAnimation.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) 
			{
				checkError.setText("");
				if (runningAnimation) 
				{
					timer2.stop();
					runningAnimation = false;
					//view.setVisible(true);
					setSeed.setVisible(true);
					createCritter.setVisible(true);
					runCritter.setVisible(true);
					quitGame.setVisible(true);
					erase.setVisible(true);
					runSteps.setVisible(true);
				} else {
					// do nothing
				}
			}
			
		});
		
		// run the animation
		runAnimation.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				checkError.setText("");
				int times = (int) slide.getValue();
				int count = times;
				int numTimes = 0;
				
				if (!runningAnimation) 
				{
					timer2 = new Animation(times, times, pane, statsPane, subTypes);
					timer2.start();
					runningAnimation = true;
					view.setVisible(false);
					setSeed.setVisible(false);
					createCritter.setVisible(false);
					runCritter.setVisible(false);
					quitGame.setVisible(false);
					erase.setVisible(false);
					runSteps.setVisible(false);
				}
			}
		});
		
		// zoom functionality on main pane --> 'primaryStage'
		pane.setOnScroll(new EventHandler<ScrollEvent>()
		{
			@Override
			public void handle(ScrollEvent event)
			{
				double zoomFactor = 1.04;
				double checkChange = event.getDeltaY();
				
				if(checkChange < 0)
				{
					zoomFactor -= 0.08;
				}
				
				if(!(zoomFactor <= 1 && (pane.getScaleX() <= 1 || pane.getScaleY() <= 1)))
				{
					//System.out.println(zoomFactor + "\t" + pane.getScaleX() + "\t" + pane.getScaleY());
					pane.setScaleX(pane.getScaleX() * zoomFactor);
					pane.setScaleY(pane.getScaleY() * zoomFactor);
					event.consume();				
				}
			}
		}
		);
		
		// display functionality 
		Text title = new Text(" Welcome to Critters Part II");
		title.setFont(Font.font("Verdana", FontWeight.BOLD,  23));
		
		Text display = new Text("  Display the current grid: ");
		display.setFont(Font.font("Verdana", FontPosture.ITALIC,  12));		
		
		view.setPrefWidth(88);
		Text start = new Text("Press Display to get started");
		start.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 48));
		pane.add(start, 2, 1);
		start.setY(300);
		view.setVisible(true);
		setSeed.setVisible(false);
		createCritter.setVisible(false);
		runCritter.setVisible(false);
		quitGame.setVisible(false);
		erase.setVisible(false);
		runSteps.setVisible(false);
		stopAnimation.setVisible(false);
		runAnimation.setVisible(false);
		view.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					checkError.setText("");
					start.setVisible(false);
					view.setVisible(false);
					pane.getChildren().remove(start);
					setSeed.setVisible(true);
					createCritter.setVisible(true);
					runCritter.setVisible(true);
					quitGame.setVisible(true);
					erase.setVisible(true);
					runSteps.setVisible(true);
					stopAnimation.setVisible(true);
					runAnimation.setVisible(true);
					Critter.displayWorld(pane);
				}
			}
		);
		
		// Images
		buttonPane.setStyle("-fx-background-image: "
				+ "url('https://ak.picdn.net/shutterstock/videos/20439940/thumb/1.jpg'); "
				+ "-fx-background-repeat: no-repeat; "
				+ "-fx-background-size: 800 600; "
				+ "-fx-background-position: center center;");
		
		statsPane.setStyle("-fx-background-image: "
				+ "url('https://mms.businesswire.com/media/20190304005328/en/546575/23/STATS.jpg'); "
				+ "-fx-background-repeat: no-repeat; "
				+ "-fx-background-size: 300 400; "
				+ "-fx-background-position: center center;");
		
		// ButtonPane display
		buttonPane.setVgap(48);
		buttonPane.setHgap(28);
		buttonPane.getRowConstraints().add(new RowConstraints(69));
		buttonPane.getColumnConstraints().add(new ColumnConstraints(182));
		
		buttonPane.add(title, 0, 0);
		
		buttonPane.add(display, 0, 1);
		buttonPane.add(view, 1, 1);
		
		buttonPane.add(seed, 0, 2);
		buttonPane.add(setSeed, 1, 2);
		buttonPane.add(getSeed, 2, 2);
		
		buttonPane.add(create, 0, 3);
		buttonPane.add(createCritter, 1, 3);
		buttonPane.add(selectCritter, 2, 3);
		//buttonPane.add(anotherCritter, 2, 3);
		buttonPane.add(numCritter, 3, 3);
		
		buttonPane.add(step, 0, 4);
		buttonPane.add(runSteps, 1, 4);
		buttonPane.add(numSteps, 2, 4);
		
		buttonPane.add(stats, 0, 5);
		buttonPane.add(runCritter, 1, 5);
		buttonPane.add(critterStats, 2, 5);
		//buttonPane.add(diffCritter, 2, 5);

		buttonPane.add(quit, 2, 1);
		buttonPane.add(quitGame, 3, 1);
		
		buttonPane.add(clear, 0, 6);
		buttonPane.add(erase, 1, 6);
		buttonPane.add(checkError, 2, 6);
		
		buttonPane.add(animation, 0, 7);
		buttonPane.add(slide, 1, 7);
		buttonPane.add(runAnimation, 2, 7);
		buttonPane.add(stopAnimation, 3, 7);
		
		// Set new Stages
		Stage thirdStage = new Stage();
		thirdStage.setX(800);
		thirdStage.setY(600);
		thirdStage.setTitle("Stats of Critter");
		
		Stage secondaryStage = new Stage();
		secondaryStage.setX(WIDTH_SIZE + 25);
		secondaryStage.setY(0);
		secondaryStage.setTitle("Buttons"); 
		
		Scene thirdScene = new Scene(statsPane, 300, 400);
		thirdStage.setScene(thirdScene);
		thirdStage.show();
		
		
		Scene secondaryScene = new Scene(buttonPane, 800, 600);
		secondaryStage.setScene(secondaryScene);
		secondaryStage.show();
		
		stage.setX(0);
		stage.setY(0);
		Scene scene = new Scene(scroll, WIDTH_SIZE, HEIGHT_SIZE);
		stage.setScene(scene); // MAIN ONE --> grid on primaryStage
		stage.show();
		
	}
	
	/**
	 * Used in conjunction with AnimationTimer to animate time steps
	 * 
	 * @param count -- number of time steps to be performed
	 * @param numSteps -- TextField in case of exceptions
	 */
	public static void runSteps(int count, TextField numSteps) {
		if (count > 0) {
        	try {
        		Critter.worldTimeStep();
        	} catch(Exception e) {
        		System.out.println("error processing: step " + numSteps.getText());
        	}
        } else {
        	timer.stop();
        }
	}
}

/**
 * Class that handles the animation for running time steps
 */
class AnimateTimer extends AnimationTimer {

	private int count;
	private TextField numSteps;
	private GridPane pane;
	private long timeNow;
	
	/**
	 * Constructor to initialize AnimateTimer objects
	 * 
	 * @param count -- number of time steps to be completed
	 * @param numSteps -- TextField in case of exceptions during time steps
	 * @param pane -- GridPane that holds the "world"
	 */
	public AnimateTimer(int count, TextField numSteps, GridPane pane) {
		this.count = count;
		this.numSteps = numSteps;
		this.pane = pane;
		timeNow = 0;
	}
	
	/**
	 * Overwritten method of AnimationTimer that handles the animation every frame
	 * 
	 * @param now -- tick speed
	 */
	@Override
	public void handle(long now) {
		// trying something to standardize animations to every 50 ms
		if (now - timeNow >= 50_000_000) {
			Main.runSteps(count, numSteps);
			Critter.displayWorld(this.pane);
			count--;
			timeNow = now;
		}
	}
	
}

class Animation extends AnimationTimer {

	private int count;
	private GridPane pane;
	private GridPane statsPane;
	private ArrayList<Class> subTypes;
	private int step;
	
	public Animation(int count, int step, GridPane pane, GridPane statsPane, ArrayList<Class> subTypes) 
	{
		this.count = count;
		this.pane = pane;
		this.step = step;
		this.statsPane = statsPane;
		this.subTypes = subTypes;
	}
	
	@Override
	public void handle(long now) 
	{
		// TODO Auto-generated method stub
		int times = count;
		while(times > 0)
		{
			Critter.worldTimeStep();
			times--;
		}
        Class c = null;
        int i = 0;
        try
        {
        	statsPane.getChildren().clear();
        	for(i = i; i < subTypes.size(); i++)
        	{
                c = subTypes.get(i);
                Object obj = c.newInstance();
                Method getStats = c.getMethod("runStats", List.class);
                String response = (String) getStats.invoke(obj, Critter.getInstances(c.getName().substring("assignment5.".length())));
    			Text printStats = new Text(response);
    			statsPane.add(printStats, 1, i);	
        	}
        }
        catch(InvalidCritterException e)
        {
			Text printStats = new Text("error processing: stats " + c.getName());
			statsPane.add(printStats, 1, i);
        }
        catch(Exception | NoClassDefFoundError e)
        {
			Text printStats = new Text("error processing: stats " + c.getName());
			statsPane.add(printStats, 1, i);
        }
		Critter.displayWorld(pane);
	}
	
}
