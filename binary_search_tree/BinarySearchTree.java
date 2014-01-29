package assignBSTSpellCheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 
 * @author Chad Miller
 *
 * Class implements a binary search tree.
 * 
 */

public class BinarySearchTree<Type extends Comparable<? super Type>> implements SortedSet<Type> {

    //root node;
	private BinaryNode<Type> root;
	//number of nodes
	private int size;

	 /**
	   * Ensures that this set contains the specified item.
	   * 
	   * @param item
	   *          - the item whose presence is ensured in this set
	   * @return true if this set changed as a result of this method call (that is,
	   *         if the input item was actually inserted); otherwise, returns false
	   * @throws NullPointerException
	   *           if the item is null
	   */
	public boolean add(Type item) {

		if (item == null)	// item is null
			throw new NullPointerException();

		if (isEmpty()) {	// empty set
			root = new BinaryNode<Type>(null, null,item);
			size++;
			return true;
		}
		// finds the place where item should be inserted
		BinaryNode<Type> temp = finds(item);
		int status = temp.item.compareTo(item);

		if(status == 0)	// item is contained in the tree
			return false;
		else{			// item is contained in the tree -- add it
			if(status > 0)
				temp.left = new BinaryNode<Type>(null, null,item);
			else 
				temp.right = new BinaryNode<Type>(null, null,item);

			size++; 
			return true;
		}
	}


	/**
	 * Finds the position of the given item in the tree. If the item is contained in the tree,
	 * the node containing this item is returned; otherwise the parent to which the given item
	 * should be inserted is returned.
	 * @param item - the given item
	 * @return the node containing this item if the item is contained in the tree;
	 * the parent to which the given item should be inserted if the item is not contained.
	 */
	private BinaryNode<Type> finds (Type item) {

		BinaryNode<Type> temp = root;
		// status indicating the value of this node is bigger than, equal to, or less than
		// the given item
		int status = temp.item.compareTo(item);

		while (status != 0) {
			if (status > 0)	// item should locate in the left branch
				if (temp.left != null)
					temp = temp.left;
				else break;	// reach the end -- item not found
			if (status < 0)	// item should locate in the right branch
				if(temp.right != null)
					temp = temp.right;
				else break;	// reach the end -- item not found

			status = temp.item.compareTo(item);
		}
		return temp;
	}

	  /**
	   * Ensures that this set contains all items in the specified collection.
	   * 
	   * @param items
	   *          - the collection of items whose presence is ensured in this set
	   * @return true if this set changed as a result of this method call (that is,
	   *         if any item in the input collection was actually inserted);
	   *         otherwise, returns false
	   * @throws NullPointerException
	   *           if any of the items is null
	   */
	public boolean addAll(Collection<? extends Type> items) {
		// the status indicating whether this set is altered or not
		boolean flag = false;
		for (Type e : items) {
			flag = add(e) || flag ;
		}
		return flag;
	}


	/**
	   * Determines if there is an item in this set that is equal to the specified
	   * item.
	   * 
	   * @param item
	   *          - the item sought in this set
	   * @return true if there is an item in this set that is equal to the input
	   *         item; otherwise, returns false
	   * @throws NullPointerException
	   *           if the item is null
	   */
	public boolean contains(Type item) {
		if (isEmpty())
			return false;
		else
			return 0 == item.compareTo(finds(item).item);
	}


	  /**
	   * Determines if for each item in the specified collection, there is an item
	   * in this set that is equal to it.
	   * 
	   * @param items
	   *          - the collection of items sought in this set
	   * @return true if for each item in the specified collection, there is an item
	   *         in this set that is equal to it; otherwise, returns false
	   * @throws NullPointerException
	   *           if any of the items is null
	   */

	public boolean containsAll(Collection<? extends Type> items) {
		// the status indicating if this set contains all items in the given collection
		boolean flag = true;
		for(Type t: items) {
			flag = contains(t) && flag;
		}
		return flag;
	}


