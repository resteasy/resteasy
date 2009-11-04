
This project is a simple example showing usage of @Path, @GET, PUT, POST, and @PathParam.  It uses pure streaming
output as well. 

System Requirements:
====================
- Maven 2.0.9 or higher

Building the project:
====================
In root directoy

mvn clean compile

Flickr Client:
1. apply for an API key from Flickr - http://www.flickr.com/services/api/keys/apply 
2. mvn exec:java -Dexec.mainClass="org.jboss.resteasy.examples.flickr.FlickrClient" -Dexec.args="<apiKey>"

Twitter Client
mvn exec:java -Dexec.mainClass="org.jboss.resteasy.examples.twitter.TwitterClient" -Dexec.args="<userId> <password>"
