Jettison Example
=====================
Example of using RestEasy with:
- Using JSON with Resteasy
- The JAXB/JSON Provider
- Using javax.ws.rs.core.Application
- Using the <context-param> resteasy.servlet.mapping.prefix
- Jetty (embedded)

System Requirements:
-------------------------
- Maven 2.0.9 or higher

Building the project:
====================

mvn clean install

Running the project and manually testing it:
-------------------------

mvn jetty:run

Open a browser at the following URL:

HTML form that pulls JSON from a JAX-RS service.  The data is JAXB annotated classes marshalled to JSON
using the Jettison mapped format:

http://localhost:9095/mapped.html

HTML form that pulls JSON from a JAX-RS service.  The data is JAXB annotated classes marshalled to JSON
using the Jettison Badger format:

http://localhost:9095/badger.html

To view the Mapped JSON format:

http://localhost:9095/resteasy/library/books/mapped.html

To view the Badger JSON format:

http://localhost:9095/resteasy/library/books/badger.html

