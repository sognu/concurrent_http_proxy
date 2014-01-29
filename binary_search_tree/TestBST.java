package assignBSTSpellCheck;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import junit.framework.TestCase;
import static org.junit.Assert.*;

/**
 * 
 * @author Chad Miller
 *
 * Class implements unit test on BinarySearchTree
 * 
 */

public class TestBST extends TestCase 
{

	private BinarySearchTree<String> set1;
	// test1 is nonempty, test2 is empty, and test3 is null
	private ArrayList<String> test1, test2, test3;

	protected void setUp() throws Exception 
	{
		super.setUp();

		set1 = new BinarySearchTree<String>();
		test1 = new ArrayList<String>();
		test2 = new ArrayList<String>();
		test1.add("one");
		test1.add("two");
		test1.add("three");
		test1.add("four");
		test1.add("two");	// test1 contains duplicates
	}

	protected void tearDown() throws Exception 
	{
		super.tearDown();
	}


	// test the add method
	public void testAdd1() 
	{	
		assertEquals(true, set1.add("Hello"));
	}

	// test the add method: duplicate element
	public void testAdd2() 
	{	
		set1.add("Hello");
		assertEquals(false, set1.add("Hello"));
	}

	// test the add method: try to add null
	public void testAdd3() 
	{	
		try {
			set1.add(null);
			fail();
		}
		catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
	}

	// test the contains method: element in the set
	public void testContains1() 
	{
		set1.add("Hello");
		set1.add("World");
		set1.add("Apple");
		set1.add("Orange");
		assertEquals(true, set1.contains("Orange"));
	}

	// test the contains method: element not in the set
	public void testContains2() 
	{
		set1.add("Hello");
		set1.add("World");
		set1.add("Apple");
		set1.add("Orange");
		assertEquals(false, set1.contains("Grape"));
	}

	// contains null?
	public void testContains3() 
	{
		set1.add("Hello");
		set1.add("World");
		set1.add("Apple");
		set1.add("Orange");
		try {
			set1.contains(null);
			fail();
		}
		catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
	}

	// test the remove method: element in the set
	public void testRemove1() 
	{
		set1.add("Hello");
		set1.add("World");
		set1.add("Apple");
		set1.add("Orange");
		assertEquals(true, set1.remove("Orange"));
		assertEquals(false, set1.contains("Orange"));
	}

	// test the remove method: element not in the set
	public void testRemove2() 
	{
		set1.add("Hello");
		set1.add("World");
		set1.add("Apple");
		set1.add("Orange");
		assertEquals(false, set1.remove("Grape"));
	}

	// test the remove method: empty set
	public void testRemove3() 
	{	
		assertEquals(false, set1.remove("Grape"));
	}

	// use the contains method to test the remove method
	public void testRemove4() 
	{
		set1.add("Hello");	
		set1.add("Orange");
		set1.remove("Hello");
		assertEquals(false, set1.contains("Hello"));
	}

	// try to remove null
	public void testRemove5() 
	{	
		set1.add("Hello");	
		try {
			set1.remove(null);
			fail();
		}
		catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

	}

	// test the size method: nonempty set
	public void testSize1() 
	{
		set1.add("Hello");
		set1.add("World");
		set1.add("Apple");
		set1.add("Orange");
		assertEquals(4, set1.size());
	}

	// test the size method: empty set
	public void testSize2() 
	{
		assertEquals(0, set1.size());
	}

	// test the empty method: empty set
	public void testIsEmpty1() 
	{
		assertEquals(true, set1.isEmpty());
	}

	// test the empty method: nonempty set
	public void testIsEmpty2() 
	{
		set1.add("Orange");
		assertEquals(false, set1.isEmpty());
	}

	// test the addAll method: add a nonempty collection to an empty set
	public void testAddAll1()
	{
		assertEquals(true, set1.addAll(test1));
		assertEquals(true, set1.contains("one"));
		assertEquals(true, set1.contains("two"));
		assertEquals(true, set1.contains("three"));
		assertEquals(true, set1.contains("four"));
	}

