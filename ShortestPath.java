import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ShortestPath {
    
	/**
	 * Calculates the shortest path between two Intersections via Dijkstra's
	 * algorithm. Returns a list of Intersections that forms the path. The list
	 * has the source of the path at the head (first element) and the
	 * destination at the tail (last element).
	 *
	 * @throws IllegalArgumentException
	 *             - if any of the arguments graph, a, or b are null
	 *             - if any component (vertex or edge) of the graph is null
	 *             - if locations of a and/or b are NOT part of the graph
	 */
	public static List<Intersection> getPath(Map<Point, List<Street>> graph,
                                             Intersection a, Intersection b) {
		
		// Throw IllegalArgumentException if graph, a, or b are null
		if (graph == null || a == null || b == null)
			throw new IllegalArgumentException();
		
		// PQ to hold Vertex-es
		BinaryMinHeapI<Vertex> pq = new JavaPQ<Vertex>();
		
		// Entry set of graph
		Set<Entry<Point, List<Street>>> graph_entry = 
				graph.entrySet();
		
		// fullvl is an ArrayList which stores all the vertices in a graph
		ArrayList<Vertex> fullvl = new ArrayList<Vertex>();
		// vertexList is an ArrayList which stores all vertices in path
		ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
		
		boolean aInGraph = false;
		int i = 0;
		int apos = 0;
		boolean bInGraph = false;

		// Extracts and creates all Vertex objects from graph
		// Also adds these Vertex-es to PQ
		for (Entry<Point, List<Street>> g: graph_entry) {
			// Throw IllegalArgumentException if any entry in graph is null
			if (g == null)
				throw new IllegalArgumentException();
			// Throw IllegalArgumentException if any part of entry is null
			if (g.getKey() == null || g.getValue() == null)
				throw new IllegalArgumentException();
			Vertex v = new Vertex(g.getKey(), Double.POSITIVE_INFINITY);
			pq.insert(v);
			fullvl.add(v);
			if (isSamePoint(g.getKey(), a.getLocation())) {
				aInGraph = true;
				apos = i;
			}
			if (isSamePoint(g.getKey(), b.getLocation()))
				bInGraph = true;
			i++;
		}
		
		// Throw IllegalArgumentException if start and end not in graph
		if(!aInGraph || !bInGraph)
			throw new IllegalArgumentException();
		
		// Creates a new Vertex of start node (distance = 0.0)
		Vertex newSource = new Vertex(a.getLocation(), 0.0);
		
		// Update PQ and entry in fullvl
		pq.updateKey(fullvl.get(apos), newSource);
		fullvl.remove(apos);
		fullvl.add(newSource);
		
		// While PQ is not empty:
		while (!pq.isEmpty()) {
			
			// Remove current vertex from PQ, 
			// mark as visited, and store in vertexList
			Vertex current = pq.removeMin();
			current.visited = true;
			vertexList.add(current);
			
			// When we reach the end node, we break (don't need more distances)
			if (isSamePoint(current.p, b.getLocation()))
				break;
			
			// Iterate through neighbors of current Vertex
			List<Street> neighbors = graph.get(current.p);
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
				
				// Finds the potential distance of neighboring Vertex
				double potentialDistance = current.distance + vdistance;
				double nextd = 0;
				
				// Extracts the Vertex from fullvl
				int j = 0;
				for (j = 0; j < i; j++) {
					Vertex fromList = fullvl.get(j);
					if (isSamePoint(fromList.p, vp)) {
						nextd = fromList.distance;
						break;
					}
				}
				
				// If potential distance is less than current distance,
				// Update PQ and fullvl
				if (potentialDistance < nextd) {
					// Very important to keep track of previous Vertex
					vvertex.previous = current;
					vvertex.distance = potentialDistance;
					pq.updateKey(fullvl.get(j), vvertex);
					fullvl.remove(j);
					fullvl.add(vvertex);
				}
			}
		}
		
		// List to store the shortest path from end to start
		List<Intersection> reverseresult = new ArrayList<Intersection>();
		Vertex target = vertexList.get(vertexList.size() - 1);
		
		Intersection last = new Intersection(target.p, graph.get(target.p));
		reverseresult.add(last);
		
		while(target.previous != null) {
			target = target.previous;
			Intersection u = new Intersection(target.p, graph.get(target.p));
			reverseresult.add(u);
		} 
		
		// List to store the shortest path (reverses the previous list)
		List<Intersection> result = new ArrayList<Intersection>();
	
		for (i = reverseresult.size() - 1; i >= 0; i--) {
			result.add(reverseresult.get(i));
		}
		
		// Returns the List of Intersections from start to end
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
		
		// Constructor
		public Vertex(Point p, double distance) {
			this.p = p;
			this.distance = distance;
			visited = false;
			previous = null;
		}
		
		// Compares the distances of 2 Vertex-es
		@Override
		public int compareTo(Vertex o) {
			return Double.compare(distance, o.distance);
		}
	}
}