// Emma Akbari (eea21)
// program to keep track of user history for project 1, uses DLB from DictTrie

import java.util.*;
import java.io.*;

public class UserHist {
	// vars set up
	private DictTrie user; // user_history.txt in trie form
	private HashMap<String, Integer> repeats; // track repeated words
	private File hist; // player_history.txt
	private boolean constructor = true; // becomes false after instantiation

	// constructor opens user history & makes DLB
	public UserHist() {
		try {
			// open user history 
			hist = new File("user_history.txt");
			
			if(!hist.exists())
			{  
			   hist.createNewFile();
			}
			
			user = new DictTrie(); // user DLB
			Scanner readHist = new Scanner(hist); // file scanner
			String word; // user word to add to DLB
			repeats = new HashMap<String, Integer>(); // track repeat words

			// fill DLB with dictionary
			while(readHist.hasNextLine()) {
				word = readHist.nextLine();
				put(word); // put word in DLB
			}
			readHist.close();
			constructor = false;
			
		} catch(IOException e) {
			System.out.println("An error occurred.");
		}
	}

	// add key to the trie and write to file 
	public void put(String word) {
		// if a word already exists, add to hashmap & increment
		if(user.get(word) != null) {
			if(repeats.containsKey(word)) { // word already tracked as repeat
				int count = (int)repeats.get(word);
				count++;
				repeats.replace(word, new Integer(count)); // incr value
			} else { // word not tracked yet
				repeats.put(word, new Integer(2)); // track
			}
		}
		user.put(word); // add key to user DLB

		if(!constructor) { // only write to file if DLB already instantiated
			// write to player_history.txt
			try {
				PrintWriter write = new PrintWriter(new FileWriter(hist, true));
				if(hist.length() != 0) {
					write.append("\n"); // only add new line if file !empty
				}
				write.append(word);
				write.close();
			} catch(IOException e) {
				System.out.println("Error writing to file.");
			}
		}
	}

	// retrieve a word from the trie based on key, null if not found
	public String get(String key) {
		return user.get(key);
	}

	// print HashMap
	public void print_hash() {
		// print keys and values
		for (String i : repeats.keySet()) {
		  System.out.println("key: " + i + ", value: " + repeats.get(i));
		}
	}

	// retrieve keys given a prefix, null if not found
	public String[] get_prefix_keys(String prefix) {
		String[] words = user.get_prefix_keys(prefix);

		// sort array
		if(repeats != null) {
			words = sort_priority(words);
		} else {
			Arrays.sort(words);
		}

		return words;
	} 

	// helper method to sort keys based on frequency, then alphabetically
	private String[] sort_priority(String[] arr) {
		String[] copy = arr.clone(); // for single occurrence predictions
		Integer[] rankings = new Integer[5]; // values from HashMap
		String[] temp = new String[5]; // to return
		int locR = 0; // track how long rankings is

		// retrieve highest occurrence Integers in rankings[]
		for(int i = 0; i < 5; i++) {
			if(repeats.containsKey(arr[i])) {
				rankings[locR] = repeats.get(arr[i]);
				copy[i] = "~";
				locR++;
			}
		}

		user.non_null(copy);
		Arrays.sort(copy);

		// sort Integers in descending order
		int rankLen = user.get_logical_len(rankings);
		Integer[] rank = new Integer[rankLen];
		for(int i = 0; i < rankLen; i++) {
			rank[i] = rankings[i];
		}
		Arrays.sort(rank, Collections.reverseOrder());

		// prioritize most used keys in temp[]
		for(int i = 0; i < rankLen; i++) {
			if(rank[i] != null) {
				for(String j: repeats.keySet()) {
					if(rank[i].equals(repeats.get(j)) && !(Arrays.asList(temp).contains(j)) && (Arrays.asList(arr).contains(j))) {
						temp[i] = j;
						break;
					}
				}
			} else {
				break;
			}
		}

		int tempLen = user.get_logical_len(temp);

		// fill the rest with predictions
		if(tempLen < 5) {
			for(int i = tempLen; i < 5; i++) {
				for(int j = 0; j < 5; j++) {
					if(copy[j] != null) {
						temp[i] = copy[j];
						copy[j] = null;
						break;
					}
				}
			}
		}

		user.to_null(temp);

		// reassign to @param arr
		arr = temp;
		return arr;
	}

}