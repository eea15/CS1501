// Emma Akbari (eea21)
// maintains min PQ
import java.util.TreeMap;
import java.util.ArrayList;

public class MinPQ {

	private static final int MAX_CAPACITY = 5000; // array backed heap size
	private Car[] heap; // array of Cars
	private String mode; // min PQ either price ("p") or mileage ("m")
	private TreeMap<String, Integer> indirection; // maps VIN to either price or mileage
	private TreeMap<String, ArrayList<Integer>> makeModel; // maps make/model to list of indices
	private int numItems;

	// constructor
	public MinPQ(String m) {
		heap = new Car[MAX_CAPACITY];
		mode = m;
		indirection = new TreeMap<String, Integer>();
		makeModel = new TreeMap<String, ArrayList<Integer>>();
	}

	// add @param Car c to next "leaf"
	public void add(Car c) {
		// add Car to the heap bottom and TreeMaps
		heap[numItems] = c;
		indirection.put(c.get_vin(), numItems);

		// add index for that make/model
		String m = c.get_make() + c.get_model();
		Integer loc = indirection.get(c.get_vin());
		add_list(m, loc);

		// maintain heap property
		push_up(numItems);
	
		// increment Cars
		numItems++;
	}

	// update Car @param vin with @param val user input
	// @param choice = 1: price, 2: mileage, 3: color
	public void update(String vin, int choice, String val) {
		// check that the Car exists
		Integer carIndex = indirection.get(vin);
		if(carIndex != null) {
			Car toUpdate = heap[carIndex]; // retrieve car to update

			if(choice == 1) { // update price
				toUpdate.set_price(Integer.parseInt(val));
				push_up((int)(indirection.get(vin)));
				push_down((int)(indirection.get(vin)));
			} else if(choice == 2) { // update mileage
				toUpdate.set_mileage(Integer.parseInt(val));
				push_up((int)(indirection.get(vin)));
				push_down((int)(indirection.get(vin)));
			} else { // update color
				toUpdate.set_color(val);
			}
		} else {
			System.out.println("Car does not exist.");
		}
	}

	// remove Car @param vin and heapify
	public void remove(String vin) {
		Integer index = indirection.get(vin); // index of car to remove

		if(index != null) {
			String replacement = heap[numItems-1].get_vin(); // "old" leaf vin 
			swap((int)index, numItems-1); // swap car to remove with leaf
			// remove leaf from makeModel
			String m = heap[numItems-1].get_make() + heap[numItems-1].get_model();
			Integer loc = new Integer(numItems-1);
			remove_list(m, loc);
			heap[numItems-1] = null; // remove leaf
			indirection.remove(vin);
			numItems--; 
			// maintain the heap property
			if(!replacement.equals(vin)) { // non leaf removal
				push_up((int)indirection.get(replacement));
				push_down((int)indirection.get(replacement));
			}
			// only print for price mode so it isn't printed twice
			if(mode.equals("p")) System.out.println("Remove successful.\n");
		} else { 
			if(mode.equals("p")) System.out.println("Invalid car.\n");
		}
	}

	// retrieve top of PQ (aka lowest price/mileage)
	public Car retrieve() {
		return heap[0];
	}

	// push the Car up the heap to maintain heap property
	// @param index of Car to check
	private void push_up(int index) {

		// end at root
		while(index != 0) {
			boolean needSwap = compare(index, (index-1)/2); 
			if(needSwap) {
				swap(index, (index-1)/2);
				index = (index-1)/2; // go to parent
			} 
			else break; // end loop if heap property in check
		}
	}

