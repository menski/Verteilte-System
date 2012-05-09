JAVAC=javac
RM=rm -rf

CLIENT=UDPClient
CLIENT_WRAPPER=udpclient

SERVER=UDPServer
SERVER_WRAPPER=udpserver

SOURCES=$(CLIENT) $(SERVER)
JAVAFILES=$(CLIENT).java $(SERVER).java UDPListenerRunnable.java
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
	@./$(SERVER_WRAPPER) 10041 &
	@sleep 1
	@./$(CLIENT_WRAPPER) 123456 unicast 127.0.0.1 10041
	
	
