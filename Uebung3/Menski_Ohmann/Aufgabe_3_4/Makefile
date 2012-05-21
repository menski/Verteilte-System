JAVAC=javac
RM=rm -rf

CLIENT=RmiClient
CLIENT_WRAPPER=rmiclient
CLIENT_POLICY=no.policy
# $(CLIENT_WRAPPER).policy

SERVER=RmiServer
SERVER_WRAPPER=rmiserver
SERVER_POLICY=no.policy
# $(SERVER_WRAPPER).policy

JAVAFILES=$(CLIENT).java $(SERVER).java RmiServerIntf.java RmiServerImpl.java
BUILD_OS:=$(shell uname)

ifeq ($(BUILD_OS),SunOS)
	ECHO=/usr/ucb/echo
else
	ECHO=echo
endif

.PHONY: clean rmicompile policies wrappers

all: compile rmicompile policies wrappers

compile: $(JAVAFILES)
	$(JAVAC) $(JAVAFILES)
	
rmicompile:
	rmic RmiServerImpl
	
policies: 
	@$(ECHO) "grant {" > $(CLIENT_POLICY)
	@$(ECHO) "permission java.net.SocketPermission \"127.0.0.1:*\", \"connect,resolve\";" >> $(CLIENT_POLICY)
	@$(ECHO) "};" >> $(CLIENT_POLICY)
	@$(ECHO) "grant {" > $(SERVER_POLICY)
	@$(ECHO) "permission java.net.SocketPermission \"127.0.0.1:*\", \"connect,resolve\";" >> $(SERVER_POLICY)
	@$(ECHO) "permission java.net.SocketPermission \"127.0.0.1:*\", \"accept\";" >> $(SERVER_POLICY)
	@$(ECHO) "};" >> $(SERVER_POLICY)
	@$(ECHO) "grant {" > no.policy
	@$(ECHO) "permission java.security.AllPermission;" >> no.policy
	@$(ECHO) "};" >> no.policy
	
wrappers:	
	@$(ECHO) "#!/bin/sh" > $(CLIENT_WRAPPER)
	@$(ECHO) -n "java -Djava.security.policy=$(CLIENT_POLICY) $(CLIENT) $$" >> $(CLIENT_WRAPPER) 
	@$(ECHO) "@" >> $(CLIENT_WRAPPER)
	
	@$(ECHO) "#!/bin/sh" > $(SERVER_WRAPPER)
	@$(ECHO) -n "java -Djava.security.policy=$(SERVER_POLICY) $(SERVER) $$" >> $(SERVER_WRAPPER) 
	@$(ECHO) "@" >> $(SERVER_WRAPPER)
	@chmod +x $(CLIENT_WRAPPER)
	@chmod +x $(SERVER_WRAPPER)

clean: 
	@$(RM) *.class
	@$(RM) *.policy
	@$(RM) $(CLIENT_WRAPPER)
	@$(RM) $(SERVER_WRAPPER)
	
test: compile wrappers
	./$(SERVER_WRAPPER) &
	@sleep 1
	./$(CLIENT_WRAPPER) 123456 127.0.0.1

	@$(ECHO) ""
	@$(ECHO) "---> Dont't forget to kill rmiserver <---"	

