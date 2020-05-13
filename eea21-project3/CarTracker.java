// Emma Akbari (eea21)
// driver for project 3

import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

public class CarTracker {

	@SuppressWarnings("unchecked")
	public static void main(String args[]) {

		try {
			// set up variables
			Scanner inScan = new Scanner(System.in);
			File carFile = new File("cars.txt"); 
			Scanner fileRead = new Scanner(carFile);

			MinPQ carsP = new MinPQ("p"); // min PQ of cars by price
			MinPQ carsM = new MinPQ("m"); // min PQ of cars by mileage
			String car = fileRead.nextLine(); // scan past first line
			String[] tokens = car.split(":"); // car attributes

			while(fileRead.hasNextLine()) {
				car = fileRead.nextLine();
				tokens = car.split(":");

				Car newCar = new Car();
				newCar.set_vin(tokens[0]);
				newCar.set_make(tokens[1]);
				newCar.set_model(tokens[2]);
				newCar.set_price(Integer.parseInt(tokens[3]));
				newCar.set_mileage(Integer.parseInt(tokens[4]));
				newCar.set_color(tokens[5]);

				carsP.add(newCar);
				carsM.add(newCar);
			}

			System.out.println("Welcome to CarTracker!");
			int choice = 0;

			// give the user options until they quit (7)
			do {
				System.out.println("Would you like to\n1) view current cars\n2) add a car\n3) update a car\n" +
					"4) remove a car\n5) retrieve a car\n6) retrieve a car by make and model\n7) quit");
				choice = inScan.nextInt();
				inScan.nextLine();

				if(choice == 1) { // view cars
					carsP.print_heap();
					//carsM.print_heap();
				} else if(choice == 2) { // add car
					Car newCar = new Car();
					String input;

					System.out.println("Enter 17 character VIN: ");
					input = inScan.nextLine();
					newCar.set_vin(input);
					System.out.println("Enter make: ");
					input = inScan.nextLine();
					newCar.set_make(input);
					System.out.println("Enter model: ");
					input = inScan.nextLine();
					newCar.set_model(input);
					System.out.println("Enter price (int): ");
					input = inScan.nextLine();
					newCar.set_price(Integer.parseInt(input));
					System.out.println("Enter mileage (int): ");
					input = inScan.nextLine();
					newCar.set_mileage(Integer.parseInt(input));
					System.out.println("Enter color: ");
					input = inScan.nextLine();
					newCar.set_color(input);
					System.out.println();

					// add car to price and mileage PQ's
					carsP.add(newCar);
					carsM.add(newCar);

				} else if(choice == 3) { // update car
					// prompt user for vin and update choice
					System.out.println("Enter the car VIN number: ");
					String vinNum = inScan.nextLine();

					if(carsP.get_car(vinNum) != null) { // car exists
						System.out.println("\nCar details: " + carsP.get_car(vinNum));
						System.out.println("Do you want to update\n1) price\n2) mileage\n3) color");
						int update = inScan.nextInt();
						inScan.nextLine();
						System.out.println("Enter the new value to update to: ");
						String value = inScan.nextLine();
						System.out.println();

						// update both PQs
						carsP.update(vinNum, update, value);
						carsM.update(vinNum, update, value);
					} else {
						System.out.println("Not a valid car.\n");
					}

				} else if(choice == 4) { // remove car
					System.out.println("Enter the VIN number of the car to remove: ");
					String vin = inScan.nextLine();
					System.out.println();
					// remove from both PQs
					carsP.remove(vin);
					carsM.remove(vin);
				} else if(choice == 5) { // retrieve car
					System.out.println("Do you want to retrieve by lowest\n1) price\n2) mileage");
					int low = inScan.nextInt();
					inScan.nextLine();

					if(low == 1) { // by price
						Car lowest = carsP.retrieve();
						if(lowest != null) System.out.println(lowest);
						else System.out.println("Invalid car.");
					} else if(low == 2) { // by mileage
						Car lowest = carsM.retrieve();
						if(lowest != null) System.out.println(lowest);
						else System.out.println("Invalid car.");
					} else {
						System.out.println("Invalid choice.\n");
					}
				} else if(choice == 6) { // retrieve car based on make/model
					System.out.println("Enter the make: ");
					String mm = inScan.nextLine();
					System.out.println("Enter the model: ");
					mm += inScan.nextLine();
					System.out.println("Do you want to retrieve by lowest\n1) price\n2) mileage");
					int low = inScan.nextInt();
					inScan.nextLine();

					if(low == 1) { // price
						// get an arraylist of cars, make a new pq, get the top of the pq
						ArrayList makeAndModel = carsP.get_cars(mm);
						MinPQ findCar = new MinPQ("p");

						for(int i = 0; i < makeAndModel.size(); i++) {
							findCar.add((Car)(makeAndModel.get(i)));
						}

						Car min = findCar.retrieve();
						if(min != null) System.out.println("The car is: " + min);
						else System.out.println("That car does not exist.\n");
					} else if(low == 2) { // mileage
						// get an arraylist of cars, make a new pq, get the top of the pq
						ArrayList<Car> makeAndModel = carsM.get_cars(mm);
						MinPQ findCar = new MinPQ("m");

						for(int i = 0; i < makeAndModel.size(); i++) {
							findCar.add(makeAndModel.get(i));
						}

						Car min = findCar.retrieve();
						if(min != null) System.out.println("The car is: " + min);
						else System.out.println("That car does not exist.\n");

					} else {
						System.out.println("Invalid choice.");
					}
				} else if(choice != 7) {
					System.out.println("Invalid input.");
				}
			} while(choice != 7);


		} catch(IOException e) {
			System.out.println("An error occurred.");
		}
	}
}