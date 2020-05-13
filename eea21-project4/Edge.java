// Emma Akbari (eea21)
// Edge objects for project 4

public class Edge {

	private int end1; // endpoint 1
	private int end2; // endpoint 2
	private String cableType; // optical or copper cable
	private int bandwidth; // megabits/sec
	private int cableLength; // length in meters

	// default constructor 
	public Edge() {}

	// initialize everything
	public Edge(int e1, int e2, String t, int b, int l) {
		end1 = e1;
		end2 = e2;
		cableType = t;
		bandwidth = b;
		cableLength = l;
	}

	// mutator for endpoint 1
	public void set_end1(int e1) {
		end1 = e1;
	}

	// accessor for endpoint 1
	public int get_end1() {
		return end1;
	}

	// mutator for endpoint 2
	public void set_end2(int e2) {
		end2 = e2;
	}

	// accessor for endpoint 2
	public int get_end2() {
		return end2;
	}

	// mutator for cable type
	public void set_type(String c) {
		cableType = c;
	}

	// accessor for cable type
	public String get_type() {
		return cableType;
	}

	// mutator for bandwidth
	public void set_bandwidth(int b) {
		bandwidth = b;
	}

	// accessor for bandwidth
	public int get_bandwidth() {
		return bandwidth;
	}

	// mutator for cable length
	public void set_length(int c) {
		cableLength = c;
	}

	// accessor for endpoint 1
	public int get_length() {
		return cableLength;
	}

	// print info
	public String toString() {
		return("Edge: " + end1 + " " + end2 + " " + cableType + " " + bandwidth + " " + cableLength + "\n");
	}
}