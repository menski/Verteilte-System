#!/usr/bin/env python

from SimpleXMLRPCServer import SimpleXMLRPCServer

# global session id
sid = 0


def session_id():
    # return new session id
    global sid
    sid += 1
    return sid

# bind new xmlrpc server to localhost and port 8000
server = SimpleXMLRPCServer(("localhost", 8000))
print "Listening on port 8000..."
# register session_id function for xmlrpc
server.register_function(session_id)
# run server
server.serve_forever()
