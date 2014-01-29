#include "cache.h"
#include "csapp.h"

#define CACHE_SIZE 50

/*
* Implements a proxy cache using a singly linked list.
*/

static node *head;
static int size;

/* Initialize an empty cache with a header node*/
void init_cache(){
  
  head =  (node *)Malloc(CACHE_SIZE *(sizeof(struct NODE)));
  head->next = NULL;
  size++;
}


/* Insert a node into the list */
void insert_node(char *uri, char *payload){

  node *current = head;
  check_size();
  while (1) {
   if (current->next == NULL){
     node *prev = current;
     current++;
     prev->next = current;
     current->uri = uri;
     current->payload = payload;
     size++;
     break;
    }
      current = current->next;
  }

}


/* Search the list for the object */
char *contains(char *uri){

  int n = 0;
  node *current = head;
  while (current->next != NULL){
    if (!strcmp(current->next->uri, uri))
      return current->next->payload;
    
    current = current->next;
    n++;
  }
  return NULL;

}

/* Checks that size is less than CACHE_SIZE, increasing size if necessary */
void check_size(){

  if(size == CACHE_SIZE - 1)
    increase_size();
}

/* Using realloc to increase the cache size retains the old contents */
void increase_size(){   
  size++;
  head = (node *) Realloc((void *)head, 2*size);
}
