#!/usr/bin/env python

import xmlrpclib
import sys
import socket


if (len(sys.argv) > 1):
    id = sys.argv[1]
else:
    id=0

for i in range(10):
    for j in range(5):
        try:
            # Connect to syncserver and get session id
            print "[Client {0}] Session Id: {1}".format(id, xmlrpclib.ServerProxy("http://localhost:8000/").session_id())
            # Leave retry loop after success
            break
        except socket.error, err:
            # Handle socket error
            print "[ERROR]", err
        except xmlrpclib.ProtocolError, err:
            # Handle xmlrpc error
            print "[ERROR]", err
        if j == 4:
            # After 5th error write to log and exit
            with open("eshop-{0}.log".format(id), "a") as fd:
                fd.write("Synchronisations-Server nicht erreichbar\n")
            sys.exit(1)