	// push a Car down the heap to maintain heap property
	// Car must exist in the heap (this is checked in update())
	private void push_down(int index) {
		// get children
		int lcIndex = 2 * index + 1;
		int rcIndex = 2 * index + 2;
		int childIndex; // higher priority child

		while(lcIndex < numItems || rcIndex < numItems) { // at least one child exists
			if(lcIndex < numItems && rcIndex < numItems) { // both children exist
				// set child to higher priority child
				boolean rightChildGreater = compare(lcIndex, rcIndex);
				if(rightChildGreater) childIndex = lcIndex;
				else childIndex = rcIndex;
			} else { // one child exists
				if(lcIndex < numItems) childIndex = lcIndex;
				else childIndex = rcIndex;
			}
			// swap if necessary
			boolean needSwap = compare(childIndex, index);
			if(needSwap) swap(childIndex, index);
			// update vars
			index = childIndex;
			lcIndex = 2 * index + 1;
			rcIndex = 2 * index + 2;
		}
	}

	// return true if Cars must be swapped to maintain heap property
	// i.e. if parent val greater than child val, p > c
	private boolean compare(int c, int p) {
		Car child = heap[c];
		Car parent = heap[p];

		if(mode.equals("p")) { // price mode
			if(child.get_price() - parent.get_price() < 0) return true;
			else return false;
		} else { // mileage mode
			if(child.get_mileage() - parent.get_mileage() < 0) return true;
			else return false;
		}
	}

	// swap Cars at given x and y
	// update their values in the indirection TreeMap
	private void swap(int x, int y) {
		Car temp = heap[x];
		heap[x] = heap[y];
		indirection.replace(heap[x].get_vin(), x);
		// update heap[x] list of indices
		String m = heap[x].get_make() + heap[x].get_model();
		add_list(m, new Integer(x));
		remove_list(m, new Integer(y));

		heap[y] = temp;
		indirection.replace(heap[y].get_vin(), y);
		// update heap[y] list of indices
		m = heap[y].get_make() + heap[y].get_model();
		add_list(m, new Integer(y));
		remove_list(m, new Integer(x));
	}

	// add to the ArrayList of indices for make/model
	// @param m: make+model, i: index of Car
	@SuppressWarnings("unchecked")
	private void add_list(String m, Integer i) {
		ArrayList indices;
		// instantiate list if it doesn't already exist
		if(!makeModel.containsKey(m)) indices = new ArrayList<Integer>();
		else indices = makeModel.get(m);
		
		indices.add(i); // add index 
		makeModel.put(m, indices);
	}

	// remove from the ArrayList of indices for make/model
	// @param m: make+model, i: index of Car
	@SuppressWarnings("unchecked")
	private void remove_list(String m, Integer i) {
		if(makeModel.containsKey(m)) {
			ArrayList indices = makeModel.get(m);
			indices.remove(i);
		}
	}

	// return an ArrayList of Cars given a particular make/model
	@SuppressWarnings("unchecked")
	public ArrayList get_cars(String m) {
		ArrayList c = new ArrayList<Car>();
		ArrayList<Integer> indices = makeModel.get(m);

		if(indices != null) {
			for(int i = 0; i < indices.size(); i++) {
				int index = indices.get(i);
				c.add(heap[index]);
			}	
		}
		
		return c;
	}

	//  debug
	public void print_make_model() {
		System.out.println("Make/model mapping:");
		for (String m: makeModel.keySet()){ 
            System.out.println(m + ": ");
            ArrayList list = makeModel.get(m);
            for(int i = 0; i < list.size(); i++) {
            	System.out.println(list.get(i));
            } 
		} 
	}

	// get a Car based on vin #
	public Car get_car(String v) {
		Integer carIndex = indirection.get(v);
		if(carIndex != null) return(heap[(int)carIndex]);
		else return null;
	}

	// print the heap/minPQ
	public void print_heap() {
		System.out.println();
		for(int i = 0; i < numItems; i++) {
			System.out.println(heap[i]);
		}
	}

	// print the HashMap of Car indices
	public void print_indirection() {
		System.out.println("Indirection mapping:");
		for (String v: indirection.keySet()){ 
            System.out.println(v + " " + indirection.get(v).toString());  
		} 
	}
}