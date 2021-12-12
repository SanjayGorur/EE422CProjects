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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/*
 * See the PDF for descriptions of the methods and fields in this
 * class.
 * You may add fields, methods or inner classes to Critter ONLY
 * if you make your additions private; no new public, protected or
 * default-package code or data can be added to Critter.
 */

public abstract class Critter {

    /* START --- NEW FOR PROJECT 5 */
    public enum CritterShape {
        CIRCLE,
        SQUARE,
        TRIANGLE,
        DIAMOND,
        STAR
    }

    /* the default color is white, which I hope makes critters invisible by default
     * If you change the background color of your View component, then update the default
     * color to be the same as you background
     *
     * critters must override at least one of the following three methods, it is not
     * proper for critters to remain invisible in the view
     *
     * If a critter only overrides the outline color, then it will look like a non-filled
     * shape, at least, that's the intent. You can edit these default methods however you
     * need to, but please preserve that intent as you implement them.
     */
    public javafx.scene.paint.Color viewColor() {
    	// changed so we can see the critters during testing 
        return javafx.scene.paint.Color.BLUE;
    }

    // outline color of Critter; can be overwritten
    public javafx.scene.paint.Color viewOutlineColor() {
        return viewColor();
    }

    // fill color of Critter; can be overwritten
    public javafx.scene.paint.Color viewFillColor() {
        return viewColor();
    }

    // abstract method to be written by subclasses of Critter
    public abstract CritterShape viewShape();
    
    /**
     * Looks at location near the current Critter to check if 
     * that location is occupied.
     *
     * @param direction - the direction(0 to 8) for the critter to look.
     * @param steps - how many steps for critter to take to look (false = 1; true = 2)
     * @return a string either containing the type of Critter found(toString() method) or null. 
     */
    protected final String look(int direction, boolean steps) {
    	
    	/*
    	 * current position of the Critter
    	 */
    	int check_x = this.x_coord;
    	int check_y = this.y_coord;
    	
    	/*
    	 * get the location after the critter "moves"
    	 * if steps is true, then move twice in specified direction
    	 */
    	if(direction > 2 && direction < 6)
        {
            check_x--;
            if (steps) {
            	check_x--;
            }
            if (check_x < 0) {
            	check_x += Params.WORLD_WIDTH;
            }
        }
        if(direction < 2 || direction > 6)
        {
            check_x++;
            if (steps) {
            	check_x++;
            }
            if (check_x >= Params.WORLD_WIDTH) {
            	check_x -= Params.WORLD_WIDTH;
            }
        }
        if(direction > 0 && direction < 4)
        {
            check_y--;
            if (steps) {
            	check_y--;
            }
            if (check_y < 0) {
            	check_y += Params.WORLD_HEIGHT;
            }
        }
        if(direction > 4 && direction < 8)
        {
            check_y++;
            if (steps) {
            	check_y++;
            }
            if (check_y >= Params.WORLD_HEIGHT) {
            	check_y -= Params.WORLD_HEIGHT;
            }
        }
        
        energy -= Params.LOOK_ENERGY_COST;
        /*
         * check to see if the new location is occupied by any other critters
         * if so, print its symbol
         * if not, return null
         */
        Point checkPt = new Point(check_x, check_y);
        if (all_critters.containsKey(checkPt)) 
        {
        	ArrayList<Critter> curr = all_critters.get(checkPt);
        	
        	if (!(curr.isEmpty())) 
        	{
        		return curr.get(0).toString();
        	}
        }
        
        return null;
    }

    /**
     * Run stats for the Critter at hand; can be rewritten/overriden by subclasses.
     *
     * @param critters - List of critters of specific type currently in grid.
     * @return stats - String containing the gathered stats for Critter type.  
     */
    public static String runStats(List<Critter> critters) 
    {
        // TODO Implement this method
        String stats = "" + critters.size() + " critters as follows -- " + "\n";
        Map<String, Integer> critter_count = new HashMap<String, Integer>();
        for (Critter crit : critters) 
        {
            String crit_string = crit.toString();
            critter_count.put(crit_string,
                    critter_count.getOrDefault(crit_string, 0) + 1);
        }
        String prefix = "";
        for (String s : critter_count.keySet()) 
        {
            stats += prefix + s + ":" + critter_count.get(s);
            prefix = ", ";
        }
        stats += "\n";
        return stats;
    }


