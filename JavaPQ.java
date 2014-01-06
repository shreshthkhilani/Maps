import java.util.NoSuchElementException;
import java.util.PriorityQueue;

/**
 * JavaPQ
 * Adapter for the Java Priority Queue
 * 
 */
public class JavaPQ<K extends Comparable<? super K>> 
implements BinaryMinHeapI<K> {
	
	private PriorityQueue<K> pq;
	
	// Constructor
	public JavaPQ() {
		pq = new PriorityQueue<K>();
	}
	
	// Checks if PQ is empty
	public boolean isEmpty() {
		return pq.size() == 0;
	}
	
	// Removes the minimum key in the PQ
	public K removeMin() {
		K e = pq.poll();
		if (e == null)
			throw new NoSuchElementException();
		return e;
	}
	
	// Inserts an element in the PQ
	public void insert(K element) {
		if (element == null)
			throw new NullPointerException();
		pq.offer(element);
	}
	
	// Updates the key of an element
	public void updateKey(K oldElem, K newElem) {
		if (oldElem == null || newElem == null)
			throw new NullPointerException();
		boolean present = pq.remove(oldElem);
		if (!present)
			throw new NoSuchElementException();
		pq.offer(newElem);
	}
}