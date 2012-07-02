#!/usr/bin/env python

from xmlrpclib import ServerProxy
import sys


if (len(sys.argv) > 1):
    id = sys.argv[1]
else:
    id=0

for i in range(10):
    print "[Client {0}] Session Id: {1}".format(id, ServerProxy("http://localhost:8000/").session_id())