    /**
     * Displays the current world grid on a GridPane object.
     * This is a change from rather using text to display the world
     *
     * @param pane - Object pane needed to be modified; important to change type of Object to corresponding type of pane. 
     */
    public static void displayWorld(Object pane) 
    {
        // TODO Implement this method
    	GridPane newPane = (GridPane) pane;
    	drawGridLines(newPane);    	
    	
    	for (int row = 0; row < Params.WORLD_WIDTH; row++) {
    	    for (int col = 0; col < Params.WORLD_HEIGHT; col++) {
    	    	
    	    	Point p = new Point(row, col);
    	    	Critter getCrit = null;
    	    	if (all_critters.containsKey(p)) 
    	    	{
    	    		if(all_critters.get(p) != null && all_critters.get(p).size() != 0)
    	    		{
    	    			getCrit = all_critters.get(p).get(0);
    	    		}
    	    	}
    	    	
    	    	if (getCrit != null) {
    	    		CritterShape s = getCrit.viewShape();
    	    		Color fill = getCrit.viewFillColor();
    	    		Color stroke = getCrit.viewOutlineColor();
    	    		int size = (Main.BOX_SIZE_WIDTH > Main.BOX_SIZE_HEIGHT) ? Main.BOX_SIZE_HEIGHT : Main.BOX_SIZE_WIDTH;
    	    		Shape shape = null;
    	    		switch(s) {
    	    		case CIRCLE 	:
    	    			shape = new Circle(size / 2);
    	    			shape.setFill(fill);
    	    			shape.setStroke(stroke);
    	    			shape.setTranslateX(0.5);
    	    			shape.setTranslateY(-0.15);
    	    			break;
    	    		case SQUARE 	:
    	    			shape = new Rectangle(Main.BOX_SIZE_WIDTH, Main.BOX_SIZE_HEIGHT);
    	    			shape.setFill(fill);
    	    			shape.setStroke(stroke);
    	    			break;
    	    		case TRIANGLE 	:
    	    			// shape = new Path(new MoveTo(0,0), new LineTo(-Main.BOX_SIZE_WIDTH+2,0), new LineTo(-Main.BOX_SIZE_WIDTH/2,-Main.BOX_SIZE_HEIGHT+2), new LineTo(0,0));
    	    			Polygon triangle = new Polygon();
    	    			triangle.getPoints().addAll(new Double[] {
    	    				0.0, 0.1,
    	    				(double)-Main.BOX_SIZE_WIDTH / 2 + 1, (double)Main.BOX_SIZE_HEIGHT - 2,
    	    				(double)Main.BOX_SIZE_WIDTH / 2 - 1, (double)Main.BOX_SIZE_HEIGHT - 2
    	    			});
    	    			shape = triangle;
    	    			shape.setFill(fill);
    	    			shape.setStroke(stroke);
    	    			break;
    	    		case DIAMOND 	:
    	    			// shape = new Path(new MoveTo(0,0), new LineTo(Main.BOX_SIZE_WIDTH/2,Main.BOX_SIZE_HEIGHT/2-2), new LineTo(Main.BOX_SIZE_WIDTH-2,0), new LineTo(Main.BOX_SIZE_WIDTH/2,-Main.BOX_SIZE_HEIGHT/2+2), new LineTo(0,0));
    	    			Polygon diamond = new Polygon();
    	    			diamond.getPoints().addAll(new Double[] {
    	    				0.0, 0.1,
    	    				(double)-Main.BOX_SIZE_WIDTH / 2 + 1, (double)Main.BOX_SIZE_HEIGHT / 2 - 1,
    	    				0.0, (double)Main.BOX_SIZE_HEIGHT - 2,
    	    				(double)Main.BOX_SIZE_WIDTH / 2 - 1, (double)Main.BOX_SIZE_HEIGHT / 2 - 1
    	    			});
    	    			shape = diamond;
    	    			shape.setFill(fill);
    	    			shape.setStroke(stroke);
    	    			break;
    	    		case STAR		:
    	    			// shape = new Path(new MoveTo(0,0), new LineTo(size/3+size/25, size/3-size/6), new LineTo(size/2,size/2-2), new LineTo(3*size/4.7, size/3-size/6), new LineTo(size-3,0), new LineTo(3+size/4.7, -size/3+size/6),new LineTo(size/2,-size/2+2), new LineTo(size/3+size/25, -size/3+size/6), new LineTo(0,0));
    	    			Polygon star = new Polygon();
    	    			star.getPoints().addAll(new Double[] {
    	    				0.0, 0.0,
    	    				(double)-Main.BOX_SIZE_WIDTH / 4, (double)5 * Main.BOX_SIZE_HEIGHT / 6 - 1,
    	    				(double)Main.BOX_SIZE_WIDTH / 3, (double)Main.BOX_SIZE_HEIGHT / 4,
    	    				(double)-Main.BOX_SIZE_WIDTH / 3, (double)Main.BOX_SIZE_HEIGHT / 4,
    	    				(double)Main.BOX_SIZE_WIDTH / 4, (double)5 * Main.BOX_SIZE_HEIGHT / 6 - 1
    	    			});
    	    			shape = star;
    	    			shape.setFill(fill);
    	    			shape.setStroke(stroke);
    	    			break;
    	    		}
    	    		
    	    		newPane.add(shape, row, col);
    	    	} else {
    	    		Shape shape = new Rectangle(Main.BOX_SIZE_WIDTH, Main.BOX_SIZE_HEIGHT);
    	    		shape.setFill(Color.PINK);
    	    		shape.setStroke(Color.BLACK);
    	    		newPane.add(shape, row, col);
    	    	}
    	    }
    	}
    	
    }
    
