// Emma Akbari (eea21) 
// main program to interact with user for project 1

import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.io.*;

public class ac_test {
	public static void main(String[] args) {
		try{
			// set up of vars
			DictTrie dlb = new DictTrie(); // DLB
			File dict = new File("dictionary.txt"); // dictionary
			Scanner readDict = new Scanner(dict); // file scanner
			String word; // dictionary word to add to DLB
			UserHist userDLB = new UserHist(); // user dictionary
			ArrayList<Double> times = new ArrayList<Double>(); // track computation times

			// fill DLB with dictionary
			while(readDict.hasNextLine()) {
				word = readDict.nextLine();
				dlb.put(word);
			}
			readDict.close();

			// user variables
			Scanner inScan = new Scanner(System.in); // user input
			String userWord = ""; // word to autocomplete
			String input;
			char next; // next entered char
			String[] predictions = new String[5]; // predictions
			String[] userPred = new String[5]; // predictions from user history

			// loop for user interaction
			do {
				System.out.println();
				// prompt user to enter a character
				System.out.println("Enter your next character: ");
				input = inScan.nextLine(); // read in chars
				next = input.charAt(0);

				// user enters 1-5
				if(Character.getNumericValue(next) >= 1 && Character.getNumericValue(next) <= 5) { 
					// add selection to user history, print word completed, reset userWord
					userWord = predictions[Character.getNumericValue(next)-1];
					userDLB.put(userWord);
					System.out.println("WORD COMPLETED: " + userWord);
					userWord = "";
				} else if(next == '$') { // user wants to complete word
					// add to user history, print word completed, reset userWord
					userDLB.put(userWord);
					System.out.println("WORD COMPLETED: " + userWord);
					userWord = "";
				} else if((next >= 60 && next <= 90) || (next >= 97 && next <= 122)) { // next is valid ascii char
					userWord += next; // add to word

					long start = System.nanoTime(); // start time to retrieve predictions

					predictions = dlb.get_prefix_keys(userWord); // retrieve dictionary suggestions
					if(dlb.get_logical_len(predictions) > 0) { // sort if not empty
						dlb.non_null(predictions);
						Arrays.sort(predictions);
						dlb.to_null(predictions);
					} 

					// retrieve user history 
					userPred = userDLB.get_prefix_keys(userWord);

					String[] temp = new String[5]; // for sorting
					int predCount = 0; // track index of prediction arrays
					int uPredCount = 0;
					int tempCount = 0;

					// fill predictions with user history then dictionary predictions
					while(tempCount < 5) {
						if(userPred[uPredCount] != null) {
							temp[tempCount] = userPred[uPredCount];
							uPredCount++;
							tempCount++;
						} else if(predictions[predCount] != null) {
							if(!Arrays.asList(temp).contains(predictions[predCount])) {
								temp[tempCount] = predictions[predCount];
								tempCount++;
							}
							predCount++;
						} else {
							break;
						}
					}
					predictions = temp;

					long elapsed = System.nanoTime() - start; // end time
					double sec = nano_to_sec(elapsed); // convert to seconds
					times.add(sec); // add to ArrayList
					
					if(dlb.get_logical_len(predictions) == 0) { // no matches found
						System.out.println("No predictions found for " + userWord + ", but keep entering.");
					} else { // show suggestions
						System.out.println("(" + new DecimalFormat("#.######").format(sec) + " s)");
						System.out.println("Predictions: ");
						for(int i = 0; i < dlb.get_logical_len(predictions); i++) {
							System.out.print("(" + (i+1) + ") " + predictions[i] + "    ");
						}
					}
				} else if(next != '!'){ // invalid input
					// print error and exit(1)
					System.out.println("Invalid input. Terminating program.");
					System.exit(1);
				}

				System.out.println();

			} while(next != '!'); // ! terminates program

			// print average time
			double avgTime = 0;
			for(int i = 0; i < times.size(); i++) {
				avgTime += ((double)(times.get(i)));
			}
			avgTime /= times.size();

			System.out.println("Average time: " + new DecimalFormat("#.######").format(avgTime) + " s");
			System.out.println("Bye!");
			

		} catch(IOException e) {
			System.out.println("An error occurred.");
		}
	}	

	// convert nanoseconds to seconds
	public static double nano_to_sec(long time) {
		double sec = (double)time/1_000_000_000.0;
		return sec;
	}
}
