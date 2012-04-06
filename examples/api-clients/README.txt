
This project is a simple example showing usage of @Path, @GET, PUT, POST, and @PathParam.  It uses pure streaming
output as well. 

System Requirements:
====================
- Maven 2.0.9 or higher

Building the project:
====================
In root directoy

mvn clean compile

****Flickr Client:
1. apply for an API key from Flickr - http://www.flickr.com/services/api/keys/apply 
2. mvn exec:java -Dexec.mainClass="org.jboss.resteasy.examples.flickr.FlickrClient" -Dexec.args="<apiKey>"

****Twitter Client
From August 31, 2010, Basic Auth has been deprecated by Twitter, and all applications must now use OAuth.
So the Twitter Client now uses OAuth to do the authentication. In this example you can see how to inject 
OAuth Authentication Header via RESTEasy Proxy Client.

Be sure to run the example *in following step!*:
1. request token from twitter:
mvn exec:java -Dexec.mainClass="org.jboss.resteasy.examples.twitter.TwitterClient" -Dexec.args=request

2. authorize token by twitter:
mvn exec:java -Dexec.mainClass="org.jboss.resteasy.examples.twitter.TwitterClient" -Dexec.args=authorize
In this step you will be provided a link for authentication. Paste it to web browser and grant access to this example.  

3. request access token for our example:
mvn exec:java -Dexec.mainClass="org.jboss.resteasy.examples.twitter.TwitterClient" -Dexec.args=access
Now we can play with twitter: 

4. tweet:
mvn exec:java -Dexec.mainClass="org.jboss.resteasy.examples.twitter.TwitterClient" -Dexec.args=update

5. query the status:
mvn exec:java -Dexec.mainClass="org.jboss.resteasy.examples.twitter.TwitterClient" -Dexec.args=query

If you meet a '401 Authorization Problem' in the last two steps, it's most probably your token has expired. 
Redo the first three steps may solve the problem. Have fun!  


 
