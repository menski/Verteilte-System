#!/usr/bin/env python

from SimpleXMLRPCServer import SimpleXMLRPCServer

sid = 1


def session_id():
    global sid
    sid += 1
    return sid

server = SimpleXMLRPCServer(("localhost", 8000))
print "Listening on port 8000..."
server.register_function(session_id)
server.serve_forever()