	// test the addAll method: add an empty collection
	public void testAddAll2()
	{
		assertEquals(false, set1.addAll(test2));		
	}

	// try to add null
	public void testAddAll3()
	{
		try {
			set1.addAll(test3);
			fail();
		}
		catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
	}

	// test the addAll method: add a nonempty collection to a set which contains all those elements
	public void testAddAll4()
	{
		set1.add("one");
		set1.add("two");
		set1.add("three");
		set1.add("four");
		assertEquals(false, set1.addAll(test1));		
	}

	// use size method to test the addAll method
	public void testAddAll5()
	{
		set1.addAll(test1);
		assertEquals(4, set1.size() );		
	}

	// test the removeAll method: 
	// remove a nonempty collection from a nonempty set which shares common elements
	public void testRemoveAll1()
	{
		set1.add("one");
		set1.add("two");
		set1.add("three");
		set1.add("four");
		assertEquals(true, set1.removeAll(test1));	
		assertEquals(false, set1.contains("one"));
		assertEquals(false, set1.contains("two"));
		assertEquals(false, set1.contains("three"));
		assertEquals(false, set1.contains("four"));
	}

	// test the removeAll method: 
	// remove a nonempty collection from a nonempty set which have no common elements
	public void testRemoveAll2()
	{
		set1.add("five");
		set1.add("six");
		set1.add("seven");
		assertEquals(false, set1.removeAll(test1));		
	}

	// use the size method to test the removeAll method
	public void testRemoveAll3()
	{		
		set1.add("three");
		set1.add("four");
		set1.add("five");
		set1.add("six");
		set1.removeAll(test1);
		assertEquals(2, set1.size());	
		assertEquals(false, set1.contains("three"));
		assertEquals(false, set1.contains("four"));
	}

	// test the containsAll method: not containing all elements
	public void testContainsAll1()
	{
		set1.add("four");
		set1.add("five");
		set1.add("six");
		assertEquals(false, set1.containsAll(test1));		
	}

	// test the containsAll method: containing all elements
	public void testContainsAll2()
	{
		set1.addAll(test1);
		assertEquals(true, set1.containsAll(test1));		
	}

	// use the size method to test the clear method
	public void testClear1()
	{
		set1.addAll(test1);
		set1.clear();
		assertEquals(0, set1.size());		
	}

	// test the first method: nonempty set
	public void testFirst1() 
	{
		set1.add("Hello");
		set1.add("World");
		set1.add("Apple");
		set1.add("Orange");
		assertEquals("Apple", set1.first());
	}

	// test the first method: empty set
	public void testFirst2() 
	{	
		try
		{
			set1.first();
			fail();
		}
		catch(Exception e)
		{
			assertTrue(e instanceof NoSuchElementException);
		}
	}

	// test the last method: nonempty set
	public void testLast1() 
	{
		set1.add("Hello");
		set1.add("World");
		set1.add("Apple");
		set1.add("Orange");
		assertEquals("World", set1.last());
	}

	// test the first method: empty set
	public void testLast2() 
	{	
		try
		{
			set1.last();
			fail();
		}
		catch(Exception e)
		{
			assertTrue(e instanceof NoSuchElementException);
		}
	}

	// test the toArray method
	public void testToArray()
	{
		set1.addAll(test1);
		String[] expected = {"four", "one", "three", "two"};
		assertArrayEquals(expected, set1.toArrayList().toArray());
	}

	// test the preOrder method
	public void testPreOrder()
	{
		set1.addAll(test1);
		String[] expected = {"one", "four", "two", "three"};
		assertArrayEquals(expected, set1.preOrder().toArray());
	}

	// test the postOrder method
	public void testPostOrder()
	{
		set1.addAll(test1);
		String[] expected = {"four", "three", "two", "one"};
		assertArrayEquals(expected, set1.postOrder().toArray());
	}

}
