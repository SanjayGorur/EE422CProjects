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

public class Game
{
    private boolean isTesting;
    private Scanner scan;

    // Constructor for Game class. Scanner passed as parameter to avoid issues with grading script
    public Game(boolean isTesting, Scanner scan)
    {
        this.isTesting = isTesting;
        this.scan = scan;      
    }

    // return whether or not user is in testing mode
    public boolean checkTesting()
    {
        return isTesting;
    }

    // return scanner in use for obtaining user input
    public Scanner getScanner()
    {
        return scan;
    }

    // print the remaining amount of gusses and prompt the user for the next guess
    public void printGuesses(int amount)
    {
        System.out.println();
        System.out.println("You have " + amount + " guess(es) left.");
        System.out.println("Enter guess: ");
    }

    // Print the history of the user's guesses for the current game
    public void printHistory(int maxGuesses, int currentGuesses, String[] keptGuesses, int[][] keptPegs)
    {
        for(int i = 0; i < (maxGuesses - currentGuesses); i++)
        {
            {System.out.println(keptGuesses[i] + " -> " + keptPegs[i][0] + "b_" + keptPegs[i][1] + "w");}
        }
    }

    // This method is used to run the game in this program. It further makes the use of both Player and Guess objects to abstract the design of the program.
    public void runGame(GameConfiguration config, SecretCodeGenerator generator)
    {
        Player user = new Player(generator.getNewSecretCode(), config.guessNumber, new String[config.guessNumber], new int[config.guessNumber][2]); // New Player Object
        if(checkTesting()) {System.out.println("Secret code: " + user.getSecretCode());} // The Secret Code in use

        /*
        String cmp = generator.getNewSecretCode();

        int amount = config.guessNumber;
        String guesses[] = new String[config.guessNumber];
        int pegs[][] = new int [config.guessNumber][2];
        */

        while(user.getRemainingGuesses() > 0) // While there's more than 0 guesses left
        {
            printGuesses(user.getRemainingGuesses());
            Guess newGuess = new Guess(getScanner().next(), false, true, 0, 0); // New Guess object, each iteration
            if(newGuess.getGuess().equals("HISTORY"))
            {
                printHistory(config.guessNumber, user.getRemainingGuesses(), user.getCurrentGuesses(), user.getPegs()); // printHistory based on user entering "HISTORY"
            }
            else
            {
                if(newGuess.checkGuess(config)) {user.setRemainingGuesses(user.getRemainingGuesses() + 1);} // reduce remaining number of guesses
                if(newGuess.getInvalid() == false) {user.getCurrentGuesses()[config.guessNumber - user.getRemainingGuesses()] = newGuess.getGuess();} // Gather if user guess is invalid

                int idx[] = new int[newGuess.getGuess().length()]; // indexes array used for finding black and white pegs
                int start = 0;
                newGuess.findBlackPegs(user.getSecretCode(), start, idx); // Find black pegs for guess in Guess class
                newGuess.findWhitePegs(user.getSecretCode(), start, idx); // Find white pegs for guess in Guess class

                if(newGuess.getInvalid() == false) {user.setPegs(newGuess.getBlackPegs(), newGuess.getWhitePegs(), config.guessNumber - user.getRemainingGuesses()); System.out.println(newGuess.getGuess() + " -> " + newGuess.getBlackPegs() + "b_" + newGuess.getWhitePegs() + "w");}
                if(newGuess.getInvalid() == true || !(newGuess.getGuess().equals(user.getSecretCode()))) {user.setRemainingGuesses(user.getRemainingGuesses() - 1);}

                if(newGuess.getGuess().equals(user.getSecretCode())) {System.out.println("You win!"); System.out.println(); return;} // User won the game!
                if(user.getRemainingGuesses() == 0) {System.out.println("You lose! The pattern was " + user.getSecretCode()); System.out.println();} // User lost the game!
            }
        }
    }
}