import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContactTracer {

	class Vertex {
		List<String> toVertex = new ArrayList<>();
		Set<String> toVertexUnordered = new HashSet<>();
		List<Integer> time = new ArrayList<Integer>();
	}

	HashMap<String, Vertex> graph = new HashMap<String, Vertex>();

	List<Integer> timesList = new ArrayList<Integer>();
	List<String> namesList = new ArrayList<String>();

	/**
	 * Initialises an empty ContactTracer with no populated contact traces.
	 */
	public ContactTracer() {
		// TODO: implement this!
	}

	/**
	 * Initialises the ContactTracer and populates the internal data structures with
	 * the given list of contract traces.
	 * 
	 * @param traces to populate with
	 * @require traces != null
	 */
	public ContactTracer(List<Trace> traces) {
		// TODO: implement this!
		// loop the traces and get each trace and add it to the graph twice( to make
		// undirectional)
		//
		for (int i = 0; i < traces.size(); i++) {
			Vertex v;
			Trace t = traces.get(i);

			if (graph.containsKey(t.getPerson1())) {
				v = graph.get(t.getPerson1());

			} else {
				v = new Vertex();
			}
			v.toVertex.add(t.getPerson2());
			v.toVertexUnordered.add(t.getPerson2());
			v.time.add(t.getTime());
			graph.put(t.getPerson1(), v);

			if (graph.containsKey(t.getPerson2())) {
				v = graph.get(t.getPerson2());
			} else {
				v = new Vertex();
			}
			v.toVertex.add(t.getPerson1());
			v.toVertexUnordered.add(t.getPerson1());
			v.time.add(t.getTime());
			graph.put(t.getPerson2(), v);
		}
		// graph is ready so we are sorting the names in the each vertix of the graph
		// using the merge sort. We will sort the names as per times in the Vetex class.
		Iterator it = graph.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// System.out.println(pair.getKey() + " = " + pair.getValue());
			Vertex v = (Vertex) pair.getValue();
			timesList = v.time;
			namesList = v.toVertex;
			mergeSortWithProperinputs(timesList, 0, timesList.size() - 1, false, namesList);
			v.time = (ArrayList<Integer>) timesList;
			v.toVertex = (ArrayList<String>) namesList;
			graph.put((String) pair.getKey(), v);

		}

	}

	/**
	 * Adds a new contact trace to
	 * 
	 * If a contact trace involving the same two people at the exact same time is
	 * already stored, do nothing.
	 * 
	 * @param trace to add
	 * @require trace != null
	 */
	public void addTrace(Trace trace) {
		// TODO: implement this!
		Vertex v;
		int time = trace.getTime();
		int rightIndexSortedList = 0;
		if (graph.containsKey(trace.getPerson1())) {
			v = graph.get(trace.getPerson1());

		} else {
			v = new Vertex();
		}

		if (v.time.isEmpty()) {
			rightIndexSortedList = 0;
		} else {
			int x = binarySearch(v.time, 0, v.time.size() - 1, time, midcopy);
			if (time > v.time.get(x)) {

				rightIndexSortedList = x + 1;
			} else {

				rightIndexSortedList = x;
			}
		}
		v.toVertex.add(rightIndexSortedList, trace.getPerson2());
		v.toVertexUnordered.add(trace.getPerson2());
		v.time.add(rightIndexSortedList, trace.getTime());
		graph.put(trace.getPerson1(), v);

		if (graph.containsKey(trace.getPerson2())) {
			v = graph.get(trace.getPerson2());
		} else {
			v = new Vertex();
		}
		if (v.time.isEmpty()) {
			rightIndexSortedList = 0;
		} else {
			int x = binarySearch(v.time, 0, v.time.size() - 1, time, midcopy);
			if (time > v.time.get(x)) {

				rightIndexSortedList = x + 1;
			} else {

				rightIndexSortedList = x;
			}
		}
		v.toVertex.add(rightIndexSortedList, trace.getPerson1());
		v.toVertexUnordered.add(trace.getPerson1());
		v.time.add(rightIndexSortedList, trace.getTime());
		graph.put(trace.getPerson2(), v);

	}

	/**
	 * Gets a list of times that person1 and person2 have come into direct contact
	 * (as per the tracing data).
	 *
	 * If the two people haven't come into contact before, an empty list is
	 * returned.
	 * 
	 * Otherwise the list should be sorted in ascending order.
	 * 
	 * @param person1
	 * @param person2
	 * @return a list of contact times, in ascending order.
	 * @require person1 != null && person2 != null
	 */
	public List<Integer> getContactTimes(String person1, String person2) {
		// TODO: implement this!
		List<Integer> sol = new ArrayList<Integer>();
		List<Integer> tempList = new ArrayList<Integer>();
		List<String> namesList = new ArrayList<String>();
		int flagForPerson = 0;

		if (graph.get(person1) == null || graph.get(person2) == null || graph.get(person1).toVertex.isEmpty()
				|| graph.get(person2).toVertex.isEmpty()) {
			return null;
		}
		if (graph.get(person1).time.size() > graph.get(person2).time.size()) {
			tempList = graph.get(person2).time;
			namesList = graph.get(person2).toVertex;
			flagForPerson = 2;
		} else {
			flagForPerson = 1;
			tempList = graph.get(person1).time;
			namesList = graph.get(person1).toVertex;
		}
		for (int i = 0; i < tempList.size(); i++) {
			if (flagForPerson == 1) {
				if (namesList.get(i).equals(person2)) {
					sol.add(tempList.get(i));
				}
			}

			if (flagForPerson == 2) {
				if (namesList.get(i).equals(person1)) {
					sol.add(tempList.get(i));
				}
			}
		}
		return sol;
	}

	/**
	 * Gets all the people that the given person has been in direct contact with
	 * over the entire history of the tracing dataset.
	 * 
	 * @param person to list direct contacts of
	 * @return set of the person's direct contacts
	 */
	public Set<String> getContacts(String person) {
		// TODO: implement this!
		return graph.get(person).toVertexUnordered;
	}

	/**
	 * Gets all the people that the given person has been in direct contact with at
	 * OR after the given timestamp (i.e. inclusive).
	 * 
	 * @param person    to list direct contacts of
	 * @param timestamp to filter contacts being at or after
	 * @return set of the person's direct contacts at or after the timestamp
	 */
	public Set<String> getContactsAfter(String person, int timestamp) {
		// TODO: implement this!
		Set<String> sol = new HashSet<String>();
		Vertex v;
		int time = timestamp;
		int rightIndexSortedList = 0;
		if (graph.containsKey(person)) {
			v = graph.get(person);

		} else {
			v = new Vertex();
		}

		if (v.time.isEmpty()) {
			rightIndexSortedList = 0;
			return null;
		} else {
			int x = binarySearch(v.time, 0, v.time.size() - 1, time, midcopy);
			if (time > v.time.get(x)) {

				rightIndexSortedList = x + 1;
			} else {

				rightIndexSortedList = x;
			}
			for (int i = rightIndexSortedList; i < v.time.size(); i++) {
				sol.add(v.toVertex.get(i));
			}

		}
		return sol;
	}

	/*
	 * The following code will send all the persons that have in contact between timestramp
	 * to max. 
	 * */
	private Vertex getToVertexBetween(String person, int timestamp, int max) {
		// TODO: implement this!
		List<String> sol = new ArrayList<String>();
		List<Integer> timeList = new ArrayList<>();
		Set<String> hashSol = new HashSet<String>();
		Vertex v;
		Vertex v2;
		int time = timestamp;
		int rightIndexSortedList = 0;
		int endIndex = 0;
		if (graph.containsKey(person)) {
			v = graph.get(person);

		} else {
			v = new Vertex();
		}

		if (v.time.isEmpty()) {
			rightIndexSortedList = 0;
			return null;
		} else {
			int x = binarySearch(v.time, 0, v.time.size() - 1, time, midcopy);
			if (time > v.time.get(x)) {

				rightIndexSortedList = x + 1;
			} else {

				rightIndexSortedList = x;
			}
			if (max == Integer.MAX_VALUE) {
				endIndex = v.time.size() - 1;
			} else {
				int time2 = time + 60;// put the time till person can spread
				int y = binarySearch(v.time, 0, v.time.size() - 1, time2, midcopy);
				if (time2 > v.time.get(y)) {

					endIndex = y + 1;
				} else {

					endIndex = y;
				}
				if (endIndex >= v.time.size()) {
					endIndex = v.time.size() - 1;
				}

			}

			for (int i = rightIndexSortedList; i <= endIndex; i++) {
				sol.add(v.toVertex.get(i));
				timeList.add(v.time.get(i));
				hashSol.add(v.toVertex.get(i));
			}

			v2 = new Vertex();
			v2.toVertex = sol;
			v2.time = timeList;
			v2.toVertexUnordered = hashSol;
		}
		return v2;
	}

	/**
	 * Initiates a contact trace starting with the given person, who became
	 * contagious at timeOfContagion.
	 * 
	 * Note that the return set shouldn't include the original person the trace
	 * started from.
	 * 
	 * @param person          to start contact tracing from
	 * @param timeOfContagion the exact time person became contagious
	 * @return set of people who may have contracted the disease, originating from
	 *         person
	 */
	public Set<String> contactTrace(String person, int timeOfContagion) {
		// TODO: implement this!
		// Mark all the vertices as not visited(By default
		// set as false)

		HashSet<String> visitedList = new HashSet<String>();
		// Create a queue for BFS
		if (graph.containsKey(person)) {
			LinkedList<String> queue = new LinkedList<>();
			LinkedList<Integer> queueTime = new LinkedList<>();
			String currentPerson = person;
			Integer currentTimeAtSearch = timeOfContagion;
			// Mark the current node as visited and enqueue it
			// visited[s]=true;
			// here s is person
			visitedList.add(currentPerson);
			queue.add(currentPerson);
			queueTime.add(currentTimeAtSearch);
			while (queue.size() != 0) {
				// Dequeue a vertex from queue and print it
				currentPerson = queue.poll();
				currentTimeAtSearch = queueTime.poll();
				// Get all adjacent vertices of the dequeued vertex s
				// If a adjacent has not been visited, then mark it
				// visited and enqueue it

				List<String> personList = null;
				List<Integer> timeList = null;
				Vertex v = null;
				if (currentPerson.equals(person)) {
					v = getToVertexBetween(currentPerson, currentTimeAtSearch, Integer.MAX_VALUE);
					personList = v.toVertex;
					timeList = v.time;

				} else {
					v = getToVertexBetween(currentPerson, currentTimeAtSearch+60, Integer.MAX_VALUE);
					personList = v.toVertex;
					timeList = v.time;

				}

				for (int l = 0; l < personList.size(); l++) {
					String n = personList.get(l);
					Integer r = timeList.get(l);
					if (!visitedList.contains(n)) {
						visitedList.add(n);
						queue.add(n);
						queueTime.add(r);
					}
				}
			}
			visitedList.remove(person);
			return visitedList;
		}
		return null;
	}

	private void mergeSortWithProperinputs(List<Integer> input, int left, int right, boolean reversed,
			List<String> names2) {
		// TODO Auto-generated method stub
		if (left < right) {

			// Same as (left + right) / 2, but avoids overflow
			// for large left and right
			int mid = left + (right - left) / 2;

			// Sort first and second halves by making method calls
			mergeSortWithProperinputs(input, left, mid, reversed, names2);
			mergeSortWithProperinputs(input, mid + 1, right, reversed, names2);

			merge(input, left, mid, right, reversed, names2);
		}

	}

	private void merge(List<Integer> input, int startIndexOfFirstArray, int mid, int end, boolean reversed,
			List<String> names2) {
		// TODO Auto-generated method stub
		int startIndexOfSecondArray = mid + 1;

		// If the direct merge is already sorted

		if (input.get(mid).compareTo(input.get(startIndexOfSecondArray)) <= 0 && reversed == false) {
			return;
		} else if (input.get(mid).compareTo(input.get(startIndexOfSecondArray)) >= 0 && reversed == true) {
			return;
		}

		// Two pointers to maintain startIndexOfFirstArray
		// of both arrays to merge
		while (startIndexOfFirstArray <= mid && startIndexOfSecondArray <= end) {

			// If element 1 is in right place

			if (input.get(startIndexOfFirstArray).compareTo(input.get(startIndexOfSecondArray)) <= 0
					&& reversed == false) {
				startIndexOfFirstArray++;
			} else if (input.get(startIndexOfFirstArray).compareTo(input.get(startIndexOfSecondArray)) >= 0
					&& reversed == true) {
				startIndexOfFirstArray++;
			} else {
				Integer temp = input.get(startIndexOfSecondArray);
				String tempString = names2.get(startIndexOfSecondArray);
				int counterIndexValue = startIndexOfSecondArray;

				// Shift all the elements between element 1
				// element 2, right by 1.
				while (counterIndexValue != startIndexOfFirstArray) {
					input.set(counterIndexValue, input.get(counterIndexValue - 1));
					names2.set(counterIndexValue, names2.get(counterIndexValue - 1));
					counterIndexValue--;
				}
				input.set(startIndexOfFirstArray, temp);
				names2.set(startIndexOfFirstArray, tempString);
				// Update all the pointers. Here we are updating all of the pointers we are
				// considering
				startIndexOfFirstArray++;
				mid++;
				startIndexOfSecondArray++;
			}
		}
	}

	int midcopy;

	private int binarySearch(List<Integer> arr, int low, int high, int key, int midcopy) {
		if (high < low)
			return midcopy;

		/* low + (high - low)/2; */
		int mid = midcopy = (low + high) / 2;
		if (key == arr.get(mid))
			return mid;
		if (key > arr.get(mid))
			return binarySearch(arr, (mid + 1), high, key, midcopy);
		return binarySearch(arr, low, (mid - 1), key, midcopy);
	}

}