	  /**
	   * Returns the first (i.e., smallest) item in this set.
	   * 
	   * @throws NoSuchElementException
	   *           if the set is empty
	   */
	public Type first() throws NoSuchElementException {
		if (isEmpty())	// empty set
			throw new NoSuchElementException();
		// finds the leftmost node
		BinaryNode<Type> temp = root;
		while (temp.left != null)
			temp = temp.left;

		return temp.item;
	}



	  /**
	   * Returns the last (i.e., largest) item in this set.
	   * 
	   * @throws NoSuchElementException
	   *           if the set is empty
	   */
	public Type last() throws NoSuchElementException {

		if (isEmpty())	// empty set
			throw new NoSuchElementException();
		// finds the rightmost node
		BinaryNode<Type> temp = root;
		while (temp.right != null)
			temp = temp.right;

		return temp.item;
	}


	  /**
	   * Ensures that this set does not contain the specified item.
	   * 
	   * @param item
	   *          - the item whose absence is ensured in this set
	   * @return true if this set changed as a result of this method call (that is,
	   *         if the input item was actually removed); otherwise, returns false
	   * @throws NullPointerException
	   *           if the item is null
	   */
	public boolean remove(Type item) {

		if(isEmpty())	// empty set
			return false;

		// temp holds the current node in the tree
		BinaryNode<Type> temp = root;
		// status indicating the value of this node is bigger than, equal to, or less than
		// the given item
		int status = temp.item.compareTo(item);

		// Case 0:
		// root should be removed and root has no children
		if (status == 0 && temp.left == null && temp.right == null) {	
			root = null;
			size--;
			return true;
		}
		// parent holds the parent of the current node
		BinaryNode<Type> parent = root;

		// finds the position of the given item
		while (status != 0) {	// not find yet
			parent = temp;
			if (status > 0)
				temp = temp.left;
			else
				temp = temp.right;

			if (temp == null)	// item is not in the tree
				return false;

			status = temp.item.compareTo(item);
		}

		// Now removing
		// Case 1:
		// temp is a leaf and temp is not the root 
		// delete it by disconnecting the link from its parent
		if (temp.left == null && temp.right == null) {
			if (temp == parent.left)
				// left child of its parent
				parent.left = null;
			else 
				// right child of its parent?
				parent.right = null;
			size--;
			return true;
		}
		// Case 2:
		// temp has only left child -- replace temp by its left child
		else if (temp.left != null && temp.right == null) { 
			temp.item = temp.left.item;
			temp.right = temp.left.right;
			temp.left = temp.left.left;
			size--;
			return true;
		}
		// Case 3:
		// temp has only right child -- replace temp by its right child
		else if (temp.left == null && temp.right != null) { 
			temp.item = temp.right.item;
			temp.left = temp.right.left;
			temp.right = temp.right.right;
			size--;
			return true;
		}
        // Case 4:
        // temp has both left and right children
        // replace temp by the smallest element of the right subtree
        else {
            // the smallest element of the right subtree
            BinaryNode<Type> smallest = temp.right;
            // the parent of the smallest element of the right subtree
            BinaryNode<Type> parentSmallest = temp;
            // finds the smallest element of the right subtree
            while (smallest.left != null) {
                parentSmallest = smallest;
                smallest = smallest.left;
            }
            // replace temp by smallest
            temp.item = smallest.item;
            if (parentSmallest.right == smallest)
                parentSmallest.right = smallest.right;
            else
                parentSmallest.left = smallest.right;    // note that smallest.left must be null
            size--;
            return true;
        }
	}


	  /**
	   * Ensures that this set does not contain any of the items in the specified
	   * collection.
	   * 
	   * @param items
	   *          - the collection of items whose absence is ensured in this set
	   * @return true if this set changed as a result of this method call (that is,
	   *         if any item in the input collection was actually removed);
	   *         otherwise, returns false
	   * @throws NullPointerException
	   *           if any of the items is null
	   */
	public boolean removeAll(Collection<? extends Type> items) {
		// the status indicating whether this set is altered or not
		boolean flag = false;
		for(Type t: items) {
			// try to remove each item in the collection
			flag = remove(t) || flag;
		}
		return flag;
	}


