// Emma Akbari (eea21) 
// Graph representation for project 4

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.io.*;

@SuppressWarnings("unchecked")
public class Graph {

	// adjacency list representation
	private LinkedList graph[];
	private int size;
	private double time[];
	private int via[];
	private boolean visited[];
	private boolean seen[];

	// initialize a graph given v vertices
	public Graph(int v) {
		graph = new LinkedList[v];

		for(int i = 0; i < v; i++) {
			graph[i] = new LinkedList<Edge>();
		}

		size = v;
	}

	// add an edge to the graph
	public void add(Edge e) {
		// add the edge to both endpoints within the array
		graph[e.get_end1()].add(e);
		graph[e.get_end2()].add(e);
	}

	// get lowest latency path
	// -----------------------------------------------------------------------------------------------------------

	// find the fastest path between @param v1 and @param v2
	// @return path as an LinkedList of Edges 
	public LinkedList get_lowest_latency(int v1, int v2) {

		// check that the the vertices are valid options
		if(v1 < 0 || v1 >= size || v2 < 0 || v2 >= size) throw new IllegalArgumentException("Invalid vertex.");
		
		// initialize arrays to get fastest path
		set_arrays(v1);

		// start from v1
		int cur = v1;

		while(cur != v2) { // end when reach dest
			set_times(cur); // compute tentative time for each unvisited neighbor
			visited[cur] = true; // mark cur as visited
			if(cur == v2) break;
			cur = get_fastest_vertex(); // unvisited vertex w/smallest tentative distance
		}

		System.out.println("\nLatency: " + time[cur] + " sec");

		// backtrack from cur using via
		LinkedList<Edge> path = new LinkedList<Edge>();
		int parent = via[cur];

		while(parent != -1) {
			path.addFirst(get_edge(cur, parent));
			cur = parent;
			parent = via[cur];
		}

		return path;
	}

	// initialize arrays
	private void set_arrays(int start) {
		time = new double[size];
		via = new int[size];
		visited = new boolean[size];

		for(int i = 0; i < size; i++) {
			if(i != start) time[i] = Double.MAX_VALUE;
		}
		for(int i = 0; i < size; i++) {
			via[i] = -1; 
		}
	}

	// compute the tentative times for each neighbor of @param vertex
	private void set_times(int vertex) {
		LinkedList neighbors = graph[vertex];
		Edge nextEdge;
		int endpoint; // vertex --> edge --> endpoint
		
		for(int i = 0; i < neighbors.size(); i++) {
			// get the next edge and find neighbor vertex
			nextEdge = (Edge)neighbors.get(i);
			if(nextEdge.get_end1() != vertex) endpoint = nextEdge.get_end1();
			else endpoint = nextEdge.get_end2();

			// check if the neighbor (endpoint) has been visited
			if(!visited[endpoint]) {
				// compute tentative time
				double tentative = time[vertex] + compute_time(nextEdge);
				// replace time if faster
				if(tentative < time[endpoint]) {
					time[endpoint] = tentative;
					via[endpoint] = vertex;
				}
			}
		}
	}

	// compute time of an edge in seconds
	private double compute_time(Edge e) {
		// time = length/speed
		if(e.get_type().equals("copper")) return e.get_length()/230000000.0;
		else return e.get_length()/200000000.0;
	}

	// get fastest unvisited vertex, -1 if not found
	private int get_fastest_vertex() {
		int index = -1;
		double fastest = Double.MAX_VALUE;

		for(int i = 0; i < size; i++) {
			if(!visited[i]) {
				if(time[i] < fastest) {
					fastest = time[i];
					index = i;
				}
			}
		}

		return index;
	}

	// get the Edge with two specific endpoints, null if not found
	// @param one the starting vertex
	// @param two the ending vertex
	private Edge get_edge(int one, int two) {
		Edge found = null;
		LinkedList edges = graph[one];

		for(int i = 0; i < size; i++) {
			found = (Edge)edges.get(i);
			if(found.get_end1() == two || found.get_end2() == two) break;
			else found = null;
		}

		return found;
	}

	// print a pathway given a LinkedList of Edges in a readable format
	// @param s the starting vertex @param e the ending vertex
	public void print_pathway(int s, int d, LinkedList l) {

		LinkedList<Integer> vertices = new LinkedList<Integer>();
		vertices.add((Integer)s); // add start
		Edge e;
		
		System.out.print("Pathway:");

		for(int i = 0; i < l.size(); i++) {
			e = (Edge)l.get(i);
			Integer edgeOne = (Integer)e.get_end1();
			Integer edgeTwo = (Integer)e.get_end2();
			// add endpoint yet not in vertices
			if(vertices.contains(edgeOne)) vertices.add(edgeTwo);
			else vertices.add(edgeOne);
		}

		// print
		for(int i = 0; i < vertices.size(); i++) {
			System.out.print(" " + vertices.get(i) + " ");
			if(i != vertices.size() - 1) System.out.print("->");
		}

		System.out.println();
	}

