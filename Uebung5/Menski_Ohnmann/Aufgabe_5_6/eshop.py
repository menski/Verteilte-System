#!/usr/bin/env python

from xmlrpclib import ServerProxy

for i in range(10):
    print "SessionId:", str(ServerProxy("http://localhost:8000/").session_id())