	 /**
	   * Removes all items from this set. The set will be empty after this method
	   * call.
	   */
	public void clear() {
		root = null;
		size = 0;
	}

	 /**
	   * Returns the number of items in this set.
	   */
	public int size() {
		return size;
	}


	  /**
	   * Returns true if this set contains no items.
	   */
	public boolean isEmpty() {
		return size == 0;
	}


	  /**
	   * Returns an ArrayList containing all of the items in this set, in sorted
	   * order.
	   */
	public ArrayList<Type> toArrayList() {
		return inOrder();	// in order traverse gives a list of node in sorted order
	}


	/**
	 * Uses post order (node, left, right) to traverse a tree and return the sequence as a list.
	 */
	public ArrayList<Type> preOrder(){
		// the list in which the traverse sequence is stored
		ArrayList<Type> result = new ArrayList<Type>();
		// use a private help method to do the job
		preOrder(this.root, result);
		return result;
	}


	/**
	 * Uses post order (left, node, right) to traverse a tree and return the sequence as a list.
	 */
	public ArrayList<Type> inOrder(){
		// the list in which the traverse sequence is stored
		ArrayList<Type> result = new ArrayList<Type>();
		// use a private help method to do the job
		inOrder(this.root, result);
		return result;
	}


	/**
	 * Uses post order (left, right, node) to traverse a tree and return the sequence as a list.
	 */
	public ArrayList<Type> postOrder(){
		// the list in which the traverse sequence is stored
		ArrayList<Type> result = new ArrayList<Type>();
		// use a private help method to do the job
		postOrder(this.root, result);
		return result;
	}


	/**
	 * Uses pre order (node, left, right) to traverse a tree 
	 * and store the result in the given list.
	 * @param root - the root of the tree to be traversed
	 * @param list - the list in which the traversing result is stored
	 */
	private void preOrder(BinaryNode<Type> root, List<Type> list) {
		list.add(root.item);
		if (root.left != null)
			preOrder(root.left, list);
		if (root.right != null)
			preOrder(root.right, list);
	}


	/**
	 * Uses in order (left, node, right) to traverse a tree 
	 * and store the result in the given list.
	 * @param root - the root of the tree to be traversed
	 * @param list - the list in which the traversing result is stored
	 */
	private void inOrder(BinaryNode<Type> root, List<Type> list) {
		if (root.left != null)
			inOrder(root.left, list);
		list.add(root.item);
		if (root.right != null)
			inOrder(root.right, list);
	}


	/**
	 * Uses post order (left, right, node) to traverse a tree 
	 * and store the result in the given list.
	 * @param root - the root of the tree to be traversed
	 * @param list - the list in which the traversing result is stored
	 */
	private void postOrder(BinaryNode<Type> root, List<Type> list) {
		if (root.left != null)
			postOrder(root.left, list);
		if (root.right != null)
			postOrder(root.right, list);
		list.add(root.item);
	}
}


/**
 * Represents a node in a binary tree.
 * 
 * @author Chad Miller and Liang Zhang
 * @param <Type>
 */
class BinaryNode<Type>{

	protected BinaryNode<Type> left;	// left child of this node
	protected BinaryNode<Type> right;	// right child of this node
	protected Type item;				// value of this node

	/**
	 * Constructs a new node from given left child, right child, and value.
	 */
	public BinaryNode (BinaryNode<Type> left, BinaryNode<Type> right,Type item){
		this.left = left;
		this.right = right;
		this.item = item;
	}

	  public int height() {
	      
	         int sizeL = 0;
	         int sizeR = 0;
	     if(left != null)
	            sizeL = left.height();
	     if(right != null)
	             sizeR = right.height();
	    

	    
	     if(sizeL > sizeR)
	         return sizeL +1;
	     else
	         return sizeR +1;

}
}