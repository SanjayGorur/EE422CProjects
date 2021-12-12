/*
 * EE422C Project 2 (Mastermind) submission by
 * Replace <...> with your actual data. 
 * <Sanjay Gorur>
 * <sg52879>
 * Slip days used: <0>
 * Fall 2021
 */

package assignment2;

import java.util.Scanner;

public class Driver {
    public static void main(String[] args) {
        // Use this for your testing.  We will not be calling this method.
        String colors[] = {"Y", "G", "B", "R", "P", "C", "A", "E", "S"};
        GameConfiguration config = new GameConfiguration(3, colors, 1);
        SecretCodeGenerator generator = new SecretCodeGenerator(config);
        start(true, config, generator);
    }

    // method used for setting up the game --> game runner!
    public static void start(Boolean isTesting, GameConfiguration config, SecretCodeGenerator generator) {
        // TODO: complete this method
		// We will call this method from our JUnit test cases.
        Scanner scan = new Scanner(System.in); // instantiate scanner
        System.out.println("Welcome to Mastermind.\nDo you want to play a new game? (Y/N):"); // ask user whether want to play
        String keep = scan.next();
        if(!(keep.equals("Y"))) {return;} // If not, you're done!
        while(true) // THE GAME RUNNER
        {
            Game master = new Game(isTesting, scan); // Each game run
            master.runGame(config, generator);
            System.out.println("Do you want to play a new game? (Y/N):");
            keep = scan.next();
            if(!(keep.equals("Y"))) {return;}
        }

    }
}
