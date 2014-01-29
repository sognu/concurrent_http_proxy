### NOTE: the clang targets assume that clang is in your PATH -- make sure that is the case

all: 
	gcc -m32 -g -O -Wall proxy.c csapp.c cache.c -o proxy -lpthread

clang:
	clang -m32 -g -O -Wall proxy.c csapp.c cache.c -o proxy -lpthread

strict:
	gcc -m32 -g -O -Wall -Werror proxy.c csapp.c cache.c -o proxy -lpthread

clang-strict:
	clang -m32 -g -fsanitize=undefined -O -Wall -Werror proxy.c csapp.c cache.c -o proxy -lpthread
 

clean:
	rm -f proxy
