import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class MinSpanningTree {
    
	/**
	 * Computes the minimum spanning tree using EAGER Prim's algorithm for the
	 * intersections that have been generated. Implementation should run in
	 * O(E*log(V)) or faster. Runtime: all steps run in O(E log V).
	 *
	 * @throws IllegalArgumentException
	 *             - if argument map is null
	 *             - if any element of the map (vertex or edge) is null
	 */
	public static List<Street> minST(Map<Point, List<Street>> map) {
		
		// Throw IllegalArgumentException if map is null
		if (map == null)
			throw new IllegalArgumentException();
		
		// PQ to hold Vertex-es
		BinaryMinHeapI<Vertex> pq = new JavaPQ<Vertex>();
		
		// Entry set of graph
		Set<Entry<Point, List<Street>>> graph_entry = 
				map.entrySet();
		
		// fullvl is an ArrayList which stores all the vertices in a graph
		ArrayList<Vertex> fullvl = new ArrayList<Vertex>();
		// vertexList is an ArrayList which stores all vertices in MST
		ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
		// List of Streets of MST
		List<Street> result = new ArrayList<Street>();
		
		// Extracts and creates all Vertex objects from graph
		for (Entry<Point, List<Street>> g: graph_entry) {
			// Throw IllegalArgumentException if any entry in graph is null
			if (g == null)
				throw new IllegalArgumentException();
			// Throw IllegalArgumentException if any part of entry is null
			if (g.getKey() == null || g.getValue() == null)
				throw new IllegalArgumentException();
			Vertex v = new Vertex(g.getKey(), Double.POSITIVE_INFINITY);
			fullvl.add(v);
		}
		
		// Creates a new Vertex of start node (distance = 0.0)
		// We can start at any node, so we start at 0th index of fullvl
		Vertex newSource = new Vertex (fullvl.get(0).p, 0.0);
		// Adds Vertex to PQ after updating the inPQ boolean value
		newSource.inPQ = true;
		pq.insert(newSource);
		// Updates the fullvl
		fullvl.add(0, newSource);
		
		boolean isFirst = true;
		
		// While PQ is not empty:
		while (!pq.isEmpty()) {
			
			// Remove current vertex from PQ, 
			// mark as visited, and store in vertexList
			Vertex current = pq.removeMin();
			current.visited = true;
			vertexList.add(current);
			
			// Store the previous vertex
			Vertex previous = current.previous;
			// In the case of the first Vertex we look at, previous is null
			if (isFirst)
				isFirst = false;
			// Otherwise, find a street linking the previous & current vertices
			else {
				List<Street> toAdd = map.get(previous.p);
				for (Street str: toAdd) {
					if ((isSamePoint(current.p, str.getFirstPoint()) &&
							isSamePoint(previous.p, str.getSecondPoint())) ||
							(isSamePoint(previous.p, str.getFirstPoint()) &&
								isSamePoint(current.p, str.getSecondPoint()))) {
						// Add to the MST List
						result.add(str);
						break;
					}	
				}
			}
			
			// Iterate through neighbors:
			List<Street> neighbors = map.get(current.p);
			int k = 0;
			while (k < neighbors.size()) {
				Street v = neighbors.get(k++);
				double vdistance = v.getDistance();
				Point vp;
				if (isSamePoint(current.p, v.getFirstPoint()))
					vp = v.getSecondPoint();
				else
					vp = v.getFirstPoint();
				Vertex vvertex = new Vertex(vp, vdistance);
				vvertex.previous = current;
				
				// If a neighbor is in the MST, skip it
				boolean flag = false;
				for (Vertex x: vertexList) {
					if(isSamePoint(x.p, vp)) {
						flag = true;
					}
				}
				if (flag) 
					continue;
				
				// If not, we find the Vertex of neighbor from fullvl
				flag = false;
				Vertex xpos = null;
				int pos = 0;
				for (Vertex x: fullvl) {
					if(isSamePoint(x.p, vp) && x.inPQ) {
						flag = true;
						xpos = x;
						break;
					}
					else
						pos++;
				}
				
				// If the neighbor is not in the PQ, insert it into PQ and 
				// also update the fullvl
				if (!flag) {
					vvertex.inPQ = true;
					pq.insert(vvertex);
					fullvl.add(pos - 1, vvertex);
				}
				// Otherwise, if the calculated distance is less than the
				// current distance, update the PQ and the fullvl
				else if(vvertex.distance < xpos.distance) {
					pq.updateKey(xpos, vvertex);
					fullvl.add(pos - 1, vvertex);
				}
			}
		}
		
		// Return the List of Streets that from the MST
		return result;
	}
	
	// This function is used to check whether two given points are the same
	// PointA and PointB are the same if they both have the same 'x's and 'y's
	public static boolean isSamePoint(Point a, Point b) {
		return (a.getX() == b.getX() && a.getY() == b.getY());
	}
	
	// Class to store a Vertex
	// A Vertex is a point and a distance (value and key)
	// Implements Comparable
	public static class Vertex implements Comparable<Vertex> {
		
		// Public variables for easy access
		public double distance;
		public Point p;
		public boolean visited;
		public Vertex previous;
		// update when we put the vertex in PQ
		public boolean inPQ;
		
		// Constructor
		public Vertex(Point p, double distance) {
			this.p = p;
			this.distance = distance;
			visited = false;
			previous = null;
			inPQ = false;
		}
		
		// Compares the distances of 2 Vertex-es
		@Override
		public int compareTo(Vertex o) {
			return Double.compare(distance, o.distance);
		}
	}
}