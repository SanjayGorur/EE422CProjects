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

public class Critter2 extends Critter
{
    private int babiesMade;
    private int[] moves = {1, 3, 5, 7}; // possible moves for a Bishop in chess.

    public Critter2()
    {
        babiesMade = 0; // babies for each bishop starts at 0.
    }
    
    /**
     * Returns outline color of Critter1
     * 
     * @return outline color for Critter1 
     */ 
    public javafx.scene.paint.Color viewOutlineColor() 
    {
    	return javafx.scene.paint.Color.PURPLE;
    }
    
    /**
     * Returns CritterShape of Critter2
     * 
     * @return CritterShape 
     */  
    public CritterShape viewShape()
    {
    	return CritterShape.STAR; 
    }

    /**
     * Returns a one-letter string to represent Bishop 'sub'-critter in grid.
     * 
     * @return one-letter String that represents Bishop critter. 
     */
    @Override
    public String toString()
    {
        return "2";
    }

   /**
     * Returns whether or not bishop wants to fight based on current amount of energy.
     * fights if power greater than (Params.START_ENERGY)/4).
     * @param not_used - String of opponent that Bishop critter will fight: same position on grid. 
     * @return whether or not bishop chooses to fight.
     */
    public boolean fight(String not_used)
    {
        if(getEnergy() > ((Params.START_ENERGY)/4) || look(getRandomInt(4), false) == null)
        {
            return true;
        }

        return false;
    }

    /**
     * The doTimeStep() method for any Bishop critter in the grid.
     * chooseDir: chooses direction from moves, a slanted direction
     * if energy greater than (Params.START_ENERGY)/2), run in chooseDir direction.
     * if energy greater than (Params.START_ENERGY)/4), walk in chooseDir direction.
     * if energy less than or equal to (Params.START_ENERGY)/4), don't move!
     * Bishop will reproduce if energy >= Params.MIN_REPRODUCE_ENERGY, and babiesMade incremented.
     */
    @Override
    public void doTimeStep()
    {
        int chooseDir = moves[getRandomInt(moves.length)];
        if(getEnergy() > ((Params.START_ENERGY)/2))
        {
            run(chooseDir);
        }
        else if(getEnergy() > ((Params.START_ENERGY)/4))
        {
            walk(chooseDir);
        }
        Critter2 baby = new Critter2();
        if(getEnergy() >= Params.MIN_REPRODUCE_ENERGY)
        {
            babiesMade++;
        }
        reproduce(baby, moves[getRandomInt(moves.length)]);
    }

    /**
     * The runStats() method gives the amount of children produced by each bishop, 
     * and it gives the number of bishops currently in the grid. 
     *  @param bishops - List of bishops found in the world. 
     */
    public static String runStats(List<Critter> bishops)
    {
        String stats = "Number of children for each Bishop: (If Applicable)\n";
        for(int i = 0; i < bishops.size(); i++)
        {
            try
            {
                stats += "\t" + "Children of Bishop " + (i + 1) + ": " + ((Critter2) bishops.get(i)).babiesMade +"\n";
            }
            catch(Exception e)
            {

            }
        }
        stats += "Bishops in grid: " + bishops.size() + "\n";
        return stats;
    }
}