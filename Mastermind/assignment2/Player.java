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

public class Player
{
    private String secretCode;
    private int remainingGuesses;
    private String[] currentGuesses;
    private int[][] keepPegs;

    // Constructor to keep track of secret string, remaining number of gusses, history of guesses, and history of pegs
    public Player(String secretCode, int remainingGuesses, String[] currentGuesses, int[][] keepPegs)
    {
        this.secretCode = secretCode;
        this.remainingGuesses = remainingGuesses;
        this.currentGuesses = currentGuesses;
        this.keepPegs = keepPegs;
    }

    // return the secret code
    public String getSecretCode()
    {
        return secretCode;
    }

    // return amount of guesses left
    public int getRemainingGuesses()
    {
        return remainingGuesses;
    }

    // return history of guesses --> array of strings
    public String[] getCurrentGuesses()
    {
        return currentGuesses;
    }

    // return 2D array of pegs --> index 0: black AND index 1: white
    public int[][] getPegs()
    {
        return keepPegs;
    }

    // Set remaining number of gusses to corresponding parameter variable
    public void setRemainingGuesses(int set)
    {
        remainingGuesses = set;
    }

    // Update the guesses--new guess made--by adding to currentGuesses
    public void setCurrentGuesses(String add, int place)
    {
        currentGuesses[place] = add; 
    }

    // Update the pegs--2D Array--by adding to keepPegs --> black and white peg amount(s)!
    public void setPegs(int bpegs, int wpegs, int place)
    {
        keepPegs[place][0] = bpegs;
        keepPegs[place][1] = wpegs;
    }

}