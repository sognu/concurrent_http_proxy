/**
 * 
 */
package assignBSTSpellCheck;

/**
 * @author C.M.
 *Implements a binary search tree.
 */
public class BST<type extends Comparable<? super type>>{


  private BNode<type> root;
  private int size;
    
   
  public boolean add(type item) {
  
      
      if(isEmpty()) {
         BNode<type> temp = new BNode<type>(null, null, item);
         size++;
         return true;
      }
      BNode<type> temp = finds(item);
      int status = temp.item.compareTo(item);
      if(status == 0)
          return false;
      if(status > 0)
          temp.l = new BNode<type>(null, null, item);
        
          else
              temp.l = new BNode<type>(null, null, item);
      size++;
      return true;
  
  }

   public boolean remove(type item) {
       if(isEmpty()) 
           return false;
       
       BNode<type> temp = root;
       
       int status = temp.item.compareTo(item);
       
       if(status == 0 && temp.l == null && temp.r == null) {
           root = null;
       size--;
       return true;
       }
       BNode<type> parent = root;
       while(status != 0) {
          parent = temp;
          if(status > 0)
                  temp = temp.l;         
          if(status < 0)
                  temp = temp.r;
        
          if(temp == null)//item is not in tree
              return false;
          status = temp.item.compareTo(item);
        }
   
   //case 1 temp is leaf.
   
   if(temp.l == null && temp.r == null) {
       if(temp == parent.l)
           parent.l = null;
       else if (temp == parent.r)
           parent.r = null;
   size--;
   return true;
   }
   
   
   //case2 temp has one left child -- replace by left child
   if(temp.l != null && temp.r ==null) {
       temp.item = temp.l.item;
       temp.r = temp.l.r;
       temp.l = temp.l.l;
    size --;
    return true;
   }
   //case3 temp has one right child -- replace by right child
   if(temp.l == null && temp.r !=null) {
       temp.item = temp.r.item;
       temp.l = temp.r.l;
       temp.r = temp.r.r;
      size--;
      return true;
   }
   
   //case4 temp has both right and left children. replace temp
   //by smallest of right subtree.
   BNode <type> psmallest = temp;
   BNode<type>  smallest = temp.r;
   while(smallest.l != null) {
       psmallest = smallest;
       smallest = smallest.l;
       //replace temp by smallest
       temp.item = smallest.item;
   } if(psmallest.r == smallest) {
           psmallest.r = smallest.r;
       }else
           psmallest.l = smallest.r; //smallest.l must be null
   size--;
   return true;
       
   }
 /**
 * 
 * @param item
 * @return
 */
private BNode<type> finds (type item)
{
 
    BNode<type> temp = root;
    int status = temp.item.compareTo(item);
    
    while(status != 0) {
       
        if(status > 0)
            if(temp.l != null)
                temp = temp.l;
            else
                break;
    if(status < 0)
        if(temp.r != null)
            temp = temp.r;
        else
            break;

    status = temp.item.compareTo(item);
    }
    return temp;
}



/**
 * @return
 */
private boolean isEmpty ()
{
   
    return size == 0;
}


static class BNode<type>{
      
      private type item;
      private BNode<type> l;
      private BNode<type> r;


      public BNode (BNode<type> l, BNode<type> r, type i) {
          this.l = l;
          this.r = r;
          item = i;
  }
      public int height() {
          
          int hl = 0;
          int hr = 0;
          if(l != null)
              hl = l.height();
          if(r != null)
              hr = r.height();
          
          if(hl > hr)
              return hl;
          else
              return hr;
          
      }
}

}




