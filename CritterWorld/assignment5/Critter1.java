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


public class Critter1 extends Critter
{
    private int[] moves = {0, 2, 4, 6}; // Koala only feels like moving up, right, left, or down. 
    private boolean isSleepy; // is a Koala sleepy?
    private int koalaBabies;

    public Critter1()
    {
        isSleepy = false; // Koala starts off as NOT sleepy.
        koalaBabies = 0;  // Babies for each koala initialized to 0. 
    }
    
    /**
     * Returns CritterShape of Critter1
     * 
     * @return CritterShape 
     */
    public CritterShape viewShape()
    {
    	return CritterShape.TRIANGLE; 
    }

    /**
     * Returns color of Critter1
     * 
     * @return color for Critter1 
     */
    public javafx.scene.paint.Color viewColor() {
    	// changed so we can see the critters during testing 
        return javafx.scene.paint.Color.RED;
    }

    /**
     * Returns a one-letter representation of the Koala critter.
     * 
     * @return one-letter string for any Koala critter.  
     */
    @Override
    public String toString()
    {
        return "1";
    }

    /**
     * Returns whether or not Koala is willing to fight; dependent on isSleepy()
     * isSleepy: false
     * NOT isSleepy: true
     * 
     * @param not_used - String of opponent that Koala is fighting; not considered.
     * @return whether or not Koala wants to fight.   
     */
    public boolean fight(String not_used)
    {
        if(isSleepy)
        {
            return false;
        }

        return true;
    }

    /**
     * The doTimeStep() method for a Koala critter performs an action(s) based on energy and isSleepy.
     * NOT isSleepy: walk in one of the directions given in moves[] array.
     * if energy <= (Params.START_ENERGY/5), set isSleepy to true. 
     * If the supposed 'parent' critter has enough energy, increment koalaBabies and produce baby critter!
     */
    @Override
    public void doTimeStep()
    {
        if(!isSleepy)
        {
            walk(moves[getRandomInt(moves.length)]);
        }

        if(getEnergy() <= ((Params.START_ENERGY)/5))
        {
            isSleepy = true;
        }

        if(getEnergy() > Params.MIN_REPRODUCE_ENERGY)
        {
            koalaBabies++;
        }

        Critter1 baby =  new Critter1();
        reproduce(baby, moves[getRandomInt(moves.length)]);
    }

    /**
     * The runStats() method takes in a list of koalas and returns stats for koalas alive in the world.
     * Number of babies for each koala
     * Number of koalas currently in grid.
     * 
     * @param koalas - List of koala critters currently in grid. 
     */
    public static String runStats(List<Critter> koalas)
    {
        String stats = "Number of babies made by each Koala: (If Applicable)\n";
        for(int i = 0; i < koalas.size(); i++)
        {
            try
            {
                stats += "\t" + "Babies produced by Koala " + (i + 1) + ": " + ((Critter1) koalas.get(i)).koalaBabies + "\n";
            }
            catch(Exception e)
            {

            }
        }
        stats += "Koalas in world: " + koalas.size() + "\n";
        return stats;
    }


}