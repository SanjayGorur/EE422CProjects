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

package assignment5;

import java.util.List;

import assignment5.Critter.CritterShape;

public class Critter4 extends Critter {
	/**
	 * Critter4 is a subclass of Critter that acts as a ChargingWarrior
	 * This critter will move in any direction, and it does so randomly
	 * Since it is a ChargingWarrior, it will always charge/run during time steps.
	 * It is a smart critter, in that it decides to run only if it has the requisite energy.
	 * Since it is a warrior, it has a very aggressive mentality and will fight any Critter that challenges it
	 * As an aggressive species, the ChargingWarrior wishes to take over the world, and thus will reproduce in every possible direction, if it has the requisite energy.
	 */
	
	private int numChildren = 0;
	
	public Critter4() {
		numChildren = 0;
	}
	
    /**
     * Returns CritterShape of Critter4
     * 
     * @return CritterShape 
     */  	
    public CritterShape viewShape()
    {
    	return CritterShape.SQUARE; 
    }
    
    /**
     * Returns color of Critter4
     * 
     * @return color for Critter4
     */   
    public javafx.scene.paint.Color viewColor() 
    {
    	// changed so we can see the critters during testing 
        return javafx.scene.paint.Color.YELLOW;
    }
    
    /**
     * Returns outline color of Critter4
     * 
     * @return outline color for Critter4 
     */     
    public javafx.scene.paint.Color viewOutlineColor() 
    {
    	return javafx.scene.paint.Color.MAGENTA;
    }
    
    /**
     * Returns fill color of Critter4
     * 
     * @return fill color for Critter4
     */   
    public javafx.scene.paint.Color viewFillColor() 
    {
    	return javafx.scene.paint.Color.BROWN;
    }
	
	/**
     * The doTimeStep() method for any ChargingWarrior critter in the grid.
     * if energy greater than or equal to Params.RUN_ENERGY_COST, run in some random direction.
     * if energy less than Params.RUN_ENERGY_COST, don't move!
     *  
     * while the ChargingWarrior has the energy, reproduce starting from SW direction and going clockwise
     */
	@Override
	public void doTimeStep() {
		// TODO Auto-generated method stub
		if (this.getEnergy() >= Params.RUN_ENERGY_COST) {
			run(getRandomInt(8));
		}
		
		else if(look(getRandomInt(4), true) == null)
		{
			walk(getRandomInt(8));
		}
		
		int num = 7;
		while(this.getEnergy() >= Params.MIN_REPRODUCE_ENERGY && num >= 0) {
			Critter4 child = new Critter4();
			this.reproduce(child, num);
			numChildren++;
			num--;
		}
	}
	
	/**
     * Returns a one-letter string to represent ChargingWarrior 'sub'-critter in grid.
     * 
     * @return one-letter String that represents ChargingWarrior critter. 
     */
	@Override
	public String toString() {
		return "4";
	}

	/**
     * The fight() method for any ChargingWarrior critter in the grid.
     * no matter the opponent, the ChargingWarrior will always fight
     *  
     *  @param opponent - String version of Critter opponent (not used)
     */
	@Override
	public boolean fight(String opponent) {
		// TODO Auto-generated method stub
		return true;
	}
	
	/**
     * The runStats() method gives the amount of children produced by each ChargingWarrior, 
     * and it gives the number of ChargingWarriors currently in the grid. 
     *  @param warriors - List of warriors found in the world. 
     */
    public static String runStats(List<Critter> warriors)
    {
        String stats = "Number of children for each ChargingWarrior: (If Applicable)\n";
        for(int i = 0; i < warriors.size(); i++)
        {
            try
            {
                stats += "\t" + "Children of ChargingWarrior " + (i + 1) + ": " + ((Critter4) warriors.get(i)).numChildren + "\n";
            }
            catch(Exception e)
            {

            }
        }
        stats += "ChargingWarriors in grid: " + warriors.size() + "\n";
        return stats;
    }

}
