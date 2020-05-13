// Emma Akbari (eea21) 
// program to create DLB for project 1

public class DictTrie {

	private Node root; // DLB root

	// nodes in DLB
	private static class Node {
		char data; // letter in key
		Node next; // next Node in LL
		Node below; // child node

		// constructor
		public Node() {
			data = '\0';
			next = null;
			below = null;
		}
	}

	// initialize empty trie
	public DictTrie(){
		root = new Node();
	}

	// add a word/key to the trie
	public void put(String word) {

		Node curr = root;
		Node prev = new Node();

		// go thru nodes and create if doesn't exist on that level
		for(int i = 0; i < word.length(); i++) {

			if(curr.data == '\0') { // add key to empty node
				curr.data = word.charAt(i);
				if(curr.below == null) curr.below = new Node();
				prev = curr;
				curr = curr.below;
			} else if(curr.data == word.charAt(i)) { // char matches part of key, continue
				if(curr.below == null) curr.below = new Node();
				prev = curr;
				curr = curr.below;
			} else { // char doesn't match node
				// loop thru siblings until either node found or reach end of LL
				while(curr.data != word.charAt(i) && curr.next != null) {
					prev = curr;
					curr = curr.next;
				}

				if(curr.data != word.charAt(i) && curr.next == null) { // node doesn't exist yet, create
					curr.next = new Node();
					curr.next.data = word.charAt(i);
					curr.next.below = new Node();
					prev = curr.next;
					curr = curr.next.below;
				} else { // node already exists, continue
					if(curr.below == null) curr.below = new Node();
					prev = curr;
					curr = curr.below;
				}
			}
		} // end for

		// terminating node with ^ 	
		if(curr.data != '\0' && curr.data != '^') { // shift nodes to not overwrite 
			Node insert = new Node();
			insert.data = '^';
			prev.below = insert;
			insert.next = curr;
		} else {
			curr.data = '^';
		}
	}

	// retrieve a word from the trie based on key, null if not found
	public String get(String key) {
		if(key == null) {
			throw new IllegalArgumentException("null argument");
		} 

		if(root == null) System.out.println("root == null");
		
		Node n = get(root, key, 0); // find key and return String

		if(n != null) return key; // found
		else return null; // not found
		
	}

	// recursive helper method to retrieve word 
	// Node non null if prefix/key found
	private Node get(Node curr, String key, int d) {
		if(curr == null) {
			return null; // base case key not found
		} 
		if(d == key.length()) {
			return curr; // base case key found
		}

		char c = key.charAt(d); // key char

		if(curr.data == c) {
			return get(curr.below, key, d+1); // go to child node
		} else {
			return get(curr.next, key, d); // go to sibling node 
		}
	}

	// retrieve keys given a prefix, null if not found
	public String[] get_prefix_keys(String prefix) {
		if(prefix == null) {
			throw new IllegalArgumentException("null argument");
		} 

		if(root == null) System.out.println("root == null");
		
		String[] prefixes = new String[5]; // array of keys with prefix
		
		Node validPrefix = get(root, prefix, 0); // true if valid
		
		if(validPrefix != null) { // valid prefix
			get_prefix_keys(prefixes, validPrefix, prefix, 0);
		}

		return prefixes;
	}

	// helper method to retrieve keys with given prefix
	// fills String[] with keys
	private void get_prefix_keys(String[] p, Node curr, String key, int i) {
		if(curr == null || i == 5) {
			// base case end of key or filled p
		} else if(curr.data == '^') { // found key
			p[i] = key; // add key to array
			get_prefix_keys(p, curr.next, key, get_logical_len(p));
		} else { // still looking for key
			if(curr.below != null) { // go to child node
				key += curr.data; // update key
				get_prefix_keys(p, curr.below, key, i);
			}
			if(curr.next != null) { // go to sibling node
				key = key.substring(0, key.length() - 1); // update key
				get_prefix_keys(p, curr.next, key, get_logical_len(p));
			}
		}
	}

	// returns the logical length of @param array
	public int get_logical_len(Object[] array) {
		int len = 0;

		for(int i = 0; i < array.length; i++) {
			if(array[i] != null) {
				len++;
			} else {
				break;
			}
		} // end for

		return len;
	}

	// The following methods are used for sorting in ac_test.java and UserHist.java

	// convert null Strings in array to "~"
	public void non_null(String[] arr){
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] == null) {
				arr[i] = "~";
			}
		}
	}

	// convert "~" to null 
	public void to_null(String[] arr){
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] == "~") {
				arr[i] = null;
			}
		}
	}
}