    /**
     * Draws grid lines on GridPane containing world grid. 
     *
     * @param pane - GridPane needed to be modified 
     */
    private static void drawGridLines(GridPane pane) {
		for (int i = 0; i < Params.WORLD_WIDTH; i++) {
			for (int j = 0; j < Params.WORLD_HEIGHT; j++) {
				Shape grid = new Rectangle(Main.BOX_SIZE_WIDTH, Main.BOX_SIZE_HEIGHT);
				grid.setFill(Color.PINK);
				grid.setStroke(Color.BLACK);
				pane.add(grid, i, j);
			}
		}
	}

	/* END --- NEW FOR PROJECT 5
			rest is unchanged from Project 4 */

    private int energy = 0;

    private int x_coord;
    private int y_coord;
    
    private boolean doneMoving = false;
    private boolean inFight = false;
    private boolean isAlive = true;

    private static List<Critter> population = new ArrayList<Critter>();
    private static List<Critter> babies = new ArrayList<Critter>();
    private static Map<Point, ArrayList<Critter>> all_critters = new TreeMap<>();

    /* Gets the package name.  This assumes that Critter and its
     * subclasses are all in the same package. */
    private static String myPackage;

    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    private static Random rand = new Random();

    public static int getRandomInt(int max) {
        return rand.nextInt(max);
    }

    public static void setSeed(long new_seed) {
        rand = new Random(new_seed);
    }

    /**
     * create and initialize a Critter subclass.
     * critter_class_name must be the qualified name of a concrete
     * subclass of Critter, if not, an InvalidCritterException must be
     * thrown.
     *
     * @param critter_class_name
     * @throws InvalidCritterException
     */
    public static void createCritter(String critter_class_name)
            throws InvalidCritterException {
        // TODO: Complete this method
    	
    	/*
    	 * create a new critter if 'critter_class_name' is valid
    	 * otherwise, catch exceptions and throw an InvalidCritterException
    	 */
    	Critter new_critter;
    	try {
    		new_critter = (Critter) Class.forName(myPackage + "." + critter_class_name).newInstance();
    	} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoClassDefFoundError e) {
    		throw new InvalidCritterException(critter_class_name);
    	}

