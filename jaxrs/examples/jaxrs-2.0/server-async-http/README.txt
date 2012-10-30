Simple JAX-RS 2.0 example of server-side async HTTP.  This is a chat application.

$ mvn jetty:run

Open your browser to http://localhost:8080/index.html
Open another tab or browser to same and chat.

FYI, some messages are lost because we are queuing response streams, and not the actual posted messages, but you get
the idea I hope.
