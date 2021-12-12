/*
 * EE422C Project 2 (Mastermind) Extra Credit submission by
 * Replace <...> with your actual data. 
 * <Sanjay Gorur>
 * <sg52879>
 * Slip days used: <0>
 * Fall 2021
 */

package assignment2ec;

public class CheckGuess {
	// You don't need GameConfiguration for this class's methods.
	// The response class checks the amount of pegs between current Guess and secretCode
	// Honestly, this method is a user-generated file for testing done by combining methods for the original assignment program.
	// For black and white peg count! --> returned as a Response object!
	public static Response checkGuess(String currentGuess, String secretCode) 
	{
		// TODO for your extra credit part testing.  Do not turn this in.
		int wpegs = 0;
		int bpegs = 0;
		if(currentGuess.length() != secretCode.length()) {return new Response(0, 0);}
		/*
		for(int i = 0; i < currentGuess.length(); i++)
		{
			for(int j = 0; j < secretCode.length(); j++)
			{
				if(currentGuess.charAt(i) == secretCode.charAt(j)) {break;}
				if((j + 1) == secretCode.length()) {System.out.println("\t" + bpegs + "\t" + wpegs); return new Response (0, 0);}
			}
		}
		*/
		//int bpegs = 0;
		int[] indexes = new int[secretCode.length()];
		for(int i = 0; i < indexes.length; i++)
		{
			indexes[i] = -2;
		}
		int trackIndex = 0;
		for(int i = 0; i < currentGuess.length(); i++)
		{
			if(currentGuess.charAt(i) == secretCode.charAt(i)) {bpegs++; indexes[trackIndex] = i; trackIndex++;}
		}
		//int wpegs = 0;
		boolean checkWhitePegs = true;
        for(int i = 0; i < currentGuess.length(); i++)
        {
            for(int j = 0; j < secretCode.length(); j++)
            {
                if(i != j && currentGuess.charAt(i) == secretCode.charAt(j) && currentGuess.charAt(i) != secretCode.charAt(i) && currentGuess.charAt(j) != secretCode.charAt(j))
                {
                    /*System.out.println(Arrays.toString(idx));*/
                    for(int k = 0; k < trackIndex; k++)
                    {
                        if(j == indexes[k]) {/*System.out.println(k + "\t" + start);*/ checkWhitePegs = false; break;}
                        //if((k + 1) == start) {wpegs++; idx[start] = j; start++; j = hold.length() - 1;}
                    }
                    if(checkWhitePegs == true) {wpegs++; /*System.out.println(j + "\t");*/ indexes[trackIndex] = j; trackIndex++; j = currentGuess.length() - 1;}
                }
                checkWhitePegs = true;
            }
        }

		//System.out.println("\t" + bpegs + "\t" + wpegs);
		return new Response(bpegs, wpegs);

	}
}