    	/*
    	 * set the parameters of new_critter, including randomized position & start energy levels
    	 */
    	new_critter.x_coord = getRandomInt(Params.WORLD_WIDTH);
    	new_critter.y_coord = getRandomInt(Params.WORLD_HEIGHT);
    	new_critter.energy = Params.START_ENERGY;
    	/*
    	 * add new_critter to all_critters map, where the key is the point at which the critter is
    	 */
    	Point critterPt = new Point(new_critter.x_coord, new_critter.y_coord);
    	if (all_critters.containsKey(critterPt))
        {
    		ArrayList<Critter> curr = all_critters.get(critterPt);
    		curr.add(new_critter);
    	}
        else
        {
    		ArrayList<Critter> curr = new ArrayList<>();
    		curr.add(new_critter);
    		all_critters.put(critterPt, curr);
    	}

    	/*
    	 * add new_critter to the population
    	 */
    	population.add(new_critter);
    }

    /**
     * Gets a list of critters of a specific type.
     *
     * @param critter_class_name What kind of Critter is to be listed.
     *                           Unqualified class name.
     * @return List of Critters.
     * @throws InvalidCritterException
     */
    public static List<Critter> getInstances(String critter_class_name)
            throws InvalidCritterException {
        // TODO: Complete this method
        
    	Critter new_critter;
    	try {
    		new_critter = (Critter) Class.forName(myPackage + "." + critter_class_name).newInstance();
    	} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
    		throw new InvalidCritterException(critter_class_name);
    	}
    	ArrayList <Critter> instances = new ArrayList<>();
    	for (int i = 0; i < population.size(); i++) {
    		if (new_critter.getClass().isInstance(population.get(i))) {
    			instances.add(population.get(i));
    		}
    	}
        return instances;
    }

    /**
     * Clear the world of all critters, dead and alive
     */
    public static void clearWorld() {
        // TODO: Complete this method
    	
    	/*
    	 * remove all of the critters from the population list
    	 */
    	population.clear();
    	/*
    	 * clear the Map of Points and Critters as well
    	 */
    	all_critters.clear();
    }
    
    /**
     * Perform encounters between critters in same spot on grid.
     */
    private static void doEncounters()
    {
    	/*
    	 * Modified Version of doEncounters()
    	 */
        ArrayList<Point> points = new ArrayList<Point>(all_critters.keySet()); 
    	for (int i = 0; i < points.size(); i++) 
        {
    		ArrayList<Critter> critter_list = all_critters.get(points.get(i));
    		while (critter_list.size() > 1)
            {
    			Critter critOne = critter_list.get(0);
    			Critter critTwo = critter_list.get(1);

    			critOne.inFight = true;
    			critTwo.inFight = true;

    			boolean fightResponseOne = critOne.fight(critTwo.toString());
    			boolean fightResponseTwo = critTwo.fight(critOne.toString());

    			if (critOne.energy <= 0)
                {
    				critter_list.remove(critOne);
    				population.remove(critOne);
    			}
    			if (critTwo.energy <= 0) {
    				critter_list.remove(critTwo);
    				population.remove(critTwo);
    			}

    			if (critOne.inFight && critTwo.inFight && critter_list.contains(critOne) && critter_list.contains(critTwo))
                {
    				/*
    				 * obtain numbers if both want to fight based on responses
    				 */
    				int critOneNum = 0;
    				if(fightResponseOne)
                    {
    					critOneNum = getRandomInt(critOne.energy);
    				}

    				int critTwoNum = 0;
    				if (fightResponseTwo)
                    {
    					critTwoNum = getRandomInt(critTwo.energy);
    				}

    				/*
    				 * determine the outcome of the encounter
    				 */
    				if (critOneNum >= critTwoNum)
                    {
    					critOne.energy += critTwo.energy / 2;
                        //System.out.println(critOne + " won fight against " + critTwo);
                        critTwo.isAlive = false;
    					critter_list.remove(critTwo);
    					population.remove(critTwo);
    				}
                    else
                    {
    					critTwo.energy += critOne.energy / 2;
                        //System.out.println(critTwo + " won fight against " + critOne);
                        critOne.isAlive = false;
    					critter_list.remove(critOne);
    					population.remove(critOne);
    				}
    			}
    		}
    	}
    }

    public static void worldTimeStep() {
        // TODO: Complete this method
    	
    	for(int i = 0; i < population.size(); i++)
        {
            if(population.get(i).isAlive)
            {
                population.get(i).doTimeStep(); // time steps for each critter first
            }
        }

        for(int i = 0; i < population.size(); i++) // Removing after calling doTimeStep for each Critter
        {
            if(population.get(i).energy <= 0)
            {
                population.get(i).isAlive = false;
                for (Point p: all_critters.keySet())
                {
                	if (all_critters.get(p).contains(population.get(i)))
                    {
                		all_critters.get(p).remove(population.get(i)); // remove critter from all_critters value set.
                	}
                }
                population.remove(population.get(i)); // remove critter if energy <= 0
                i--;
            }
        }

        doEncounters(); // do encounters between all critters

        for(int i = 0; i < population.size(); i++)
        {
            population.get(i).energy -= Params.REST_ENERGY_COST; // subtract rest energy
            if(population.get(i).energy <= 0)
            {
                population.get(i).isAlive = false;
            }
            population.get(i).doneMoving = false;
            population.get(i).inFight = false;
        }

        for(int i = 0; i < Params.REFRESH_CLOVER_COUNT; i++)
        {
            try
            {
                Critter.createCritter("Clover"); // updateClovers()
            }
            catch(Exception e) // Might have to be InvalidCritterException?
            {
                e.printStackTrace();
            }
        }

        population.addAll(babies); // add all babies (reproduced) to population
        /*
         * Add offspring critters to all_critters in addition to population
         */
        for (int i = 0; i < babies.size(); i++) {
        	Critter new_critter = babies.get(i);
        	Point critterPt = new Point(new_critter.x_coord, new_critter.y_coord);
        	if (all_critters.containsKey(critterPt)) {
        		ArrayList<Critter> curr = all_critters.get(critterPt);
        		curr.add(new_critter);
        	} else {
        		ArrayList<Critter> curr = new ArrayList<>();
        		curr.add(new_critter);
        		all_critters.put(critterPt, curr);
        	}
        }
        babies.clear();
    }

    public abstract void doTimeStep();

    public abstract boolean fight(String oponent);

    /* a one-character long string that visually depicts your critter
     * in the ASCII interface */
    public String toString() {
        return "";
    }

    protected int getEnergy() {
        return energy;
    }

    protected final void walk(int direction) {
        // TODO: Complete this method
    	
    	if(!(isAlive))
        {
            return;
        }
        int x = x_coord;
        int y = y_coord;
        if(!doneMoving)
        {
            if(direction > 0 && direction < 4)
            {
                y_coord--;
                if(y_coord == -1) {y_coord += Params.WORLD_HEIGHT;}
            }
            if(direction > 4 && direction < 8)
            {
                y_coord++;
                if(y_coord == Params.WORLD_HEIGHT) {y_coord -= Params.WORLD_HEIGHT;}
            }
            if(direction > 2 && direction < 6)
            {
                x_coord--;
                if(x_coord == -1) {x_coord += Params.WORLD_WIDTH;}
            }
            if(direction < 2 || direction > 6)
            {
                x_coord++;
                if(x_coord == Params.WORLD_WIDTH) {x_coord -= Params.WORLD_WIDTH;}
            }
        }
        if(inFight)
        {
            for(int i = 0; i < population.size(); i++)
            {
                if(population.get(i) != this)
                {
                    if(population.get(i).x_coord == this.x_coord && population.get(i).y_coord == this.y_coord)
                    {
                        this.x_coord = x;
                        this.y_coord = y;
                        break;
                    }
                }

                if(i + 1 == population.size()) {inFight = false;}
            }
        }
        /*
         * update the current critter if the critter did move in this method
         */
        if (this.x_coord != x || this.y_coord != y) {
        	int indexOfCritter = population.indexOf(this);
        	Point newPt = new Point(this.x_coord, this.y_coord);
        	Point oldPt = new Point(x, y);
        	all_critters.get(oldPt).remove(population.get(indexOfCritter));
        	if (all_critters.containsKey(newPt)) {
        		ArrayList<Critter> curr = all_critters.get(newPt);
        		curr.add(population.get(indexOfCritter));
        	} else {
        		ArrayList<Critter> curr = new ArrayList<>();
        		curr.add(population.get(indexOfCritter));
        		all_critters.put(newPt, curr);
        	}
        }
        doneMoving = true;
        energy -= Params.WALK_ENERGY_COST;
        //energy -= Params.REST_ENERGY_COST;
    }

    protected final void run(int direction) {
        // TODO: Complete this method
    	
    	if(!(isAlive))
        {
            return;
        }
        if(!doneMoving)
        {
            walk(direction);
            doneMoving = false;
            walk(direction);
        }
        else
        {
            energy -= (Params.WALK_ENERGY_COST * 2);
            //energy -= (Params.REST_ENERGY_COST * 2);
        }
        energy += (Params.WALK_ENERGY_COST * 2);
        //energy += Params.REST_ENERGY_COST;
        energy -= Params.RUN_ENERGY_COST;
    }

    protected final void reproduce(Critter offspring, int direction) {
        // TODO: Complete this method
    	
    	if(energy < Params.MIN_REPRODUCE_ENERGY)
        {
            return;
        }

        offspring.energy = (energy/2);
        energy -= offspring.energy;

        int x = 0;
        int y = 0;

        if(direction > 2 && direction < 6)
        {
            x--;
        }
        if(direction < 2 || direction > 6)
        {
            x++;
        }
        if(direction > 0 && direction < 4)
        {
            y--;
        }
        if(direction > 4 && direction < 8)
        {
            y++;
        }

        offspring.x_coord = x_coord + x;
        offspring.y_coord = y_coord + y;

        babies.add(offspring);
    }

    /**
     * The TestCritter class allows some critters to "cheat". If you
     * want to create tests of your Critter model, you can create
     * subclasses of this class and then use the setter functions
     * contained here.
     * <p>
     * NOTE: you must make sure that the setter functions work with
     * your implementation of Critter. That means, if you're recording
     * the positions of your critters using some sort of external grid
     * or some other data structure in addition to the x_coord and
     * y_coord functions, then you MUST update these setter functions
     * so that they correctly update your grid/data structure.
     */
    static abstract class TestCritter extends Critter {

        protected void setEnergy(int new_energy_value) {
            super.energy = new_energy_value;
        }

        protected void setX_coord(int new_x_coord) {
            
        	int indexOfCritter = population.indexOf(this);
            Point newPt = new Point(new_x_coord, this.getY_coord());
            Point oldPt = new Point(this.getX_coord(), this.getY_coord());
            all_critters.get(oldPt).remove(population.get(indexOfCritter));
            if (all_critters.containsKey(newPt)) {
                ArrayList<Critter> curr = all_critters.get(newPt);
                curr.add(population.get(indexOfCritter));
            } else {
                ArrayList<Critter> curr = new ArrayList<>();
                curr.add(population.get(indexOfCritter));
                all_critters.put(newPt, curr);
            }

            super.x_coord = new_x_coord;
        }

        protected void setY_coord(int new_y_coord) {
            
        	int indexOfCritter = population.indexOf(this);
        	Point newPt = new Point(this.getX_coord(), new_y_coord);
        	Point oldPt = new Point(this.getX_coord(), this.getY_coord());
        	all_critters.get(oldPt).remove(population.get(indexOfCritter));
        	if (all_critters.containsKey(newPt)) {
        		ArrayList<Critter> curr = all_critters.get(newPt);
        		curr.add(population.get(indexOfCritter));
        	} else {
        		ArrayList<Critter> curr = new ArrayList<>();
        		curr.add(population.get(indexOfCritter));
        		all_critters.put(newPt, curr);
        	}

        	super.y_coord = new_y_coord;
        }

        protected int getX_coord() {
            return super.x_coord;
        }

        protected int getY_coord() {
            return super.y_coord;
        }

        /**
         * This method getPopulation has to be modified by you if you
         * are not using the population ArrayList that has been
         * provided in the starter code.  In any case, it has to be
         * implemented for grading tests to work.
         */
        protected static List<Critter> getPopulation() {
            return population;
        }

        /**
         * This method getBabies has to be modified by you if you are
         * not using the babies ArrayList that has been provided in
         * the starter code.  In any case, it has to be implemented
         * for grading tests to work.  Babies should be added to the
         * general population at either the beginning OR the end of
         * every timestep.
         */
        protected static List<Critter> getBabies() {
            return babies;
        }
    }
}
