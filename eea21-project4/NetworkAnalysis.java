// Emma Akbari (eea21) 
// driver for project 4

import java.util.Scanner;
import java.util.LinkedList;
import java.io.*;

public class NetworkAnalysis {

	public static void main(String[] args) {

		try {

			// build graph based on input file
			Scanner fileScan = new Scanner(new File(args[0]));
			Graph network = new Graph(fileScan.nextInt());
			String info = fileScan.nextLine(); // scan past first line
			String[] tokens;

			// read in each line and parse into edge components
			while(fileScan.hasNextLine()) {
				info = fileScan.nextLine();
				tokens = info.split(" ");

				// create an edge and add to the graph
				Edge edge = new Edge();
				edge.set_end1(Integer.parseInt(tokens[0]));
				edge.set_end2(Integer.parseInt(tokens[1]));
				edge.set_type(tokens[2]);
				edge.set_bandwidth(Integer.parseInt(tokens[3]));
				edge.set_length(Integer.parseInt(tokens[4]));

				network.add(edge);
			}

			System.out.println("--Graph loaded--");
			fileScan.close();

			// prompt user
			Scanner inScan = new Scanner(System.in);
			String answer = "0";

			do {
				System.out.println("\nWhat would you like to do? [1-4]\n1) Find the lowest latency path " 
					+ "\n2) Determine whether the graph is copper only connected " 
					+ "\n3) Determine whether graph would be connected if any two vertices fail"
					+ "\n4) Quit");
				answer = inScan.nextLine();

				if(answer.equals("1")) { // lowest latency
					System.out.println("Enter a starting vertex: ");
					int start = inScan.nextInt();
					System.out.println("Enter an ending vertex: ");
					int end = inScan.nextInt();
					inScan.nextLine();
					// print the pathway info
					LinkedList pathway = network.get_lowest_latency(start, end);
					network.print_pathway(start, end, pathway);
					System.out.println("Available bandwidth: " + network.get_min_bandwidth(pathway) + " mbps");
				} else if(answer.equals("2")) { // copper connectivity
					System.out.println("\nThe graph is copper-only connected: " + network.is_copper_connected());
				} else if(answer.equals("3")) { // two vertices fail
					int[] failPair = network.graph_fail();

					if(failPair[0] == -1 && failPair[1] == -1) { // no pair disconnects graph
						System.out.println("\nNo two vertices disconnect the graph.");
					} else { // graph disconnected
						System.out.print("\nThe graph disconnects at vertex pair: ");
						System.out.println("(" + failPair[0] + ", " + failPair[1] + ")");
					}
				} else if(!answer.equals("4")) { 
					System.out.println("Invalid input.");
				}
			} while(!answer.equals("4")); // 4 means quit

		} catch(IOException e) {
			System.out.println("An error occurred.");
		}
	}
}