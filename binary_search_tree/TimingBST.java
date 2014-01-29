package assignBSTSpellCheck;

/* Implemented by C.M. */
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.TreeSet;

@SuppressWarnings("unused")
public class TimingBST {

	public static void main(String[] args) {
		timeBuildingBST();
		timeBestBSTAdd();
		timeBestContains();
	}


	/**
	 * Times the performance of add method for our BST
	 */
	private static void timeBuildingBST() {
		System.out.println("Building BST:");
		// Experiment timesToLoop times and use the average running time
		int timesToLoop = 100;

		// For each problem size n . . .
		for (int n = 1000; n <= 10000; n += 1000) {

			long startTime, midpointTime, stopTime;

			startTime = System.nanoTime();

			// First, spin computing stuff until one second has gone by.
			// This allows this thread to stabilize.
			while (System.nanoTime() - startTime < 1000000000) { 
				// empty block 
			}
			// Now, run the test. 
			startTime = System.nanoTime();

			for (int i=0;i<(timesToLoop);i++) {
				// adds a random collection to the BST
				BinarySearchTree<Integer> set = new BinarySearchTree<Integer>();
				set.addAll(generateRandomCollection(n, timesToLoop));
			}

			midpointTime = System.nanoTime();

			for (long i = 0; i < timesToLoop; i++) { 
				BinarySearchTree<Integer> set = new BinarySearchTree<Integer>();
				generateRandomCollection(n, timesToLoop);
			}

			stopTime = System.nanoTime();

			// Compute the time, subtract the cost of running the loop and generating
			// random collection from the cost of running the loop, adding random collection
			// Average it over the number of runs.
			double averageTimeRandom = ((midpointTime - startTime) - (stopTime - midpointTime))
					/ timesToLoop;

			while (System.nanoTime() - startTime < 1000000000) { 
				// empty block 
			}

			startTime = System.nanoTime();

			for (int i=0;i<(timesToLoop);i++) {
				// adds a sorted collection to the BST
				BinarySearchTree<Integer> set = new BinarySearchTree<Integer>();
				set.addAll(generateSortedCollection(n));
			}

			midpointTime = System.nanoTime();

			for (long i = 0; i < timesToLoop; i++) { 
				BinarySearchTree<Integer> set = new BinarySearchTree<Integer>();
				generateSortedCollection(n);
			}

			stopTime = System.nanoTime();

			// Compute the time, subtract the cost of running the loop and generating
			// sorted collection from the cost of running the loop, adding sorted collection
			// Average it over the number of runs.
			double averageTimeSorted = ((midpointTime - startTime) - (stopTime - midpointTime))
					/ timesToLoop;

			System.out.println(n + "\t" + averageTimeRandom + "\t" + averageTimeSorted);
		}
	}

	
	/**
	 * Times the performance of add method for our BST and Java's TreeSet.
	 */
	private static void timeBestBSTAdd() {
		System.out.println("Best BST Add:");
		// Experiment timesToLoop times and use the average running time
		int timesToLoop = 100;

		// For each problem size n . . .
		for (int n = 1000; n <= 10000; n += 1000) {

			long startTime, midpointTime, stopTime;

			startTime = System.nanoTime();

			// First, spin computing stuff until one second has gone by.
			// This allows this thread to stabilize.
			while (System.nanoTime() - startTime < 1000000000) { 
				// empty block 
			}
			// Now, run the test. 
			startTime = System.nanoTime();

			for (int i=0;i<(timesToLoop);i++) {
				// adds a random collection to the BST
				BinarySearchTree<Integer> set = new BinarySearchTree<Integer>();
				set.addAll(generateRandomCollection(n, timesToLoop));
			}

			midpointTime = System.nanoTime();

			for (long i = 0; i < timesToLoop; i++) { 
				BinarySearchTree<Integer> set = new BinarySearchTree<Integer>();
				generateRandomCollection(n, timesToLoop);
			}

			stopTime = System.nanoTime();

			// Compute the time, subtract the cost of running the loop and generating
			// random collection from the cost of running the loop, adding random collection
			// Average it over the number of runs.
			double averageTimeBST = ((midpointTime - startTime) - (stopTime - midpointTime))
					/ timesToLoop;

			while (System.nanoTime() - startTime < 1000000000) { 
				// empty block 
			}

			startTime = System.nanoTime();

			for (int i=0;i<(timesToLoop);i++) {
				// adds a random collection to a Java TreeSet
				TreeSet<Integer> set = new TreeSet<Integer>();
				set.addAll(generateRandomCollection(n, timesToLoop));
			}

			midpointTime = System.nanoTime();

			for (long i = 0; i < timesToLoop; i++) { 
				TreeSet<Integer> set = new TreeSet<Integer>();
				generateRandomCollection(n, timesToLoop);
			}

			stopTime = System.nanoTime();

			// Compute the time, subtract the cost of running the loop and generating
			// random collection from the cost of running the loop, adding random collection
			// Average it over the number of runs.
			double averageTimeJava = ((midpointTime - startTime) - (stopTime - midpointTime))
					/ timesToLoop;

			System.out.println(n + "\t" + averageTimeBST + "\t" + averageTimeJava);
		}
	}

	
	/**
	 * Times the performance of contains method for our BST and Java's TreeSet.
	 */
	private static void timeBestContains() {
		System.out.println("Best BST Contains:");
		// Experiment timesToLoop times and use the average running time
		int timesToLoop = 20;

		// For each problem size n . . .
		for (int n = 1000; n <= 10000; n += 1000) {

			long startTime, midpointTime, stopTime;

			startTime = System.nanoTime();

			// First, spin computing stuff until one second has gone by.
			// This allows this thread to stabilize.
			while (System.nanoTime() - startTime < 1000000000) { 
				// empty block 
			}
			// Now, run the test. 
			startTime = System.nanoTime();

			for (int i=0;i<(timesToLoop);i++) {
				// generates a random collection of integers
				Collection<Integer> c = generateRandomCollection(n, timesToLoop);
				// uses this collection to construct a BST
				BinarySearchTree<Integer> set = new BinarySearchTree<Integer>();
				set.addAll(c);
				// calls contains method for each item in the collection
				for(Integer k : c)
					set.contains(k);
			}

			midpointTime = System.nanoTime();

			for (long i = 0; i < timesToLoop; i++) { 
				Collection<Integer> c = generateRandomCollection(n, timesToLoop);
				BinarySearchTree<Integer> set = new BinarySearchTree<Integer>();
				set.addAll(c);
			}

			stopTime = System.nanoTime();

			// Compute the time, subtract the cost of running the loop and generating
			// random BST from the cost of running the loop, generating random BST and  
			// calling contains method.
			// Average it over the number of runs.
			double averageTimeBST = ((midpointTime - startTime) - (stopTime - midpointTime))
					/ timesToLoop;

			while (System.nanoTime() - startTime < 1000000000) { 
				// empty block 
			}

			startTime = System.nanoTime();

			for (int i=0;i<(timesToLoop);i++) {
				// generates a random collection of integers
				Collection<Integer> c = generateRandomCollection(n, timesToLoop);
				// uses this collection to construct a TreeSet
				TreeSet<Integer> set = new TreeSet<Integer>();
				set.addAll(c);
				// calls contains method for each item in the collection
				for(Integer k : c)
					set.contains(k);
			}

			midpointTime = System.nanoTime();

			for (long i = 0; i < timesToLoop; i++) { 
				Collection<Integer> c = generateRandomCollection(n, timesToLoop);
				TreeSet<Integer> set = new TreeSet<Integer>();
				set.addAll(c);
			}

			stopTime = System.nanoTime();

			// Compute the time, subtract the cost of running the loop and generating
			// random BST from the cost of running the loop, generating random BST and  
			// calling contains method.
			// Average it over the number of runs.
			double averageTimeJava = ((midpointTime - startTime) - (stopTime - midpointTime))
					/ timesToLoop;

			System.out.println(n + "\t" + averageTimeBST + "\t" + averageTimeJava);
		}
	}



	/**
	 * Generates a collection of integers in sorted order.
	 * @param size - the size of the generated collection
	 */
	public static Collection<Integer> generateSortedCollection(int size) {

		Integer[] arr = new Integer[size];
		for (int i=0; i<arr.length; i++) {
			arr[i] = i;
		}
		return Arrays.asList(arr);
	}


	/**
	 * Generates a collection of integers in random order.
	 * @param size - the size of the generated collection
	 * @param seed - the seed that controls the randomness of the collection
	 */
	public static Collection<Integer> generateRandomCollection(int size, int seed) {

		Integer[] arr = new Integer[size];
		generateSortedCollection(size).toArray(arr);
		Random rng = new Random(seed);
		// random permutation
		for (int i=0; i<arr.length; i++) {
			int temp = rng.nextInt(arr.length);
			int a = arr[i];
			arr[i] = arr[temp];
			arr[temp]= a;
		}
		return Arrays.asList(arr);
	}

}

