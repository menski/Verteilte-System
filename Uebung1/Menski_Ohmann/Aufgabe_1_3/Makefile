CC=gcc
CCFLAGS= -ansi -pedantic -W -Wall
SERVER=tcp_server
CLIENT=tcp_client

build: $(SERVER).c $(CLIENT).c
	$(CC) $(CCFLAGS) -o $(SERVER) $(SERVER).c
	$(CC) $(CCFLAGS) -o $(CLIENT) $(CLIENT).c

test: build
	@./$(SERVER) &
	@sleep 1
	@./$(CLIENT) 127.0.0.1 123456

clean:
	rm -rf $(SERVER)
	rm -rf $(CLIENT)

.PHONY: build test clean