	// @return min bandwidth given an LinkedList of Edges
	// LL must be non-empty
	public int get_min_bandwidth(LinkedList l) {
		Edge e = (Edge)l.get(0);
		int min = e.get_bandwidth();

		for(int i = 0; i < l.size(); i++) {
			e = (Edge)l.get(i);
			if(e.get_bandwidth() < min) min = e.get_bandwidth();
		}

		return min;
	}
	
	// -----------------------------------------------------------------------------------------------------------

	// determine copper only connectivity
	// -----------------------------------------------------------------------------------------------------------

	// return true if graph connected when only copper links are used
	public boolean is_copper_connected() {

		seen = new boolean[size]; // no vertices seen yet
		depth_first_search(0); // start DFS from vertex 0
		boolean connected = all_seen(); // check if connected

		return(connected);
	}

	// DFS thru graph via copper links, updating seen[]
	private void depth_first_search(int vertex) {

		seen[vertex] = true; // mark vertex as seen

		// iterate thru neighbors (LL of graph[vertex])
		Edge e;
		for(int i = 0; i < graph[vertex].size(); i++) {
			e = (Edge)graph[vertex].get(i);

			if(e.get_type().equals("copper")) {
				int dest; // determine destination via edge
				if(e.get_end1() == vertex) dest = e.get_end2();
				else dest = e.get_end1();
				// recurse to next unseen neighbor via copper
				if(!seen[dest]) depth_first_search(dest);
			}
		}
	}

	// check if all vertices have been seen, return true if so
	private boolean all_seen() {

		for(int i = 0; i < size; i++) {
			if(!seen[i]) return false;
		}

		return true;
	}

	// -----------------------------------------------------------------------------------------------------------


	// any two vertices fail
	// -----------------------------------------------------------------------------------------------------------

	// return two vertices that would cause the graph to disconnect
	// [-1, -1] if none found
	public int[] graph_fail() {

		int[] pair = new int[2]; // to return
		seen = new boolean[size]; // track seen vertices

		// for each vertex pair, "remove"
		for(int i = 0; i < size; i++) {
			for(int j = i + 1; j < size; j++) {
				int start = start(i, j);
				dfs_pairs(i, j, start); // dfs
				boolean connected = some_seen(i, j); // check connectivity
				// report failing vertex pair
				if(!connected) {
					pair[0] = i;
					pair[1] = j;
					return pair;
				}
				// reset seen
				seen = new boolean[size];
			}
		}

		// no disconnecting pair found
		pair[0] = -1;
		pair[1] = -1;
		return pair;
	}

	// DFS search where vertices @param ignore1 and @param ignore2 are "removed"
	// updates seen[]
	private void dfs_pairs(int ignore1, int ignore2, int vertex) {

		seen[vertex] = true; // mark vertex as seen

		// iterate thru neighbors (LL of graph[vertex])
		Edge e;
		for(int i = 0; i < graph[vertex].size(); i++) {
			e = (Edge)graph[vertex].get(i);

			if(e.get_end1() != ignore1 && e.get_end1() != ignore2 && e.get_end2() != ignore2 && e.get_end2() != ignore1) {
				int dest; // determine destination via edge
				if(e.get_end1() == vertex) dest = e.get_end2();
				else dest = e.get_end1();
				// recurse to next unseen neighbor
				if(!seen[dest]) dfs_pairs(ignore1, ignore2, dest);
			}
		}
	}

	// determine a starting vertex to start dfs 
	private int start(int not1, int not2) {

		for(int i = 0; i < size; i++) {
			if(i != not1 && i != not2) return i;
		}

		return -1; // no valid vertex found
	}

	// check if non-removed vertices have been seen, return true if so
	private boolean some_seen(int remove1, int remove2) {

		for(int i = 0; i < size; i++) {
			if(!seen[i] && i != remove1 && i != remove2) return false;
		}

		return true;
	}

	// -----------------------------------------------------------------------------------------------------------

	// print the graph as a string 
	public String toString() {
		StringBuilder info = new StringBuilder();
		for(int i = 0; i < graph.length; i++) {
			info.append("Vertex: " + i + "\n");
			for(int j = 0; j < graph[i].size(); j++) {
				info.append(graph[i].get(j));
			}
		}

		return info.toString();
	}
}