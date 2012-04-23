JAVAC=javac
RM=rm -rf

CLIENT=TCPClient
CLIENT_WRAPPER=tcp_client

SERVER=TCPServer
SERVER_WRAPPER=tcp_server

SOURCES=$(CLIENT) $(SERVER)
JAVAFILES=$(CLIENT).java $(SERVER).java
BUILD_OS:=$(shell uname)

ifeq ($(BUILD_OS),SunOS)
	ECHO=/usr/ucb/echo
else
	ECHO=echo
endif

.PHONY: clean wrappers

all: compile wrappers

compile: $(JAVAFILES)
	$(JAVAC) $(JAVAFILES)
	
wrappers:	
	$(ECHO) "#!/bin/sh" > $(CLIENT_WRAPPER)
	$(ECHO) -n "java $(CLIENT) $$" >> $(CLIENT_WRAPPER) 
	$(ECHO) "@" >> $(CLIENT_WRAPPER)
	
	$(ECHO) "#!/bin/sh" > $(SERVER_WRAPPER)
	$(ECHO) -n "java $(SERVER) $$" >> $(SERVER_WRAPPER) 
	$(ECHO) "@" >> $(SERVER_WRAPPER)
	chmod +x $(CLIENT_WRAPPER)
	chmod +x $(SERVER_WRAPPER)

clean: 
	$(RM) *.class
	$(RM) $(CLIENT_WRAPPER)
	$(RM) $(SERVER_WRAPPER)
	
test: compile wrappers
	@./$(SERVER_WRAPPER) &
	@sleep 1
	@./$(CLIENT_WRAPPER) 127.0.0.1 123456
	
	
