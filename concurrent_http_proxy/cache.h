#ifndef __NODE_H__
#define __NODE_H__

struct NODE {
  struct NODE *next;
  char *uri;
  char *payload;
};

typedef struct NODE node;


void insert_node(char *uri, char *payload);
void remove_node(node *n);
void resize();
char *contains(char *uri);
void check_size();
void increase_size();
#endif
