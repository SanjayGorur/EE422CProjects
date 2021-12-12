/*
 * EE422C Project 2 (Mastermind) submission by
 * Replace <...> with your actual data. 
 * <Sanjay Gorur>
 * <sg52879>
 * Slip days used: <0>
 * Fall 2021
 */

package assignment2;

import java.util.*;
import java.lang.*;
import java.io.*;

public class Guess 
{
    private String guess;
    private boolean checkInvalid;
    private boolean checkWhitePegs;
    private int blackPegs;
    private int whitePegs;

    // Constructor used to keep track of made guess, whether guess is invalid, whether there are white pegs, and amount(s) of both black and white pegs
    Guess(String guess, boolean checkInvalid, boolean checkWhitePegs, int blackPegs, int whitePegs)
    {
        this.guess = guess;
        this.checkInvalid = checkInvalid;
        this.checkWhitePegs = checkWhitePegs;
        this.blackPegs = blackPegs;
        this.whitePegs = whitePegs;
    }

    // return guess made by user
    public String getGuess()
    {
        return guess;
    }

    // return whether guess is invalid
    public boolean getInvalid()
    {
        return checkInvalid;
    }

    // return number of black pegs in guess
    public int getBlackPegs()
    {
        return blackPegs;
    }

    // return number of white pegs in guess
    public int getWhitePegs()
    {
        return whitePegs;
    }

    // Check guess to see whether or not it's invalid
    public boolean checkGuess(GameConfiguration config)
    {
        if(guess.length() != config.pegNumber) {System.out.println("INVALID_GUESS"); checkInvalid = true; return true;}
        else
        {
            for(int i = 0; i < guess.length(); i++)
            {
                for(int j = 0; j < config.colors.length; j++)
                {
                    if(guess.charAt(i) == config.colors[j].charAt(0)) {break;}
                    //System.out.println(hold.charAt(i) + "\t" + config.colors[j]);
                    if((j + 1) == config.colors.length) {System.out.println("INVALID_GUESS"); checkInvalid = true; return true;}
                }
                if(checkInvalid == true) {return true;}
            }
        }

        return false;
    }

    // Find number of black pegs in guess --> set variable (void)
    public void findBlackPegs(String cmp, int trackIndex, int[] indexes)
    {
        for(int i = 0; i < guess.length() && guess.length() == cmp.length() && checkInvalid == false; i++)
        {
            if(guess.charAt(i) == cmp.charAt(i)) {indexes[trackIndex] = i; trackIndex++; blackPegs++;}
        }
    }

    // Find number of white pegs in guess --> set variable (void)
    public void findWhitePegs(String cmp, int trackIndex, int[] indexes)
    {
        for(int i = 0; i < guess.length() && guess.length() == cmp.length() && checkInvalid == false; i++)
        {
            for(int j = 0; j < guess.length() && guess.length() == cmp.length() && checkInvalid == false; j++)
            {
                if(i != j && guess.charAt(i) == cmp.charAt(j) && guess.charAt(i) != cmp.charAt(i) && guess.charAt(j) != cmp.charAt(j))
                {
                    /*System.out.println(Arrays.toString(idx));*/
                    for(int k = 0; k < trackIndex; k++)
                    {
                        if(j == indexes[k]) {/*System.out.println(k + "\t" + start);*/ checkWhitePegs = false; break;}
                        //if((k + 1) == start) {wpegs++; idx[start] = j; start++; j = hold.length() - 1;}
                    }
                    if(checkWhitePegs == true) {whitePegs++; /*System.out.println(j + "\t");*/ indexes[trackIndex] = j; trackIndex++; j = guess.length() - 1;}
                }
                checkWhitePegs = true;
            }
        }
    }
}