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

public class Critter3 extends Critter{
	/**
	 * Critter3 is a subclass of Critter that acts as a GoblinSlayer
	 * This critter will only move in horizontal or vertical directions, and it does so randomly
	 * It is a smart critter, so it will decide to run, walk, or stay still depending on its current energy levels
	 * It does not reproduce, because this critter is solely focused on one thing, and that is slaying/fighting Goblins.
	 */
	
	private int [] moves = {0, 2, 4, 6};
	
    /**
     * Returns CritterShape of Critter3
     * 
     * @return CritterShape 
     */  	
    public CritterShape viewShape()
    {
    	return CritterShape.DIAMOND; 
    }
    
    /**
     * Returns fill color of Critter3
     * 
     * @return fill color for Critter3 
     */
    public javafx.scene.paint.Color viewFillColor() 
    {
    	return javafx.scene.paint.Color.ORANGE;
    }

	
	/**
     * The doTimeStep() method for any GoblinSlayer critter in the grid.
     * if energy greater than Params.RUN_ENERGY_COST, run in some horizontal/vertical direction.
     * if energy greater than Params.WALK_ENERGY_COST, walk in some horizontal/vertical direction.
     * if energy less than or equal to Params.WALK_ENERGY_COST, don't move!
     */
	@Override
	public void doTimeStep() {
		// TODO Auto-generated method stub
		
		// no children for GoblinSlayers!
		if (this.getEnergy() > Params.RUN_ENERGY_COST) {
			run(moves[getRandomInt(moves.length)]);
		} else if (this.getEnergy() > Params.WALK_ENERGY_COST) {
			walk(moves[getRandomInt(moves.length)]);
		} 
	}
	
	/**
     * Returns a one-letter string to represent GoblinSlayer 'sub'-critter in grid.
     * 
     * @return one-letter String that represents GoblinSLayer critter. 
     */
	@Override
	public String toString() {
		return "3";
	}

	/**
     * The fight() method for any GoblinSlayer critter in the grid.
     * if the opponent is a Goblin, the critter will fight
     * if the opponent is not a Goblin, the critter will not fight
     *  
     *  @param opponent - String version of Critter opponent
     */
	@Override
	public boolean fight(String opponent) {
		// TODO Auto-generated method stub
		
		// will only fight Goblins (hence the name GoblinSlayer)
		if (opponent.equals("G")) {
			return true;
		}
		return false;
	}
	
	/**
     * The runStats() method gives the number of GoblinSlayers currently in the grid. 
     *  @param slayers - List of GoblinSlayers found in the world. 
     */
    public static String runStats(List<Critter> slayers)
    {
        return "Slayers in grid: " + slayers.size() + "\n";
    }

}
